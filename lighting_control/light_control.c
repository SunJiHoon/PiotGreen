#include <wiringPi.h>
#include <mcp3004.h>
#include <wiringPiSPI.h>
#include <sqlite3.h>
#include <stdio.h>
#include <unistd.h>

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
    sqlite3_stmt *stmt1;

    char *err_msg = 0, user_input;
    int rc = sqlite3_open("light.db", &db);

    if (rc != SQLITE_OK) // 오류처리구문
    {
        fprintf(stderr, "Cannot open database: %s\n", sqlite3_errmsg(db));
        sqlite3_close(db);
        return 1;
    }

    const char *sql_sel = "SELECT * FROM LIGHT;";
    const char *sql_upd = "UPDATE LIGHT SET Led1 = ?, Led2 = ? WHERE rowid = 1;";
    int led1 = 0, led2 = 0;

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
                if (Value1 > THRESHOLD)
                {
                    digitalWrite(LEDPIN, HIGH);
                    led1 = 1;
                }
                else
                {
                    digitalWrite(LEDPIN, LOW);
                    led1 = 0;
                }
                if (Value2 > THRESHOLD)
                {
                    digitalWrite(LEDPIN, HIGH);
                    led2 = 1;
                }
                else
                {
                    digitalWrite(LEDPIN, LOW);
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
        }

        sqlite3_finalize(stmt1);

        // 5초 대기
        sleep(1);
    }
}