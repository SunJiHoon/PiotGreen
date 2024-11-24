import cv2
import numpy as np
import time
from ultralytics import YOLO

# 스트리밍 URL 설정
stream_url = "http://192.168.0.200:8081/"
cap = cv2.VideoCapture(stream_url)

if not cap.isOpened():
    print("Cannot open stream.")
    exit()

# 해상도를 480p로 설정
frame_width, frame_height = 640, 480

# YOLOv8 경량화 모델 로드
model = YOLO('yolov8n.pt')  # 'yolov8n.pt'는 경량화된 YOLOv8 모델

while True:
    ret, frame = cap.read()
    if not ret:
        continue

    # 해상도 조정
    frame = cv2.resize(frame, (frame_width, frame_height))

    # YOLO 모델을 사용하여 객체 감지
    results = model(frame)

    # 감지된 객체에 대한 바운딩 박스 그리기
    for result in results:
        boxes = result.boxes
        for box in boxes:
            x1, y1, x2, y2 = map(int, box.xyxy[0])
            confidence = box.conf[0]
            if confidence > 0.5:  # 신뢰도 임계값 설정
                cv2.rectangle(frame, (x1, y1), (x2, y2), (0, 255, 0), 2)
                print(f"Object detected: Bounding box=(({x1}, {y1}), ({x2}, {y2}))", flush=True)

    # 결과를 화면에 표시
    cv2.imshow('YOLO Motion Detection', frame)

    # 'q' 키를 누르면 종료
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

# 자원 해제
cap.release()
cv2.destroyAllWindows()
