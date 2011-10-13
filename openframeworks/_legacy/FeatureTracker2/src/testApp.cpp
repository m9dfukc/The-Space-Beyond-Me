#include "testApp.h"

//--------------------------------------------------------------
void testApp::setup()
{
	ofSetFrameRate(30);
	
	ofSetBackgroundAuto(false);
	ofBackground(0,0,0);
	ofEnableAlphaBlending();
	
	frameByframe = false;
	
	DIR.allowExt("mov");
	numFiles = DIR.listDir("videos_neu/");
	vidIndex = 0;
	
	vidPlayer.loadMovie("videos_neu/"+DIR.getName(vidIndex));
	//vidPlayer.play();
	//vidPlayer.nextFrame();
	
	filmWidth = 640;
	filmHeight = 360;
	
	
	/*
	filmWidth = 720;
	filmHeight = 576;
	*/
	
	
	colorImg.allocate(filmWidth,filmHeight);
	grayImage.allocate(filmWidth,filmHeight);
	
	grayImage.setAnchorPercent(0.5, 0.5);
	
	flt_horiz = 0;
	flt_vert = 0 ;
	
	saveImgCount = 0;
	
	factor = 0.5;
	blur = 100;
	
	current_zoom=1.0;
	
	movX = 1; movY = 1;
	count = 0; 
	
	initX, initializedX = (ofGetWidth()/2-filmWidth*factor/2)/factor;
	initY, initializedY = (ofGetHeight()/2-filmHeight*factor/2)/factor;
	
	playToggle = false; clearScreen = false; saveFile = false;
	frameIndex = 0; playDirection = 1;
	///ofSetVerticalSync(true);
	vidPlayer.play();
	
}

//--------------------------------------------------------------
void testApp::update()
{
	vidPlayer.idleMovie();
//	ofBackground(100,100,100);
	if (true /* playToggle */) {
		
		/*
		vidPlayer.idleMovie();
		
		if (frameIndex > vidPlayer.getTotalNumFrames()) {
			clearScreen = true;
			frameIndex = 0;
			saveFile = false;
			playToggle = false;
		} else if (frameIndex < 0) {
			playToggle = false;
			clearScreen = true;
			frameIndex = vidPlayer.getTotalNumFrames();
		}

		vidPlayer.setFrame(frameIndex);
		*/
		
		
		bool bNewFrame = false;		
		bNewFrame = vidPlayer.isFrameNew();

		if (true){
			colorImg.setFromPixels(vidPlayer.getPixels(), filmWidth,filmHeight);

			grayImage = colorImg;
			grayImage.mirror(false, false);
			//std::cout << vidPlayer.getCurrentFrame() << std::endl;
			
			
			IplImage* eig = cvCreateImage( cvGetSize(grayImage.getCvImage()), 32, 1 );
			IplImage* temp = cvCreateImage( cvGetSize(grayImage.getCvImage()), 32, 1 );
			
			double quality = 0.1;
			double min_distance = 10;
			count = MAX_COUNT;
			int win_size = 5;
			
			// Retrieve features of actual frame
			act_points = (CvPoint2D32f*)cvAlloc(MAX_COUNT*sizeof(CvPoint2D32f));
			cvGoodFeaturesToTrack( grayImage.getCvImage(), eig, temp, act_points, &count, quality, min_distance, 0, 5, 0, 0.04 );
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
				float mindist = 1000000;
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
			
			
			if (stable_points.size() >= 4)
			{
				
				
				double avg_dx = 0;
				double avg_dy = 0;
				double avg_zoom = 1;
				
				for(int i=0; i<stable_points.size(); i++)
				{
					avg_dx += (stable_points[i].dst_point.x - stable_points[i].src_point.x);
					avg_dy += (stable_points[i].dst_point.y - stable_points[i].src_point.y);
					
					double dx1 = stable_points[0].dst_point.x + 800;
					double dy1 = stable_points[0].dst_point.y + 600;
					double dist1 = sqrt(dx1*dx1+dy1*dy1);
					
					double dx2 = stable_points[0].src_point.x + 800;
					double dy2 = stable_points[0].src_point.y + 600;
					double dist2 = sqrt(dx2*dx2+dy2*dy2);
					
					if (dist2>0)
					{
						//avg_zoom += dist1/dist2;
					}
					
				}
				
				avg_dx/= (double)stable_points.size();
				avg_dy/= (double)stable_points.size();
				avg_zoom/= (double)stable_points.size();
				
				current_zoom *= avg_zoom;
				//printf("Zoom: %f\n",current_zoom);
				
				// majority estimation
				double* weight = new double[stable_points.size()]; 
				for(int i=0; i<stable_points.size(); i++)
				{
					weight[i]=1;
				}
				double sum_weight =  0;
				
				
				for (int iter =0; iter<200; iter++)
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
				delete [] weight;
				
				
				flt_horiz = 0.885*flt_horiz + 0.17*avg_dx;
				flt_vert = 0.885*flt_vert + 0.17*avg_dy;
				
				
				initX += flt_horiz*-1;
				initY += flt_vert*-1;
				
				//std::cout << -flt_horiz << " " << -flt_vert << " " << avg_zoom << std::endl;
				
				
				double offsetX = flt_horiz;
				double offsetY = flt_vert; 
				
				if (saveFile) {
					if (myfile.is_open()) myfile << ofToString(vidIndex) << " " << ofToString(frameIndex) << " " << ofToString(offsetX) << " " << ofToString(offsetY) << " " << ofToString(avg_zoom) << "\n";
				}
				
			}
			
			frameIndex += playDirection;
		
		}
		
		
	}
	
}

