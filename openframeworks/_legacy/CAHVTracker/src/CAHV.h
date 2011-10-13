/*
 *  CAHV.h
 *  SpaceBeyond
 *
 *  Created by m9dfukc on 12.06.10.
 *  Copyright 2010 invertednothing. All rights reserved.
 *
 */


#ifndef _CAHV
#define _CAHV

#include "ofMain.h"

class CAHV {
	
public:
	
	void setup(string _scene);
	
	bool prev();
	bool next();
	void reset();
	void toConsole();
	void setPos(int _x, int _y);
	void setScale(int _z);
	
private:
	bool update(int dir);
	
	
};

#endif