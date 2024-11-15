import cv2
import socket
import struct

# 소켓 서버 설정
server_address = ('192.168.0.200', 8081)  # 송신할 대상의 IP 주소와 포트

client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
client_socket.connect(server_address)

connection = client_socket.makefile('wb')

try:
    # 웹캠을 통해 영상 캡처 시작
    capture = cv2.VideoCapture(0)  # 0번 카메라 (기본 웹캠)

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
