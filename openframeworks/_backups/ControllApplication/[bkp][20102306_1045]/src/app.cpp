#include "app.h"


//--------------------------------------------------------------
void app::setup(){
	
	int overallFrameRate = 40;
	
	ofEnableAlphaBlending();
	ofSetVerticalSync(false);
	ofSetFrameRate(overallFrameRate);
	
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
	fenster->setBounds(0, 790, 500, 80);
		
	setupScene(scenePos);
	ofSetBackgroundAuto(false);
	ofBackground(0, 0, 0);
	
	ofColor tColor;
	tColor.r = ofRandom(0, 255);
	tColor.g = ofRandom(0, 255);
	tColor.b = ofRandom(0, 255);
	drawLineColor.push_back(tColor);
	
	playToggle = false;
	buffer = "";
	
	tmpPositions.clear();
	drawPositions.push_back(tmpPositions);
}

void app::setupScene(int _scenePos) {
	if (_scenePos < 0 || _scenePos - 1 > sceneNum) return;
	cahv.setup(scenes[_scenePos]);
	saveFile = scenes[_scenePos]+"/"+scenes[_scenePos]+".txt";
	cout << "scene loaded: '" << scenes[_scenePos] << "' (" << _scenePos+1 << "/" << sceneNum << ")" << endl;
}

//--------------------------------------------------------------
void app::update(){
	if (playToggle) { 
		playToggle = cahv.nextFrame();
		
		buffer += ofToString(1) + " ";
		buffer += ofToString((int) (cahv.getFrame()+1)) + " ";
		buffer += ofToString((int) 10.0*cahv.getPosition().x) + " ";
		buffer += ofToString((int) 10.0*cahv.getPosition().y) + " ";
		buffer += ofToString((float) cahv.getPosition().z) + " ";
		buffer += "\n";
		
		tmpPositions.push_back(cahv.getPosition());
	} 
}

//--------------------------------------------------------------
void app::draw(){
	
	ofSetColor(255, 255, 255, 40);
	if (playToggle) cahv.draw();
	ofSetColor(255, 255, 255, 255);
	drawScreenGrid();
	drawPositionsAsLine();

}

void app::fensterUpdate(){
}

void app::fensterDraw(){
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
void app::keyPressed  (int key){
	
	if (key == 'r') {
		ofBackground(0, 0, 0);
		cahv.resetFrames();
		buffer = "";
	} else if (key == 's') {
		if (cahv.finished) {
			myFile.open(ofToDataPath(saveFile).c_str(), std::ios::out | std::ios::trunc);
			if (myFile.is_open()) {
				myFile << buffer;
				myFile.close();
			}
			//ofTexture screenImage.allocate(300,300,GL_RGB);
			drawPositions[scenePos] = tmpPositions;
			tmpPositions.clear();
			cout << "Positions saved to textfile" << endl;
			buffer = "";
		} else {
			cout << "Please wait till the scene is finished" << endl;
		}
	} else if (key == 'n') {
		if (sceneNum > scenePos) scenePos++;
		
		ofBackground(0, 0, 0);
		
		ofColor tColor;
		tColor.r = ofRandom(0, 255);
		tColor.g = ofRandom(0, 255);
		tColor.b = ofRandom(0, 255);
		
		tmpPositions.clear();
		drawLineColor.push_back(tColor);
		drawPositions.push_back(tmpPositions);

		setupScene(scenePos);
		
	} else if (key == 'p') {
		if (scenePos > 1) scenePos--;
		tmpPositions.clear();
		setupScene(scenePos);
	} else if (key == '+') {
		cahv.setScale(cahv.scale += 0.01);
	} else if (key == '-') {
		cahv.setScale(cahv.scale -= 0.01);
	} else if (key == ' ') {
		playToggle = !playToggle;
	} 

}

//--------------------------------------------------------------
void app::keyReleased  (int key){
}

//--------------------------------------------------------------
void app::mouseMoved(int x, int y ){
}

//--------------------------------------------------------------
void app::mouseDragged(int x, int y, int button){
}

//--------------------------------------------------------------
void app::mousePressed(int x, int y, int button){
	cahv.setPosXY(x, y);
	cahv.resetFrames();
	tmpPositions.clear();
	ofBackground(0, 0, 0);
	buffer = "";
	playToggle = true;
}

//--------------------------------------------------------------
void app::mouseReleased(int x, int y, int button){

}

//--------------------------------------------------------------
void app::windowResized(int w, int h){

}

void app::fensterKeyPressed  (int key){
	
}
void app::fensterKeyReleased(int key){
	
}
void app::fensterMouseMoved(int x, int y ){
	
}
void app::fensterMouseDragged(int x, int y, int button){
	
}
void app::fensterMousePressed(int x, int y, int button){
	
}
void app::fensterMouseReleased(int x, int y, int button){
	
}
void app::fensterWindowResized(int w, int h){
}

