import cv2
import numpy as np
import time
import socket
import threading
import RPi.GPIO as GPIO

# GPIO 설정
GPIO.setmode(GPIO.BCM)
GPIO.setwarnings(False)

# 사용할 GPIO 핀 번호 설정
SECURITY_LED_PIN = 23  # 보안 모드 활성화 LED
MOTION_LED_PIN = 24    # 움직임 감지 LED
SECURITY_OFF_LED_PIN = 25  # 보안 모드 비활성화 LED
BUZZER_PIN = 8         # 움직임 감지 시 부저

# GPIO 핀 출력 모드로 설정
GPIO.setup(SECURITY_LED_PIN, GPIO.OUT)
GPIO.setup(MOTION_LED_PIN, GPIO.OUT)
GPIO.setup(SECURITY_OFF_LED_PIN, GPIO.OUT)
GPIO.setup(BUZZER_PIN, GPIO.OUT)

# 초기 상태 설정 (모두 꺼짐)
GPIO.output(SECURITY_LED_PIN, GPIO.LOW)
GPIO.output(MOTION_LED_PIN, GPIO.LOW)
GPIO.output(SECURITY_OFF_LED_PIN, GPIO.LOW)
GPIO.output(BUZZER_PIN, GPIO.LOW)

# 소켓 통신 설정
sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
sock.bind(('0.0.0.0', 9999))  # UDP 소켓 설정 및 포트 바인딩

# 스트리밍 URL 설정
stream_url = "http://192.168.0.200:8081/"
cap = cv2.VideoCapture(stream_url)

# 스트리밍 열기 확인
if not cap.isOpened():
    print("Cannot open stream.")
    exit()

# 해상도를 480p로 설정
frame_width, frame_height = 640, 480

# 첫 번째 프레임 초기화
ret, prev_frame = cap.read()
if not ret:
    print("Cannot get the initial frame.")
    exit()

prev_frame = cv2.resize(prev_frame, (frame_width, frame_height))
prev_gray = cv2.cvtColor(prev_frame, cv2.COLOR_BGR2GRAY)

# 처리 속도를 높이기 위해 프레임 스킵 설정
frame_skip = 2  # 매 2번째 프레임만 처리하여 CPU 사용 감소
frame_count = 0

# 감지 활성화 플래그 초기화
detection_enabled = False

# 소켓 수신 스레드 함수
def receive_commands():
    global detection_enabled
    while True:
        try:
            data, addr = sock.recvfrom(1024)  # 최대 1024 바이트 수신
            try:
                message = data.decode('utf-8')
            except UnicodeDecodeError:
                continue
            
            if message == "intrusion_detection:danger:on":
                detection_enabled = True
                print("Detection enabled")
                # 보안 모드 활성화: GPIO 23 켜고, 나머지 끔
                GPIO.output(SECURITY_LED_PIN, GPIO.HIGH)
                GPIO.output(SECURITY_OFF_LED_PIN, GPIO.LOW)
            elif message == "intrusion_detection:danger:off":
                detection_enabled = False
                print("Detection disabled")
                # 보안 모드 비활성화: GPIO 25 켜고, 나머지 끔
                GPIO.output(SECURITY_LED_PIN, GPIO.LOW)
                GPIO.output(MOTION_LED_PIN, GPIO.LOW)
                GPIO.output(SECURITY_OFF_LED_PIN, GPIO.HIGH)
                GPIO.output(BUZZER_PIN, GPIO.LOW)
        except socket.error:
            pass  # 수신할 데이터가 없을 경우 패스

# 소켓 수신 스레드를 시작
receive_thread = threading.Thread(target=receive_commands, daemon=True)
receive_thread.start()

while True:
    # 프레임 읽기
    ret, frame = cap.read()
    if not ret:
        continue

    frame_count += 1
    if frame_count % frame_skip != 0:
        continue

    # 감지 기능이 비활성화된 경우 프레임을 계속 읽기만 함
    if not detection_enabled:
        continue

    # 해상도 조정 및 그레이스케일 변환
    frame = cv2.resize(frame, (frame_width, frame_height))
    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)

    # 가우시안 블러를 사용하여 노이즈 제거
    gray = cv2.GaussianBlur(gray, (5, 5), 0)

    # 프레임 차이를 사용하여 움직임 감지
    frame_delta = cv2.absdiff(prev_gray, gray)
    motion_mask = frame_delta > 25  # 움직임 감지 민감도 높임
    motion_detected = np.sum(motion_mask) > 1000  # 최소 움직임 임계값 증가

    if motion_detected:
        # 움직임이 감지된 경우 GPIO 24번 LED와 부저 켜기
        GPIO.output(MOTION_LED_PIN, GPIO.HIGH)
        GPIO.output(BUZZER_PIN, GPIO.HIGH)

        # 움직임이 감지된 영역의 좌표 계산
        y_indices, x_indices = np.where(motion_mask)

        # 마진 내에 있는 움직임만 허용
        if len(x_indices) > 0 and len(y_indices) > 0:
            x_min, x_max = np.min(x_indices), np.max(x_indices)
            y_min, y_max = np.min(y_indices), np.max(y_indices)

            if (x_min > MARGIN and x_max < frame_width - MARGIN and
                y_min > MARGIN and y_max < frame_height - MARGIN):
                print(f"Motion detected: Bounding box=(({x_min}, {y_min}), ({x_max}, {y_max}))", flush=True)
    else:
        # 움직임이 감지되지 않으면 GPIO 24번 LED와 부저 끄기
        GPIO.output(MOTION_LED_PIN, GPIO.LOW)
        GPIO.output(BUZZER_PIN, GPIO.LOW)

    # 이전 프레임 업데이트
    prev_gray = gray.copy()

    # 'q' 키를 누르면 종료
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

# 자원 해제
cap.release()
cv2.destroyAllWindows()
GPIO.cleanup()  # GPIO 자원 해제
