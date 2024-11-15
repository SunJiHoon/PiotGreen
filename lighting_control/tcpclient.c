#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <stdlib.h>
#include <wiringPi.h>
#include <mcp3004.h>
#include <wiringPiSPI.h>

#define TCP_PORT 8091
#define BUFSIZE 1024

#define BASE 100
#define SPI_CHAN 0

int main(int argc, char **argv)
{
	int sockfd, light;
	struct sockaddr_in server_addr;
	char buf[BUFSIZE];

	if (argc < 2)
	{
		printf("usage: %s IP_ADDRESS\n", argv[0]);
		return -1;
	}

	/* 소켓 생성 */
	if ((sockfd = socket(AF_INET, SOCK_STREAM, 0)) < 0)
	{
		perror("socket()");
		return -1;
	}

	/* 접속 주소 지정 */
	memset(&server_addr, 0, sizeof(server_addr));
	server_addr.sin_family = AF_INET;

	/* 문자열을 네트워크 주소로 변경 */
	inet_pton(AF_INET, argv[1], &(server_addr.sin_addr.s_addr));
	server_addr.sin_port = htons(TCP_PORT);

	/* 지정된 네트워크 주소로 접속 */
	if (connect(sockfd, (struct sockaddr *)&server_addr, sizeof(server_addr)) < 0)
	{
		perror("connect()");
		return -1;
	}

	int i;
	printf("wiringPiSPISetup return=%d\n", wiringPiSPISetup(0, 500000));
	mcp3004Setup(BASE, SPI_CHAN);

	while (1)
	{
		memset(buf, 0, BUFSIZE);
		printf("서버로 보낼 메시지: ");
		// fgets(buf, BUFSIZE, stdin);
		light = analogRead(BASE + 2);
		sprintf(buf, "LIGHT: %d", light);
		printf("%s\n", buf);
		delay(500);

		if (send(sockfd, buf, strlen(buf), 0) <= 0)
		{ // MSG_DONTWAIT 제거, strlen(buf) 사용
			perror("send()");
			break;
		}

		// 서버로부터 응답 데이터를 읽어서 출력
		ssize_t received = recv(sockfd, buf, BUFSIZE, 0);
		if (received <= 0)
		{
			if (received == 0)
			{
				printf("서버가 연결을 종료했습니다.\n");
			}
			else
			{
				perror("recv()");
			}
			break;
		}
		buf[received] = '\0'; // 받은 데이터의 끝에 NULL 문자 추가
		printf("서버에서 받은 메시지: %s", buf);

		// 'q'로 연결 종료
		if (strncmp(buf, "q", 1) == 0)
		{
			break;
		}
	}

	/* 소켓을 닫음 */
	close(sockfd);

	return 0;
}
