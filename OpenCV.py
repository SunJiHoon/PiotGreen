import cv2
import time

# 스트리밍 URL 설정
stream_url = "http://192.168.0.200:8081/"
cap = cv2.VideoCapture(stream_url)

if not cap.isOpened():
    print("스트림을 열 수 없습니다.")
    exit()

# 첫 번째 프레임 저장 (고정 배경 프레임으로 사용)
ret, frame1 = cap.read()
if not ret:
    print("첫 번째 프레임을 가져올 수 없습니다.")
    exit()

# 해상도 줄이기 (속도 향상을 위해)
frame1 = cv2.resize(frame1, (320, 240))  # 해상도 키우기
gray1 = cv2.cvtColor(frame1, cv2.COLOR_BGR2GRAY)

# 프레임 스킵 설정
frame_skip = 30  # 1초에 1프레임만 처리 (30fps 기준)
frame_count = 0

while True:
    ret, frame2 = cap.read()
    if not ret:
        continue

    # 해상도 줄이기
    frame2 = cv2.resize(frame2, (320, 240))
    gray2 = cv2.cvtColor(frame2, cv2.COLOR_BGR2GRAY)

    frame_count += 1
    if frame_count % frame_skip != 0:
        continue

    # 고정된 배경 프레임과 현재 프레임 비교
    frame_delta = cv2.absdiff(gray1, gray2)
    thresh = cv2.threshold(frame_delta, 25, 255, cv2.THRESH_BINARY)[1]

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

    # 가장 큰 윤곽선에 대해 사각형 그리기
    if max_contour is not None and max_area > 1000:  # 최소 크기 필터링
        (x, y, w, h) = cv2.boundingRect(max_contour)
        cv2.rectangle(gray2, (x, y), (x + w, y + h), (0, 255, 0), 2)

    # 결과를 화면에 표시
    cv2.imshow('Largest Motion Detection', gray2)

    # 'q' 키를 누르면 종료
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

# 자원 해제
cap.release()
cv2.destroyAllWindows()
