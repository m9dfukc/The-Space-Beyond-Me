/*
 *  CAHVFile.cpp
 *  SpaceBeyond
 *
 *  Created by m9dfukc on 12.06.10.
 *  Copyright 2010 invertednothing. All rights reserved.
 *
 */
#include "CAHVFile.h"

CAHVFile::CAHVFile() {
	ready	= false;
	frames	= 0;
	size	= 0;
	
	posFile = "";
	scene   = "";
	path	= "";	
}

void CAHVFile::setup(string _scene, string _posFile) {
	
	positions.clear();
	cahvScene.clear();
	
	frames	= 0;
	size	= 0;
	
	posFile = _posFile;
	scene   = _scene;
	path	= scene+"/"+_posFile;
	
	float maxFocal = 0; 
	
	string line;
	ifstream data;
	data.open(ofToDataPath(path).c_str());
	
	while( std::getline( data, line ) ) {
		vector<string> pieces;
		pieces = ofSplitString(line, "\t");
		if (pieces.size() == 29) {
			ofPoint position;
			CAHVObject cahvFrame;
			
			float Cx = cahvFrame.Cx = (pieces[_Cx] != "") ? ofToFloat(pieces[_Cx]) : 0.0;
			float Cy = cahvFrame.Cy = (pieces[_Cy] != "") ? ofToFloat(pieces[_Cy]) : 0.0;
			float Cz = cahvFrame.Cz = (pieces[_Cz] != "") ? ofToFloat(pieces[_Cz]) : 0.0;
			
			float Ax = cahvFrame.Ax = (pieces[_Ax] != "") ? ofToFloat(pieces[_Ax]) : 0.0;
			float Ay = cahvFrame.Ay = (pieces[_Ay] != "") ? ofToFloat(pieces[_Ay]) : 0.0;
			float Az = cahvFrame.Az = (pieces[_Az] != "") ? ofToFloat(pieces[_Az]) : 0.0;
			
			float Hx = cahvFrame.Hx = (pieces[_Hx] != "") ? ofToFloat(pieces[_Hx]) : 0.0;
			float Hy = cahvFrame.Hy = (pieces[_Hy] != "") ? ofToFloat(pieces[_Hy]) : 0.0;
			float Hz = cahvFrame.Hz = (pieces[_Hz] != "") ? ofToFloat(pieces[_Hz]) : 0.0;
			
			float Vx = cahvFrame.Vx = (pieces[_Vx] != "") ? ofToFloat(pieces[_Vx]) : 0.0;
			float Vy = cahvFrame.Vy = (pieces[_Vy] != "") ? ofToFloat(pieces[_Vy]) : 0.0;
			float Vz = cahvFrame.Vz = (pieces[_Vz] != "") ? ofToFloat(pieces[_Vz]) : 0.0;
			
			float sx = cahvFrame.sx = (pieces[_sx] != "") ? ofToFloat(pieces[_sx]) : 0.0;
			float sy = cahvFrame.sy = (pieces[_sy] != "") ? ofToFloat(pieces[_sy]) : 0.0;
			
			float Width  = cahvFrame.Width  = (pieces[_Width]  != "") ? ofToFloat(pieces[_Width])  : 0.0;
			float Height = cahvFrame.Height = (pieces[_Height] != "") ? ofToFloat(pieces[_Height]) : 0.0;
			
			//float focal = cahvFrame.focal = (pieces[_f] != "") ? ofToFloat(pieces[_f]) : 0.0;
			
			float px = cahvFrame.px = -1.0;
			float py = cahvFrame.py = 1.0;
			
			cahvScene.push_back(cahvFrame);
			
			/* Formel */
			float _x = px - Width / 2.0; 
			float _y = py - Height / 2.0; 
			
			float AH = (Ax * Hx + Ay * Hy + Az * Hz);
			float AV = (Ax * Vx + Ay * Vy + Az * Vz);
			
			float xh = _x - AH;
			float yh = _y - AV;
			
			float H0x = Hx - AH * Ax;
			float H0y = Hy - AH * Ay;
			float H0z = Hz - AH * Az;
			
			float V0x = Vx - AV * Ax;
			float V0y = Vy - AV * Ay;
			float V0z = Vz - AV * Az;
			
			float H0 = sqrt( H0x * H0x + H0y * H0y + H0z * H0z );
			float V0 = sqrt( V0x * V0x + V0y * V0y + V0z * V0z ); 
			
			float H0x_ = H0x / H0;
			float H0y_ = H0y / H0;
			float H0z_ = H0z / H0;
			
			float V0x_ = V0x / V0;
			float V0y_ = V0y / V0;
			float V0z_ = V0z / V0;
			
			float Fx = Hx - Ax * AH;
			float Fy = Hy - Ay * AH;
			float Fz = Hz - Az * AH;
			
			float IFI = sqrt( Fx * Fx + Fy * Fy + Fz * Fz );
			float f = IFI * px;
			float focal = IFI / 100;
			
			float Lx = f * Ax + sx * (_x-xh) * H0x_ + sy * (_y-yh) * V0x_;
			float Ly = f * Ay + sx * (_x-xh) * H0y_ + sy * (_y-yh) * V0y_;
			float Lz = f * Az + sx * (_x-xh) * H0z_ + sy * (_y-yh) * V0z_;
			
			/* Winkel Berechnung
			 float ILI = sqrt( Lx * Lx + Ly * Ly + Lz * Lz );
			 
			 float alpha = atan2(Ly,Lx);
			 float beta  = acos(Lz);
			 */
			
			//cout << "zoom: " << IFI << "  x: " << Lx << "  y: " << Ly << endl;
			if (focal > maxFocal) maxFocal = focal;
			
			/* HACK FOR VARININg IMAGE SIZES */
			float multi = 1.0;
			if (Width == 320) multi = 2.0;
			
			position.set(Lx*multi, Ly*multi, focal);
			positions.push_back(position);
		}
		
	}
	
	data.close();
	
	for (int i = 0; i < positions.size(); i++) {
		float _x = positions[i].x;
		float _y = positions[i].y;
		float _z = positions[i].z / maxFocal;
		positions[i].set(_x, _y, _z);
		
		//cout << "zoom:	" << _z << "	x: " << _x << "		y: " << _y << endl;
	}
	
	frames = cahvScene.size() + 1;
	size = cahvScene.size();
	
	if (size > 0) ready = true;	
	cout << "Number of 6DOF Position Points: " << size << endl;
}


