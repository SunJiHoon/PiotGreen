import cv2
import numpy as np
import time
import socket

# 소켓 통신 설정
sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
sock.bind(('0.0.0.0', 9999))  # UDP 소켓 설정 및 포트 바인딩

# 스트리밍 URL 설정
stream_url = "http://192.168.0.200:8081/"
cap = cv2.VideoCapture(stream_url)

if not cap.isOpened():
    print("Cannot open stream.")
    exit()

# 해상도를 480p로 설정
frame_width, frame_height = 640, 480

# 첫 번째 프레임 초기화
ret, prev_frame = cap.read()
if not ret:
    print("Cannot get the initial frame.")
    exit()

prev_frame = cv2.resize(prev_frame, (frame_width, frame_height))
prev_gray = cv2.cvtColor(prev_frame, cv2.COLOR_BGR2GRAY)

# 처리 속도를 높이기 위해 프레임 스킵 설정
frame_skip = 2  # 매 2번째 프레임만 처리하여 CPU 사용 감소
frame_count = 0

# 감지 활성화 플래그 초기화
detection_enabled = False

while True:
    # 소켓을 통해 메시지 수신 (논블로킹 방식)
    try:
        data, addr = sock.recvfrom(1024)  # 최대 1024 바이트 수신
        try:
            message = data.decode('utf-8')
        except UnicodeDecodeError:
            continue
        if message == "intrusion_detection:danger:on":
            detection_enabled = True
        elif message == "intrusion_detection:danger:off":
            detection_enabled = False
    except socket.error:
        pass  # 수신할 데이터가 없을 경우 패스

    # 프레임 읽기
    ret, frame = cap.read()
    if not ret:
        continue

    frame_count += 1
    if frame_count % frame_skip != 0:
        continue

    # 감지 기능이 비활성화된 경우 프레임을 계속 읽기만 함
    if not detection_enabled:
        continue

    # 해상도 조정 및 그레이스케일 변환
    frame = cv2.resize(frame, (frame_width, frame_height))
    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)

    # 가우시안 블러를 사용하여 노이즈 제거
    gray = cv2.GaussianBlur(gray, (5, 5), 0)

    # 프레임 차이를 사용하여 움직임 감지
    frame_delta = cv2.absdiff(prev_gray, gray)
    motion_mask = frame_delta > 25  # 움직임 감지 민감도 높임
    motion_detected = np.sum(motion_mask) > 1000  # 최소 움직임 임계값 증가

    if motion_detected:
        # 움직임이 감지된 영역의 좌표 계산
        y_indices, x_indices = np.where(motion_mask)
        if len(x_indices) > 0 and len(y_indices) > 0:
            x_min, x_max = np.min(x_indices), np.max(x_indices)
            y_min, y_max = np.min(y_indices), np.max(y_indices)
            print(f"Motion detected: Bounding box=(({x_min}, {y_min}), ({x_max}, {y_max}))", flush=True)

    # 이전 프레임 업데이트
    prev_gray = gray.copy()

    # 'q' 키를 누르면 종료
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

# 자원 해제
cap.release()
cv2.destroyAllWindows()

def send_command(command):
    sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    server_address = ('192.168.0.200', 9999)  # 수신 측 IP와 포트 설정
    sock.sendto(command.encode('utf-8'), server_address)
    sock.close()

