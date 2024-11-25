import socket
import threading
import spidev
from gpiozero import DigitalOutputDevice
from time import sleep

TCP_PORT = 8090  # 서버 포트 번호
BUFSIZE = 1024  # 메시지 버퍼 크기
MAX_CLIENT = 3  # 동시 클라이언트 수

SPI_CHANNEL = 0
SPI_SPEED = 500000

PUMP_PIN = 4

# SPI 설정
spi = spidev.SpiDev()
spi.open(0, SPI_CHANNEL)
spi.max_speed_hz = SPI_SPEED

# PUMP 설정
pump = DigitalOutputDevice(PUMP_PIN)
pump.on()

# ADC 값 읽기 함수
def read_adc_per(channel):
    if channel < 0 or channel > 7:
        return -1
    
    buf = [1, (8 + channel) << 4, 0]
    response = spi.xfer2(buf)
    adc_value = ((response[1] & 3) << 8) + response[2]
    percentage = (1023 - adc_value) * 100 // 1023
    return percentage

# 클라이언트 스레드 처리
def client_thread_loop(client_socket, client_address):
    print(f"클라이언트 {client_address} 와 연결되었습니다.")
    is_auto = True
    
    try:
        while True:
            data = client_socket.recv(BUFSIZE).decode('utf-8').strip()
            if not data:
                break

            print(f"클라이언트 {client_address} 에서 보낸 데이터: {data}")
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

            if is_auto:
                if read_adc_per(SPI_CHANNEL) < 30:
                    while read_adc_per(SPI_CHANNEL) < 80:
                        pump.off()
                        sleep(3)
                        pump.on()
                        sleep(3)
            else:
                tokens = data.split(":")
                if tokens[0] == "irrigation_system" and len(tokens) > 2:
                    command, value = tokens[1], int(tokens[2])
                    if command == "pump":
                        print(f"토양 수분이 {value}% 이상 될 때까지 워터 펌프 작동")
                        while read_adc_per(SPI_CHANNEL) < value:
                            pump.off()
                            sleep(3)
                            pump.on()
                            sleep(3)

            client_socket.sendall(send_data.encode('utf-8'))
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

