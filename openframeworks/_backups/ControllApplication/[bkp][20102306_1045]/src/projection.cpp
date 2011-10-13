/*
 *  projection.cpp
 *  RoomBeyondController
 *
 *  Created by m9dfukc on 22.06.10.
 *  Copyright 2010 invertednothing. All rights reserved.
 *
 */

#include "projection.h"

projection::projection() {
	ready = false;
	width = 0;
	height = 0;
	position.set(0, 0, 0);
}

projection::~projection() {
	ofTexture::clear();
}

void projection::clear() {
	ofTexture::clear();
	ready = false;
}

void projection::grabScreen(int _x, int _y, int _width, int _height) {
	ofTexture::allocate(_width, _height, GL_RGB);
	ofTexture::loadScreenData(_x, _y, _width, _height);
	
	width = _width;
	height = _height;
	position.set(_x, _y);
	
	ready = true;
}

void projection::draw(int _x, int _y) {
	if ( ready ) ofTexture::draw(_x, _y); else cout << "texture not ready!" << endl;
}

void projection::draw() {
	if ( ready ) ofTexture::draw(position.x, position.y); else cout << "texture not ready!" << endl;
}

