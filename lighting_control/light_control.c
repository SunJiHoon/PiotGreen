#include <light_control.h>
#include <wiringPi.h>
#include <pthread.h>
#include <mcp3004.h>
#include <wiringPiSPI.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

int LEDstatus[LED_NUM] = {
    0,
};
int mode = 0; // mode: 자동/수동
int light;    // 공유 메모리이므로 mutex 활용할 것
pthread_mutex_t mutex_mode, mutex_light;

void setup()
{
    wiringPiSetup();
    pinMode(LEDPIN, OUTPUT);
    wiringPiSPISetup(0, 500000);
    mcp3004Setup(BASE, SPI_CHAN);
}

void changeMode(int t_mode)
{
    mode = t_mode;
}

int changeLEDstatus(int t_status, int num_led)
{
    if (mode == 0)
        return 1;
    LEDstatus[num_led] = t_status;
    return 0;
}

void sendLight()
{
}
void autoMode()
{
    // 자동모드일 때 사용할 함수
    // 광량 측정해서 기준 보다 밝으면 LED 끄기

    light = analogRead(BASE + 2);
    int target_status = (light <= LIGHT_THRESHOLD ? 0 : 1);
    for (int i = 0; i < LED_NUM; i++)
    {
        LEDstatus[i] = target_status;
    }
}
