#include "testApp.h"

//--------------------------------------------------------------
void testApp::setup()
{
	//640 x 360
	ofSetFrameRate(100);
	ofSetBackgroundAuto(false);
	ofBackground(0,0,0);
	ofEnableAlphaBlending();
	
#ifdef _USE_LIVE_VIDEO
	vidGrabber.setVerbose(true);
	vidGrabber.initGrabber(160,120);
#else
	DIR.allowExt("mov");
	//DIR.setVerbose(false);
	numFiles = DIR.listDir("videos/");
	vidIndex = 0;
	
	vidPlayer.loadMovie("videos/"+DIR.getName(vidIndex));
	vidPlayer.play();
#endif

	colorImg.allocate(640,360);
	grayImage.allocate(640,360);
	//grayImageLast.allocate(640,360);
	//grayImageDifference.allocate(640,360);
	
	flt_horiz = 0;
	flt_vert = 0 ;
	
	saveImgCount = 0;
	
	factor = 0.3;
	blur = 100;
	
	movX = 1; movY = 1; homX = 0; homY = 0;
	
	initX, initializedX = (ofGetWidth()/2-640*factor/2)/factor;
	initY, initializedY = (ofGetHeight()/2-360*factor/2)/factor;
	
	movement_prev = 0.0; scene = 0;
}

//--------------------------------------------------------------
void testApp::update()
{
//	ofBackground(100,100,100);

	bool bNewFrame = false;
#ifdef _USE_LIVE_VIDEO
	vidGrabber.grabFrame();
	bNewFrame = vidGrabber.isFrameNew();
#else
	vidPlayer.idleMovie();
	bNewFrame = vidPlayer.isFrameNew();
#endif

	if (bNewFrame){
#ifdef _USE_LIVE_VIDEO
		colorImg.setFromPixels(vidGrabber.getPixels(), 640,360);
#else
		colorImg.setFromPixels(vidPlayer.getPixels(), 640,360);
#endif
		
		//std::cout << vidPlayer.get
		grayImage = colorImg;
		grayImage.mirror(false, false);
		
		
		
		/*
		if (!grayImageLast.getCvImage()) {
			grayImageLast = grayImage;
		}
		
		
		cvAbsDiff(grayImage.getCvImage(), grayImageLast.getCvImage(), grayImageDifference.getCvImage());
		cvThreshold(grayImageDifference.getCvImage(), grayImageDifference.getCvImage(), 20, 255, CV_THRESH_BINARY);
		float movement = cvSum(grayImageDifference.getCvImage()).val[0] / (640*360);
		

		if (movement > movement_prev * 10) {
			scene++;
			//std::cout << "scene: " << scene << std::endl;
		}

		
		movement_prev = movement;
		grayImageLast = grayImage;
		*/
		
	}
	
	
	/* reset the videostream */
	if(vidPlayer.getPosition() >= 0.99) { 
		cPress = 1;
		homX = 0; homY = 0;
		//ofImage img;
		//img.allocate(ofGetWidth(), ofGetHeight(), OF_IMAGE_COLOR);
		//img.grabScreen(0,0,ofGetWidth(), ofGetHeight());
		//img.saveImage("screenshots/shoot_"+ofToString(saveImgCount)+".jpg");
		//saveImgCount++;
	}
	
	//std::cout << vidPlayer.getPosition() << std::endl;
	
	
}

