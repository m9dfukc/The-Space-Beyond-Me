#include <avr/pgmspace.h>
#include "Data.h"

int trigger;
long* myframe;

void setup() {
  data.setup();
  Serial.begin(115200);
  data.nextScene();
}

void loop() {
  if (Serial.available()) {
      trigger = Serial.read();
      data.nextScene();
  }
  
  delay(50);
    
  data.nextFrame();
  myframe = data.getFrame();

  Serial.print("scene: ");
  Serial.print(myframe[0]);
  Serial.print(" frame: ");
  Serial.print(myframe[1]);
  Serial.print(" x: ");
  Serial.print(myframe[2]);
  Serial.print(" y: "); 
  Serial.print(myframe[3]);
  Serial.print(" z: ");
  Serial.println(myframe[4]);
}






