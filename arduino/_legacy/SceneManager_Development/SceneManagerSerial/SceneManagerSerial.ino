#include <avr/pgmspace.h>
#include <AccelStepper.h>
#include <InTimerOne.h>
#include "Data.h"
#include "Sensor.h"

using namespace sbm;

AccelStepper stepper1(1,2,3);
AccelStepper stepper2(1,4,5);
AccelStepper stepper3(1,8,9);

unsigned long lastEvent = 0;
unsigned long timeBetween = 0;

long* myFrame;

void setup() {
  Serial.begin(115200);
  
  steppers.setup();
  sensor.setup( callbackNextFrame, callbackNextScene, callbackStartFilm, callbackEndFilm );
  
  data.setup();
  data.nextScene();

  stepper1.setMaxSpeed(4000);
  stepper1.setCurrentPosition(0);
  stepper2.setMaxSpeed(4000);
  stepper2.setCurrentPosition(0);
  stepper3.setMaxSpeed(4000);
  stepper3.setCurrentPosition(0);
}

void loop() {
  stepper1.runSpeed();
  stepper2.runSpeed();
  stepper3.runSpeed();

  sensor.update();
}

void callbackNextFrame() {
  timeBetween = micros() - lastEvent;
  lastEvent = micros();
  
  data.nextFrame();
  myFrame = data.getFrame();

  float timebase      = float(1000000) / float(timeBetween);
  int posX            = myFrame[2];
  int posY            = myFrame[3];
  int posZ            = myFrame[4];
  float stepsX        = posX - stepper1.currentPosition(); 
  float stepsY        = posY - stepper2.currentPosition();
  float stepsZ        = posZ - stepper3.currentPosition();
  float stepsPerSecX  = timebase * stepsX;
  float stepsPerSecY  = timebase * stepsY;
  float stepsPerSecZ  = timebase * stepsZ;
  
  stepper1.setSpeed( stepsPerSecX ); 
  stepper1.moveTo( posX ); 
  stepper2.setSpeed( stepsPerSecY ); 
  stepper2.moveTo( posY );
  stepper3.setSpeed( stepsPerSecZ ); 
  stepper3.moveTo( posZ );

  toConsole();
}

void callbackNextScene() {
  data.nextScene();
}

void callbackEndFilm() {
  data.reset();
}

void callbackStartFilm() {
  data.reset();
}

void toConsole() {
  Serial.print("scene: ");
  Serial.print(myFrame[0]);
  Serial.print(" frame: "); 
  Serial.print(myFrame[1]);
  Serial.print(" target pos: "); 
  Serial.print(myFrame[2]);
  Serial.print(" ");
  Serial.print(myFrame[3]);
  Serial.print(" ");
  Serial.print(myFrame[4]);
  Serial.print(" current pos: "); 
  Serial.print(stepper1.currentPosition());
  Serial.print(" ");
  Serial.print(stepper2.currentPosition());
  Serial.print(" ");
  Serial.println(stepper3.currentPosition());
}

  


