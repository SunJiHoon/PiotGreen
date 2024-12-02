import socket
import threading
import spidev
from gpiozero import DigitalOutputDevice
from time import sleep
import RPi.GPIO as GPIO

TCP_PORT = 8090  # 서버 포트 번호
BUFSIZE = 1024  # 메시지 버퍼 크기
MAX_CLIENT = 3  # 동시 클라이언트 수

SPI_CHANNEL = 0
SPI_SPEED = 500000

PUMP_PIN = 17  # 펌프에 연결된 GPIO 핀

# 자동/수동 모드 전역 변수
is_auto = True

# SPI 설정
spi = spidev.SpiDev()
spi.open(0, SPI_CHANNEL)
spi.max_speed_hz = SPI_SPEED

# GPIO 설정
GPIO.setmode(GPIO.BCM)
GPIO.setup(PUMP_PIN, GPIO.OUT)
GPIO.output(PUMP_PIN, GPIO.HIGH)  # 펌프 초기 상태 (HIGH에서 끄기)

# ADC 값 읽기 함수
def read_adc_per(channel):
    if channel < 0 or channel > 7:
        return -1
    
    buf = [1, (8 + channel) << 4, 0]
    response = spi.xfer2(buf)
    adc_value = ((response[1] & 3) << 8) + response[2]
    percentage = (1023 - adc_value) * 100 // 1023
    return percentage

# 자동 모드에서 펌프 제어를 계속 반복하는 함수
def auto_mode_loop():
    global is_auto  # 전역 변수 사용
    while True:
        if is_auto:
            soil_moisture = read_adc_per(SPI_CHANNEL)
            print(f"현재 측정 퍼센트: {soil_moisture}%")
            if soil_moisture < 30:
                while read_adc_per(SPI_CHANNEL) < 80:
                    print("펌프 작동중...")
                    GPIO.output(PUMP_PIN, GPIO.LOW)  # 펌프 켜기
                    sleep(3)
                    GPIO.output(PUMP_PIN, GPIO.HIGH)   # 펌프 끄기
                    sleep(3)
                    if not is_auto:  # is_auto가 False일 때 종료
                        GPIO.output(PUMP_PIN, GPIO.HIGH)
                        break

# 클라이언트 메시지 처리
def handle_client_message(client_socket, data):
    global is_auto  # 전역 변수 사용
    send_data = data
    if "mode" in data:
        tokens = data.split(":")
        if len(tokens) > 1:
            mode = tokens[1]
            if mode == "auto":
                is_auto = True
            elif mode == "pass":
                is_auto = False
        print(f"모드 설정: {'자동' if is_auto else '수동'}")

    # 수동 모드에서만 펌프 제어
    if not is_auto:
        tokens = data.split(":")
        if len(tokens) > 2 and tokens[0] == "irrigation_system":
            command, value = tokens[1], int(tokens[2])
            if command == "pump":
                print(f"토양 수분이 {value}% 이상 될 때까지 워터 펌프 작동")
                while read_adc_per(SPI_CHANNEL) < value:
                    GPIO.output(PUMP_PIN, GPIO.HIGH)
                    sleep(3)
                    GPIO.output(PUMP_PIN, GPIO.LOW)
                    sleep(3)
                    if is_auto:  # 자동 모드로 변경되면 종료
                        GPIO.output(PUMP_PIN, GPIO.HIGH)
                        break

    # 클라이언트에게 응답 보내기
    client_socket.sendall(send_data.encode('utf-8'))

# 클라이언트 스레드 처리
def client_thread_loop(client_socket, client_address):
    global is_auto  # 전역 변수 사용
    print(f"클라이언트 {client_address} 와 연결되었습니다.")
    
    try:
        while True:
            # 클라이언트에서 데이터를 계속 받아오는 부분을 별도의 스레드에서 처리
            data = client_socket.recv(BUFSIZE).decode('utf-8').strip()
            if not data:
                break

            print(f"클라이언트 {client_address} 에서 보낸 데이터: {data}")
            # 클라이언트 메시지 처리
            threading.Thread(target=handle_client_message, args=(client_socket, data)).start()
            
    except Exception as e:
        print(f"에러 발생: {e}")
    finally:
        print(f"클라이언트 {client_address} 와 연결을 종료합니다.")
        client_socket.close()

# 메인 서버 실행
def main():
    server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server_socket.bind(("", TCP_PORT))
    server_socket.listen(MAX_CLIENT)
    print(f"포트 {TCP_PORT}에서 대기 중...")

    # 자동 모드 실행을 위한 백그라운드 스레드 시작
    threading.Thread(target=auto_mode_loop, daemon=True).start()

    client_threads = []
    
    try:
        while True:
            client_socket, client_address = server_socket.accept()
            thread = threading.Thread(target=client_thread_loop, args=(client_socket, client_address))
            thread.start()
            client_threads.append(thread)
    except KeyboardInterrupt:
        print("서버를 종료합니다.")
    finally:
        for thread in client_threads:
            thread.join()
        server_socket.close()

if __name__ == "__main__":
    main()
