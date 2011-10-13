#ifndef _TEST_APP
#define _TEST_APP

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

	ofxCvGrayscaleImage 	grayImage;
	//ofxCvGrayscaleImage 	grayImageLast;
	//ofxCvGrayscaleImage 	grayImageDifference;

	static const int MAX_COUNT = 500;
	vector<tStablePoint> stable_points;
	
	int cPress;
	int saveImgCount;
	
	double flt_horiz;
	double flt_vert;
	double initX; double initializedX;
	double initY; double initializedY;
	double movement_prev;
	
	int scene;

	
	float factor;
	int blur;	
	int vidIndex;
	int numFiles;
	ofxDirList DIR;
	
	
	float movX;
	float movY;
	
	double homX;
	double homY;
	
};

#endif
