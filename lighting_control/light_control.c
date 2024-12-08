#include <wiringPi.h>
#include <sqlite3.h>
#include <stdio.h>
#include <unistd.h>

#define THRESHOLD 40
#define LED1PINfir 1
#define LED2PINfir 23
#define LED1PINsec 24
#define LED2PINsec 26

// 본 프로그램은 자동/수종모드 여부에 따라 led를 제어합니다.

int main()
{
    wiringPiSetup();
    // pinMode(LED1PINfir, OUTPUT);
    // pinMode(LED2PINfir, OUTPUT);
    // pinMode(LED1PINsec, OUTPUT);
    // pinMode(LED2PINsec, OUTPUT);
    pinMode(LED1PINfir, PWM_OUTPUT);
    pinMode(LED2PINfir, PWM_OUTPUT);
    pinMode(LED1PINsec, PWM_OUTPUT);
    pinMode(LED2PINsec, PWM_OUTPUT);
    pwmSetMode(PWM_MODE_MS);
    pwmSetClock(19);
    pwmSetRange(1024);

    sqlite3 *db;
    sqlite3_stmt *stmt1;

    char *err_msg = 0;
    int rc = sqlite3_open("light.db", &db);

    if (rc != SQLITE_OK) // 오류처리구문
    {
        fprintf(stderr, "Cannot open database: %s\n", sqlite3_errmsg(db));
        sqlite3_close(db);
        return 1;
    }

    const char *sql_sel = "SELECT * FROM LIGHT;";
    const char *sql_upd = "UPDATE LIGHT SET Led1 = ?, Led2 = ? WHERE rowid = 1;";

    while (1)
    {
        sqlite3_stmt *stmt2;
        rc = sqlite3_prepare_v2(db, sql_sel, -1, &stmt1, 0);
        if (rc != SQLITE_OK)
        {
            fprintf(stderr, "SQL error: %s\n", sqlite3_errmsg(db));
            sqlite3_close(db);
            return 1;
        }

        // 테이블에서 행을 한 개씩 가져와 출력
        while (sqlite3_step(stmt1) == SQLITE_ROW)
        {
            int Value1 = sqlite3_column_int(stmt1, 0);
            int Value2 = sqlite3_column_int(stmt1, 1);
            int led1 = sqlite3_column_int(stmt1, 2);
            int led2 = sqlite3_column_int(stmt1, 3);
            int mode = sqlite3_column_int(stmt1, 4);
            printf("foundData: Value1: %d, Value2: %d, Led1: %d, Led2: %d, Mode: %d\n", Value1, Value2, led1, led2, mode);
            if (mode == 1)
            {
                // 자동모드
                if (Value1 < THRESHOLD)
                {
                    // digitalWrite(LED1PINfir, HIGH);
                    // digitalWrite(LED1PINsec, HIGH);
                    pwmWrite(LED1PINfir, 1024 - Value1 * 1024 / 100);
                    pwmWrite(LED1PINsec, 1024 - Value1 * 1024 / 100);
                    led1 = 1;
                }
                else
                {
                    // digitalWrite(LED1PINfir, LOW);
                    // digitalWrite(LED1PINsec, LOW);
                    pwmWrite(LED1PINfir, 0);
                    pwmWrite(LED1PINsec, 0);
                    led1 = 0;
                }
                if (Value2 < THRESHOLD)
                {
                    // digitalWrite(LED2PINfir, HIGH);
                    // digitalWrite(LED2PINsec, HIGH);
                    pwmWrite(LED2PINfir, 1024 - Value2 * 1024 / 100);
                    pwmWrite(LED2PINsec, 1024 - Value2 * 1024 / 100);
                    led2 = 1;
                }
                else
                {
                    // digitalWrite(LED2PINfir, LOW);
                    // digitalWrite(LED2PINsec, LOW);
                    pwmWrite(LED2PINfir, 0);
                    pwmWrite(LED2PINsec, 0);
                    led2 = 0;
                }
                rc = sqlite3_prepare_v2(db, sql_upd, -1, &stmt2, 0);
                sqlite3_bind_int(stmt2, 1, led1);
                sqlite3_bind_int(stmt2, 2, led2);
                rc = sqlite3_step(stmt2);

                if (rc != SQLITE_DONE)
                {
                    fprintf(stderr, "SQL error: %s\n", sqlite3_errmsg(db));
                    sqlite3_close(db);
                    return 1;
                }
            }
            else
            {
                // 수동모드
                if (led1 == 1)
                {
                    // digitalWrite(LED1PINfir, HIGH);
                    // digitalWrite(LED1PINsec, HIGH);
                    pwmWrite(LED1PINfir, 1024);
                    pwmWrite(LED1PINsec, 1024);
                }
                else
                {
                    // digitalWrite(LED1PINfir, LOW);
                    // digitalWrite(LED1PINsec, LOW);
                    pwmWrite(LED1PINfir, 0);
                    pwmWrite(LED1PINsec, 0);
                }
                if (led2 == 1)
                {
                    // digitalWrite(LED2PINfir, HIGH);
                    // digitalWrite(LED2PINsec, HIGH);
                    pwmWrite(LED2PINfir, 1024);
                    pwmWrite(LED2PINsec, 1024);
                }
                else
                {
                    // digitalWrite(LED2PINfir, LOW);
                    // digitalWrite(LED2PINsec, LOW);
                    pwmWrite(LED2PINfir, 0);
                    pwmWrite(LED2PINsec, 0);
                }
            }
        }

        sqlite3_finalize(stmt1);

        // 5초 대기
        sleep(1);
    }
}