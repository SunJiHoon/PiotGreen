디렉터리: tcp_socket
담당자: 선지훈

이 디렉터리는 TCP 소켓 통신을 사용하여 클라이언트와 서버 간의 통신을 구현하는 코드 파일을 포함하고 있습니다.
## 파일 설명

- **tcpclient.c**: TCP 클라이언트 코드로, 사용자가 서버에 메시지를 전송하고 서버의 응답을 받는 역할을 합니다.

- **tcpserver.c**: TCP 서버 코드로, Raspberry Pi에서 클라이언트의 연결을 수락하고 메시지를 처리하는 역할을 합니다. 

이 TCP 소켓 기반의 통신 시스템은 PiotGreen 프로젝트의 제어 인터페이스로 작동하며, 원격으로 장치를 제어하고 모니터링하는 데 중요한 역할을 합니다.

## 각 파일 build 방법
- gcc -o server tcpserver.c -lpthread -lwiringPi
- gcc -o client tcpclient.c

## 각 실행 파일 실행법
- ./server
- ./client serverip