//--------------------------------------------------------------
void testApp::draw()
{
	
	ofSetColor(255,255,255,blur);
	grayImage.draw(initX*factor, initY*factor, filmWidth*factor, filmHeight*factor);
	ofSetColor(0);
	ofRect(15, 25, 385, 130);
	ofSetColor(255);
	ofDrawBitmapString("blur amount +/- (0-255): "+ofToString(blur), 20, 40);
	ofDrawBitmapString("play videofile n/p: "+DIR.getName(vidIndex)+" ("+ofToString((vidIndex+1))+"/"+ofToString(numFiles)+")", 20, 55);
	ofDrawBitmapString("video zoomfactor i/o: "+ofToString(factor).substr(0,4), 20, 70);
	ofDrawBitmapString("frame: "+ofToString(frameIndex)+" / "+ofToString(vidPlayer.getTotalNumFrames()), 20, 85);
	ofDrawBitmapString("save Position: "+ofToString(saveFile), 20, 100);

	if (stable_points.size() >= 1)
	{
		
		float colorScale = 0.4;
		
		ofSetColor(0);
		ofRect(0, ofGetHeight()-colorImg.getHeight()*colorScale-30, colorImg.getWidth()*colorScale+30, colorImg.getHeight()*colorScale+30);
		
		ofSetColor(255,255,255,255);
		colorImg.draw(15, ofGetHeight()-colorImg.getHeight()*colorScale-15, colorImg.getWidth()*colorScale, colorImg.getHeight()*colorScale);
		
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
			
		}

	}
	
	if (playToggle) {
		// Set new source to current dest	
		stable_points.clear();
		for(int i=0; i<count; i++)
		{
			tStablePoint pt;
			pt.src_point = act_points[i];
			stable_points.push_back(pt);
		}
	}
	
	if(clearScreen) {
		initX = initializedX;
		initY = initializedY;
		
		ofBackground(0,0,0);	
		clearScreen = false;
	}
	
}


//--------------------------------------------------------------
void testApp::keyPressed  (int key){

	switch (key){
		case 's' :
		{
			/*
			ofImage img;
			img.allocate(ofGetWidth(), ofGetHeight(), OF_IMAGE_COLOR);
			img.grabScreen(0,0,ofGetWidth(), ofGetHeight());
			img.saveImage("screenshots/shoot_"+ofToString(saveImgCount)+".jpg");
			saveImgCount++;
			break;
			 */
			if (!saveFile) {
				
				myfile.open("/Users/m9dfukc/Documents/Openframeworks/of_preRelease_v0061_osxSL_FAT/apps/sketches/opencvGoodFeaturesToTrack/bin/positions.txt", std::ios::out | std::ios::app | std::ios::app);
				saveFile = true;
				std::cout << "begin record" << std::endl;
			} else {
				std::cout << "end record" << std::endl;
				myfile.close();
				saveFile = !saveFile;
			}
		}
		case 'r' :
			clearScreen = true;
			playToggle = false;
			frameIndex = 0;
			break;
		case '+' :
			if (blur < 255)	blur++;
			break;
		case '-' :
			if (blur > 0) blur--;
			break;
		case 'n' :
			if (vidIndex < numFiles-1) vidIndex++; else vidIndex = 0;
			ofBackground(0,0,0);
			vidPlayer.loadMovie("videos/"+DIR.getName(vidIndex));
			vidPlayer.play();
			frameIndex = 0;
			current_zoom = 1.0;
			break;
		case 'p' :
			if (vidIndex > 0) vidIndex--; else vidIndex = numFiles-1;
			ofBackground(0,0,0);
			vidPlayer.loadMovie("videos/"+DIR.getName(vidIndex));
			vidPlayer.play();
			frameIndex = 0;
			break;
		case ' ' :
			if (playToggle) { 
				playToggle = false;
			} else {
				playToggle = true;
			}
			break;
			/*
		case OF_KEY_RIGHT :
			playToggle = true;
			playDirection = 1;
			break;
		case OF_KEY_LEFT :
			playToggle = true;
			playDirection = -1;
			break;
			*/
		case 'f':
            frameByframe=!frameByframe;
            vidPlayer.setPaused(frameByframe);
			break;
        case OF_KEY_LEFT:
            vidPlayer.previousFrame();
			break;
        case OF_KEY_RIGHT:
            vidPlayer.nextFrame();
			break;
        case '0':
            vidPlayer.firstFrame();
			break;
	}
}

//--------------------------------------------------------------
void testApp::keyReleased(int key){
	switch(key) {
		case OF_KEY_RIGHT :
		{
			playToggle = false;
		}
		case OF_KEY_LEFT:
		{
			playToggle = false;
		}
			
	}
	
}

//--------------------------------------------------------------
void testApp::mouseMoved(int x, int y ){
}

//--------------------------------------------------------------
void testApp::mouseDragged(int x, int y, int button){
	ofBackground(0,0,0);	
	initX = (x-(filmWidth*factor/2))/factor;
	initY = (y-(filmWidth*factor/2))/factor;
	
	initializedX = initX;
	initializedY = initY;
	
	ofSetColor(255,255,255,255);
	//grayImage.draw(initX*factor, initY*factor, filmWidth*factor, filmHeight*factor);
	
}

//--------------------------------------------------------------
void testApp::mousePressed(int x, int y, int button){
}

//--------------------------------------------------------------
void testApp::mouseReleased(int x, int y, int button){
}

//--------------------------------------------------------------
void testApp::windowResized(int w, int h){

}

