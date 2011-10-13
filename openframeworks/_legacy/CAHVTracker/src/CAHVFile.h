/*
 *  CAHVFile.h
 *  SpaceBeyond
 *
 *  Created by m9dfukc on 12.06.10.
 *  Copyright 2010 invertednothing. All rights reserved.
 *
 */

#ifndef _CAHVFILE
#define _CAHVFILE

#define _Cx 0
#define _Cy 1
#define _Cz 2
#define _Ax 3
#define _Ay 4
#define _Az 5
#define _Hx 6
#define _Hy 7
#define _Hz 8
#define _Vx 9
#define _Vy 10
#define _Vz 11
#define _sx 14
#define _sy 15
#define _Width 16
#define _Height 17
#define _ppx 18
#define _ppy 19
#define _f 20


#include "ofMain.h"
#include <iostream>
#include <fstream>

template <class T>
bool from_string(T& t, const std::string& s, std::ios_base& (*f)(std::ios_base&)){
	if(s != ""){
		std::istringstream iss(s);
		return !(iss >> f >> t).fail();   
	}
};



class CAHVObject {
public:
	float Cx;
	float Cy;
	float Cz;
	
	float Ax;
	float Ay;
	float Az;
	
	float Hx;
	float Hy;
	float Hz;
	
	float Vx;
	float Vy;
	float Vz;
	
	float sx;
	float sy;
	
	float Width;
	float Height;
	
	float focal;
	
	float ppx;
	float ppy;
	
	float px;
	float py;
};



class CAHVFile {
	
public:
	void setup(string _scene, string _posFile = "positions.txt");
	string posFile;
	string scene;
	string path;
	
private:
	vector<CAHVObject> cahv;
	
};

#endif