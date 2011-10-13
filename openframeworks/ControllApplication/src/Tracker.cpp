/*
 *  Tracker.cpp
 *  SpaceBeyond
 *
 *  Created by m9dfukc on 12.06.10.
 *  Copyright 2010 invertednothing. All rights reserved.
 *
 */

#include "Tracker.h"

Tracker::Tracker() {
	counter = 0;
}


void Tracker::setup(string _scene, string posFile, string imgDir) {
	CAHVFile::setup(_scene);
	GoodFeaturesToTrack::setup(641, 360);
	
	imgPath = _scene + "/" + imgDir;
	savePosPath = _scene + ".txt";
	
	DIR.allowExt("jpg");
    nImages = DIR.listDir(imgPath);
	
	if (nImages != size) {
		cout << "count of images doesn't match with count of corresponding tracking points!" << endl; 
	}
	
	
	finished	= false;
	curFrame	= -1;
	imgWidth	= 0;
	imgHeight	= 0;
	scale		= 0.20;
	scaleFactor = 1.0;
	
	if (counter == 0) {
		trackingImage.allocate(641, 360, OF_IMAGE_COLOR_ALPHA);
		position.set(0, 0, 1);
		absolutePosition.set(0, 0, 1);
		trackingMode = trackingModeLastFrame = false;
		cout << "set initial position" << endl;
	} else {
		cout << "take over last position with and reset scale" << endl;
		position.set(position.x, position.y, 1);
		absolutePosition.set(absolutePosition.x, absolutePosition.y, 1);
	}
	
	counter++;
}

bool Tracker::prevFrame() {
	return update(-1);
}

bool Tracker::nextFrame() {
	return update(+1);
}

void Tracker::resetFrames() {
	if (trackingMode) GoodFeaturesToTrack::reset();
		
	finished = false;
	curFrame = -1;
}

void Tracker::setInternalTracking(bool mode) {
	//resetFrames();
	trackingMode = mode;
}

void Tracker::toConsole() {
	// TODO
}

void Tracker::setPosXY(int _x, int _y) {
	absolutePosition.set(_x, _y);	
}

void Tracker::setScale(float _scale) {
	if (_scale > 0.15 && _scale < 1) scale = _scale;
	cout << "image scale: " << _scale << endl;
}

int Tracker::getFrame() {
	return (curFrame+1);
}

int Tracker::getFrameCount() {
	return (size+1);
}

void Tracker::draw() {
	// center image in variables aX and aY
	float aX = absolutePosition.x-(image.getWidth()*absolutePosition.z)  / 2;
	float aY = absolutePosition.y-(image.getHeight()*absolutePosition.z) / 2; 
	cout << aX << "   " << aY << "   " << image.getHeight()*absolutePosition.z << endl;
	
	float aZ = (absolutePosition.z > 0.11) ? absolutePosition.z : 0.11;
	aZ = (aZ < 1.0) ? aZ : 1.0;
	if (curFrame > -1) image.draw(aX, aY, image.getWidth()*aZ, image.getHeight()*aZ);
}

void Tracker::saveToFile(int sceneNum) {
	
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

ofPoint Tracker::getPosition() {
	float posX = absolutePosition.x;
	float posY = absolutePosition.y;
	float posZ = absolutePosition.z;	
	return ofPoint(posX, posY, posZ);
}

ofRectangle Tracker::getBoundary() {
	if ( finished ) {
		return boundary;
	} else {
		cout << "scene not finished yet!" << endl;
		return ofRectangle(0,0,0,0);
	}
}

bool Tracker::update(int step) {
	
	bool imgLoaded = false;
	
	if (curFrame >= size - 1) finished = true;
	if (curFrame < -1 || curFrame >= size - 1) return false;
	
	curFrame += step;
	
	if (curFrame < nImages) { 
		
		cout << "load image: " << DIR.getPath(curFrame) << endl;
		
		imgLoaded = image.loadImage(DIR.getPath(curFrame));
		imgWidth  = image.getWidth();
		imgHeight = image.getHeight();
	}
	
	cout << "FRAMENR: " << curFrame << " STEP:" << endl;
	
	if (curFrame == 0) {
		/* relativ ... funktiiniert gerade nicht 
		position = positions[curFrame] + position;
		 */
		position = positions[curFrame];
	}
	
	if (trackingMode) {
		if (imgLoaded) {
			trackingImage.resize(image.getWidth(), image.getHeight());
			trackingImage = image;
			trackingImage.setImageType(OF_IMAGE_COLOR);
			
			ofPoint relPos = GoodFeaturesToTrack::trackPoints(&trackingImage);
			position = position + relPos;
			cout << "INTERNAL:   x " << position.x << "   y " << position.y << "   z " << position.z << endl;
		}
	} else {
		/* relativ ... funktiiniert gerade nicht 
		position = position + positions[curFrame]; 
		 */
		if (trackingModeLastFrame != trackingMode) {
			scaleFactor = position.z / positions[curFrame].z;
		}
		position = positions[curFrame] * scaleFactor;
		
	}
	
	trackingModeLastFrame = trackingMode;
	
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
	
	//if (_z < 0.11) _z = 0.11;
	
	absolutePosition.set(posX, posY, _z);
	cout << "posX: "<< posX << " posY: " << posY << " scale: " << _z << endl;
	
	
	if ( curFrame == 0 ) {
		boundary.x = absolutePosition.x - (image.getWidth()*absolutePosition.z)  / 2; 
		boundary.y = absolutePosition.y - (image.getHeight()*absolutePosition.z) / 2;
		boundary.width  = imgWidth*_z;
		boundary.height = imgHeight*_z;
	} else {
		if (boundary.x > absolutePosition.x - (image.getWidth()*absolutePosition.z)  / 2 ) boundary.x = absolutePosition.x - (image.getWidth()*absolutePosition.z)  / 2;
		if (boundary.y > absolutePosition.y - (image.getHeight()*absolutePosition.z) / 2 ) boundary.y = absolutePosition.y - (image.getHeight()*absolutePosition.z) / 2;
		if (boundary.width  < absolutePosition.x - (image.getWidth()*absolutePosition.z)  / 2 + (imgWidth*_z)  - boundary.x) boundary.width  = absolutePosition.x - (image.getWidth()*absolutePosition.z)  / 2 + (imgWidth*_z)  - boundary.x;
		if (boundary.height < absolutePosition.y - (image.getHeight()*absolutePosition.z) / 2 + (imgHeight*_z) - boundary.y) boundary.height = absolutePosition.y - (image.getHeight()*absolutePosition.z) / 2 + (imgHeight*_z) - boundary.y;
	}
	
	return true;
	
}

