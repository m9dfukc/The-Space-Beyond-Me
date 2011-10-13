#ifndef DATA_H
#define DATA_H

namespace sbm {

class Data {
public:
  void setup();
  int nextScene();
  int nextFrame();
  int getSceneCount();
  int getFrameCount();
  int getFramePos();
  long* getSteps();
  long* getFrame();
  void reset();
  bool isInterpolating();
  int availableMemory();

private:
  void interpolate(long from[3], long to[3], long dst[3], int in_stack);
  float CosineInterpolate(float y1, float y2, float mu);
  
  int scene_count; 
  int frame_count;
  int scene_pos;  
  int frame_pos; 
  int last_frame_pos;
  int frames_in_between;
  int frames_at_start;
  bool interpolating;

  long steps[3];
  long frame[5];
  long tstart_pos[3];
  long tend_pos[3];
  long tlast_pos[3];
  long tsteps[3];
  
  bool verbose;
};
extern Data data;

}; // end namespace sbm

#endif
