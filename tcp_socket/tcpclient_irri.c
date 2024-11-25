#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>
#include <unistd.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <wiringPi.h>
#include <wiringPiSPI.h>

#define TCP_PORT 8090
#define BUFSIZE 1024

#define SPI_CHANNEL 0
#define SPI_SPEED 500000

#define pump 4

// ADC 값을 읽는 함수
int read_adc(int channel) {
    if (channel < 0 || channel > 7) return -1;

    unsigned char buf[3];
    buf[0] = 1;               // Start bit
    buf[1] = (8 + channel) << 4; // SGL/DIFF = 1, D2, D1, D0 (채널 선택)
    buf[2] = 0;               // 채워넣기

    int result = wiringPiSPIDataRW(channel, buf, 3);
    if (result == -1) {
        printf("SPI 통신 오류\n");
        return -1;
    }

    int adc_value = ((buf[1] & 3) << 8) + buf[2]; // 상위 2비트 + 하위 8비트
    return adc_value;
}

// ADC 값을 읽어와 출력값을 설정하는 함수
void return_value(int* val, float* percent) {
    int adc_value = read_adc(0); // 채널 0에서 읽음
    if (adc_value == -1) {
        printf("ADC 읽기 오류\n");
        *val = -1;
        *percent = 0.0f;
        return;
    }

    double percentage = (1023 - adc_value) * 100 / 1023;
    *val = adc_value;
    *percent = percentage;
}

int main(int argc, char** argv) {
    int soil_value;
    float percent;

    int sockfd;
    struct sockaddr_in server_addr;
    char buf[BUFSIZE];

    // WiringPi 초기화
    if (wiringPiSetup() == -1) {
        printf("WiringPi 초기화 실패\n");
        return -1;
    }

    pinMode(pump, OUTPUT);
    digitalWrite(pump, HIGH);

    if (wiringPiSPISetup(SPI_CHANNEL, SPI_SPEED) == -1) {
        printf("SPI 설정 실패\n");
        return -1;
    }

    // IP 주소를 인자로 받지 않은 경우
    if (argc < 2) {
        printf("Usage: %s IP_ADDRESS\n", argv[0]);
        return -1;
    }

    // 소켓 생성
    if ((sockfd = socket(AF_INET, SOCK_STREAM, 0)) < 0) {
        perror("socket()");
        return -1;
    }

    // 접속 주소 설정
    memset(&server_addr, 0, sizeof(server_addr));
    server_addr.sin_family = AF_INET;

    // 문자열을 네트워크 주소로 변환
    inet_pton(AF_INET, argv[1], &(server_addr.sin_addr.s_addr));
    server_addr.sin_port = htons(TCP_PORT);

    // 서버에 연결
    if (connect(sockfd, (struct sockaddr*)&server_addr, sizeof(server_addr)) < 0) {
        perror("connect()");
        close(sockfd);
        return -1;
    }

    while (1) {
        // ADC 값 읽기
        return_value(&soil_value, &percent);
        if (soil_value == -1) {
            printf("센서 값 읽기 실패\n");
            break;
        }

        // 전송할 문자열 준비
	printf("토양 수분 값:%d\n", soil_value);
        char str_val[50]; // 동적 메모리 대신 정적 크기의 배열 사용
        snprintf(str_val, sizeof(str_val), "irrigation_system:moisture:%.1f%\n", percent);

	if(percent < 30){
		digitalWrite(pump, LOW);
	} else{
		digitalWrite(pump, HIGH);
	}

        // 문자열을 소켓으로 전송
        if (send(sockfd, str_val, strlen(str_val), 0) <= 0) {
            perror("send()");
            break;
        }

        // 서버로부터 응답 읽기
        ssize_t received = recv(sockfd, buf, BUFSIZE - 1, 0);
        if (received <= 0) {
            if (received == 0) {
                printf("서버가 연결을 종료했습니다.\n");
            }
            else {
                perror("recv()");
            }
            break;
        }
        buf[received] = '\0'; // 널 문자로 문자열 종료
        printf("서버에서 받은 메시지: %s\n", buf);

        // 'q' 메시지로 종료
        if (strncmp(buf, "q", 1) == 0) {
            break;
        }

        delay(1000); // 1초 대기
    }

    // 소켓 닫기
    close(sockfd);
    return 0;
}
