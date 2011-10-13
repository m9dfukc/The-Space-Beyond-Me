#ifndef _TEST_APP
#define _TEST_APP

#include <iostream>
#include <string>
#include <fstream>

#include "ofMain.h"

#include "ofxOpenCv.h"
#include "ofxDirList.h"

//#define _USE_LIVE_VIDEO		// uncomment this to use a live camera
// otherwise, we'll use a movie file

struct tStablePoint
{
	CvPoint2D32f src_point;
	CvPoint2D32f dst_point;
};

class testApp : public ofBaseApp
{

public:

	void setup();
	void update();
	void draw();

	void keyPressed  (int key);
	void keyReleased  (int key);
	void mouseMoved(int x, int y );
	void mouseDragged(int x, int y, int button);
	void mousePressed(int x, int y, int button);
	void mouseReleased(int x, int y, int button);
	void windowResized(int w, int h);

	
#ifdef _USE_LIVE_VIDEO
	ofVideoGrabber 		vidGrabber;
#else
	ofVideoPlayer 		vidPlayer;
#endif

	ofxCvColorImage		colorImg;
	ofxCvGrayscaleImage grayImage;

	static const int MAX_COUNT = 1000;
	vector<tStablePoint> stable_points;
	CvPoint2D32f* act_points;
	vector<string> fileNames;
	ofxDirList DIR;
	ofstream myfile;

	double current_zoom; 
	double factor;
	double posOffsetX;
	double posOffsetY;
	double initX; 
	double initY; 
	double flt_vert;
	double flt_horiz;
	
	double posOffsetX_local;
	double posOffsetY_local;
	double flt_vert_local;
	double flt_horiz_local;
	double initX_local; 
	double initY_local; 
	
	int blur;	
	int vidIndex;
	int numFiles;
	int count;
	int frameIndex;
	int filmWidth;
	int filmHeight;

	bool playToggle;
	bool clearScreen;
	bool saveFile;	
	bool verbose;
	bool bNewFrame;
	bool subpixels;
	
};

#endif
