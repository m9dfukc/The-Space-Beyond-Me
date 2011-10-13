#include "app.h"


//--------------------------------------------------------------
void app::setup(){
	
	int overallFrameRate = 30;
	
	ofEnableAlphaBlending();
	ofSetVerticalSync(false);
	ofSetFrameRate(overallFrameRate);
	glEnable(GL_BLEND);
	
	projectionMode = true;
	
	framePosX = 0;
	framePosY = 0;
	sceneSize = 0.0;
	
	scenePos = 0;
	sceneNum = 0;
	framePos = 0;
	frameNum = 0;
	
	sceneSaved = false;
	
    int nFiles = DIR2.listDir("./");
	for (int i = 0; i < nFiles; i++) {
		string _files = DIR2.getName(i);
		if (_files.find("scene", 0) == 0 && _files.find(".", 0) == string::npos) {
			scenes.push_back(_files);
			sceneNum++;
			cout << "scene found: " << _files << endl;
		}
	}
		
	fenster->setFPS(overallFrameRate);
	fenster->setBounds(0, 765, 500, 105);
		
	setupScene(scenePos);
	tracker.setPosXY(500, 360);
	tracker.resetFrames();
	
	ofSetBackgroundAuto(false);
	ofBackground(0, 0, 0);
	
	ofColor tColor;
	tColor.r = ofRandom(0, 255);
	tColor.g = ofRandom(0, 255);
	tColor.b = ofRandom(0, 255);
	drawLineColor.push_back(tColor);
	
	playToggle = false;
	buffer = "";
	
	tmpProjection.clear();
	projections.push_back(tmpProjection);
	tmpPositions.clear();
	drawPositions.push_back(tmpPositions);
	
}

void app::setupScene(int _scenePos) {
	if (_scenePos < 0 || _scenePos - 1 > sceneNum) return;
	tracker.setup(scenes[_scenePos]);
	saveFile = scenes[_scenePos]+"/"+scenes[_scenePos]+".txt";
	cout << "scene loaded: '" << scenes[_scenePos] << "' (" << _scenePos+1 << "/" << sceneNum << ")" << endl;
}

//--------------------------------------------------------------
void app::update(){
	if (playToggle) { 
		
		int frameNum = tracker.getFrame();
		
		if (frameNum >= 1 && frameNum <= tracker.size) {
			buffer += ofToString(1) + " ";
			buffer += ofToString((int) (tracker.getFrame())) + " ";
			buffer += ofToString((int) 10.0*tracker.getPosition().x) + " ";
			buffer += ofToString((int) 10.0*tracker.getPosition().y) + " ";
			float _z = (tracker.getPosition().z > 0.11) ? tracker.getPosition().z : 0.11;
			_z = (_z < 1.0) ? _z : 1.00;
			buffer += ofToString((float) _z) + " ";
			buffer += "\n";
			
			tmpPositions.push_back(tracker.getPosition());
		}
		
		if (frameNum < 1) buffer = "";
		
		playToggle = tracker.nextFrame();
	} 
}

//--------------------------------------------------------------
void app::draw(){
	ofSetColor(255, 255, 255);
	drawProjections();

	ofSetColor(255, 255, 255);
	
	if ( projectionMode && playToggle ) {
		
		glColor4f(1.0f, 1.0f, 1.0f, 0.04f); 
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);  
		tracker.draw();

		glColor4f(0.5f, 0.5f, 0.5f, 0.15f);  
		glBlendFunc(GL_SRC_ALPHA, GL_ONE);   
		tracker.draw();
		
	} else if ( playToggle ) {
		tracker.draw();
	}
	
	glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	ofSetColor(255, 255, 255, 255);
			   
	drawScreenGrid();
	drawPositionsAsLine();

}

void app::fensterUpdate(){
}

void app::fensterDraw(){
	ofDrawBitmapString("KEY SHORTCUTS:", 10, 18);
	ofDrawBitmapString("save (s)", 10, 35);
	ofDrawBitmapString("jump (p/n)", 10, 50);
	ofDrawBitmapString("resize (-/+)", 10, 65);
	ofDrawBitmapString("play (space)", 10, 80);
	ofDrawBitmapString("display mode (m)", 10, 95);
	
	ofDrawBitmapString("POSITION DATA:", 200, 18);
	ofDrawBitmapString("scene: "+ofToString(scenePos+1, 0) + "/" + ofToString(sceneNum, 0), 200, 35);
	ofDrawBitmapString("frame: "+ofToString(tracker.getFrame(), 0) + "/" + ofToString(tracker.getFrameCount()-1, 0), 200, 50);
	ofDrawBitmapString("position: "+ofToString(tracker.getPosition().x, 0) + "/" + ofToString(tracker.getPosition().y, 0) + " (x/y)", 200, 65);
	ofDrawBitmapString("scale: " + ofToString(tracker.getPosition().z, 4), 200, 80);
	if ((tracker.getPosition().z > 1.0 || tracker.getPosition().z < 0.11) && ofGetFrameNum()%2 == 0) {
		ofSetColor(255, 0, 0);
		ofDrawBitmapString("clipping!", 320, 80);
		ofSetColor(255, 255, 255);
	}
	
	if (tracker.trackingMode) ofDrawBitmapString("tracking (t): internal" , 200, 95);
	else ofDrawBitmapString("tracking (t): file" , 200, 95);
	
}

