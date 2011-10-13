#ifndef DISPLAY_H
#define DISPLAY_H

namespace sbm {

class Display {
public:
  void setup();
  void end();
  void start();
  void update(long _scenePos, long _framePos, long _x, long _y, long _z);
  
private:  
  void clearscreen();
  void clearline();
  void linefeed();
  void homepos();
  void brightness(int bright);
};
extern Display disp;

}; // end namespace sbm

#endif
