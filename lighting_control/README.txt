디렉터리: lighting_control
담당자: 김주영

이 디렉터리는 광량 제어 시스템 코드를 포함하고 있습니다. 조도 센서를 통해 광량을 실시간으로 측정하고, 농작물에 필요한 빛의 양이 부족할 경우 LED 조명을 자동으로 활성화하여 일정한 광량을 유지하도록 설계되었습니다.

주요 역할:
- 실시간 광량 모니터링 및 LED 조명을 통한 빛의 양 조절
- 작물 생장에 필요한 광량을 일정하게 유지하여 생장 환경 최적화
- 필요 시 LED를 자동으로 활성화하여 에너지 효율성을 높임

프로그램 별 기능:
- sensor2db.c
    조도센서를 통해 측정한 광량을 DB에 저장 sensor<->DB
- tcpclient.c
    측정한 광량 값과 led 상태를 메인서버에 송신 DB<->socket
- tcpserver_lighting.c
    메인서버에서 받은 데이터(수동/자동모드, led 상태 전환)를 DB에 저장 socket<->DB
- lighting_control.c
    DB에 저장된 값(수동/자동모드 및 광량)을 통해서 led 제어 DB<->LED

영역은 1번과 2번이 있다고 가정
DB schema(Value, Led1, Led2, Mode):
- Value1: 센서1에서 측정한 광량 값
- Value2: 센서2에서 측정한 광량 값
- Led1: Led1의 상태 (꺼짐: 0, 켜짐: 1)
- Led2: Led2의 상태 (꺼짐: 0, 켜짐: 1)
- Mode: Led 제어 모드 (수동: 0, 자동: 1)

컴파일 명령어:
- gcc -o sensor2db sensor2db.c -lsqlite3 -lwiringPi
- gcc -o tcpclient tcpclient.c -lsqlite3
