/*
 *  FeaturesTracker.h
 *  RoomBeyondController
 *
 *  Created by m9dfukc on 01.07.10.
 *  Copyright 2010 invertednothing. All rights reserved.
 *
 */


#ifndef _FETR
#define _FETR

#include "ofMain.h"
#include "ofxOpenCv.h"
#include "ofxDirList.h"

struct tStablePoint
{
	CvPoint2D32f src_point;
	CvPoint2D32f dst_point;
};

class FeatureTracker {
	
public:
	FeatureTracker();
	
	void setup(string _scene, int _imgWidth = 641, int _imgHeight = 360, string imgDir = "images");
	
	bool prevFrame();
	bool nextFrame();
	void resetFrames();
	void setPosXY(int _x, int _y);
	void setScale(float _scale);
	void setScaleFactor(float _scaleFactor);
	int getFrame();
	int getFrameCount();
	void draw();

	ofPoint getPosition();
	ofRectangle getBoundary();
	
	ofPoint		position;
	ofPoint		absolutePosition;
	
	float		scale;
	float		scaleFactor;
	
	int			imgWidth;
	int			imgHeight;
	
	bool		finished;
	int			counter;
	
private:
	bool update(int step);
	ofPoint trackPoints(unsigned char* imgPixels);
	
	int			curFrame;
	int 		nImages;
	ofImage	 	image;
	string		imgPath;
	string		savePosPath;
	
	ofxDirList  DIR;
	
	ofxCvColorImage		colorImg;
	ofxCvGrayscaleImage grayImage;
	
	static const int MAX_COUNT = 1000;
	vector<tStablePoint> stable_points;
	CvPoint2D32f* act_points;
	
	double flt_vert;
	double flt_horiz;
	
	float newX;
	float newY;
	
	float incPosX;
	float incPosY;
	
	float newX_;
	float newY_;
	
	int	  count;
	
	ofRectangle boundary;

};

#endif

