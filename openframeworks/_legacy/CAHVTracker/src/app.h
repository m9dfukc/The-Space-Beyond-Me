#ifndef _APP
#define _APP


#include "ofMain.h"
#include "ofxDirList.h"
#include "CAHVFile.h"


class app : public ofBaseApp{

	public:

		void setup();
		void update();
		void draw();

		void keyPressed  (int key);
		void keyReleased (int key);

		void mouseMoved(int x, int y );
		void mouseDragged(int x, int y, int button);
		void mousePressed(int x, int y, int button);
		void mouseReleased(int x, int y, int button);
		void windowResized(int w, int h);

		// we will have a dynaimic number of images, based on the content of a director:
		int 		nImages;
		ofImage	* 	images;
        ofxDirList   DIR;

        int         currentImage;
		CAHVFile	cahv;

};

#endif

