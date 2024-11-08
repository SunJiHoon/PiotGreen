#include <light_control.h>
#include <wiringPi.h>
#include <pthread.h>
#include <mcp3004.h>
#include <wiringPiSPI.h>

#define LEDPIN 1
#define LIGHT_THRESHOLD 100
#define BASE 100
#define SPI_CHAN 0

// server 코드에는 wiringPi관련 코드 없애고 여기로 옮기기
// 메인서버로 조도 측정값을 실시간으로 보내기 위한 스레드 추가로 만들기
// 공유 메모리 동기화하기 (뮤텍스), 추가 스레드 안만들어서 아직은 문제 없을 듯

int LEDstatus = 0, mode = 0; // mode: 자동/수동
int light;                   // 공유 메모리이므로 mutex 활용할 것
pthread_mutex_t mutex_mode, mutex_light;

void setup()
{
    wiringPiSetup();
    pinMode(LEDPIN, OUTPUT);
    // wiringPiSPISetup();
    // mcp3004Setup(BASE, SPI_CHAN);
}

void changeMode(int t_mode, int init)
{
    mode = t_mode;
    LEDstatus = init;
}

int changeLEDstatus(int t_status)
{
    if (mode == 1)
        return 1;
    LEDstatus = t_status;
    return 0;
}

void sendLight()
{
    // 메인서버로 송신할 때 새로운 스레드가 사용할 함수
    // ...
}
void autoMode()
{
    // 자동모드일 때 사용할 함수
    // 광량 측정해서 기준 보다 밝으면 LED 끄기

    // light = analogRead(BASE+2);
    if (light <= LIGHT_THRESHOLD)
        LEDstatus = 0;
    else
        LEDstatus = 1;
}
