import cv2
import numpy as np
import time
import socket
import threading
import RPi.GPIO as GPIO

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

# 소켓 통신 설정
server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server_socket.bind(('0.0.0.0', 8089))
server_socket.listen(1)  # 하나의 클라이언트 연결 대기

# 클라이언트와의 연결 수락
print("Waiting for a connection...")
conn, addr = server_socket.accept()
print(f"Connected to {addr}")

# 송신 소켓 설정
sock_send = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
sock_send.connect(('main.putiez.com', 8088))  # 수신 측 IP와 포트 설정

stream_url = "http://192.168.137.223:8081/"
cap = cv2.VideoCapture(stream_url)

if not cap.isOpened():
    print("Cannot open stream.")
    exit()

frame_width, frame_height = 640, 480
MARGIN = 5

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
            data = conn.recv(1024).decode('utf-8')  # TCP 데이터 수신
            if not data:
                break
            print(f"Received command: {data}")  # 받은 데이터를 출력
            if "intrusion_detection:danger:on" in data:
                detection_enabled = True
                print("Detection enabled")
                GPIO.output(SECURITY_LED_PIN, GPIO.HIGH)
                GPIO.output(SECURITY_OFF_LED_PIN, GPIO.LOW)
            elif "intrusion_detection:danger:off" in data :
                detection_enabled = False
                print("Detection disabled")
                GPIO.output(SECURITY_LED_PIN, GPIO.LOW)
                GPIO.output(MOTION_LED_PIN, GPIO.LOW)
                GPIO.output(SECURITY_OFF_LED_PIN, GPIO.HIGH)
                GPIO.output(BUZZER_PIN, GPIO.LOW)
        except socket.error as e:
            print(f"Socket error: {e}")  # 소켓 오류 출력
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

    mask = np.zeros_like(gray)
    mask[:, frame_width // 2:] = 255

    frame_delta = cv2.absdiff(prev_gray, gray)
    #frame_delta = cv2.bitwise_and(frame_delta, frame_delta, mask=mask)

    motion_mask = frame_delta > 25
    motion_detected = np.sum(motion_mask) > 1000

    if motion_detected:
        GPIO.output(MOTION_LED_PIN, GPIO.HIGH)
        GPIO.output(BUZZER_PIN, GPIO.HIGH)

        if not motion_detected_flag:
            sock_send.send(b"intrusion_detection:danger:1\n")
            motion_detected_flag = True

        y_indices, x_indices = np.where(motion_mask)

        if len(x_indices) > 0 and len(y_indices) > 0:
            x_min, x_max = np.min(x_indices), np.max(x_indices)
            y_min, y_max = np.min(y_indices), np.max(y_indices)
            cv2.rectangle(frame, (x_min, y_min), (x_max, y_max), (0, 255, 0), 2)
            print(f"Motion detected: Bounding box=(({x_min}, {y_min}), ({x_max}, {y_max}))")

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
