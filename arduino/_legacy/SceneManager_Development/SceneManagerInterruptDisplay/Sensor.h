#ifndef SENSOR_H
#define SENSOR_H

namespace sbm {

class Sensor {
public:
  void setup(void (*_nextFrame)(), void (*_previousFrame)(), void (*_nextScene)(), void (*_startFilm)(), void (*_endFilm)());
  void update();
  
private:  
  char input;
  bool forward;
  long framePos;
  long frameLastTrigger;
  unsigned int nextSceneThreshold;
  unsigned int nextSceneCounter;
  unsigned int startThreshold;
  unsigned int startCounter;
  unsigned int endThreshold;
  unsigned int endCounter;
  
  void (*nextFrame)();
  void (*previousFrame)();
  void (*nextScene)();
  void (*endFilm)();
  void (*startFilm)();
  
  
};
extern Sensor sensor;

}; // end namespace sbm

#endif
