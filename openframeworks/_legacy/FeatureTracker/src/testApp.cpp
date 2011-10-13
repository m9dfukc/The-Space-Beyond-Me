#include "testApp.h"

//--------------------------------------------------------------
void testApp::setup()
{
	ofSetWindowPosition(0, 30);
	ofSetFrameRate(75);
	
	ofSetBackgroundAuto(false);
	ofBackground(0,0,0);
	ofEnableAlphaBlending();
	
	/* loads the videoclips from the data directory */
	DIR.allowExt("*");
	numFiles = DIR.listDir("videos/");
	vidIndex = 0;
	
	for(int i = 0; i < numFiles; i++) { fileNames.push_back(DIR.getName(i)); }
	std::sort(fileNames.begin(),fileNames.end());
	for(int i = 0; i < numFiles; i++) { cout << fileNames[i] << "\n"; } 
	vidPlayer.loadMovie("videos/"+fileNames[0]);
	
	filmWidth = 768; //720;
	filmHeight = 576; //576;
	
	/* allocates the memory for the film Ð substracts -40 form width and height 
	** from the grayImages size because of ROI settings below */
	colorImg.allocate(filmWidth,filmHeight);
	grayImage.allocate(filmWidth-40,filmHeight-40);
	
	/* standart zoom factor (40mm lens) 
	** and factor in relation to the film size */
	current_zoom=1.0; 
	factor = 0.25;
	blur = 255;
	
	/* ammount of relative horizontal and 
	** vertical movement per frame */
	flt_horiz = 0; 
	flt_vert = 0;
	
	flt_horiz_local = 0; 
	flt_vert_local = 0;
	
	/* stage position offset */
	posOffsetX, posOffsetX_local = 0;
	posOffsetY, posOffsetY_local = 0;
	
	/* sum of stage and relative positions */
	initX = (ofGetScreenWidth()/2-filmWidth*factor/2)/factor;
	initY = (ofGetHeight()/2-filmHeight*factor/2)/factor;
	
	
	/* sum of stage and relative positions */
	initX_local = (ofGetScreenWidth()/2-filmWidth*factor/2)/factor;
	initY_local = (ofGetHeight()/2-filmHeight*factor/2)/factor;
	
	/* some various settings here */
	playToggle = false; 
	clearScreen = false; 
	bNewFrame = false;
	saveFile = false;
	verbose = false;
	subpixels = false;
	
	frameIndex = 0;
	
	grayImage.setAnchorPercent(0.5, 0.5);
	vidPlayer.setPosition(0);
	
	std::cout << "sum of frames per video: " << vidPlayer.getTotalNumFrames() << std::endl;
}

