/*
 *  Tracker.h
 *  SpaceBeyond
 *
 *  Created by m9dfukc on 12.06.10.
 *  Copyright 2010 invertednothing. All rights reserved.
 *
 */


#ifndef _TRACKER
#define _TRACKER

#include "ofMain.h"
#include "ofxDirList.h"
#include "CAHVFile.h"
#include "GoodFeaturesToTrack.h"

class Tracker : public CAHVFile, public GoodFeaturesToTrack {
	
public:
	
	Tracker();
	void setup(string _scene, string posFile = "positions.txt", string imgDir = "images");
	
	bool prevFrame();
	bool nextFrame();
	void resetFrames();
	void toConsole();
	void setInternalTracking(bool mode);
	void setPosXY(int _x, int _y);
	void setScale(float _scale);
	void setScaleFactor(float _scaleFactor);
	int getFrame();
	int getFrameCount();
	void draw();
	void saveToFile(int sceneNum);
	ofPoint getPosition();
	ofRectangle getBoundary();
	
	ofPoint		position;
	ofPoint		absolutePosition;
	
	float		scale;
	float		scaleFactor;
	
	int			imgWidth;
	int			imgHeight;
	
	bool		finished;
	bool		trackingMode;
	
	int			counter;
	
private:
	bool update(int step);
	
	bool		trackingModeLastFrame;
	
	int			curFrame;
	int 		nImages;
	ofImage	 	image;
	ofImage	 	trackingImage;
	string		imgPath;
	string		savePosPath;
	
	ofxDirList  DIR;
	
	float newX;
	float newY;
	
	float incPosX;
	float incPosY;
	
	float newX_;
	float newY_;
	
	ofRectangle boundary;
	
};

#endif