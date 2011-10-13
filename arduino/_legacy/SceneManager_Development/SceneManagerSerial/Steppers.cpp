#ifndef STEPPERS_CPP
#define STEPPERS_CPP

#if ARDUINO >= 100
#include <Arduino.h>
#else 
#include <WProgram.h>
#endif

#include <InTimerOne.h>
#include <InTimerThree.h>
#include <InTimerFive.h>
#include "Steppers.h"

namespace sbm {
  
uint8_t pinMotorX = 2;
uint8_t pinMotorY = 4; 
uint8_t pinMotorZ = 8; 

uint8_t pinDirX   = 3; 
uint8_t pinDirY   = 5;
uint8_t pinDirZ   = 9;

Steppers steppers;

void Steppers::setup() {
  previousMicros = frameTime = 0;
  stepsX = stepsY = stepsZ = 0;
  absStepsX = absStepsY = absStepsZ = 0;
  statusMotorX = statusMotorY = statusMotorZ = false;
  speedLimit = 2000;
  resetTime = 1000000;
  
  pinMode(pinMotorX, OUTPUT); 
  pinMode(pinMotorY, OUTPUT); 
  pinMode(pinMotorZ, OUTPUT);
  pinMode(pinDirX, OUTPUT);  
  pinMode(pinDirY, OUTPUT); 
  pinMode(pinDirZ, OUTPUT); 

  Timer1.initialize(500000);        
  Timer1.attachInterrupt(Steppers::callbackX, 500000);
  Timer1.stop();
  Timer3.initialize(500000);       
  Timer3.attachInterrupt(Steppers::callbackY, 500000);
  Timer3.stop();
  Timer5.initialize(500000); 
  Timer5.attachInterrupt(Steppers::callbackZ, 500000);
  Timer5.stop();
}

void Steppers::stepToPosition() {
  
}

void Steppers::stepTo(int _stepsX, int _stepsY, int _stepsZ) {
  stepsX = _stepsX;
  stepsY = _stepsY;
  stepsZ = _stepsZ;
  
  // set Motor Direction depending if stepsN are positive or negative
  if(stepsX < 0) digitalWrite(pinDirX, HIGH); 
  else digitalWrite(pinDirX, LOW); 
  if(stepsY < 0) digitalWrite(pinDirY, HIGH); 
  else digitalWrite(pinDirY, LOW);
  if(stepsZ < 0) digitalWrite(pinDirZ, HIGH); 
  else digitalWrite(pinDirZ, LOW);

  // watchdog for a upper speed limit
  if(abs(stepsX) > speedLimit) stepsX = speedLimit;
  if(abs(stepsY) > speedLimit) stepsY = speedLimit;
  if(abs(stepsY) > speedLimit) stepsZ = speedLimit;
  
  // calculates the amount of steps for the low/hight pulses
  stepCountX = abs(stepsX*2);
  stepCountY = abs(stepsY*2);
  stepCountZ = abs(stepsZ*2);
  
  // getting the speed and calucate the timing for the timers
  frameTime = micros() - previousMicros;
  previousMicros = micros();

  if(stepsX != 0) {
    Timer1.setPeriod(abs(frameTime/stepsX));
    Timer1.start();
  } else {
    Timer1.stop();
  }
  
  if(stepsY != 0) {
    Timer3.setPeriod(abs(frameTime/stepsY));
    Timer3.start();
  } else {
    Timer3.stop();
  }
  
  if(stepsZ != 0) {
    Timer5.setPeriod(abs(frameTime/stepsZ));
    Timer5.start();
  } else {
    Timer5.stop();
  }
}

void Steppers::setMaxSpeed(int maxSteps) {
  speedLimit = maxSteps;
}

void Steppers::timeout() {
  if(micros() - previousMicros > frameTime * 2) {
    Timer1.stop();
    Timer3.stop();
    Timer5.stop();
  }
}

void Steppers::callbackX() {
  digitalWrite( pinMotorX, (statusMotorX = !statusMotorX) ); 
  timeout();
}
void Steppers::callbackY() {
  digitalWrite( pinMotorY, (statusMotorY = !statusMotorY) );
  timeout();
}
void Steppers::callbackZ() {
  digitalWrite( pinMotorZ, (statusMotorZ = !statusMotorZ) );
  timeout();
}

}; // end namespace sbm

#endif