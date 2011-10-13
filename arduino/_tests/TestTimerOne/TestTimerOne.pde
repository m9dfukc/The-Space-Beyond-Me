#include "TimerOne.h"
int counter1 = 0;
int counter2 = 0;
boolean out1 = false; 
boolean out2 = false;

void setup()
{
  Serial.begin(9600);
  pinMode(10, OUTPUT);
  pinMode(9, OUTPUT);
  
  Timer1.initialize(1000000);         // initialize timer1, and set a 1/2 second period
  Timer1.attachInterrupt(callback1, 1000000);
  //Timer1.attachInterrupt(callback2);
  //attachInterrupt(10, callback2, CHANGE);
  //Timer1.attachInterrupt(callback, 1000000);  // attaches callback() as a timer overflow interrupt
}

void callback1()
{
  digitalWrite(13, out1=!out1);
  counter1++;
  Serial.print("Counter1 value: "); 
  Serial.println(counter1);
}

void callback2()
{
  digitalWrite(12, out2=!out2);
  counter2++;
  Serial.print("Counter2 value: "); 
  Serial.println(counter2);
}

void loop()
{

}
