/*
 *  CAHVFile.cpp
 *  SpaceBeyond
 *
 *  Created by m9dfukc on 12.06.10.
 *  Copyright 2010 invertednothing. All rights reserved.
 *
 */
#include "CAHVFile.h"

void CAHVFile::setup(string _scene, string _posFile) {
	posFile = _posFile;
	scene   = _scene;
	path	= scene+"/"+_posFile;
	
	string line;
	ifstream data;
	data.open(ofToDataPath(path).c_str());
	
	while(!data.eof()){
		getline(data, line);
		cout << "Reading: " << line << endl;
	}
	
	data.close();
	
	
}

