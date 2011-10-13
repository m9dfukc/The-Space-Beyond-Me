#ifndef DATA_CPP
#define DATA_CPP

#include <WProgram.h>
#include <avr/pgmspace.h>
#include "data.h"

Data mydata;
PROGMEM prog_uint16_t test1[]  = { 65000, 32796, 16843, 10, 11234, 11234 };
PROGMEM prog_uint16_t test2[]  = { 123, 234, 345, 456, 567, 11234 };
PROGMEM const prog_uint16_t* table[] = { test1, test2 };

void Data::setup() {
  Serial.begin(9600);  
}

void Data::listData() {
  for (int k=0; k<2; k++) {
    unsigned int ausgabe;
    ausgabe = pgm_read_word(table[0] + (k * 3));
    Serial.println(ausgabe);
    ausgabe = pgm_read_word(table[0] + (k * 3 + 1));
    Serial.println(ausgabe);
    ausgabe = pgm_read_word(table[0] + (k * 3 + 2));
    Serial.println(ausgabe);
  }
}

#endif