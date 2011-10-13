#include <avr/pgmspace.h>
#include "Data.h"
#include "Sensor.h"
#include "Steppers.h"
#include "Display.h"

using namespace sbm;

unsigned long lastEvent = 0;
unsigned long timeBetween = 0;

long* myFrame;
long* mySteps;

long framePos       = 0;
long lastFramePos   = -1;
long voidDataFrames = 0;

void setup() {
  sensor.setup( callbackNextFrame, callbackPreviousFrame, callbackNextScene, callbackStartFilm, callbackEndFilm );
  steppers.setup();
  data.setup();
  data.start();
  disp.setup();  
  disp.boot();
}

void loop() {
  sensor.update();
  steppers.timeout();
}

void callbackNextFrame() { 
  framePos = data.nextFrame();
  myFrame = data.getFrame();
  mySteps = data.getSteps();
  steppers.stepTo(mySteps[0], mySteps[1], mySteps[2]);
  
  disp.update(myFrame[0], myFrame[1], myFrame[2], myFrame[3], myFrame[4]);
  
  if (framePos == lastFramePos) {
    disp.clearscreen();
    voidDataFrames++;
    Serial.print("Frames missing: ");
    Serial.print(voidDataFrames);
  } else {
    voidDataFrames = 0;
  }
  lastFramePos = framePos;
}

void callbackPreviousFrame() {
  disp.rewind();
}

void callbackNextScene() {
  data.nextScene();
}

void callbackEndFilm() {
  data.start();
  disp.end(); 
}

void callbackStartFilm() {
  data.start();
  disp.start();
}

  


