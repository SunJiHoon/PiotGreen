import cv2
import numpy as np
import time

# 스트리밍 URL 설정
stream_url = "http://192.168.0.200:8081/"
cap = cv2.VideoCapture(stream_url)

if not cap.isOpened():
    print("스트림을 열 수 없습니다.")
    exit()

# 해상도를 480p로 설정
frame_width, frame_height = 640, 480

# 첫 번째 프레임 초기화
ret, prev_frame = cap.read()
if not ret:
    print("초기 프레임을 가져올 수 없습니다.")
    exit()

prev_frame = cv2.resize(prev_frame, (frame_width, frame_height))
prev_gray = cv2.cvtColor(prev_frame, cv2.COLOR_BGR2GRAY)

# 초당 프레임 수 제한 설정
fps_limit = 30  # 최대 프레임 수를 30으로 설정
prev_time = time.time()

while True:
    ret, frame = cap.read()
    if not ret:
        continue

    # 현재 시간 계산 (0.01초 단위로 변경)
    current_time = time.time()
    if (current_time - prev_time) < 1.0 / fps_limit:
        continue

    prev_time = current_time

    # 해상도 조정 및 그레이스케일 변환
    frame = cv2.resize(frame, (frame_width, frame_height))
    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)

    # 가우시안 블러를 사용하여 노이즈 제거
    gray = cv2.GaussianBlur(gray, (5, 5), 0)

    # 프레임 차이를 사용하여 움직임 감지
    frame_delta = cv2.absdiff(prev_gray, gray)
    _, thresh = cv2.threshold(frame_delta, 15, 255, cv2.THRESH_BINARY)  # 민감도 높임

    # 윤곽선 찾기
    contours, _ = cv2.findContours(thresh, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)

    # 가장 큰 윤곽선 찾기
    max_contour = None
    max_area = 0
    for contour in contours:
        area = cv2.contourArea(contour)
        if area > max_area:
            max_area = area
            max_contour = contour

    # 가장 큰 윤곽선에 대해 움직임 감지
    if max_contour is not None and max_area > 500:  # 최소 크기 필터링, 민감도 증가
        (x, y, w, h) = cv2.boundingRect(max_contour)
        print(f"움직임 감지됨: 위치=({x}, {y}), 크기=({w}, {h})")

    # 이전 프레임 업데이트
    prev_gray = gray.copy()

    # 'q' 키를 누르면 종료
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

# 자원 해제
cap.release()
cv2.destroyAllWindows()
