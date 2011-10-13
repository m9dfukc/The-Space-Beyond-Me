#include "WProgram.h"
void setup();
void loop();
int trigger;

void setup() {
  Serial.begin(115200);
}

void loop() {
  if (Serial.available()) {
      trigger = Serial.read();
      Serial.println(trigger);
    }
    
  //nextScene();
  //nextFrame();
    
}






