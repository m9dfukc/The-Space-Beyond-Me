#ifndef SENSOR_H
#define SENSOR_H
#include "Display.h"

namespace sbm {

class Sensor {
public:
  void setup(void (*_nextFrame)(), void (*_nextScene)(), void (*_endFilm)(), void (*_startFilm)());
  void update();
  
private:  
int dispMode;
  char input;
  bool forward;
  long framePos;
  long frameLastSceneTrigger;
  unsigned int nextSceneThreshold;
  unsigned int nextSceneCounter;
  unsigned int startThreshold;
  unsigned int startCounter;
  unsigned int endThreshold;
  unsigned int endCounter;
  
  void (*nextFrame)();
  void (*nextScene)();
  void (*endFilm)();
  void (*startFilm)();
  
  
};
extern Sensor sensor;

}; // end namespace sbm

#endif
