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
#define NUM_SECTION 5

int main(int argc, char **argv)
{
	int sockfd, light;
	struct sockaddr_in server_addr;
	char lightBuf[BUFSIZE], ledBuf[BUFSIZE];
	int ledStatus[NUM_SECTION] = {
		0,
	};

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
		delay(1000);
		memset(lightBuf, 0, BUFSIZE);
		memset(ledBuf, 0, BUFSIZE);

		light = analogRead(BASE + 2);
		sprintf(lightBuf, "LIGHT: %d\n", light);
		sprintf(ledBuf, "LED: [%d, %d, %d]\n", ledStatus[0], ledStatus[1], ledStatus[2]);

		if (send(sockfd, lightBuf, strlen(lightBuf), 0) <= 0)
		{ // MSG_DONTWAIT 제거, strlen(buf) 사용
			perror("send()");
			break;
		}
		if (send(sockfd, ledBuf, strlen(ledBuf), 0) <= 0)
		{ // MSG_DONTWAIT 제거, strlen(buf) 사용
			perror("send()");
			break;
		}
	}

	/* 소켓을 닫음 */
	close(sockfd);

	return 0;
}