void app::drawProjections() {
	for (int i = 0; i < projections.size(); i++) {
		projections[i].draw();
	}
}

void app::drawScreenGrid() {
	for (int i = 1; i < 25; i++) {
		ofSetColor(255, 255, 255);
		double raster = ofGetWidth() / 11;
		ofDrawBitmapString(ofToString(i)+"m", (raster * i), (ofGetHeight() - 20));
		ofLine((raster * i)-3, ofGetHeight()/2, (raster * i)+3, ofGetHeight()/2);
		ofLine((raster * i), ofGetHeight()/2-3, (raster * i), ofGetHeight()/2+3);
	}
}

void app::drawPositionsAsLine() {
	ofNoFill();
	ofSetColor(255, 0, 0);
	for (int i = 0; i < drawPositions.size(); i++) {
		ofSetColor(drawLineColor[i].r, drawLineColor[i].g, drawLineColor[i].b);
		
		if (drawPositions[i].size() > 0) {
			ofBeginShape();
			for (int j = 0; j < drawPositions[i].size() - 4; j += 6) { 
				float tX = drawPositions[i][j].x;
				float tY = drawPositions[i][j].y;
				if (j == 0) ofDrawBitmapString("scene "+ofToString(i+1), tX, tY );
				if (j > 12)	ofVertex(tX, tY);
			}
			ofEndShape();
		}
		
	}
	ofSetColor(255,255,255);
}

//--------------------------------------------------------------
void app::keyPressed  (int key) {
	
	if (key == 'r') {
		ofBackground(0, 0, 0);
		tracker.resetFrames();
		buffer = "";
	} else if (key == 's') {
		if (tracker.finished) {
			
			if (fileHelper::doesFileExist(saveFile)) fileHelper::deleteFile(saveFile);
			
			myFile.open(ofToDataPath(saveFile).c_str(), std::ios::out | std::ios::trunc);
			if (myFile.is_open()) {
				myFile << buffer;
				myFile.close();
			}
			
			tmpProjection.grabScreen(tracker.getBoundary().x, tracker.getBoundary().y, tracker.getBoundary().width, tracker.getBoundary().height);
			tmpProjection.setImageType(OF_IMAGE_COLOR_ALPHA);
			for(int i = 3; i < tmpProjection.height*tmpProjection.width*4; i+=4){
				if( tmpProjection.getPixels()[i-1] == 0 ) {
					tmpProjection.getPixels()[i] = 0;
				}
			}
			tmpProjection.update();
			
			projections[scenePos] = tmpProjection;
			tmpProjection.clear();
			
			drawPositions[scenePos] = tmpPositions;
			tmpPositions.clear();
			
			cout << "Positions saved to textfile" << endl;
			buffer = "";
			
		} else {
			cout << "Please wait till the scene is finished" << endl;
		}
	} else if (key == 'n') {
		if (sceneNum - 1 > scenePos) scenePos++;
		
		ofBackground(0, 0, 0);
		
		ofColor tColor;
		tColor.r = ofRandom(0, 255);
		tColor.g = ofRandom(0, 255);
		tColor.b = ofRandom(0, 255);
		
		tmpPositions.clear();
		drawLineColor.push_back(tColor);
		drawPositions.push_back(tmpPositions);

		tmpProjection.clear();
		projections.push_back(tmpProjection);
		
		setupScene(scenePos);
		tracker.setInternalTracking(false);
		
	} else if (key == 'p') {
		if (scenePos > 0) scenePos--;
		
		ofBackground(0, 0, 0);
		
		tmpPositions.clear();
		tmpProjection.clear();
		
		setupScene(scenePos);
		tracker.setInternalTracking(false);
		
	} else if (key == '+') {
		
		tracker.resetFrames();
		tmpPositions.clear();
		ofBackground(0, 0, 0);
		//ofBackground(255, 255, 255);
		buffer = "";
		playToggle = true;		
		
		tracker.setScale(tracker.scale += 0.01);
	} else if (key == '-') {
		tracker.resetFrames();
		tmpPositions.clear();
		ofBackground(0, 0, 0);
		//ofBackground(255, 255, 255);
		buffer = "";
		playToggle = true;

		tracker.setScale(tracker.scale -= 0.01);
	} else if (key == 'm') {
		// scene from the beginning!
		ofBackground(0, 0, 0);
		tracker.resetFrames();
		buffer = "";
		projectionMode = !projectionMode;
	} else if (key == 't') {
		cout << "change tracking mode" << endl;
		tracker.setInternalTracking(!tracker.trackingMode);
	} else if (key == ' ') {
		playToggle = !playToggle;
	}

}

//--------------------------------------------------------------
void app::mousePressed(int x, int y, int button){
	tracker.setPosXY(x, y);
	tracker.resetFrames();
	tmpPositions.clear();
	ofBackground(0, 0, 0);
	//ofBackground(255, 255, 255);
	buffer = "";
	playToggle = true;
}

void app::fensterKeyPressed  (int key){
	
	keyPressed(key);
	
}
