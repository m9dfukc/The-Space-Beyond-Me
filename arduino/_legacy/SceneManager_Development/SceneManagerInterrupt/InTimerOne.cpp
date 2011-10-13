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
#ifndef INTIMERONE_cpp
#define INTIMERONE_cpp

#include "InTimerOne.h"

InTimerOne Timer1;              // preinstatiate

ISR(TIMER1_OVF_vect)            // interrupt service routine that wraps a user defined function supplied by attachInterrupt
{
  Timer1.isrCallback();
}

void InTimerOne::initialize(long microseconds)
{
  TCCR1A = 0;                 // clear control register A 
  TCCR1B = _BV(WGM13);        // set mode 8: phase and frequency correct pwm, stop the timer
  setPeriod(microseconds);
}

void InTimerOne::setPeriod(long microseconds)
{
  long cycles = (F_CPU / 2000000) * microseconds;                                // the counter runs backwards after TOP, interrupt is at BOTTOM so divide microseconds by 2
  if(cycles < RESOLUTION)              clockSelectBits = _BV(CS10);              // no prescale, full xtal
  else if((cycles >>= 3) < RESOLUTION) clockSelectBits = _BV(CS11);              // prescale by /8
  else if((cycles >>= 3) < RESOLUTION) clockSelectBits = _BV(CS11) | _BV(CS10);  // prescale by /64
  else if((cycles >>= 2) < RESOLUTION) clockSelectBits = _BV(CS12);              // prescale by /256
  else if((cycles >>= 2) < RESOLUTION) clockSelectBits = _BV(CS12) | _BV(CS10);  // prescale by /1024
  else        cycles = RESOLUTION - 1, clockSelectBits = _BV(CS12) | _BV(CS10);  // request was out of bounds, set as maximum
  ICR1 = cycles;                                                     // ICR1 is TOP in p & f correct pwm mode
  TCCR1B &= ~(_BV(CS10) | _BV(CS11) | _BV(CS12));
  TCCR1B |= clockSelectBits;                                                     // reset clock select register
}

void InTimerOne::attachInterrupt(void (*isr)(), long microseconds)
{
  if(microseconds > 0) setPeriod(microseconds);
  isrCallback = isr;                                       // register the user's callback with the real ISR
  TIMSK1 = _BV(TOIE1);                                     // sets the timer overflow interrupt enable bit
  sei();                                                   // ensures that interrupts are globally enabled
  start();
}

void InTimerOne::detachInterrupt()
{
  TIMSK1 &= ~_BV(TOIE1);                                   // clears the timer overflow interrupt enable bit 
}

void InTimerOne::start()
{
  TCCR1B |= clockSelectBits;
}

void InTimerOne::stop()
{
  TCCR1B &= ~(_BV(CS10) | _BV(CS11) | _BV(CS12));          // clears all clock selects bits
}

void InTimerOne::restart()
{
  TCNT1 = 0;
}

unsigned long InTimerOne::read()		//returns the value of the timer in microseconds
{									//rember! phase and freq correct mode counts up to then down again
	unsigned int tmp=TCNT1;
	char scale=0;
	switch (clockSelectBits)
	{
	case 1:// no prescalse
		scale=0;
		break;
	case 2:// x8 prescale
		scale=3;
		break;
	case 3:// x64
		scale=6;
		break;
	case 4:// x256
		scale=8;
		break;
	case 5:// x1024
		scale=10;
		break;
	}
	while (TCNT1==tmp) //if the timer has not ticked yet
	{
		//do nothing -- max delay here is ~1023 cycles
	}
	tmp = (  (TCNT1>tmp) ? (tmp) : (ICR1-TCNT1)+ICR1  );//if we are counting down add the top value to how far we have counted down
	return ((tmp*1000L)/(F_CPU /1000L))<<scale;
}

#endif
