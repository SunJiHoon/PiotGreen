import cv2
from ultralytics import YOLO

# YOLOv8 모델 로드 (pretrained)
model = YOLO('yolov8s.pt')  # 경량화된 YOLOv8s 모델 사용

# 스트리밍 URL 설정
stream_url = "http://192.168.0.200:8081/"
cap = cv2.VideoCapture(stream_url)

if not cap.isOpened():
    print("스트림을 열 수 없습니다.")
    exit()

while True:
    ret, frame = cap.read()
    if not ret:
        continue

    # YOLOv8 모델로 객체 탐지 수행
    results = model(frame, stream=False)  # 각 프레임마다 탐지 수행

    # 결과를 프레임에 그려서 출력
    annotated_frame = results[0].plot()  # 인식 결과를 프레임에 그리기

    # 결과를 화면에 표시
    cv2.imshow('YOLOv8 Object Detection', annotated_frame)

    # 'q' 키를 누르면 종료
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

# 자원 해제
cap.release()
cv2.destroyAllWindows()
