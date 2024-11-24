import cv2
import numpy as np

# 스트리밍 URL 설정
stream_url = "http://192.168.0.200:8081/"
cap = cv2.VideoCapture(stream_url)

if not cap.isOpened():
    print("스트림을 열 수 없습니다.")
    exit()

# 해상도를 720p로 설정
frame_width, frame_height = 1280, 720

# 이전 프레임 초기화
ret, prev_frame = cap.read()
if not ret:
    print("초기 프레임을 가져올 수 없습니다.")
    exit()

prev_frame = cv2.resize(prev_frame, (frame_width, frame_height))
prev_gray = cv2.cvtColor(prev_frame, cv2.COLOR_BGR2GRAY)

while True:
    ret, frame = cap.read()
    if not ret:
        continue

    # 해상도 조정 및 그레이스케일 변환
    frame = cv2.resize(frame, (frame_width, frame_height))
    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)

    # Farneback Optical Flow를 사용하여 움직임 계산
    flow = cv2.calcOpticalFlowFarneback(prev_gray, gray, None, 0.5, 3, 15, 3, 5, 1.2, 0)
    mag, ang = cv2.cartToPolar(flow[..., 0], flow[..., 1])

    # 움직임이 큰 영역만 마스킹 (임계값 설정)
    mask = np.zeros_like(frame)
    mask[mag > 2.5] = [0, 255, 0]  # 움직임이 큰 부분을 초록색으로 표시

    # 결과를 화면에 표시
    output = cv2.addWeighted(frame, 1, mask, 0.5, 0)
    cv2.imshow('Optical Flow Motion Detection', output)

    # 이전 프레임 업데이트
    prev_gray = gray

    # 'q' 키를 누르면 종료
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

# 자원 해제
cap.release()
cv2.destroyAllWindows()
