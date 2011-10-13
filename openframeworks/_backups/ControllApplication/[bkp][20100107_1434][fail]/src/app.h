#ifndef _APP
#define _APP


#include "ofMain.h"
#include "ofxVectorMath.h"
#include "ofxFenster.h"
#include "ofxDirList.h"
#include "CAHV.h"
//#include "FeatureTracker.h"
#include "projection.h"
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
		
		void drawProjections();
		void drawScreenGrid();
		void drawPositionsAsLine();
	
		void setupScene(int _scenePos);

		void keyPressed  (int key);
		void mousePressed(int x, int y, int button);
		void fensterKeyPressed  (int key);

	//FeatureTracker  tracker;
		CAHV		cahv;
		ofxDirList	DIR2;
		ofstream	myFile;
		string		saveFile;
		string		buffer;
		projection  tmpProjection;
	vector<string>  scenes;
	vector<ofPoint>	tmpPositions;
	vector<vector<ofPoint> > drawPositions;
	vector<ofColor>	drawLineColor;
	vector<projection> projections;
	
		int			framePosX;
		int			framePosY;
		float		sceneSize;
		int			scenePos;
		int			sceneNum;
		int			framePos;
		int			frameNum;
		int			trackingMode;
		bool		sceneSaved;
		bool		playToggle;
		bool		projectionMode;
	

};

#endif

