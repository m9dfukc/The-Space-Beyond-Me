/*
 *  CAHV.h
 *  SpaceBeyond
 *
 *  Created by m9dfukc on 12.06.10.
 *  Copyright 2010 invertednothing. All rights reserved.
 *
 */


#ifndef _CAHV
#define _CAHV

#include "ofMain.h"
#include "ofxDirList.h"
#include "CAHVFile.h"
#include "ofxFBOTexture.h"

class CAHV : public CAHVFile {
	
public:
	
	CAHV();
	void setup(string _scene, string posFile = "positions.txt", string imgDir = "images");
	
	bool prevFrame();
	bool nextFrame();
	void resetFrames();
	void toConsole();
	void setPosXY(int _x, int _y);
	void setScale(float _scale);
	void setScaleFactor(float _scaleFactor);
	int getFrame();
	void draw();
	void saveToFile(int sceneNum);
	ofPoint getPosition();
	
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
	
	int			curFrame;
	int 		nImages;
	ofImage	 	image;
	string		imgPath;
	string		savePosPath;
	ofxFBOTexture myFBO;
	
	ofxDirList  DIR;
	
	float newX;
	float newY;
	
	float incPosX;
	float incPosY;
	
	float newX_;
	float newY_;
	
	
};

#endif