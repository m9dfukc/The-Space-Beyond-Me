/*
 *  FeaturesTracker.cpp
 *  RoomBeyondController
 *
 *  Created by m9dfukc on 01.07.10.
 *  Copyright 2010 invertednothing. All rights reserved.
 *
 */

#include "FeatureTracker.h"

FeatureTracker::FeatureTracker() {
	counter = 0;
}


void FeatureTracker::setup(string _scene, int _imgWidth, int _imgHeight, string imgDir) {

	imgPath = _scene + "/" + imgDir;
	savePosPath = _scene + ".txt";
	
	DIR.allowExt("jpg");
    nImages = DIR.listDir(imgPath);

	flt_vert	= 0.0;
	flt_horiz	= 0.0;
	
	finished	= false;
	curFrame	= -1;
	imgWidth	= _imgWidth;
	imgHeight	= _imgHeight;
	scale		= 0.20;
	scaleFactor = 1.0;
	if (counter == 0) {
		position.set(0, 0, 0);
		absolutePosition.set(0, 0, 0);
		cout << "set initial position" << endl;
	} else {
		cout << "take over last position with and reset scale" << endl;
		position.set(position.x, position.y, 1);
		absolutePosition.set(absolutePosition.x, absolutePosition.y, 1);
	}
	
	counter++;
	
	colorImg.allocate(imgWidth, imgHeight);
	grayImage.allocate(imgWidth, imgHeight);
}

bool FeatureTracker::prevFrame() {
	return update(-1);
}

bool FeatureTracker::nextFrame() {
	return update(+1);
}

void FeatureTracker::resetFrames() {
	finished = false;
	curFrame = -1;
}

void FeatureTracker::setPosXY(int _x, int _y) {
	absolutePosition.set(_x, _y);	
}

void FeatureTracker::setScale(float _scale) {
	if (_scale > 0.15 && _scale < 1) scale = _scale;
	cout << "image scale: " << _scale << endl;
}

int FeatureTracker::getFrame() {
	return (curFrame+1);
}

int FeatureTracker::getFrameCount() {
	return (nImages+1);
}

void FeatureTracker::draw() {
	// center image in variables aX and aY
	float aX = absolutePosition.x-(image.getWidth()*absolutePosition.z)  / 2;
	float aY = absolutePosition.y-(image.getHeight()*absolutePosition.z) / 2; 
	if (curFrame > -1) image.draw(aX, aY, image.getWidth()*absolutePosition.z, image.getHeight()*absolutePosition.z);
}

ofPoint FeatureTracker::getPosition() {
	float posX = absolutePosition.x;
	float posY = absolutePosition.y;
	float posZ = absolutePosition.z;	
	return ofPoint(posX, posY, posZ);
}

ofRectangle FeatureTracker::getBoundary() {
	if ( finished ) {
		return boundary;
	} else {
		cout << "scene not finished yet!" << endl;
		return ofRectangle(0,0,0,0);
	}
}

ofPoint FeatureTracker::trackPoints(unsigned char* imgPixels) {
	
	colorImg.setFromPixels(imgPixels, imgWidth, imgHeight);
	grayImage = colorImg;
	
	/* find good points to track */
	double quality = 0.1;
	double min_distance = 15;
	count = MAX_COUNT;
	int win_size = 10;
	
	IplImage* eig = cvCreateImage( cvGetSize(grayImage.getCvImage()), 32, 1 );
	IplImage* temp = cvCreateImage( cvGetSize(grayImage.getCvImage()), 32, 1 );

	act_points = (CvPoint2D32f*)cvAlloc(MAX_COUNT*sizeof(CvPoint2D32f));
	cvGoodFeaturesToTrack( grayImage.getCvImage(), eig, temp, act_points, &count, quality, min_distance, 0, 3, 0, 0.04 );
	
	cvReleaseImage( &eig );
	cvReleaseImage( &temp );
	
	/* Match them with the array of stable points to prevent too much temporal hopping */
	if (stable_points.size() == 0) {
		for(int i=0; i<count; i++) {
			tStablePoint pt;
			pt.src_point = act_points[i];
			stable_points.push_back(pt);
		}
	}
	
	for(vector<tStablePoint>::iterator it = stable_points.begin(); it!=stable_points.end();) {
		/* find best matching point in last imgae */
		float mindist = 1000000;
		int bestInd = -1;
		for(int i=0; i<count; i++) {
			float dx = act_points[i].x-it->src_point.x;
			float dy = act_points[i].y-it->src_point.y;
			float dist = sqrt(dx*dx+dy*dy);
			if (dist < mindist) {
				mindist = dist;
				bestInd=i;
			}
		}
		
		if (mindist<16)	{
			it->dst_point = act_points[bestInd];
			it++;
		} else {
			it = stable_points.erase(it);
		}
	}
	
	
	if (stable_points.size() >= 4) {
		
		double avg_dx = 0;
		double avg_dy = 0;
		double avg_zoom = 1;
		
		for(int i=0; i<stable_points.size(); i++) {
			avg_dx += (stable_points[i].dst_point.x - stable_points[i].src_point.x);
			avg_dy += (stable_points[i].dst_point.y - stable_points[i].src_point.y);
			
		}
		
		avg_dx/= (double)stable_points.size();
		avg_dy/= (double)stable_points.size();
		
		/* majority estimation - filter the outliers */
		double* weight = new double[stable_points.size()]; 
		for(int i=0; i<stable_points.size(); i++) {
			weight[i]=1;
		}
		double sum_weight =  0;
		
		for (int iter=0; iter<20; iter++) {
			avg_dx = 0;
			avg_dy = 0;
			double sum_weight =  0;
			for(int i=0; i<stable_points.size(); i++) {
				avg_dx += weight[i] * (stable_points[i].dst_point.x - stable_points[i].src_point.x);
				avg_dy += weight[i] * (stable_points[i].dst_point.y - stable_points[i].src_point.y);
				sum_weight+=weight[i];
			}
			avg_dx/= (double)sum_weight;
			avg_dy/= (double)sum_weight;
			for(int i=0; i<stable_points.size(); i++) {
				double dx = avg_dx - (stable_points[i].dst_point.x - stable_points[i].src_point.x);
				double dy = avg_dy - (stable_points[i].dst_point.y - stable_points[i].src_point.y);
				double dst = sqrt(dx*dx+dy*dy);
				weight[i]*=1/(1+dst);
			}
		}
		delete [] weight;
		
		float ofactor = 0.88;
		float nfactor = 0.19;
		
		/* actual offset is calculated here - 
		 ** mixed up with interpolation data from the last frame */
		flt_horiz = ofactor*flt_horiz + nfactor*avg_dx;
		flt_vert = ofactor*flt_vert + nfactor*avg_dy;
		
	}
	
	return ofPoint(flt_horiz, flt_vert, 1.0);
}

bool FeatureTracker::update(int step) {
	
	if (curFrame >= nImages - 1) finished = true;
	if (curFrame < -1 || curFrame >= nImages - 1) return false;
	curFrame += step;
	
	if (curFrame < nImages) { 
		
		cout << "load image: " << DIR.getPath(curFrame) << endl;
		
		image.loadImage(DIR.getPath(curFrame));
		imgWidth  = image.getWidth();
		imgHeight = image.getHeight();
	}

	trackPoints(image.getPixels());
	
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
	
	
}

