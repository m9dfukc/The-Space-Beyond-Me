#include <InTimerOne.h>

int ledPin = 13;
boolean stat = false;

void setup() {
  pinMode(ledPin, OUTPUT); 
  digitalWrite(ledPin, LOW);
  Timer1.initialize(500000);         // initialize timer1, and set for every 500 ms
  Timer1.attachInterrupt(callback1, 500000);
}

void loop() {
  
}

void callback1() {
  digitalWrite(ledPin, stat = !stat);
}

