#include "app.h"


//--------------------------------------------------------------
void app::setup(){
	ofSetVerticalSync(false);
	ofSetFrameRate(15);
	cahv.setup("zomm-out");
	ofSetBackgroundAuto(false);
	ofBackground(0, 0, 0);
	
	playToggle = false;
	saveFile = "zomm-out.txt";
	buffer = "";

}

//--------------------------------------------------------------
void app::update(){
	if (playToggle) { 
		playToggle = cahv.nextFrame();
		
		buffer += ofToString(1) + " ";
		buffer += ofToString((int) (cahv.getFrame()+1)) + " ";
		buffer += ofToString((int) cahv.absolutePosition.x) + " ";
		buffer += ofToString((int) cahv.absolutePosition.y) + " ";
		buffer += ofToString((float) cahv.absolutePosition.z) + " ";
		buffer += "\n";
	} 
}

//--------------------------------------------------------------
void app::draw(){
	
	if (playToggle) cahv.draw();
	
	drawScreenGrid();

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

//--------------------------------------------------------------
void app::keyPressed  (int key){
	
	if (key == 'r') {
		ofBackground(0, 0, 0);
		cahv.resetFrames();
		buffer = "";
	} else if (key == 's') {
		myFile.open(ofToDataPath(saveFile).c_str(), std::ios::out | std::ios::trunc);
		if (myFile.is_open()) {
			myFile << buffer;
			myFile.close();
		}
		cout << "Positions saved to textfile" << endl;
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
	ofBackground(0, 0, 0);
	buffer = "";
}

//--------------------------------------------------------------
void app::mouseReleased(int x, int y, int button){

}

//--------------------------------------------------------------
void app::windowResized(int w, int h){

}

