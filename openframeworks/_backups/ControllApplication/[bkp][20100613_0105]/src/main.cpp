#include "ofMain.h"
#include "app.h"
#include "ofAppGlutWindow.h"

//========================================================================
int main( ){

	/* Fläche der Bühne: 1148 x 200 cm */
    ofAppGlutWindow window;
	ofSetupOpenGL(&window, 4140,720, OF_WINDOW);			// <-------- setup the GL context

	// this kicks off the running of my app
	// can be OF_WINDOW or OF_FULLSCREEN
	// pass in width and height too:
	ofRunApp( new app());

}
