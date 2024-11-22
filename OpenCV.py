from flask import Flask, Response
import cv2
import numpy as np
import threading

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

# 전역 프레임 변수
global_frame = None

# 프레임 캡처 함수
def capture_frames():
    global global_frame
    while True:
        ret, frame = cap.read()
        if not ret:
            continue

        # 프레임 해상도 줄이기 (320x240)
        frame = cv2.resize(frame, (320, 240))
        global_frame = frame

# 프레임 캡처 스레드 시작
capture_thread = threading.Thread(target=capture_frames)
capture_thread.daemon = True
capture_thread.start()

# Flask 애플리케이션 생성
app = Flask(__name__)

def generate_frames():
    global global_frame
    frame_skip = 3  # 프레임 간격 설정 (매 3번째 프레임만 처리)
    frame_count = 0

    while True:
        if global_frame is None:
            continue

        frame_count += 1
        if frame_count % frame_skip != 0:
            continue

        # 현재 프레임 가져오기
        frame = global_frame.copy()

        # 그레이스케일로 변환 (Haar Cascade는 흑백 이미지에서 인식 수행)
        gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)

        # 객체 인식 수행
        objects = object_cascade.detectMultiScale(
            gray,
            scaleFactor=1.3,  # 원본보다 더 큰 비율로 이미지 피라미드를 감소시킴 (속도 향상)
            minNeighbors=3,   # 낮추면 속도 향상, 그러나 false positive가 증가할 수 있음
            minSize=(30, 30)
        )

        # 인식된 객체에 대해 초록색 박스를 그림
        for (x, y, w, h) in objects:
            cv2.rectangle(frame, (x, y), (x + w, y + h), (0, 255, 0), 2)

        # 프레임을 JPEG로 인코딩
        ret, buffer = cv2.imencode('.jpg', frame)
        frame = buffer.tobytes()

        # 스트리밍 데이터 생성
        yield (b'--frame\r\n'
               b'Content-Type: image/jpeg\r\n\r\n' + frame + b'\r\n')

@app.route('/video_feed')
def video_feed():
    return Response(generate_frames(), mimetype='multipart/x-mixed-replace; boundary=frame')

if __name__ == "__main__":
    app.run(host='0.0.0.0', port=5000, threaded=True)
