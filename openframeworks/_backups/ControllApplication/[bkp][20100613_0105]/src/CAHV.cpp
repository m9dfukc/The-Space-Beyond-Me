/*
 *  CAHV.cpp
 *  SpaceBeyond
 *
 *  Created by m9dfukc on 12.06.10.
 *  Copyright 2010 invertednothing. All rights reserved.
 *
 */

#include "CAHV.h"


void CAHV::setup(string _scene, string posFile, string imgDir) {
	CAHVFile::setup(_scene);
	imgPath = _scene + "/" + imgDir;
	savePosPath = _scene + ".txt";
	
	DIR.allowExt("jpg");
    nImages = DIR.listDir(imgPath);
	
	if (nImages != size) {
		cout << "count of images doesn't match with count of corresponding tracking points!" << endl; 
	}
	
	curFrame	= -1;
	imgWidth	= 0;
	imgHeight	= 0;
	scale		= 0.3;
	scaleFactor = 1.0;
	position.set(0, 0, 0);
	
}

bool CAHV::prevFrame() {
	return update(-1);
}

bool CAHV::nextFrame() {
	return update(+1);
}

void CAHV::resetFrames() {
	curFrame = -1;
}

void CAHV::toConsole() {
	// TODO
}

void CAHV::setPosXY(int _x, int _y) {
	absolutePosition.set(_x, _y);	
}

void CAHV::setScale(float _scale) {
	
}

int CAHV::getFrame() {
	return (curFrame+1);
}

void CAHV::draw() {
	
	float aX = absolutePosition.x-(image.getWidth()*absolutePosition.z)  / 2;
	float aY = absolutePosition.y-(image.getHeight()*absolutePosition.z) / 2; 
	if (curFrame > -1) image.draw(aX, aY, image.getWidth()*absolutePosition.z, image.getHeight()*absolutePosition.z);
}

void CAHV::saveToFile(int sceneNum) {
	
	/*
	
	string buffer = "";
	
	for (int i = 0; i < absolutePosition.size(); i++) {
		buffer += ofToString(1) + " ";
		buffer += ofToString((int) (i+1)) + " ";
		buffer += ofToString((int) absolutePosition.x) + " ";
		buffer += ofToString((int) cahv.absolutePosition.y) + " ";
		buffer += ofToString((float) cahv.absolutePosition.z) + " ";
		buffer += "\n";
	}
	
	myFile.open(ofToDataPath(savePosPath).c_str(), std::ios::out | std::ios::trunc);
	if (myFile.is_open()) {
		myFile << buffer;
		myFile.close();
	}
	cout << "Positions saved to textfile" << endl;
	 
	*/
}

bool CAHV::update(int step) {
	
	
	
	if (curFrame < -1 || curFrame >= size - 1) return false;
	curFrame += step;
	
	// cout << "update called in frame: " << curFrame << endl; 
	
	if (curFrame <= nImages) { 
		
		// cout << "load image: " << DIR.getPath(curFrame) << endl;
		
		image.loadImage(DIR.getPath(curFrame));
		//image.setAnchorPercent(50, 50);
		imgWidth  = image.getWidth();
		imgHeight = image.getHeight();
	}
	position = positions[curFrame];
	
	newX = ((position.x / position.z) * scale);
	newY = ((position.y / position.z) * scale);
	
	incPosX = (newX_ - newX);
	incPosY = (newY_ - newY);
	
	newX_ = newX;
	newY_ = newY;
	
	float posX = absolutePosition.x;
	float posY = absolutePosition.y;
	
	posX += incPosX;
	posY += incPosY;
	
	float _z = scale / position.z;
	
	absolutePosition.set(posX, posY, _z);
	// cout << "posX: "<< posX << " posY: " << posY << " scale: " << _z << endl;
	
	
}