//--------------------------------------------------------------
void testApp::update()
{
	if (ofGetFrameNum()%3 == 0) {
		
		if (verbose) {
			/*
			std::cout	<< " x pos(width mouse offset): " 
						<< ((initX*current_zoom)+posOffsetX)  
						<< ", x pos(no offset): / " 
						<< (initX*current_zoom) 
						<< " y pos(width mouse offset): " 
						<< ((initY*current_zoom)+posOffsetY)  
						<< ", y pos(no offset): " 
						<< (initY*current_zoom) 
			<< std::endl; 
			 */
		}
		
		/* stops the video if the current Frames reaches the last Frame */
		if (vidPlayer.getCurrentFrame() >= vidPlayer.getTotalNumFrames()) { 
			vidPlayer.stop(); 
			frameIndex = 0; 
			playToggle = false;
			vidPlayer.setPosition(0);
			
			if (verbose) {
				std::cout << "filmend" << std::endl;
				std::cout << playToggle << std::endl;
			}
		}
		
		/* everything in here gets processed if playToggle == TRUE */
		if (playToggle) {
			vidPlayer.idleMovie();

			
			/* shifts the video frame y frame */
			if (vidPlayer.getCurrentFrame() < vidPlayer.getTotalNumFrames()) {
				vidPlayer.setPosition(frameIndex * 1.0f/vidPlayer.getTotalNumFrames());
			}
			
			bNewFrame = false;		
			bNewFrame = vidPlayer.isFrameNew();
			
			/* indicates if the new frame is already available and then executes the code */ 
			if (bNewFrame){
				
				/* get the image out of the video player and set the ROI because we don't 
				** wanna have the black borders from the analog film material - Quicktime sucks!! */				
				colorImg.setFromPixels(vidPlayer.getPixels(), filmWidth,filmHeight);
				colorImg.setROI(20, 20, 728, 536);
				grayImage = colorImg;
				colorImg.resetROI();
				
				/* find good points to track */
				double quality = 0.1;
				double min_distance = 15;
				count = MAX_COUNT;
				int win_size = 10;

				IplImage* eig = cvCreateImage( cvGetSize(grayImage.getCvImage()), 32, 1 );
				IplImage* temp = cvCreateImage( cvGetSize(grayImage.getCvImage()), 32, 1 );

				/* Retrieve features of actual frame */
				act_points = (CvPoint2D32f*)cvAlloc(MAX_COUNT*sizeof(CvPoint2D32f));
				cvGoodFeaturesToTrack( grayImage.getCvImage(), eig, temp, act_points, &count, quality, min_distance, 0, 3, 0, 0.04 );
				if (subpixels) cvFindCornerSubPix( grayImage.getCvImage(), act_points, count, cvSize(win_size,win_size), cvSize(-1,-1), cvTermCriteria(CV_TERMCRIT_ITER|CV_TERMCRIT_EPS,20,0.03));
				
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
					initX += flt_horiz*-1;
					initY += flt_vert*-1;
					
					
					/* local copy for displaying it on screen */

					flt_horiz_local = (ofactor*flt_horiz_local + nfactor*avg_dx);
					flt_vert_local = (ofactor*flt_vert_local + nfactor*avg_dy);
					
					initX_local += flt_horiz_local*-1;
					initY_local += flt_vert_local*-1;

					double _x = (initX*current_zoom)+posOffsetX;
					double _y = (initY*current_zoom)+posOffsetY;
					double _z = _y; //not really in use atm!
					
					/* save the data to a txt file frame for frame, line for line */
					if (saveFile && (vidPlayer.getCurrentFrame() < vidPlayer.getTotalNumFrames())) {
						if (myfile.is_open()) myfile 
												<< ofToString(vidIndex) 
												<< " " << ofToString(vidPlayer.getCurrentFrame()) 
												<< " " << ofToString((int)((float)_x/(float)factor)) 
												<< " " << ofToString((int)((float)_y/(float)factor)) 
												<< " " << ofToString(_z) 
												<< " " << ofToString((int)((float)posOffsetX/(float)factor)) 
												<< " " << ofToString((int)((float)posOffsetY/(float)factor)) 
												<< " " << ofToString((int)((float)initX/(float)factor)) 
												<< " " << ofToString((int)((float)initY/(float)factor)) 
											<< "\n";
						if ( verbose ) {
							std::cout << ofToString(vidIndex) << " " << ofToString(vidPlayer.getCurrentFrame()) << " " << ofToString((int)((float)_x/(float)factor)) << " " << ofToString((int)((float)_y/(float)factor)) << " " << ofToString(_z) << std::endl;
						}
					}
				}

				frameIndex++;
			}
			
		}
	}
}

