/*  InTimer
 *  Interrupt utilities for 16 bit Timer1/Timer3/Timer5 on ATmega168/328/1280/2560
 *  Copyright by invertednothing September 2011
 *  Derivated from Timer1 by Jesse Tane http://labs.ideo.com August 2008
 *
 *  This is free software. You can redistribute it and/or modify it under
 *  the terms of Creative Commons Attribution 3.0 United States License. 
 *  To view a copy of this license, visit http://creativecommons.org/licenses/by/3.0/us/ 
 *  or send a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, 
 *  California, 94105, USA.
 */
#ifndef INTIMERFIVE_h
#define INTIMERFIVE_h

//#include <avr/io.h>
#include <avr/interrupt.h>

#define RESOLUTION 65536    // InTimer is 16 bit

class InTimerFive
{
  public:
  
    // properties
    unsigned char clockSelectBits;

    // methods
    void initialize(long microseconds=1000000);
    void start();
    void stop();
    void restart();
	  unsigned long read();
    void attachInterrupt(void (*isr)(), long microseconds=-1);
    void detachInterrupt();
    void setPeriod(long microseconds);
    void (*isrCallback)();
};


extern InTimerFive Timer5;

#endif
