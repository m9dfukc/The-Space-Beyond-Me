package controlP5;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class ControllerSprite {

	// http://www.filamentgroup.com/examples/buttonFrameworkCSS
	
	protected PImage sprite;
	protected PImage display;
	protected PImage mask;
	protected int offsetX;
	protected int offsetY;
	protected int width;
	protected int height;
	protected int wh;
	protected boolean isMask;
	protected int _myState;
	
	public ControllerSprite(ControlP5 theControlP5,
	        PImage theImage,
	        int theWidth,
	        int theHeight) {
		sprite = theImage;
		width = theWidth;
		height = theHeight;
		wh = width*height;
		_myState = 0;
		display = new PImage(theWidth, theHeight);
		display = theControlP5.papplet.createImage(theWidth, theHeight,PApplet.RGB);
		update();

	}

	public void draw(
	        PApplet theApplet) {
		theApplet.pushStyle();
		theApplet.imageMode(PApplet.CORNER);
		if(isMask) {
			display.mask(mask);
		}
		theApplet.image(display, 0, 0);
		theApplet.popStyle();
	}

	public void update() {
		display.loadPixels();
		System.arraycopy(sprite.pixels,wh * _myState,display.pixels,0,wh);
		display.updatePixels();
	}

	public void setOffset(
	        int theX,
	        int theY) {
		offsetX = theX;
		offsetY = theY;
		update();
	}
	
	public void setState(int theState) {
		if(theState!=_myState) {
		_myState = theState;
		offsetY = height * _myState;
		update();
		}
	}
	
	public int getState() {
		return _myState;
	}
	
	public int width() {
		return width;
	}

	public int height() {
		return height;
	}

	public void setMask(
	        PImage theImage) {
		mask = theImage;
	}

	public void enableMask() {
		isMask = true;
	}

	public void disableMask() {
		isMask = true;
	}
	
}