//--------------------------------------------------------------
void testApp::draw()
{
	
	// Draw static stuff
	ofSetColor(255,255,255,blur);
	grayImage.draw(initX*factor, initY*factor, 640*factor, 360*factor);
	//ofDrawBitmapString("capture  : "+ofToString((int) ofGetFrameRate())+" fps", 20, 20);
	
	ofSetColor(0);
	ofRect(15, 25, 385, 90);
	ofSetColor(255);
	ofDrawBitmapString("blur amount +/- (0-255): "+ofToString(blur), 20, 40);
	ofDrawBitmapString("play videofile n/p: "+DIR.getName(vidIndex)+" ("+ofToString(vidIndex+1)+"/"+ofToString(numFiles)+")", 20, 55);
	ofDrawBitmapString("video zoomfactor i/o: "+ofToString(factor).substr(0,4), 20, 70);
	ofDrawBitmapString("horizontal/vertical factor q/e & y/c: "+ofToString(movX).substr(0,4)+"/"+ofToString(movY).substr(0,4), 20, 85);
	

	
    IplImage* eig = cvCreateImage( cvGetSize(grayImage.getCvImage()), 32, 1 );
    IplImage* temp = cvCreateImage( cvGetSize(grayImage.getCvImage()), 32, 1 );

	double quality = 0.10;
	double min_distance = 16;
	int count = MAX_COUNT;
	int win_size = 1;

	// Retrieve features of actual frame
	CvPoint2D32f* act_points = (CvPoint2D32f*)cvAlloc(MAX_COUNT*sizeof(CvPoint2D32f));
	cvGoodFeaturesToTrack( grayImage.getCvImage(), eig, temp, act_points, &count, quality, min_distance, 0, 3, 0, 0.04 );
	//cvFindCornerSubPix( grayImage.getCvImage(), act_points, count, cvSize(win_size,win_size), cvSize(-1,-1), cvTermCriteria(CV_TERMCRIT_ITER|CV_TERMCRIT_EPS,20,0.03));

	
	cvReleaseImage( &eig );
    cvReleaseImage( &temp );

	// Match them with the array of stable points to prevent too much temporal hopping
	if (stable_points.size() == 0)
	{
		for(int i=0; i<count; i++)
		{
			tStablePoint pt;
			pt.src_point = act_points[i];
			stable_points.push_back(pt);
		}
	}
	
	for(vector<tStablePoint>::iterator it = stable_points.begin(); it!=stable_points.end();)
	{
		// find best matching point in last imgage
		float mindist =1000000;
		int bestInd = -1;
		for(int i=0; i<count; i++)
		{
			float dx = act_points[i].x-it->src_point.x;
			float dy = act_points[i].y-it->src_point.y;
			float dist = sqrt(dx*dx+dy*dy);
			if (dist < mindist)
			{
				mindist = dist;
				bestInd=i;
			}
		}

		if (mindist<15)
		{
			it->dst_point = act_points[bestInd];
			it++;
		} else
		{
			it = stable_points.erase(it);
		}
	}
	

	if (stable_points.size() > 4)
	{
		
		
		//cvFindHomography: Not working somehow
		/*
		CvMat *tst =cvCreateMat(3, 3, CV_64FC2);
		CvMat *src_points = cvCreateMat(stable_points.size(),3, CV_64FC2);
		CvMat *dst_points = cvCreateMat(stable_points.size(),3, CV_64FC2);
		for(int i=0; i<stable_points.size(); i++)
		{
			cvmSet(src_points,i,0,stable_points[i].src_point.x);
			cvmSet(src_points,i,1,stable_points[i].src_point.y);
			cvmSet(dst_points,i,0,stable_points[i].dst_point.x);
			cvmSet(dst_points,i,1,stable_points[i].dst_point.y);
		}
		
		
		//cvFindHomography(src_points, dst_points, tst, CV_RANSAC, 5);
		
		
		for (int i = 0; i < 3; i++) {
			std::cout << i << ":: " << cvmGet(tst,i,0) << ", " << cvmGet(tst,i,1) << ", " << cvmGet(tst,i,2) << std::endl;
		}
		std::cout << " " << std::endl;

		 
		homX = cvmGet(tst,0,2)/cvmGet(tst,2,2); 
		homY = cvmGet(tst,1,2)/cvmGet(tst,2,2);
		ofDrawBitmapString("hom horiz/vert: "+ofToString(homX)+"/"+ofToString(homY), 20, 100);
		

		cvReleaseMat(&src_points);
		cvReleaseMat(&dst_points);
		cvReleaseMat(&tst);
		
		*/
	
		double avg_dx = 0;
		double avg_dy = 0;
		double avg_zoom;
		
		//std::cout  << stable_points[0].src_point.x << std::endl;
		for(int i=0; i<stable_points.size(); i++)
		{
			avg_dx += (stable_points[i].dst_point.x - stable_points[i].src_point.x);
			avg_dy += (stable_points[i].dst_point.y - stable_points[i].src_point.y);
		}
		avg_dx/= (double)stable_points.size();
		avg_dy/= (double)stable_points.size();
		
		
		// majority estimation
		double* weight = new double[stable_points.size()]; 
		for(int i=0; i<stable_points.size(); i++)
		{
			weight[i]=1;
		}
		double sum_weight =  0;
		 
		 
		for (int iter =0; iter<10; iter++)
		{
			avg_dx = 0;
			avg_dy = 0;
			double sum_weight =  0;
			for(int i=0; i<stable_points.size(); i++)
			{
				avg_dx += weight[i] * (stable_points[i].dst_point.x - stable_points[i].src_point.x);
				avg_dy += weight[i] * (stable_points[i].dst_point.y - stable_points[i].src_point.y);
				sum_weight+=weight[i];
			}
			avg_dx/= (double)sum_weight;
			avg_dy/= (double)sum_weight;
			for(int i=0; i<stable_points.size(); i++)
			{
				double dx = avg_dx - (stable_points[i].dst_point.x - stable_points[i].src_point.x);
				double dy = avg_dy - (stable_points[i].dst_point.y - stable_points[i].src_point.y);
				double dst = sqrt(dx*dx+dy*dy);
				weight[i]*=1/(1+dst);
			}

		}

		
		
		flt_horiz = 0.9*flt_horiz + (0.1*movX)*avg_dx;
		flt_vert = 0.9*flt_vert + (0.1*movY)*avg_dy;
		//ofDrawBitmapString("horiz: "+ofToString(flt_horiz), 20, 40);
		//ofDrawBitmapString("vert: "+ofToString(flt_vert), 20, 60);
		
		
		initX += flt_horiz*-1;//homX*-1;
		initY += flt_vert*-1;//homY*-1; 
		
		float colorScale = 0.35;
		
		ofSetColor(0);
		ofRect(0, ofGetHeight()-colorImg.getHeight()*colorScale-30, colorImg.getWidth()*colorScale+30, colorImg.getHeight()*colorScale+30);
		
		ofSetColor(255,255,255,255);
		colorImg.draw(15, ofGetHeight()-colorImg.getHeight()*colorScale-15, colorImg.getWidth()*colorScale, colorImg.getHeight()*colorScale);
		
		//ofSetColor(255,0,0,255);
		//ofSetLineWidth(3);
		//ofLine(80*flt_horiz,120*flt_vert,120+80*flt_horiz,120+80*flt_vert);
		
		
		ofSetLineWidth(1);
		
		for(int i=0; i<stable_points.size(); i++)
		{
			float sx = (stable_points[i].src_point.x * colorScale)+15;
			float sy = (stable_points[i].src_point.y * colorScale)+(ofGetHeight()-colorImg.getHeight() * colorScale)-15;
		
			ofSetColor(255,0,0);
			ofLine(sx-3, sy, sx+3, sy);
			ofLine(sx, sy-3, sx, sy+3);

			float dx = (stable_points[i].dst_point.x * colorScale)+15;
			float dy = (stable_points[i].dst_point.y * colorScale)+(ofGetHeight()-colorImg.getHeight() * colorScale)-15;
			
			ofSetColor(0,255,0);
			ofLine(dx-3, dy, dx+3, dy);
			ofLine(dx, dy-3, dx, dy+3);
			
			ofSetColor(0,0,255);
			ofLine(sx,sy,dx,dy);
			
		}
		

	}

	// Set new source to current dest	
	stable_points.clear();
	for(int i=0; i<count; i++)
	{
		tStablePoint pt;
		pt.src_point = act_points[i];
		stable_points.push_back(pt);
	}
	
	if(cPress == 1) {
		initX = initializedX;
		initY = initializedY;
		
		ofBackground(0,0,0);	
		cPress = 0;
	}
}


