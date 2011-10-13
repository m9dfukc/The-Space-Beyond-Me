#ifndef DATA_H
#define DATA_H

class Data {
public:
  void setup();
  int nextScene();
  int nextFrame();
  int getSceneCount();
  int getFrameCount();
  int getFramePos();
  long* getPosition();
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
  long point[3];
  long frame[5]; 
  int frames_in_between;
  int frames_at_start;
  bool interpolating;

  long lscene_lframe[3];
  long tscene_fframe[3];
  long tframe[3];
  
  bool verbose;
};
extern Data data;

#endif
