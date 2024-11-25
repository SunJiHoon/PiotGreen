import cv2
import socket
import struct

# 소켓 서버 설정
server_address = ('192.168.137.124', 8081)  # 송신할 대상의 IP 주소와 포트

client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
client_socket.connect(server_address)

connection = client_socket.makefile('wb')

try:
    # motion에서 스트리밍되는 영상 주소 (motion.conf 파일의 stream_localhost 설정을 'off'로 설정해야 외부에서도 접근 가능)
    stream_url = 'http://127.168.137.223:8081'  # motion에서 사용하는 MJPEG 스트림 URL

    # OpenCV로 MJPEG 스트림 받아오기
    capture = cv2.VideoCapture(stream_url)

    if not capture.isOpened():
        print("스트림을 열 수 없습니다.")
        exit()

    while True:
        ret, frame = capture.read()
        if not ret:
            break

        # JPEG로 인코딩
        ret, jpeg = cv2.imencode('.jpg', frame)
        if not ret:
            continue

        # JPEG 데이터를 바이너리 형식으로 변환
        frame_data = jpeg.tobytes()

        # 프레임 크기와 데이터 전송
        frame_size = len(frame_data)
        connection.write(struct.pack('<L', frame_size))  # 프레임 크기 전송 (작은 엔디안 방식으로 길이 전송)
        connection.write(frame_data)  # 실제 프레임 데이터 전송

finally:
    capture.release()
    connection.close()
    client_socket.close()
