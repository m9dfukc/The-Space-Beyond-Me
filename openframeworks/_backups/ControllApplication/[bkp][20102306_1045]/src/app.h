#ifndef _APP
#define _APP


#include "ofMain.h"
#include "ofxVectorMath.h"
#include "ofxFenster.h"
#include "ofxDirList.h"
#include "CAHV.h"
#include "fileHelper.h"
#include <iostream>
#include <fstream>


class app : public ofBaseApp, public ofxFensterListener {

	public:

		void setup();
		void update();
		void draw();
	
		void fensterDraw();
		void fensterUpdate();
		
		void drawScreenGrid();
		void drawPositionsAsLine();
	
		void setupScene(int _scenePos);

		void keyPressed  (int key);
		void keyReleased (int key);

		void mouseMoved(int x, int y );
		void mouseDragged(int x, int y, int button);
		void mousePressed(int x, int y, int button);
		void mouseReleased(int x, int y, int button);
		void windowResized(int w, int h);
	
		void fensterKeyPressed  (int key);
		void fensterKeyReleased(int key);
		void fensterMouseMoved(int x, int y );
		void fensterMouseDragged(int x, int y, int button);
		void fensterMousePressed(int x, int y, int button);
		void fensterMouseReleased(int x, int y, int button);
		void fensterWindowResized(int w, int h);

		CAHV		cahv;
		ofxDirList	DIR2;
		ofstream	myFile;
		string		saveFile;
		string		buffer;
	vector<string>  scenes;
	vector<ofPoint>	tmpPositions;
	vector<vector<ofPoint> > drawPositions;
	vector<ofColor>	drawLineColor;
	//vector<ofTexture> projections;
	
		int			framePosX;
		int			framePosY;
		float		sceneSize;
		int			scenePos;
		int			sceneNum;
		int			framePos;
		int			frameNum;
		bool		sceneSaved;
		bool		playToggle;
	

};

#endif

