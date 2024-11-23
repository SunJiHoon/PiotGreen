import cv2
from ultralytics import YOLO
import threading
import time

# YOLOv8 모델 로드 (경량화된 YOLOv8s 모델 사용)
model = YOLO('yolov8n.pt')  # 경량화된 YOLOv8 Nano 모델 사용

# 스트리밍 URL 설정
stream_url = "http://192.168.0.200:8081/"
cap = cv2.VideoCapture(stream_url)

if not cap.isOpened():
    print("스트림을 열 수 없습니다.")
    exit()

# 전역 프레임 변수
global_frame = None
lock = threading.Lock()

# 프레임 캡처 함수 (스레드로 실행)
def capture_frames():
    global global_frame
    while True:
        ret, frame = cap.read()
        if not ret:
            continue

        # 프레임 크기 축소 (해상도 줄이기)
        resized_frame = cv2.resize(frame, (320, 240))

        with lock:
            global_frame = resized_frame

# 프레임 캡처 스레드 시작
capture_thread = threading.Thread(target=capture_frames)
capture_thread.daemon = True
capture_thread.start()

# YOLO 탐지 설정
frame_skip = 1  # 매 프레임 처리 (프레임 스킵 없음)
frame_count = 0

while True:
    with lock:
        if global_frame is None:
            time.sleep(0.01)
            continue
        frame = global_frame.copy()

    frame_count += 1
    if frame_count % frame_skip != 0:
        continue

    # YOLO 모델로 객체 탐지 수행 (scale 설정으로 빠르게)
    start_time = time.time()
    results = model(frame, stream=False, conf=0.3, iou=0.3)  # 낮은 confidence와 IoU threshold 설정으로 속도 증가

    # 결과를 프레임에 그려서 출력
    annotated_frame = results[0].plot()

    # 결과를 화면에 표시
    cv2.imshow('YOLOv8 Object Detection', annotated_frame)

    # 'q' 키를 누르면 종료
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

    end_time = time.time()
    fps = 1 / (end_time - start_time)
    print(f"FPS: {fps:.2f}")

# 자원 해제
cap.release()
cv2.destroyAllWindows()
