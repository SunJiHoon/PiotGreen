#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <wiringPi.h>
#include <wiringPiSPI.h>

#define SPI_CHANNEL 0
#define SPI_SPEED 500000  // SPI 속도 낮춤

int read_adc(int channel) {
    if (channel < 0 || channel > 7) return -1;

    unsigned char buf[3];
    buf[0] = 1;
    buf[1] = (8 + channel) << 4;
    buf[2] = 0;

    int result = wiringPiSPIDataRW(SPI_CHANNEL, buf, 3);
    if (result == -1) {
        printf("SPI 통신 오류\n");
        return -1;
    }

    printf("SPI buf: %x %x %x\n", buf[0], buf[1], buf[2]);  // 디버깅 출력

    int adc_value = ((buf[1] & 3) << 8) + buf[2];
    return adc_value;
}

void return_value(int* val, float* vol) {
    int adc_value = read_adc(0);  // CH0에서 읽음
    if (adc_value == -1) {
        printf("ADC 읽기 오류\n");
        *val = -1;
        *vol = 0;
        return;
    }

    double voltage = adc_value * 3.3 / 1023;
    *val = adc_value;
    *vol = voltage;
}

int main() {
    int soil_value;
    float soil_voltage;

    if (wiringPiSetup() == -1) {
        printf("WiringPi 초기화 실패\n");
        return 1;
    }
    if (wiringPiSPISetup(SPI_CHANNEL, SPI_SPEED) == -1) {
        printf("SPI 설정 실패\n");
        return 1;
    }

    while (1) {
        return_value(&soil_value, &soil_voltage);
        if (soil_value == -1) {
            printf("센서 값 읽기 실패\n");
        }
        else {
            printf("토양 수분 값: %d\n", soil_value);
            printf("아날로그 출력값: %f\n", soil_voltage);

            if (soil_value == 0) {
                printf("센서 연결 문제 또는 물 과다\n");
            }
            else if (soil_value > 800) {
                printf("건조\n");
            }
            else if (soil_value > 400) {
                printf("적정\n");
            }
            else {
                printf("촉촉\n");
            }
        }
        delay(1000);
    }
    return 0;
}
