#include <wiringPi.h>
#include <stdio.h>

#define WATER 4

int main(){
	if(wiringPiSetup() == -1){
		printf("WiringPi 초기화 실패\n");
		return 1;
	}

	pinMode(WATER, OUTPUT);
	digitalWrite(WATER, LOW);

	while(1){

		digitalWrite(WATER, LOW);
		printf("water pump is off\n");
		delay(1000);

		digitalWrite(WATER, HIGH);
		printf("water pump is on\n");
		delay(1000);
	}

	return 0;
}
