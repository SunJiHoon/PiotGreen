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
fps_limit = 30
prev_time = time.time()

# 이동 경로를 그리기 위한 초기 설정
tracking_path = np.zeros((frame_height, frame_width, 3), dtype=np.uint8)

while True:
    ret, frame = cap.read()
    if not ret:
        continue

    # 현재 시간 계산
    current_time = time.time()
    if (current_time - prev_time) < 1.0 / fps_limit:
        continue

    prev_time = current_time

    # 해상도 조정 및 그레이스케일 변환
    frame = cv2.resize(frame, (frame_width, frame_height))
    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)

    # 프레임 차이를 사용하여 움직임 감지
    frame_delta = cv2.absdiff(prev_gray, gray)
    _, thresh = cv2.threshold(frame_delta, 50, 255, cv2.THRESH_BINARY)

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

    # 가장 큰 윤곽선에 대해 사각형 그리기 및 이동 경로 표시
    if max_contour is not None and max_area > 5000:  # 최소 크기 필터링
        (x, y, w, h) = cv2.boundingRect(max_contour)
        cx, cy = x + w // 2, y + h // 2  # 중심점 계산
        cv2.rectangle(frame, (x, y), (x + w, y + h), (0, 255, 0), 2)
        cv2.circle(tracking_path, (cx, cy), 2, (0, 0, 255), -1)  # 이동 경로에 점 그리기

    # 이동 경로를 현재 프레임에 합성
    combined_frame = cv2.addWeighted(frame, 0.8, tracking_path, 0.5, 0)

    # 결과를 화면에 표시
    cv2.imshow('Motion Tracking', combined_frame)

    # 이전 프레임 업데이트
    prev_gray = gray.copy()

    # 'q' 키를 누르면 종료
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

# 자원 해제
cap.release()
cv2.destroyAllWindows()
