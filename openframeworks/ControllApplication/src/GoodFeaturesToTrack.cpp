/*
 *  GoodFeaturesToTrack.cpp
 *  RoomBeyondController
 *
 *  Created by m9dfukc on 01.07.10.
 *  Copyright 2010 invertednothing. All rights reserved.
 *
 */

#include "GoodFeaturesToTrack.h"

GoodFeaturesToTrack::GoodFeaturesToTrack() {
	counter = 0;
}

void GoodFeaturesToTrack::setup(int _imgWidth, int _imgHeight) {
	
	imgWidth		= _imgWidth;
	imgHeight		= _imgHeight;
	
	/* ammount of relative horizontal and 
	 ** vertical movement per frame */
	flt_horiz = 0; 
	flt_vert = 0;

	/* sum of stage and relative positions */
	initX = 0;
	initY = 0;

	
	//colorImg.clear();
	//grayImage.clear();
	
	if (counter == 0) {
		colorImg.allocate(_imgWidth, _imgHeight);
		grayImage.allocate(_imgWidth, _imgHeight);
	}
	
	counter++;
}

void GoodFeaturesToTrack::reset() {
	flt_horiz = 0; 
	flt_vert = 0;
	
	initX = 0;
	initY = 0;
}

ofPoint GoodFeaturesToTrack::trackPoints(ofImage* _image) {
	
	colorImg.resize(_image->getWidth(), _image->getHeight());
	grayImage.resize(_image->getWidth(), _image->getHeight());
	
	colorImg.setFromPixels(_image->getPixels(), _image->getWidth(), _image->getHeight());
	grayImage = colorImg;
	//grayImage.flagImageChanged();
	
	//colorImg.draw(390, 200);
	
	/* find good points to track */
	double quality = 0.1;
	double min_distance = 15;
	int count = MAX_COUNT;
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
		
		/* make it absolute */
		//initX += flt_horiz;
		//initY += flt_vert;
		
		//cout << "VOM TRACKER: " << initX << "," << initY << endl;
	}
	
	stable_points.clear();
	for(int i=0; i<count; i++) {
		tStablePoint pt;
		pt.src_point = act_points[i];
		stable_points.push_back(pt);
	}
	
	return ofPoint((float)flt_horiz, (float)flt_vert, 0);
	
}
