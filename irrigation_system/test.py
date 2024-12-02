import socket
import threading
import spidev
from gpiozero import DigitalOutputDevice
from time import sleep
import RPi.GPIO as GPIO
import requests
from datetime import datetime, timedelta

# 현재 시각을 가져오기
current_datetime = datetime.now()

# 현재 시각의 시(hour) 값을 가져와서 조건을 만족하면 전날 날짜로 설정
if 0 <= current_datetime.hour < 6:
    today_date = (current_datetime - timedelta(days=1)).strftime("%Y%m%d")
else:
    today_date = current_datetime.strftime("%Y%m%d")

current_time = current_datetime.strftime("%H%M")
base_time = "0500"

url = "http://localhost:8080/weather/json"

params = {
    "baseDate": today_date,
    "baseTime": base_time,
    "nx": "55",
    "ny": "127"
}

def updateParams():
    global params, base_time, current_datetime
    current_datetime = datetime.now()
    if 0 <= current_datetime.hour < 6:
        today_date = (current_datetime - timedelta(days=1)).strftime("%Y%m%d")
    else:
        today_date = current_datetime.strftime("%Y%m%d")
    
    current_time = current_datetime.strftime("%H%M")
    params = {
        "baseDate": today_date,
        "baseTime": base_time,
        "nx": "55",
        "ny": "127"
    }


def willRain(hours):
    try:
        updateParams()
        response = requests.get(url, params=params)
        
        if response.status_code == 200:
            data = response.json()
            
            # 현재 시각부터 hours시간 내의 데이터를 확인
            time_threshold = current_datetime + timedelta(hours=hours)
            rain_alert = False  # 비가 올 확률이 70% 이상인 시간대가 있는지 여부
            
            for entry in data:
                forecast_time = datetime.strptime(entry['fcstDate'] + entry['fcstTime'], '%Y%m%d%H%M')
                
                # 현재 시각부터 24시간 이내의 예보만 확인
                if current_datetime <= forecast_time <= time_threshold:
                    precip_prob = int(entry['propabilityOfPrecipitation'])
                    if precip_prob >= 70:
                        rain_alert = True
                        print(f"{forecast_time} 시점에 비가 올 확률이 70% 이상입니다. 확률: {precip_prob}%")
                        return True
            
            if not rain_alert:
                print("24시간 내에 비가 올 확률이 70% 이상인 시간대는 없습니다.")
                return False
        else:
            print(f"API 요청 실패: 상태 코드 {response.status_code}")
    except Exception as e:
        print(f"오류 발생: {e}")


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
    last_executed_hour = -1
    willRain24 = False
    willRain48 = False
    turnOff = False
    executed = False
    while True:
        if is_auto:
            current_datetime = datetime.now()
            # 정각(?)이고 실행되지 않았을 경우
            if current_datetime.minute == 0 or True:
                # 지금 시간대와 마지막으로 측정한 시간대가 같지 않아야 작동 ->
                if current_datetime.hour != last_executed_hour:
                    print(f"{current_datetime.strftime('%Y-%m-%d %H:%M:%S')} - 정각입니다. 비 예측 코드 실행.")
                    willRain24 = willRain(24)
                    last_executed_hour = current_datetime.hour
            
            while (willRain24 or not is_auto):
                if (last_executed_hour < datetime.now().hour or not is_auto):
                    willRain24 = False
                    break
                    
                    
                    
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

# 작업 중지 플래그
stop_current_task = False
#클라이언트 메세지 처리
def handle_client_message(client_socket, data):
    global is_auto, stop_current_task  # 전역 변수 사용
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
                # 이전 작업 중지 요청
                stop_current_task = True
                sleep(7)  # 이전 작업이 종료될 시간을 확보
                stop_current_task = False  # 새 작업 시작 준비

                print(f"토양 수분이 {value}% 이상 될 때까지 워터 펌프 작동")
                while read_adc_per(SPI_CHANNEL) < value:
                    if stop_current_task or is_auto:  # 중지 요청 확인
                        print("작업 중단 요청 수신. 현재 작업 중지.")
                        GPIO.output(PUMP_PIN, GPIO.HIGH)  # 펌프 끄기
                        break
                    
                    GPIO.output(PUMP_PIN, GPIO.HIGH)
                    sleep(3)
                    GPIO.output(PUMP_PIN, GPIO.LOW)
                    sleep(3)


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
