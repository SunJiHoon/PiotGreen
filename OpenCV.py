import cv2
import numpy as np
import time
import torch
import torchvision.transforms as transforms
from torchvision import models
from torchvision.models.detection import fasterrcnn_resnet50_fpn

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

# 초당 프레임 수 제한 설정
fps_limit = 30  # 최대 프레임 수를 30으로 설정
prev_time = time.time()

# Pre-trained Faster R-CNN 모델 로드 (객체 탐지 모델 사용)
model = fasterrcnn_resnet50_fpn(weights='DEFAULT').eval()
device = torch.device('cuda' if torch.cuda.is_available() else 'cpu')
model.to(device)

# COCO 데이터셋 클래스 레이블
coco_labels = {
    1: 'person', 2: 'bicycle', 3: 'car', 4: 'motorcycle', 5: 'airplane', 6: 'bus',
    7: 'train', 8: 'truck', 9: 'boat', 10: 'traffic light', 11: 'fire hydrant',
    13: 'stop sign', 14: 'parking meter', 15: 'bench', 16: 'bird', 17: 'cat',
    18: 'dog', 19: 'horse', 20: 'sheep', 21: 'cow', 22: 'elephant', 23: 'bear',
    24: 'zebra', 25: 'giraffe', 27: 'backpack', 28: 'umbrella', 31: 'handbag',
    32: 'tie', 33: 'suitcase', 34: 'frisbee', 35: 'skis', 36: 'snowboard',
    37: 'sports ball', 38: 'kite', 39: 'baseball bat', 40: 'baseball glove',
    41: 'skateboard', 42: 'surfboard', 43: 'tennis racket', 44: 'bottle',
    46: 'wine glass', 47: 'cup', 48: 'fork', 49: 'knife', 50: 'spoon',
    51: 'bowl', 52: 'banana', 53: 'apple', 54: 'sandwich', 55: 'orange',
    56: 'broccoli', 57: 'carrot', 58: 'hot dog', 59: 'pizza', 60: 'donut',
    61: 'cake', 62: 'chair', 63: 'couch', 64: 'potted plant', 65: 'bed',
    67: 'dining table', 70: 'toilet', 72: 'tv', 73: 'laptop', 74: 'mouse',
    75: 'remote', 76: 'keyboard', 77: 'cell phone', 78: 'microwave',
    79: 'oven', 80: 'toaster', 81: 'sink', 82: 'refrigerator', 84: 'book',
    85: 'clock', 86: 'vase', 87: 'scissors', 88: 'teddy bear', 89: 'hair drier',
    90: 'toothbrush'
}

# 이미지 전처리 설정
transform = transforms.Compose([
    transforms.ToPILImage(),
    transforms.Resize((480, 480)),
    transforms.ToTensor(),
    transforms.Normalize(mean=[0.485, 0.456, 0.406], std=[0.229, 0.224, 0.225]),
])

while True:
    ret, frame = cap.read()
    if not ret:
        continue

    # 현재 시간 계산 (0.05초 단위로 변경)
    current_time = time.time()
    if (current_time - prev_time) < 0.05:
        continue

    prev_time = current_time

    # 해상도 조정 및 그레이스케일 변환
    frame = cv2.resize(frame, (frame_width, frame_height))
    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)

    # 가우시안 블러를 사용하여 노이즈 제거
    gray = cv2.GaussianBlur(gray, (5, 5), 0)

    # 프레임 차이를 사용하여 움직임 감지
    frame_delta = cv2.absdiff(prev_gray, gray)
    _, thresh = cv2.threshold(frame_delta, 10, 255, cv2.THRESH_BINARY)  # 민감도 높임

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

    # 가장 큰 윤곽선에 대해 움직임 감지 및 물체 인식
    if max_contour is not None and max_area > 500:  # 최소 크기 필터링, 민감도 증가
        (x, y, w, h) = cv2.boundingRect(max_contour)
        print(f"Motion detected: Position=({x}, {y}), Size=({w}, {h})")

        # 물체 인식
        cropped_frame = frame[y:y + h, x:x + w]
        input_tensor = transform(cropped_frame).unsqueeze(0).to(device)
        with torch.no_grad():
            outputs = model(input_tensor)
            labels = outputs[0]['labels'].cpu().numpy()
            scores = outputs[0]['scores'].cpu().numpy()
            for label, score in zip(labels, scores):
                if score > 0.5:  # 신뢰도 50% 이상만 출력
                    label_name = coco_labels.get(label, "Unknown")
                    print(f"Detected object: {label_name}, Confidence={score:.2f}")

    # 이전 프레임 업데이트
    prev_gray = gray.copy()

    # 'q' 키를 누르면 종료
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

# 자원 해제
cap.release()
cv2.destroyAllWindows()
