#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <sqlite3.h>

#define TCP_PORT 8088
#define BUFSIZE 1024

int sockfd;

void update_callback(void *arg, int action, const char *db_name, const char *table_name, sqlite3_int64 row_id)
{
	if (action == SQLITE_INSERT)
	{
		// `INSERT`가 실행된 경우, 해당 데이터를 조회하는 SQL 실행
		sqlite3 *db = (sqlite3 *)arg;
		sqlite3_stmt *stmt;
		char sql[256];

		// 삽입된 데이터의 row_id를 이용해 실제 데이터 조회
		snprintf(sql, sizeof(sql), "SELECT * FROM %s WHERE Id = %lld;", table_name, row_id);

		// SQL 실행
		if (sqlite3_prepare_v2(db, sql, -1, &stmt, NULL) == SQLITE_OK)
		{
			if (sqlite3_step(stmt) == SQLITE_ROW)
			{
				// 삽입된 데이터 추출 (예: 첫 번째 컬럼의 값을 가져오는 경우)
				const char *inserted_data = (const char *)sqlite3_column_text(stmt, 0);

				// 데이터를 소켓으로 송신
				if (send(sockfd, inserted_data, strlen(inserted_data), 0) <= 0)
				{ // MSG_DONTWAIT 제거, strlen(buf) 사용
					perror("send()");
				}
			}
			sqlite3_finalize(stmt);
		}
	}
}

int main(int argc, char **argv)
{
	struct sockaddr_in server_addr;
	char buf1[BUFSIZE], buf2[BUFSIZE];
	sqlite3 *db;
	sqlite3_stmt *stmt;

	char *err_msg = 0, user_input;
	int rc = sqlite3_open("light.db", &db);

	if (rc != SQLITE_OK) // 오류처리구문
	{
		fprintf(stderr, "Cannot open database: %s\n", sqlite3_errmsg(db));
		sqlite3_close(db);
		return 1;
	}

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

	// 5초마다 테이블을 반복해서 검색
	const char *sql = "SELECT * FROM LIGHT;";
	while (1)
	{
		sqlite3_stmt *stmt;
		rc = sqlite3_prepare_v2(db, sql, -1, &stmt, 0);
		if (rc != SQLITE_OK)
		{
			fprintf(stderr, "SQL error: %s\n", sqlite3_errmsg(db));
			sqlite3_close(db);
			return 1;
		}

		// 테이블에서 행을 한 개씩 가져와 출력
		while (sqlite3_step(stmt) == SQLITE_ROW)
		{
			int col1 = sqlite3_column_int(stmt, 0);
			int col2 = sqlite3_column_int(stmt, 1);
			int col3 = sqlite3_column_int(stmt, 2);
			int col4 = sqlite3_column_int(stmt, 3);
			int col5 = sqlite3_column_int(stmt, 4);
			snprintf(buf1, sizeof(buf1), "lighting_control:light:[%d, %d]\n", col1, col2);
			printf("client: %s", buf1);
			snprintf(buf2, sizeof(buf2), "lighting_control:led:[%d, %d]\n", col3, col4);
			printf("client: %s", buf2);
		}

		if (send(sockfd, buf1, strlen(buf1), 0) <= 0)
		{ // MSG_DONTWAIT 제거, strlen(buf) 사용
			perror("send()");
			break;
		}
		if (send(sockfd, buf2, strlen(buf2), 0) <= 0)
		{ // MSG_DONTWAIT 제거, strlen(buf) 사용
			perror("send()");
			break;
		}

		sqlite3_finalize(stmt);

		// 1초 대기
		sleep(1000);
	}
	close(sockfd);

	return 0;
}
