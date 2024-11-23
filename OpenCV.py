import cv2
from ultralytics import YOLO
import threading

# YOLOv8 모델 로드 (pretrained)
model = YOLO('yolov8s.pt')

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
        with lock:
            global_frame = frame

# 프레임 캡처 스레드 시작
capture_thread = threading.Thread(target=capture_frames)
capture_thread.daemon = True
capture_thread.start()

while True:
    with lock:
        if global_frame is None:
            continue
        frame = global_frame.copy()

    # YOLO 모델로 객체 탐지 수행
    results = model(frame, stream=False)

    # 결과를 프레임에 그려서 출력
    annotated_frame = results[0].plot()

    # 결과를 화면에 표시
    cv2.imshow('YOLOv8 Object Detection', annotated_frame)

    # 'q' 키를 누르면 종료
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

# 자원 해제
cap.release()
cv2.destroyAllWindows()
