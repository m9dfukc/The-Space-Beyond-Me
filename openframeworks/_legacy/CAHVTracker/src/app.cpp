#include "app.h"


//--------------------------------------------------------------
void app::setup(){
	
	cahv.setup("zomm-out");


    DIR.setVerbose(false);
    nImages = DIR.listDir("images/of_logos");
 	images = new ofImage[nImages];
    //you can now iterate through the files as you like
    for(int i = 0; i < nImages; i++){
            images[i].loadImage(DIR.getPath(i));
    }
    currentImage = 0;

    ofBackground(255,255,255);

}

//--------------------------------------------------------------
void app::update(){

}

//--------------------------------------------------------------
void app::draw(){



    if (nImages > 0){
        ofSetColor(0xffffff);
        images[currentImage].draw(300,50);
        ofSetColor(0x999999);
        string pathInfo = DIR.getPath(currentImage)
                          + "\n\n" + "press any key to advance current image"
                           + "\n\n" + "many thanks to hikaru furuhashi for the OFs" ;
        ofDrawBitmapString(pathInfo, 300,images[currentImage].height + 80);
    }

    ofSetColor(0x000000);
    for(int i = 0; i < nImages; i++){
            if (currentImage == i) ofSetColor(0xff0000);
            else ofSetColor(0x000000);
            string fileInfo = "file " + ofToString(i+1) + " = " + DIR.getName(i); // +  "path is " + DIR.getPath(i);
            ofDrawBitmapString(fileInfo, 50,i*20 + 50);
    }


}

//--------------------------------------------------------------
void app::keyPressed  (int key){
    if (nImages > 0){
        currentImage++;
        currentImage %= nImages;
    }
}

//--------------------------------------------------------------
void app::keyReleased  (int key){
}

//--------------------------------------------------------------
void app::mouseMoved(int x, int y ){
}

//--------------------------------------------------------------
void app::mouseDragged(int x, int y, int button){
}

//--------------------------------------------------------------
void app::mousePressed(int x, int y, int button){
}

//--------------------------------------------------------------
void app::mouseReleased(int x, int y, int button){

}

//--------------------------------------------------------------
void app::windowResized(int w, int h){

}

