#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <pthread.h>
#include <sys/socket.h>
#include <arpa/inet.h>

#include <wiringPi.h>
#include <wiringPiSPI.h>

#define TCP_PORT 8090	// 서버 포트 번호 
#define BUFSIZE 1024	// 메시지 버퍼 크기 
#define MAX_CLIENT 3   // 동시 클라이언트 수 

#define SPI_CHANNEL 0
#define SPI_SPEED 500000

#define PUMP 4

int read_adc_per(int channel) {
	if (channel < 0 || channel > 7) return -1;

	unsigned char buf[3];
	buf[0] = 1;
	buf[1] - (8 + channel) << 4;
	buf[2] = 0;

	int result = wiringPiSPIDataRW(channel, buf, 3);
	if (result == -1) {
		printf("SPI 통신 오류\n");
		return -1;
	}
	int adc_value = ((buf[1] & 3) << 8) + buf[2];
	int percentage = (1023 - adc_value) * 100 / 1023;
	return percentage;
}


struct client_thread_info {
	pthread_t thread;
	struct sockaddr_in client_addr;	
	int sockfd;
	int is_created;
};


/* 스레드 종료시 구조체 미사용 설정을 위한 콜백 함수 */
void client_thread_cleanup(void *aux)
{
	struct client_thread_info *cti = (struct client_thread_info*) aux;

	/* 스레드 구조체 사용 가능 */
	cti->is_created = 0;
}


/* 클라이언트 스레드 함수 */
void *client_thread_loop(void *aux)
{
	struct client_thread_info *cti = (struct client_thread_info*) aux;
	char fromstr[64];
	char buf[BUFSIZE];

	/* 스레드 종료 핸들러 설정 */
	pthread_cleanup_push(client_thread_cleanup, (void*) aux);
	
	/* 네트워크 주소를 문자열로 변경 */
	inet_ntop(AF_INET, &cti->client_addr.sin_addr, fromstr, 64);
	printf("클라이언트 %s 와 연결되었습니다.\n", fromstr);

        /* 클라이언트 loop 시작 */
        do {
                int n;

				char* token;
				char command[64];
				int value = 0;

                /* 클라이언트가 보낸 데이터를 buf로부터 읽음 */
		memset(buf, 0x00, BUFSIZE);
                if ((n = read(cti->sockfd, buf, BUFSIZE)) <= 0) {
                        perror("client thread read()");
                        break;
                }

				char send_buf[BUFSIZE];
				strcat(send_buf, buf);

                printf("클라이언트 %s 에서 보낸 데이터: %s", fromstr, buf);

				token = strtok(buf, ":");
				if (token != NULL) {
					if (strcmp(token, "MODE") == 0) {
						token = strtok(NULL, ":");
						if (token != NULL && (strcmp(token, "auto") == 0 || (strcmp(token, "pass")))) {
							if (write(cti->sockfd, token, sizeof(token)) <= 0) {
								perror("client thread write()");
								break;
							}
						}
					}
				}
				if (token != NULL && strcmp(token, "irrigation_system") == 0) {
					token = strtok(NULL, ":");
					if (token != NULL) {
						strncpy(command, token, sizeof(command));
						command[sizeof(command) - 1] = '\0';

						token = strtok(NULL, ":");
						if (token != NULL) {
							value = atoi(token);
						}
					}

					if (strcmp(command, "pump") == 0) {
						printf("토양 수분이 %d%이상 될때 까지 워터 펌프 작동\n", value);

						while (read_adc_per(SPI_CHANNEL) < value) {
							digitalWrite(PUMP, LOW);
							delay(3000);
							digitalWrite(PUMP, HIGH);
							delay(3000);
						}
					}
				}

				/* buf에 있는 문자열 전송 */
				if (write(cti->sockfd, send_buf, n) <= 0) {
					perror("client thread write()");
					break;
				}

        } while (strncmp(buf, "q", 1) != 0);

	/* 클라이언트 종료 후 소켓을 닫음 */
	printf("클라이언트 %s와 연결을 종료합니다\n", fromstr);
	close(cti->sockfd);

	pthread_exit((void *) 0);
	pthread_cleanup_pop(0);
}


/* 클라이언트 스레드를 생성하는 함수 */
void start_client_thread(struct client_thread_info *cti)
{
	/* 스레드 구조체 초기화 */
	memset(&cti->thread, 0x00, sizeof (pthread_t));
	if (!pthread_create(&cti->thread, NULL, client_thread_loop, cti)) {
		cti->is_created = 1;
	} else {
		fprintf(stderr, "error creating client thread\n");
	}
}

int main(int argc, char **argv)
{
	struct client_thread_info client_threads[MAX_CLIENT];
	struct sockaddr_in server_addr, client_addr; 	// 소켓 주소 구조체 
	socklen_t client_addr_len; 
	int server_sockfd, client_sockfd;	// 소켓 디스크립터 
	char buf[BUFSIZE];
	int i;

	if (wiringPiSetup() == -1) {
		printf("WiringPi 초기화 실패\n");
		return -1;
	}
	pinMode(PUMP, OUTPUT);
	digitalWrite(PUMP, HIGH);

	if (wiringPiSPISetup(SPI_CHANNEL, SPI_SPEED) == -1) {
		printf("SPI 설정 실패\n");
		return -1;
	}

	/* 스레드 정보 초기화 */
	for (i=0; i<MAX_CLIENT; i++) {
		memset(client_threads, 0x00, sizeof (client_threads));
	}

	/* 서버 소켓 생성 */
	if ((server_sockfd = socket(AF_INET, SOCK_STREAM, 0)) < 0) {
		perror("socket()");
		return -1;
	}

	/* 주소 구조체에 주소 지정 */
	memset(&server_addr, 0x00, sizeof(server_addr));
	server_addr.sin_family = AF_INET; 
	server_addr.sin_addr.s_addr = htonl(INADDR_ANY);
	server_addr.sin_port = htons(TCP_PORT);

	/* bind 함수를 사용하여 서버 소켓의 주소 설정 */
	if (bind(server_sockfd, (struct sockaddr *) &server_addr, sizeof(server_addr)) < 0) {
		perror("bind()");
		return -1;
	}

	/* 클라이언트 연결 대기 listen 시작 */
	if (listen(server_sockfd, MAX_CLIENT) < 0) {
		perror("listen()");
		return -1;
	}
	printf("listening on port %d ...\n", TCP_PORT);

	client_addr_len = sizeof(client_addr);

	/* 서버 loop 시작 */
	while (1) {
		/* 클라이언트가 접속하면 접속을 허용하고 클라이언트 소켓 생성 */
		client_sockfd = accept(server_sockfd, (struct sockaddr *) &client_addr, &client_addr_len);
		if (client_sockfd < 0) {
			perror("accept()");
			return -1;
		}
		
		/* sockfd가 유효하면 클라이언트 스레드 생성 */
		for (i=0; i<MAX_CLIENT; i++) {
			/* 빈 클라이언트 스레드 구조체를 찾아 생성 */
			if (!client_threads[i].is_created) {
				client_threads[i].client_addr = client_addr;
				client_threads[i].sockfd = client_sockfd; 
				start_client_thread(&client_threads[i]);
				break;
			}
		}
		
		if (i >= MAX_CLIENT) {
			fprintf(stderr, "cannot accept more client\n");
		}
	};

	return 0;
}