//--------------------------------------------------------------
void testApp::draw()
{
	
	double _x = (initX_local*current_zoom)*factor+posOffsetX_local;
	double _y = (initY_local*current_zoom)*factor+posOffsetY_local;
	double _w = (filmWidth*factor)*current_zoom;
	double _h = (filmHeight*factor)*current_zoom;
	
	std::cout << (initX*current_zoom) << " " << (initY*current_zoom) << " " << posOffsetX << std::endl;
	std::cout << (initX*current_zoom)*factor << " " << (initY*current_zoom)*factor << " " << posOffsetX << std::endl;
	std::cout << factor << " " << factor << " " << posOffsetX << std::endl;
		
	ofSetColor(255,255,255,blur);
	grayImage.draw(_x, _y, _w, _h);
	
	/* translates our information layer so it's always viewable */
	ofPushMatrix();
	ofTranslate(-ofGetWindowPositionX(), 0, 0);
	
	ofSetColor(0);
	ofRect(-20, 25, 350, 109);
	ofSetColor(255);
	ofDrawBitmapString("blur amount -/+ (0-255): "+ofToString(blur), 20, 40);
	ofDrawBitmapString("select scene p/n: "+DIR.getName(vidIndex)+" ("+ofToString((vidIndex+1))+"/"+ofToString(numFiles)+")", 20, 52);
	ofDrawBitmapString("zoomfactor k/l: "+ofToString(current_zoom).substr(0,4), 20, 64);
	ofDrawBitmapString("subpixel accurancy: "+ofToString(subpixels), 20, 76);
	ofDrawBitmapString("frame: "+ofToString(vidPlayer.getCurrentFrame())+" / "+ofToString(vidPlayer.getTotalNumFrames()), 20, 88);
	ofDrawBitmapString("save Position: "+ofToString(saveFile), 20, 100);

	ofPopMatrix();
	
	/* draws kind of a grid to the screen */
	for (int i = 1; i < 25; i++) {
		ofSetColor(255, 255, 255);
		double raster = ofGetWidth() / 25;
		ofDrawBitmapString(ofToString(i)+"m", (raster * i), (ofGetHeight() - 20));
		ofLine((raster * i)-3, ofGetHeight()/2, (raster * i)+3, ofGetHeight()/2);
		ofLine((raster * i), ofGetHeight()/2-3, (raster * i), ofGetHeight()/2+3);
	}
		
	ofPushMatrix();
	ofTranslate(-ofGetWindowPositionX(), 0, 0);
	
	if (ofGetFrameNum()%3 == 0) {
		
		/* draws the tracking features to the screen */
		if (stable_points.size() >= 4) {
			
			float colorScale = 0.3;
			
			ofSetColor(0);
			ofRect(-20, ofGetHeight()-colorImg.getHeight()*colorScale-40, colorImg.getWidth()*colorScale+50, colorImg.getHeight()*colorScale+50);
			
			ofSetColor(255,255,255,255);
			colorImg.draw(15, ofGetHeight()-colorImg.getHeight()*colorScale-15, colorImg.getWidth()*colorScale, colorImg.getHeight()*colorScale);
			
			ofSetLineWidth(1);
			
			for(int i=0; i<stable_points.size(); i++) {
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
		
		if (playToggle && bNewFrame) {	
			stable_points.clear();
			for(int i=0; i<count; i++) {
				tStablePoint pt;
				pt.src_point = act_points[i];
				stable_points.push_back(pt);
			}
		}
	}
	ofPopMatrix();
	
	/* clear the screen inbetween the scenes */
	if(clearScreen) {
		clearScreen = false;
		ofBackground(0,0,0);
	}
	
}


//--------------------------------------------------------------
void testApp::keyPressed  (int key){

	switch (key){
		case 's' :
		{
			if (!saveFile) {
				char formatNr[10];
				sprintf (formatNr, "%04d.txt", vidIndex);

				string mystring = ofToDataPath("positions/positions_", true);
				char mychar[mystring.size()+1];

				for(int i=0;i<mystring.size()+1;i++)
					mychar[i]=mystring[i];
				
				int size = sizeof(formatNr)+sizeof(mychar);
				char outPath[size];
				
				strcpy (outPath, mychar);
				strcat (outPath, formatNr);
				
				myfile.open(outPath, std::ios::out | std::ios::trunc);
				saveFile = true;
				std::cout << "begin record" << std::endl;
			} else {
				std::cout << "end record" << std::endl;
				
				myfile.close();
				saveFile = !saveFile;
			}
		}
		case 'c' :
			clearScreen = true;
			break;
		case 'r' :
			vidPlayer.stop();
			
			clearScreen = true;
			playToggle = false;
			frameIndex = 0;
			current_zoom = 1.0;
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
			vidPlayer.close();
			vidPlayer.loadMovie("videos/"+fileNames[vidIndex]);
			vidPlayer.setPosition(0);
			frameIndex = 0;
			current_zoom = 1.0;
			break;
		case 'p' :
			if (vidIndex > 0) vidIndex--; else vidIndex = numFiles-1;
			ofBackground(0,0,0);
			vidPlayer.close();
			vidPlayer.loadMovie("videos/"+fileNames[vidIndex]);
			vidPlayer.setPosition(0);
			frameIndex = 0;
			current_zoom = 1.0;
			break;
		case 'a' :
			subpixels = !subpixels;
			break;
		case ' ' :
			if (verbose) std::cout << "hit space, toggle state: " << playToggle << std::endl;
			if (playToggle) { 
				playToggle = false;
			} else {
				if (frameIndex == 0) {
					vidPlayer.loadMovie("videos/"+fileNames[vidIndex]);
					vidPlayer.setPosition(0 * 1.0f/vidPlayer.getTotalNumFrames());
				}
				vidPlayer.setPosition((frameIndex+1) * 1.0f/vidPlayer.getTotalNumFrames());
				playToggle = true;
			}
			break;
		case 'k':
			if (factor > 0.31) {
				ofBackground(0,0,0);
				current_zoom -= 0.01;
			}
			break;
		case 'l':
			if (factor < 3.15) {
				ofBackground(0,0,0);
				current_zoom += 0.01;
			}
			break;
	}
}
//--------------------------------------------------------------
void testApp::keyReleased(int key){
	
}

//--------------------------------------------------------------
void testApp::mouseMoved(int x, int y ){
}

//--------------------------------------------------------------
void testApp::mouseDragged(int x, int y, int button){
	if (!playToggle) ofBackground(0,0,0);	
	
	posOffsetX = x - (initX*current_zoom);
	posOffsetY = y - (initY*current_zoom);
	
	posOffsetX_local = x - (initX*current_zoom*factor);
	posOffsetY_local = y - (initY*current_zoom*factor);
	
	ofSetColor(255,255,255,255);
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

