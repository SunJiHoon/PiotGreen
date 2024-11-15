#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <wiringPi.h>
#include <mcp3004.h>
#include <wiringPiSPI.h>

#define BASE 100
#define SPI_CHAN 0
int main(int argc, char *argv[])
{
    int i;
    printf("wiringPiSPISetup return=%d\n", wiringPiSPISetup(0, 500000));
    mcp3004Setup(BASE, SPI_CHAN);
    while (1)
    {
        printf("CDS value : %d\n", analogRead(BASE + 2));
        delay(500);
    }
}