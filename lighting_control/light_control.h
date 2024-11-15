#ifndef LIGHT_CONTROL_H
#define LIGHT_CONTROL_H

#define LEDPIN 1
#define LIGHT_THRESHOLD 400
#define BASE 100
#define SPI_CHAN 0
#define LED_NUM 1

void setup();
void changeMode();
int changeLEDstatus();
void sendLight();
void autoMode();

#endif
