/*
 *  projection.h
 *  RoomBeyondController
 *
 *  Created by m9dfukc on 22.06.10.
 *  Copyright 2010 invertednothing. All rights reserved.
 *
 */
#include "ofMain.h"

class projection : public ofTexture {
	
public:
	projection();
	~projection();
	ofPoint position;
	int width;
	int height;
	bool ready;
	
	void grabScreen(int _x, int _y, int _width, int _height);
	void draw(int _x, int _y);
	void draw();
	
};
