import cv2
import numpy as np
import time
import socket
import threading
import RPi.GPIO as GPIO

# GPIO 설정 (생략: 기존 설정 유지)

# 프레임 경계 마진 설정 (예: 경계에서 20픽셀 떨어진 내부만 감지)
MARGIN = 20

# 소켓 수신 스레드 함수 (생략: 기존 설정 유지)

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

# 자원 해제 (생략: 기존 설정 유지)
