import cv2
import numpy as np
import socket
import threading
import RPi.GPIO as GPIO
import requests
import time

# GPIO 설정
GPIO.setmode(GPIO.BCM)
GPIO.setwarnings(False)

SECURITY_LED_PIN = 23  # 보안 모드 활성화 LED
MOTION_LED_PIN = 24    # 움직임 감지 LED
SECURITY_OFF_LED_PIN = 25  # 보안 모드 비활성화 LED
BUZZER_PIN = 8         # 움직임 감지 시 부저

GPIO.setup(SECURITY_LED_PIN, GPIO.OUT)
GPIO.setup(MOTION_LED_PIN, GPIO.OUT)
GPIO.setup(SECURITY_OFF_LED_PIN, GPIO.OUT)
GPIO.setup(BUZZER_PIN, GPIO.OUT)

GPIO.output(SECURITY_LED_PIN, GPIO.LOW)
GPIO.output(MOTION_LED_PIN, GPIO.LOW)
GPIO.output(SECURITY_OFF_LED_PIN, GPIO.LOW)
GPIO.output(BUZZER_PIN, GPIO.LOW)

# 날씨 API 설정
WEATHER_API_KEY = "e548fb177a976133d31021053019b35d"  # OpenWeatherMap API 키
LAT = "37.5665"  # 서울의 위도
LON = "126.9780"  # 서울의 경도
WEATHER_API_URL = f"https://api.openweathermap.org/data/2.5/weather?lat={LAT}&lon={LON}&appid={WEATHER_API_KEY}"

weather_data = {}
motion_threshold = 30
min_area = 1500

def fetch_weather():
    global motion_threshold, min_area, weather_data
    while True:
        try:
            print("Fetching weather data...")
            response = requests.get(WEATHER_API_URL)
            if response.status_code == 200:
                # 응답 처리
                weather_data = response.json()
                weather_conditions = [w['main'].lower() for w in weather_data.get('weather', [])]
                wind_speed = weather_data.get('wind', {}).get('speed', 0)

                # 날씨 조건에 따른 민감도 조정
                if 'snow' in weather_conditions:
                    motion_threshold = 50  # 눈: 민감도 낮춤
                    min_area = 2000
                elif 'rain' in weather_conditions:
                    motion_threshold = 60  # 비: 민감도 더 낮춤
                    min_area = 2500
                elif wind_speed > 10:  # 바람이 많이 부는 경우
                    motion_threshold = 80
                    min_area = 3000
                else:
                    motion_threshold = 30  # 기본 민감도
                    min_area = 1500

                print(f"Weather updated: {weather_conditions}, Wind Speed: {wind_speed}, "
                      f"Motion Threshold: {motion_threshold}, Min Area: {min_area}")
            elif response.status_code == 401:
                print("Unauthorized: Check your API key.")
            else:
                print(f"Failed to fetch weather data: HTTP {response.status_code}")
        except Exception as e:
            print(f"Error fetching weather data: {e}")

        time.sleep(300)  # 5분 간격으로 날씨 데이터 갱신

# 날씨 업데이트 스레드 시작
weather_thread = threading.Thread(target=fetch_weather, daemon=True)
weather_thread.start()

# 소켓 통신 설정
server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server_socket.bind(('0.0.0.0', 8089))
server_socket.listen(1)

print("Waiting for a connection...")
conn, addr = server_socket.accept()
print(f"Connected to {addr}")

sock_send = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
sock_send.connect(('main.putiez.com', 8088))

stream_url = "http://192.168.137.223:8081/"
cap = cv2.VideoCapture(stream_url)

if not cap.isOpened():
    print("Cannot open stream.")
    exit()

frame_width, frame_height = 640, 480

ret, prev_frame = cap.read()
if not ret:
    print("Cannot get the initial frame.")
    exit()

prev_frame = cv2.resize(prev_frame, (frame_width, frame_height))
prev_gray = cv2.cvtColor(prev_frame, cv2.COLOR_BGR2GRAY)
prev_gray = cv2.GaussianBlur(prev_gray, (5, 5), 0)

frame_skip = 2
frame_count = 0
detection_enabled = False
motion_detected_flag = False

def receive_commands():
    global detection_enabled
    while True:
        try:
            data = conn.recv(1024).decode('utf-8')
            if not data:
                break
            print(f"Received command: {data}")
            if "intrusion_detection:danger:on" in data:
                detection_enabled = True
                print("Detection enabled")
                GPIO.output(SECURITY_LED_PIN, GPIO.HIGH)
                GPIO.output(SECURITY_OFF_LED_PIN, GPIO.LOW)
            elif "intrusion_detection:danger:off" in data:
                detection_enabled = False
                print("Detection disabled")
                GPIO.output(SECURITY_LED_PIN, GPIO.LOW)
                GPIO.output(MOTION_LED_PIN, GPIO.LOW)
                GPIO.output(SECURITY_OFF_LED_PIN, GPIO.HIGH)
                GPIO.output(BUZZER_PIN, GPIO.LOW)
        except socket.error as e:
            print(f"Socket error: {e}")
            break

receive_thread = threading.Thread(target=receive_commands, daemon=True)
receive_thread.start()

while True:
    ret, frame = cap.read()
    if not ret:
        continue

    frame_count += 1
    if frame_count % frame_skip != 0:
        continue

    if not detection_enabled:
        continue

    frame = cv2.resize(frame, (frame_width, frame_height))
    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
    gray = cv2.GaussianBlur(gray, (5, 5), 0)

    frame_delta = cv2.absdiff(prev_gray, gray)
    _, thresh = cv2.threshold(frame_delta, motion_threshold, 255, cv2.THRESH_BINARY)
    thresh = cv2.dilate(thresh, None, iterations=2)

    contours, _ = cv2.findContours(thresh, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
    largest_contour = None
    max_area = 0

    for contour in contours:
        area = cv2.contourArea(contour)
        if area > min_area and area > max_area:
            largest_contour = contour
            max_area = area

    if largest_contour is not None:
        GPIO.output(MOTION_LED_PIN, GPIO.HIGH)
        GPIO.output(BUZZER_PIN, GPIO.HIGH)

        if not motion_detected_flag:
            sock_send.send(b"intrusion_detection:danger:1\n")
            motion_detected_flag = True

        x, y, w, h = cv2.boundingRect(largest_contour)
        cv2.rectangle(frame, (x, y), (x + w, y + h), (0, 255, 0), 2)
        print(f"Motion detected: Bounding box=(({x}, {y}), ({x+w}, {y+h}))")
    else:
        GPIO.output(MOTION_LED_PIN, GPIO.LOW)
        GPIO.output(BUZZER_PIN, GPIO.LOW)

        if motion_detected_flag:
            sock_send.send(b"intrusion_detection:danger:0\n")
            motion_detected_flag = False

    cv2.imshow("Motion Detection", frame)
    prev_gray = gray.copy()

    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

cap.release()
cv2.destroyAllWindows()
GPIO.cleanup()
