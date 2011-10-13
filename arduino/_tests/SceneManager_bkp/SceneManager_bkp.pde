int trigger;
int* myframe;

void setup() {
  Serial.begin(115200);
  nextScene();
}

void loop() {
  if (Serial.available()) {
      trigger = Serial.read();
      Serial.print(trigger, DEC);
      nextScene();
  }
  
  delay(10);  
  nextFrame();
  
  myframe = getFrame();
  
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






