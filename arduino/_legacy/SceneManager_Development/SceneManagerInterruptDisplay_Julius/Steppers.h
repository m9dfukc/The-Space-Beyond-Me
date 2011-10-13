#ifndef STEPPERS_H
#define STEPPERS_H

namespace sbm {

class Steppers {
public:
  void setup();
  void stepTo(int _stepsX, int _stepsY, int _stepsZ);
  void stepToPosition(int _absStepsX, int _absStepsY, int _absStepsZ);
  void setMaxSpeed(int maxSteps);
  void timeout();
  
private:
  long absStepsX;
  long absStepsY;
  long absStepsZ;
  
  int stepsX;
  int stepsY;
  int stepsZ;

  int speedLimit;
  
  unsigned long previousMicros;
  unsigned long frameTime;

  static bool statusMotorX;
  static bool statusMotorY;
  static bool statusMotorZ;
  
  static void callbackX();
  static void callbackY();
  static void callbackZ();

};
extern Steppers steppers;
  
}; // end namespace sbm

#endif
