/*
 *  GoodFeaturesToTrack.h
 *  RoomBeyondController
 *
 *  Created by m9dfukc on 01.07.10.
 *  Copyright 2010 invertednothing. All rights reserved.
 *
 */

#include "ofMain.h"
#include "ofxOpenCv.h"

struct tStablePoint
{
	CvPoint2D32f src_point;
	CvPoint2D32f dst_point;
};

class GoodFeaturesToTrack {
	public:
		GoodFeaturesToTrack();
		void reset();
		void setup(int _imgWidth, int _imgHeight);
		ofPoint trackPoints(ofImage* _image);
		
		ofxCvColorImage		colorImg;
		ofxCvGrayscaleImage grayImage;
		
		static const int MAX_COUNT = 1000;
		vector<tStablePoint> stable_points;
		CvPoint2D32f* act_points;
		
		double		initX; 
		double		initY; 
		double		flt_vert;
		double		flt_horiz;
	
		int			imgWidth;
		int			imgHeight;
	
	private:
		int			counter;
};
