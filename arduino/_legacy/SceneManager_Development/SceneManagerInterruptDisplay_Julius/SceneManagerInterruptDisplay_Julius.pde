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





void setup() {
  sensor.setup( callbackNextFrame, callbackNextScene, callbackStartFilm, callbackEndFilm );
  disp.setup();  
  steppers.setup();
  data.setup();
  data.start();
  disp.boot();
}

void loop() {
  sensor.update();
  steppers.timeout();
}

void callbackNextFrame() { 
  data.nextFrame();
  myFrame = data.getFrame();
  mySteps = data.getSteps();
  steppers.stepTo(mySteps[0], mySteps[1], mySteps[2]);
  
  disp.update(myFrame[0], myFrame[1], myFrame[2], myFrame[3], myFrame[4]);
 
}

void callbackNextScene() {
  data.nextScene();
}

void callbackEndFilm() {
  data.start();
  
}

void callbackStartFilm() {
  data.start();

}

  