//--------------------------------------------------------------
void testApp::keyPressed  (int key){

	switch (key){
		case 's' :
		{
			ofImage img;
			img.allocate(ofGetWidth(), ofGetHeight(), OF_IMAGE_COLOR);
			img.grabScreen(0,0,ofGetWidth(), ofGetHeight());
			img.saveImage("screenshots/shoot_"+ofToString(saveImgCount)+".jpg");
			saveImgCount++;
		}
			break;
		case 'r' :
			cPress = 1;
			vidPlayer.setFrame(1);
			break;
		case '+' :
			if (blur < 255)	blur++;
			break;
		case '-' :
			if (blur > 0) blur--;
			break;
		case 'n' :
			if (vidIndex < numFiles) vidIndex++;
			ofBackground(0,0,0);
			vidPlayer.loadMovie("videos/"+DIR.getName(vidIndex));
			vidPlayer.play();
			homX = 0; homY = 0;
			break;
		case 'p' :
			if (vidIndex > 0) vidIndex--;
			ofBackground(0,0,0);
			vidPlayer.loadMovie("videos/"+DIR.getName(vidIndex));
			vidPlayer.play();
			homX = 0; homY = 0;
			break;
		case 'q' :
			if(factor < 1.7) movX += 0.05;
			break;
		case 'e' :
			if(factor > 0.3) movX -= 0.05;
			break;
		case 'y' :
			if(factor < 1.7) movY += 0.05;
			break;
		case 'c' :
			if(factor > 0.3) movY -= 0.05;
			break;
	}
}

//--------------------------------------------------------------
void testApp::mouseMoved(int x, int y ){
}

//--------------------------------------------------------------
void testApp::mouseDragged(int x, int y, int button){
	vidPlayer.setPaused(true);
	ofBackground(0,0,0);	
	initX = (x-(640*factor/2))/factor;
	initY = (y-(640*factor/2))/factor;
	
	initializedX = initX;
	initializedY = initY;
	
	ofSetColor(255,255,255,255);
	//grayImage.draw(initX*factor, initY*factor, 640*factor, 360*factor);
	
}

//--------------------------------------------------------------
void testApp::mousePressed(int x, int y, int button){
}

//--------------------------------------------------------------
void testApp::mouseReleased(int x, int y, int button){
	vidPlayer.setPaused(false);
}

//--------------------------------------------------------------
void testApp::windowResized(int w, int h){

}

