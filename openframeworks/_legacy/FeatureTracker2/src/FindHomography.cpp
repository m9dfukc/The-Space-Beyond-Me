/*
 *  findHomography.cpp
 *  opencvExample
 *
 *  Created by m9dfukc on 28.01.10.
 *  Copyright 2010 __invertednothing__. All rights reserved.
 *
 */
/*
#include "FindHomography.h"

void FindHomography::update(){
	
	// cvFindHomography: Not working somehow
	/*
	 CvMat *tst =cvCreateMat(3, 3, CV_64FC1);
	 CvMat *src_points = cvCreateMat(stable_points.size(),3, CV_64FC1);
	 CvMat *dst_points = cvCreateMat(stable_points.size(),3, CV_64FC1);
	 for(int i=0; i<stable_points.size(); i++)
	 {
	 cvmSet(src_points,i,0,stable_points[i].src_point.x);
	 cvmSet(src_points,i,1,stable_points[i].src_point.y);
	 cvmSet(src_points,i,2,1);
	 cvmSet(dst_points,i,0,stable_points[i].dst_point.x);
	 cvmSet(dst_points,i,1,stable_points[i].dst_point.y);
	 cvmSet(dst_points,i,2,1);
	 }
	 */
	/*
	 CvMat *tst =cvCreateMat(3, 3, CV_32FC1);
	 CvMat *src_points = cvCreateMat(4,3, CV_32FC1);
	 CvMat *dst_points = cvCreateMat(4,3, CV_32FC1);
	 
	 int tx=34;
	 int ty=-17;
	 float zoom=1.6;
	 
	 
	 cvmSet(src_points,0,0,0);
	 cvmSet(src_points,0,1,0);
	 cvmSet(src_points,0,2,1);
	 cvmSet(dst_points,0,0,zoom*0+tx);
	 cvmSet(dst_points,0,1,zoom*0+ty);
	 cvmSet(dst_points,0,2,1);
	 
	 cvmSet(src_points,1,0,10);
	 cvmSet(src_points,1,1,0);
	 cvmSet(src_points,1,2,1);
	 cvmSet(dst_points,1,0,zoom*10+tx);
	 cvmSet(dst_points,1,1,zoom*0+ty);
	 cvmSet(dst_points,1,2,1);
	 
	 cvmSet(src_points,2,0,10);
	 cvmSet(src_points,2,1,10);
	 cvmSet(src_points,2,2,1);
	 cvmSet(dst_points,2,0,zoom*10+tx);
	 cvmSet(dst_points,2,1,zoom*10+ty);
	 cvmSet(dst_points,2,2,1);
	 
	 cvmSet(src_points,3,0,0);
	 cvmSet(src_points,3,1,10);
	 cvmSet(src_points,3,2,1);
	 cvmSet(dst_points,3,0,zoom*0+tx);
	 cvmSet(dst_points,3,1,zoom*10+ty);
	 cvmSet(dst_points,3,2,1);
	 */
	/*
	 
	 CvMat *rnd_src_points = cvCreateMat(4,3, CV_64FC1);
	 CvMat *rnd_dst_points = cvCreateMat(4,3, CV_64FC1);
	 
	 for(int i=0; i<4; i++)
	 {
	 int rand_point = rand() % stable_points.size();
	 cvmSet(rnd_src_points,i,0,stable_points[rand_point].src_point.x);
	 cvmSet(rnd_src_points,i,1,stable_points[rand_point].src_point.y);
	 cvmSet(rnd_src_points,i,2,1);
	 cvmSet(rnd_dst_points,i,0,stable_points[rand_point].dst_point.x);
	 cvmSet(rnd_dst_points,i,1,stable_points[rand_point].dst_point.y);
	 cvmSet(rnd_dst_points,i,2,1);
	 }
	 
	 cvFindHomography(rnd_src_points, rnd_dst_points, tst, CV_RANSAC, 20);
	 
	 
	 for (int i = 0; i < 3; i++) {
	 std::cout << i << ":: " << cvmGet(tst,i,0) << ", " << cvmGet(tst,i,1) << ", " << cvmGet(tst,i,2) << std::endl;
	 }
	 std::cout << " " << std::endl;
	 
	 
	 homX = cvmGet(tst,0,2)/cvmGet(tst,2,2); 
	 homY = cvmGet(tst,1,2)/cvmGet(tst,2,2);
	 
	 initX += homX*-1;
	 initY += homY*-1;
	 
	 ofDrawBitmapString("hom horiz/vert: "+ofToString(homX)+"/"+ofToString(homY), 20, 100);
	 
	 
	 cvReleaseMat(&src_points);
	 cvReleaseMat(&dst_points);
	 
	 cvReleaseMat(&rnd_src_points);
	 cvReleaseMat(&rnd_dst_points);
	 
	 cvReleaseMat(&tst);
	 */
//}
