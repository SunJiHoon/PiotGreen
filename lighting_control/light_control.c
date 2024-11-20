#include <wiringPi.h>
#include <mcp3004.h>
#include <wiringPiSPI.h>
#include <sqlite3.h>
#include <stdio.h>

#define THRESHOLD 400
#define LEDPIN 0

void set_led_state()
{
}

int main()
{
    wiringPiSetup();
    pinMode(LEDPIN, OUTPUT);

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
            const char *Value1 = (const char *)sqlite3_column_text(stmt, 0);
            const char *Value2 = (const char *)sqlite3_column_text(stmt, 1);
            const char *led1 = (const char *)sqlite3_column_text(stmt, 2);
            const char *led2 = (const char *)sqlite3_column_text(stmt, 3);
            const char *mode = (const char *)sqlite3_column_text(stmt, 4);
            printf("foundData: Value1: %s, Value2: %s, Led1: %s, Led2: %s, Mode: %s\n", Value1, Value2, led1, led2, mode);
            if (mode == 1)
            {
            }
        }

        sqlite3_finalize(stmt);

        // 5초 대기
        sleep(1);
    }
}