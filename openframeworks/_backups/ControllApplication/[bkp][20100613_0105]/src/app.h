#ifndef _APP
#define _APP


#include "ofMain.h"
#include "CAHV.h"
#include "fileHelper.h"
#include <iostream>
#include <fstream>


class app : public ofBaseApp{

	public:

		void setup();
		void update();
		void draw();
	
		void drawScreenGrid();

		void keyPressed  (int key);
		void keyReleased (int key);

		void mouseMoved(int x, int y );
		void mouseDragged(int x, int y, int button);
		void mousePressed(int x, int y, int button);
		void mouseReleased(int x, int y, int button);
		void windowResized(int w, int h);

		CAHV		cahv;
		bool		playToggle;
		ofstream	myFile;
		string		saveFile;
		string		buffer;
		fileHelper  ioHelper;

};

#endif

