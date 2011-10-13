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

/* setup the callbacks for nextFrame, nextScene, startFilm, endFilm */
void Sensor::setup(void (*_nextFrame)(), void (*_nextScene)(), void (*_startFilm)(), void (*_endFilm)()){
  Serial1.begin(115200);
  
  forward               = false;
  framePos              = 0;
  frameLastSceneTrigger = 0;
  nextSceneThreshold    = 10;
  startThreshold        = 30;
  endThreshold          = 30; 
  nextSceneCounter = startCounter = endCounter = 0;
  
  nextFrame = _nextFrame;
  nextScene = _nextScene;
  endFilm   = _endFilm;
  startFilm = _startFilm;
}

/* run this in you mainloop, constantly polls the serial1 (pin 18, 19) port for new sensor data */
void Sensor::update() {
  if (Serial1.available() > 0) {
     input = Serial1.read();
     
     /* check if running forward (0) or backward (1) */
     if( input == 0 || input == 1 ) {
       if (forward = (input == 0)) {
          nextFrame(); // trigger next Frame Callback
          framePos++; 
          if (framePos == frameLastSceneTrigger + 5) nextSceneCounter = 0; // reset nextScene Counter Threshold
        } else {
          framePos--;
          nextSceneCounter = 0; // reset nextScene Counter Threshold
        } 
     /* check if nextScene Sensor (2) got triggered */
     } else if ( input == 2) {
       if (forward) nextSceneCounter++;
       if (nextSceneCounter == nextSceneThreshold) { 
         /* only trigger next after a certain amount of sensor ticks, 
          * and lock this for at least 5 frames before new trigger is possible (nextSceneCounter) 
          */
         nextScene(); 
         frameLastSceneTrigger = framePos;
       }
     /* trigger end of film, start of film - depending on forward or backward running frameCounter */
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

}; // end namsespace sbm

#endif
