#ifndef DISPLAY_CPP
#define DISPLAY_CPP

#if ARDUINO >= 100
#include <Arduino.h>
#else 
#include <WProgram.h>
#endif

#include "Display.h"

namespace sbm {

Display disp;

/* setup the Display for a serial rate of 19200 bauds */
void Display::setup() {
  Serial.begin(19200);
}

/* update the position Data on the display */
void Display::update(long _scenePos, long _framePos, long _x, long _y, long _z) {
  if (_framePos < 1) {
    static char framePos[4];
    sprintf(framePos, "%03d", int(_framePos));
    
    clearscreen();
    Serial.print("Interpolating between Scenes! ");
    Serial.print(framePos);
  } else {
    static char scenePos[3];
    static char framePos[6];
    static char x[7];
    static char y[7];
    static char z[7];
    sprintf(scenePos, "%02d", int(_scenePos));
    sprintf(framePos, "%05d", int(_framePos));
    sprintf(x, "%06d", int(_x/2)); // workaround, sprintf works only with int, so I divide by 2
    sprintf(y, "%06d", int(_y/2));
    sprintf(z, "%06d", int(_z/2));
    
    clearscreen();
    Serial.print(scenePos);
    Serial.print(" ");
    Serial.print(framePos);
    linefeed();
    Serial.print(x);
    Serial.print(" ");
    Serial.print(y);
    Serial.print(" ");
    Serial.print(z);
  }
  
}

/* for start of film */
void Display::start() {
  clearscreen();
  Serial.print("Beginning of Film! Turn the light on!");
}

/* for end of film */
void Display::end() {
  clearscreen();
  Serial.print("End of Film! Turn off the light&rewind!");
}

void Display::clearscreen() { 
  Serial.write(0xC);
}

void Display::linefeed() {
  Serial.write(0xA);
}

void Display::clearline() {
  Serial.write(0x18);
}

void Display::homepos() {
   Serial.write(0xB);
}

//1=40%,2=60%,3=80%,4=100% Standard:4
void Display::brightness(int bright) { 
  Serial.write(0x1F);
  Serial.write(0x58);
  Serial.write(bright);
}

}; // end namsespace sbm

#endif
