import cv2
import numpy as np

# 스트리밍 URL 설정 (Motion의 MJPEG 스트림 URL)
stream_url = "http://192.168.0.200:8081/"

# OpenCV로 MJPEG 스트림 열기
cap = cv2.VideoCapture(stream_url)

if not cap.isOpened():
    print("스트림을 열 수 없습니다.")
    exit()

# 객체 인식을 위한 Haar Cascade 파일 로드 (얼굴 인식용 예제)
cascade_path = cv2.data.haarcascades + "haarcascade_frontalface_default.xml"
object_cascade = cv2.CascadeClassifier(cascade_path)

while True:
    ret, frame = cap.read()
    if not ret:
        print("프레임을 읽을 수 없습니다.")
        break

    # 그레이스케일로 변환 (Haar Cascade는 흑백 이미지에서 인식 수행)
    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)

    # 객체 인식 수행
    objects = object_cascade.detectMultiScale(
        gray,
        scaleFactor=1.1,
        minNeighbors=5,
        minSize=(30, 30)
    )

    # 인식된 객체에 대해 초록색 박스를 그림
    for (x, y, w, h) in objects:
        cv2.rectangle(frame, (x, y), (x + w, y + h), (0, 255, 0), 2)

    # 결과를 화면에 표시
    cv2.imshow("Object Tracking", frame)

    # 'q' 키를 누르면 종료
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

# 자원 해제
cap.release()
cv2.destroyAllWindows()
