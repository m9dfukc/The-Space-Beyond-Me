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
    /*
    static char framePos[4];
    sprintf(framePos, "%03d", int(_framePos));
    
    clearscreen();
    Serial.print("Interpolating between Scenes! ");
    Serial.print(framePos);
    */
    
    
        clearscreen();
    moveto(1,1);
 Serial.print("go to scene "); 
moveto(13,1);
 Serial.print(_scenePos); 
 moveto(16,1);
 Serial.print("T"); 
 moveto(17,1);
 Serial.print(_framePos); 
    
    
    
    moveto(1,2);
 Serial.print("x"); 
moveto(2,2);
 Serial.print(_x); 
 moveto(8,2);
 Serial.print("y"); 
moveto(9,2);
 Serial.print(_y); 
  moveto(15,2);
 Serial.print("z"); 
moveto(16,2);
 Serial.print(_z); 
static int dispMode= 2;
    
  } else {
    
    
    clearscreen();
    moveto(1,1);
 Serial.print("scene "); 
moveto(7,1);
 Serial.print(_scenePos); 
 moveto(10,1);
 Serial.print("frame "); 
 moveto(16,1);
 Serial.print(_framePos); 
 
moveto(1,2);
 Serial.print("x"); 
moveto(2,2);
 Serial.print(_x); 
 moveto(8,2);
 Serial.print("y"); 
moveto(9,2);
 Serial.print(_y); 
  moveto(15,2);
 Serial.print("z"); 
moveto(16,2);
 Serial.print(_z); 
//static int dispMode= 2;

    
    /*
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
    */
  }
  
}

/* for start of film */
void Display::start() {
  //if (dispMode != 1){

  clearscreen();
  Serial.print("Beginning of film");
  moveto(1,2);
  Serial.print("turn both sw. to F/V");
  //static int dispMode= 1;
//  }
}

/* for end of film */
void Display::end() {
 // if (dispMode != 3){
  clearscreen();
  Serial.print("ENDE! turn of lamp &");
  moveto(1,2);
  Serial.print("both sw. to R & rew.");
 // static int dispMode= 3;
  //}
}



void Display::rewind() {

  clearscreen();
  Serial.print("rewinding film");

}


void Display::boot() {

  clearscreen();
  moveto(1,1);
  Serial.print("check film state &");
moveto(1,2);
Serial.print("start or rewind film");
}


void Display::clearscreen() { 
  Serial.write(0xC);
}

void Display::linefeed() {
  Serial.write(0xA);
}


void Display::moveto(int n, int m){
  Serial.write(0x1F);
  Serial.write(0x24);
  Serial.write(n); 
  Serial.write(m); 
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
