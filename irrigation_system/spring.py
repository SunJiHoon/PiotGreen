import requests
from datetime import datetime

current_datetime = datetime.now()
today_date = current_datetime.strftime("%Y%m%d")
current_time = current_datetime.strftime("%H%M")


url = "http://localhost:8080/weather/json"

params = {
        "baseDate": today_date,
        "baseTime": current_time,
        "nx": "55",
        "ny": "127"
        }

try:
    response = requests.get(url, params=params)

    if response.status_code == 200:
        data = response.json()
        print("응답 데이터:")
        for object in data:
            print(object)

    else:
        print("오류 발생:", response.status_code)
        print("응답 내용:", response.text)

except requests.exceptions.RequestException as e:
    print("요청 실패:", e)
