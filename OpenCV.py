import cv2
import time
import numpy as np

# 스트리밍 URL 설정
stream_url = "http://192.168.0.200:8081/"
cap = cv2.VideoCapture(stream_url)

if not cap.isOpened():
    print("스트림을 열 수 없습니다.")
    exit()

# 해상도를 720p로 설정
frame_width, frame_height = 1280, 720

# 배경 제거기 설정 (BackgroundSubtractorKNN 사용)
fgbg = cv2.createBackgroundSubtractorKNN(history=400, dist2Threshold=1000, detectShadows=False)

while True:
    ret, frame = cap.read()
    if not ret:
        continue

    # 해상도 조정 및 그레이스케일 변환
    frame = cv2.resize(frame, (frame_width, frame_height))
    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)

    # 배경 제거를 통해 움직임 감지
    fgmask = fgbg.apply(gray)

    # 노이즈 제거 (모폴로지 연산 사용)
    fgmask = cv2.morphologyEx(fgmask, cv2.MORPH_OPEN, np.ones((5, 5), np.uint8))

    # 윤곽선 찾기
    contours, _ = cv2.findContours(fgmask, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)

    # 가장 큰 윤곽선 찾기
    max_contour = None
    max_area = 0
    for contour in contours:
        area = cv2.contourArea(contour)
        if area > max_area:
            max_area = area
            max_contour = contour

    # 가장 큰 윤곽선에 대해 사각형 그리기
    if max_contour is not None and max_area > 1000:  # 최소 크기 필터링
        (x, y, w, h) = cv2.boundingRect(max_contour)
        cv2.rectangle(frame, (x, y), (x + w, y + h), (0, 255, 0), 2)

    # 결과를 화면에 표시
    cv2.imshow('Largest Motion Detection', frame)

    # 'q' 키를 누르면 종료
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

# 자원 해제
cap.release()
cv2.destroyAllWindows()
