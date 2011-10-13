/*
 *  ofxFenster.h
 *  ofxFensterExample
 *
 *  Created by Philip Whitfield on 5/19/10.
 *  Copyright 2010 undef.ch. All rights reserved.
 *
 */

#ifndef _CLASS_ofxFenster_
#define _CLASS_ofxFenster_

#include "ofMain.h"
#include "ofAppGlutWindow.h"

class ofxFenster;

class ofxFensterEvent{
public:
	ofxFenster* fenster;
};

class ofxFensterResizeEvent: public ofxFensterEvent{
public:
	int w;
	int h;
};

class ofxFensterEventsStruct{
public:
	ofEvent<ofxFensterEvent> draw;
	ofEvent<ofxFensterEvent> update;
	ofEvent<ofxFensterResizeEvent> resize;
};

extern ofxFensterEventsStruct ofxFensterEvents;

class ofxFensterListener{
public:
	virtual void fensterUpdate(){}
	virtual void fensterDraw(){ofGetAppPtr()->draw();};
	
	virtual void fensterKeyPressed  (int key){ofGetAppPtr()->keyPressed(key);};
	virtual void fensterKeyReleased(int key){ofGetAppPtr()->keyReleased(key);};
	virtual void fensterMouseMoved(int x, int y ){ofGetAppPtr()->mouseMoved(x, y);};
	virtual void fensterMouseDragged(int x, int y, int button){ofGetAppPtr()->mouseDragged(x, y, button);};
	virtual void fensterMousePressed(int x, int y, int button){ofGetAppPtr()->mousePressed(x, y, button);};
	virtual void fensterMouseReleased(int x, int y, int button){ofGetAppPtr()->mouseReleased(x, y, button);};
	virtual void fensterWindowResized(int w, int h){};
	
	ofxFenster* fenster;
};

class ofxFenster: public ofRectangle{
public:
	ofxFenster();
	~ofxFenster();
	void init(ofxFensterListener* l, string title=""); //open the window
	void update();
	void draw(bool force=false);
	
	void setFPS(int fps);
	void setFPS(int fpsU, int fpsD);
	
	void toContext();
	void toMainContext();
	
	ofRectangle getBounds();
	void setBounds(ofRectangle r);
	void setBounds(int x, int y, int w, int h);
	void toggleFullscreen();
	void setBackground(int r, int g, int b, int a=255);
	
	ofxFensterListener* listener;
	
	int getX();
	int getY();
	int getWidth();
	int getHeight();
	
protected:
private:
	void update(ofEventArgs &e);
	void draw(ofEventArgs &e);
	
	int fpsUpdate;
	int fpsDraw;
	
	int winRef;
	int mainWinRef;
	int nextRender();
	
	int nextWinUpdate;
	int nextWinDraw;
	bool hasGlut;
	bool isFullscreen;
	
	int mainContextSkip;
	
	ofRectangle origSize;
};

#endif