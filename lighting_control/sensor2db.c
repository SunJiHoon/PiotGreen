#include <sqlite3.h>
#include <stdio.h>
#include <wiringPi.h>
#include <mcp3004.h>
#include <wiringPiSPI.h>

#define BASE 100
#define SPI_CHAN 0

int kbhit();

int main(void)
{
    sqlite3 *db;
    sqlite3_stmt *stmt;

    char *err_msg = 0, user_input;
    int rc = sqlite3_open("light.db", &db);
    int light = 0;
    printf("wiringPiSPISetup return=%d\n", wiringPiSPISetup(0, 500000));
    mcp3004Setup(BASE, SPI_CHAN);

    if (rc != SQLITE_OK) // 오류처리구문
    {
        fprintf(stderr, "Cannot open database: %s\n", sqlite3_errmsg(db));
        sqlite3_close(db);
        return 1;
    }

    const char *sql = "DROP TABLE IF EXISTS LIGHT;"
                      "CREATE TABLE LIGHT("
                      "Value INTEGER,"
                      "Led1 INTEGER,"
                      "Led2 INTEGER,"
                      "Mode INTEGER);";

    rc = sqlite3_exec(db, sql, 0, 0, &err_msg);

    if (rc != SQLITE_OK)
    {
        fprintf(stderr, "SQL error: %s\n", err_msg);
        sqlite3_free(err_msg);
        sqlite3_close(db);

        return 1;
    }

    sql = "INSERT INTO LIGHT (Value, Led1, Led2, Mode) VALUES (0, 0, 0, 0);";

    rc = sqlite3_exec(db, sql, 0, 0, &err_msg);

    if (rc != SQLITE_OK)
    {
        fprintf(stderr, "SQL error: %s\n", err_msg);
        sqlite3_free(err_msg);
        sqlite3_close(db);

        return 1;
    }

    sql = "UPDATE LIGHT SET Value = ? WHERE rowid = 1;";
    rc = sqlite3_prepare_v2(db, sql, -1, &stmt, NULL);
    if (rc != SQLITE_OK)
    {
        fprintf(stderr, "Failed to prepare statement: %s\n", sqlite3_errmsg(db));
        sqlite3_close(db);
        return 1;
    }

    printf("Press 'q' to quit the program.\n");
    while (1)
    {
        if (kbhit())
        {
            user_input = getchar();
            if (user_input == 'q')
            {
                printf("Exiting program...\n");
                break;
            }
        }
        light = analogRead(BASE + 3);
        char led[20] = "[0,0,0,0,0]";
        sqlite3_bind_int(stmt, 1, light);
        sqlite3_bind_text(stmt, 2, led, -1, SQLITE_STATIC);

        rc = sqlite3_step(stmt);

        if (rc != SQLITE_DONE)
        {
            fprintf(stderr, "Execution failed: %s\n", sqlite3_errmsg(db));
        }
        else
        {
            printf("Inserted Value: %d, %s\n", light, led);
        }
        sqlite3_reset(stmt);
        delay(1000);
    }
    sqlite3_finalize(stmt);
    sqlite3_close(db);

    return 0;
}

#include <unistd.h>
#include <termios.h>
#include <fcntl.h>

int kbhit(void)
{
    struct termios oldt, newt;
    int ch;
    int oldf;

    // 터미널 설정
    tcgetattr(STDIN_FILENO, &oldt);
    newt = oldt;
    newt.c_lflag &= ~(ICANON | ECHO);
    tcsetattr(STDIN_FILENO, TCSANOW, &newt);
    oldf = fcntl(STDIN_FILENO, F_GETFL, 0);
    fcntl(STDIN_FILENO, F_SETFL, oldf | O_NONBLOCK);

    ch = getchar();

    // 원래 터미널 설정 복원
    tcsetattr(STDIN_FILENO, TCSANOW, &oldt);
    fcntl(STDIN_FILENO, F_SETFL, oldf);

    if (ch != EOF)
    {
        ungetc(ch, stdin);
        return 1;
    }

    return 0;
}