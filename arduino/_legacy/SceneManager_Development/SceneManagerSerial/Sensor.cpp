#ifndef SENSOR_CPP
#define SENSOR_CPP

#if ARDUINO >= 100
#include <Arduino.h>
#else 
#include <WProgram.h>
#endif

#include "Sensor.h"

namespace sbm {

Sensor sensor;
    
void Sensor::setup(void (*_nextFrame)(), void (*_nextScene)(), void (*_startFilm)(), void (*_endFilm)()){
  Serial1.begin(115200);
  
  forward             = false;
  frameCounter        = 0;
  nextSceneThreshold  = 30;
  startThreshold      = 30;
  endThreshold        = 30;
  lastEvent = timeBetween = 0;  
  nextSceneCounter = startCounter = endCounter = 0;
  
  nextFrame = _nextFrame;
  nextScene = _nextScene;
  endFilm   = _endFilm;
  startFilm = _startFilm;
}

void Sensor::update() {
  if (Serial1.available() > 0) {
     input = Serial1.read();
     if( input == 0 || input == 1 ) {
       if (forward = (input == 0)) {
          calcTiming();
          nextFrame();
          frameCounter++; 
        } else {
          frameCounter--;
        } 
     } else if ( input == 2) {
       if (forward) nextSceneCounter++;
       if (nextSceneCounter >= nextSceneThreshold) {
         nextScene();
         nextSceneCounter = 0;
       }
     } else if ( input == 3 ) {
       if (forward) endCounter++; else startCounter++;
       if (endCounter >= endThreshold) {
         endFilm();
         endCounter = startCounter = 0;
       } else if (startCounter >= startThreshold) {
         startFilm();
         endCounter = startCounter = 0;
       } 
     }
  }
}

unsigned long Sensor::getTiming() {
  return timeBetween;
}

void Sensor::calcTiming() {
  timeBetween = micros() - lastEvent;
  lastEvent = micros();
}

}; // end namsespace sbm

#endif