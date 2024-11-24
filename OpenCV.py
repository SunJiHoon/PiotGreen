import cv2
import numpy as np
import torch
import torchvision.transforms as transforms
from torchvision import models
import time

# 스트리밍 URL 설정
stream_url = "http://192.168.0.200:8081/"
cap = cv2.VideoCapture(stream_url)

if not cap.isOpened():
    print("스트림을 열 수 없습니다.")
    exit()

# 해상도를 480p로 설정
frame_width, frame_height = 640, 480

# 첫 번째 프레임 초기화
ret, prev_frame = cap.read()
if not ret:
    print("초기 프레임을 가져올 수 없습니다.")
    exit()

prev_frame = cv2.resize(prev_frame, (frame_width, frame_height))
prev_gray = cv2.cvtColor(prev_frame, cv2.COLOR_BGR2GRAY)

# 초당 프레임 수 제한 설정
fps_limit = 30
prev_time = time.time()

# Pre-trained MobileNet 모델 로드 (경량화된 모델 사용)
model = models.mobilenet_v2(pretrained=True).eval()
device = torch.device('cuda' if torch.cuda.is_available() else 'cpu')
model.to(device)

# 이미지 전처리 설정
transform = transforms.Compose([
    transforms.ToPILImage(),
    transforms.Resize((224, 224)),
    transforms.ToTensor(),
    transforms.Normalize(mean=[0.485, 0.456, 0.406], std=[0.229, 0.224, 0.225]),
])

# 이동 경로를 그리기 위한 초기 설정
tracker_initialized = False
bbox = None

while True:
    ret, frame = cap.read()
    if not ret:
        continue

    # 현재 시간 계산 (0.01초 단위)
    current_time = time.time()
    if (current_time - prev_time) < 0.01:
        continue

    prev_time = current_time

    # 해상도 조정 및 그레이스케일 변환
    frame = cv2.resize(frame, (frame_width, frame_height))
    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)

    # 가우시안 블러를 사용하여 노이즈 제거
    gray = cv2.GaussianBlur(gray, (5, 5), 0)

    # 프레임 차이를 사용하여 움직임 감지
    frame_delta = cv2.absdiff(prev_gray, gray)
    _, thresh = cv2.threshold(frame_delta, 20, 255, cv2.THRESH_BINARY)  # 민감도 높임

    # 윤곽선 찾기
    contours, _ = cv2.findContours(thresh, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)

    # 가장 큰 윤곽선 찾기
    max_contour = None
    max_area = 0
    for contour in contours:
        area = cv2.contourArea(contour)
        if area > max_area:
            max_area = area
            max_contour = contour

    # 트래커 초기화 또는 업데이트
    if max_contour is not None and max_area > 1000:  # 최소 크기 필터링, 민감도 증가
        (x, y, w, h) = cv2.boundingRect(max_contour)
        bbox = (x, y, w, h)
        tracker_initialized = True

    # MobileNet을 사용하여 객체 인식
    if tracker_initialized and bbox is not None:
        x, y, w, h = bbox
        cropped_frame = frame[y:y + h, x:x + w]
        input_tensor = transform(cropped_frame).unsqueeze(0).to(device)
        with torch.no_grad():
            output = model(input_tensor)
        # 바운딩 박스 그리기
        cv2.rectangle(frame, (x, y), (x + w, y + h), (0, 255, 0), 2)

    # 결과를 화면에 표시
    cv2.imshow('Motion Tracking', frame)

    # 이전 프레임 업데이트
    prev_gray = gray.copy()

    # 'q' 키를 누르면 종료
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

# 자원 해제
cap.release()
cv2.destroyAllWindows()
