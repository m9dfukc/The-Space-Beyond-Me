#include <avr/pgmspace.h>
#include "Data.h"
#include <AccelStepper.h>
#include <InTimerOne.h>

using namespace sbm;

AccelStepper stepper1(2,2,3);
AccelStepper stepper2(2,4,5);
AccelStepper stepper3(2,8,9);

unsigned long lastEvent = 0;
unsigned long timeBetween = 0;
unsigned long timeSteppers = 0;
unsigned long newPeriod = 0;

bool trigger = false;

long recurringEvent = 80000;
long randomBit = 35000;

long* myFrame;

void setup() {
  Serial.begin(115200);
  data.setup();
  data.nextScene();
  
  stepper1.setMaxSpeed(1500);
  stepper1.setCurrentPosition(0);
  stepper2.setMaxSpeed(1500);
  stepper2.setCurrentPosition(0);
  stepper3.setMaxSpeed(1500);
  stepper3.setCurrentPosition(0);
  
  Timer1.initialize(recurringEvent);
  Timer1.attachInterrupt(callbackX, recurringEvent);
  Timer1.start();
  
  randomSeed(analogRead(0));
}

void loop() {
  
  stepper1.runSpeed();
  stepper2.runSpeed();
  stepper3.runSpeed();
  
  if (trigger) {
    
    if (data.getFramePos() >= data.getFrameCount()) data.nextScene();
    
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
    
    trigger = false;
    
    toConsole();
  }
  
}

void callbackX() {
  newPeriod = ( recurringEvent + random(randomBit) - (randomBit/2) );
  Timer1.setPeriod( newPeriod );
  
  trigger = true;
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

  


