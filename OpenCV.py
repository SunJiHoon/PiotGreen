import cv2
import numpy as np
from ultralytics import YOLO

# YOLO 경량화된 모델 로드 (YOLOv5n)
model = YOLO('yolov5n.pt')

# 스트리밍 URL 설정
stream_url = "http://192.168.0.200:8081/"
cap = cv2.VideoCapture(stream_url)

if not cap.isOpened():
    print("스트림을 열 수 없습니다.")
    exit()

# 해상도를 480p로 설정
frame_width, frame_height = 640, 480

# 초당 프레임 수 제한 설정
fps_limit = 30
prev_time = 0

while True:
    ret, frame = cap.read()
    if not ret:
        continue

    # 현재 시간 계산
    current_time = cv2.getTickCount() / cv2.getTickFrequency()
    if (current_time - prev_time) < 1.0 / fps_limit:
        continue

    prev_time = current_time

    # 해상도 조정
    frame = cv2.resize(frame, (frame_width, frame_height))

    # YOLO 경량 모델을 사용하여 객체 감지
    results = model(frame, stream=False)

    # 가장 큰 객체에 대한 바운딩 박스 그리기
    max_area = 0
    max_box = None
    for result in results:
        for box in result.boxes:
            x1, y1, x2, y2 = map(int, box.xyxy[0])
            area = (x2 - x1) * (y2 - y1)
            if area > max_area:
                max_area = area
                max_box = (x1, y1, x2, y2)

    # 가장 큰 바운딩 박스를 그리기
    if max_box is not None and max_area > 8000:  # 최소 크기 필터링
        x1, y1, x2, y2 = max_box
        cv2.rectangle(frame, (x1, y1), (x2, y2), (0, 255, 0), 2)

    # 결과를 화면에 표시
    cv2.imshow('Largest Object Detection', frame)

    # 'q' 키를 누르면 종료
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

# 자원 해제
cap.release()
cv2.destroyAllWindows()
