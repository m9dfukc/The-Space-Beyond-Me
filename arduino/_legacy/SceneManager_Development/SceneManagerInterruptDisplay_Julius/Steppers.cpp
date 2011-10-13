#ifndef STEPPERS_CPP
#define STEPPERS_CPP

#if ARDUINO >= 100
#include <Arduino.h>
#else 
#include <WProgram.h>
#endif

#include "InTimerOne.h"
#include "InTimerThree.h"
#include "InTimerFive.h"
#include "Steppers.h"

namespace sbm {
  
/* Constant Motor and Direction Pin settings */
const uint8_t pinMotorX = 2;
const uint8_t pinMotorY = 4; 
const uint8_t pinMotorZ = 6; 

const uint8_t pinDirX   = 3; 
const uint8_t pinDirY   = 5;
const uint8_t pinDirZ   = 7;

Steppers steppers;
/* status of the motor pins high/low */
bool Steppers::statusMotorX = false;
bool Steppers::statusMotorY = false;
bool Steppers::statusMotorZ = false;

/* initialize the stepper and timer settings */
void Steppers::setup() {  
  previousMicros = frameTime = 0;
  stepsX = stepsY = stepsZ = 0;
  absStepsX = absStepsY = absStepsZ = 0;
  speedLimit = 2000; // max speed limit per clock cycle
  
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

/* goto absolute stepper position */
void Steppers::stepToPosition(int _absStepsX, int _absStepsY, int _absStepsZ) {
  
}

/* go to a relative position in steps, 
 * values can be positive or negative
 * positive: clockwise direction, nagative: counter clockwise
 */
void Steppers::stepTo(int _stepsX, int _stepsY, int _stepsZ) {
  stepsX = _stepsX;
  stepsY = _stepsY;
  stepsZ = _stepsZ;
  
  // set Motor Direction depending if stepsN are positive or negative
  if(stepsX > 0) digitalWrite(pinDirX, HIGH); 
  else digitalWrite(pinDirX, LOW); 
  if(stepsY > 0) digitalWrite(pinDirY, HIGH); 
  else digitalWrite(pinDirY, LOW);
  if(stepsZ < 0) digitalWrite(pinDirZ, HIGH); 
  else digitalWrite(pinDirZ, LOW);

  // watchdog for a upper speed limit
  if(abs(stepsX) > speedLimit) stepsX = speedLimit;
  if(abs(stepsY) > speedLimit) stepsY = speedLimit;
  if(abs(stepsY) > speedLimit) stepsZ = speedLimit;
  
  // calculates the amount of steps for the low/hight pulses
  unsigned long stepCountX = abs(stepsX*2);
  unsigned long stepCountY = abs(stepsY*2);
  unsigned long stepCountZ = abs(stepsZ*4);
  
  // getting the speed and calucate the timing for the timers
  frameTime = micros() - previousMicros;
  previousMicros = micros();

  // absolute cycles for the steppers, multiplyed by 2 because we need high/low pulses
  if(stepsX != 0) {
    Timer1.setPeriod(abs(frameTime/stepCountX));
    Timer1.start();
  } else {
    Timer1.stop();
  }
  
  if(stepsY != 0) {
    Timer3.setPeriod(abs(frameTime/stepCountY));
    Timer3.start();
  } else {
    Timer3.stop();
  }
  
  if(stepsZ != 0) {
    Timer5.setPeriod(abs(frameTime/stepCountZ));
    Timer5.start();
  } else {
    Timer5.stop();
  }
}

/* set's the maximum speed limit */
void Steppers::setMaxSpeed(int maxSteps) {
  speedLimit = maxSteps;
}

/* call this in your mainloop, this will prevent the steppers from running away if you stop the machine inbetween */
void Steppers::timeout() {
  if(micros() - previousMicros > frameTime * 2) {
    Timer1.stop();
    Timer3.stop();
    Timer5.stop();
  }
}

/* timer callbacks fpr x y z */
void Steppers::callbackX() {
  digitalWrite( pinMotorX, (statusMotorX = !statusMotorX) ); 
}
void Steppers::callbackY() {
  digitalWrite( pinMotorY, (statusMotorY = !statusMotorY) );
}
void Steppers::callbackZ() {
  digitalWrite( pinMotorZ, (statusMotorZ = !statusMotorZ) );
}

}; // end namespace sbm

#endif
