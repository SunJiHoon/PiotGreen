import cv2

# 스트리밍 URL 설정
stream_url = "http://192.168.0.200:8081/"
cap = cv2.VideoCapture(stream_url)

if not cap.isOpened():
    print("스트림을 열 수 없습니다.")
    exit()

# 첫 번째 프레임 저장
ret, frame1 = cap.read()
if not ret:
    print("첫 번째 프레임을 가져올 수 없습니다.")
    exit()

gray1 = cv2.cvtColor(frame1, cv2.COLOR_BGR2GRAY)
gray1 = cv2.GaussianBlur(gray1, (21, 21), 0)

while True:
    # 현재 프레임 가져오기
    ret, frame2 = cap.read()
    if not ret:
        continue

    gray2 = cv2.cvtColor(frame2, cv2.COLOR_BGR2GRAY)
    gray2 = cv2.GaussianBlur(gray2, (21, 21), 0)

    # 프레임 간 차이 계산
    frame_delta = cv2.absdiff(gray1, gray2)
    thresh = cv2.threshold(frame_delta, 25, 255, cv2.THRESH_BINARY)[1]

    # 윤곽선 찾기
    contours, _ = cv2.findContours(thresh.copy(), cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
    for contour in contours:
        if cv2.contourArea(contour) < 500:
            continue
        (x, y, w, h) = cv2.boundingRect(contour)
        cv2.rectangle(frame2, (x, y), (x + w, y + h), (0, 255, 0), 2)

    # 결과를 화면에 표시
    cv2.imshow('Motion Detection', frame2)

    # 'q' 키를 누르면 종료
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

    # 이전 프레임 업데이트
    gray1 = gray2

# 자원 해제
cap.release()
cv2.destroyAllWindows()
