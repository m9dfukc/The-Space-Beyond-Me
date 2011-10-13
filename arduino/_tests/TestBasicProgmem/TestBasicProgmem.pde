#include <WProgram.h>
#include <avr/pgmspace.h>
#include "data.h"

void setup() {
  mydata.setup();
  mydata.listData();
}

void loop() {}
