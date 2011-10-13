package controlP5;

import processing.core.PApplet;

/**
 * @shortdesc press the mouse inside of the numberbox and move up and down to change the
 * values of a numberbox.
 * 
 * 
 * @example ControlP5numberbox
 * @nosuperclasses Controller
 * @related Controller
 */
public class Numberbox extends Controller {
	
	/*
	 * @todo
	 * numberboxes in pd: up=higher, down=lower
	 * till version 0.3.2 it has been the other way round,
	 * with version 0.3.3 this has been reversed and follows the
	 * pd convention.
	 * 
	 * properly implement the increasing and decreasing of numbers.
	 * do so for up, down as well as left and right.
	 */
	
	protected int cnt;

	protected boolean isActive;
	
	public static int LEFT = 0;
	public static int UP = 1;
	public static int RIGHT = 2;
	public static int DOWN = 3;
	
	protected int _myNumberCount = UP;
	
	protected float _myMultiplier = 1; 

	/**
	 * @invisible
	 * @param theControlP5
	 *            ControlP5
	 * @param theParent
	 *            Tab
	 * @param theName
	 *            String
	 * @param theDefaultValue
	 *            float
	 * @param theX
	 *            int
	 * @param theY
	 *            int
	 * @param theWidth
	 *            int
	 * @param theHeight
	 *            int
	 */
	public Numberbox(
	        ControlP5 theControlP5,
	        Tab theParent,
	        String theName,
	        float theDefaultValue,
	        int theX,
	        int theY,
	        int theWidth,
	        int theHeight) {
		super(theControlP5, theParent, theName, theX, theY, theWidth, theHeight);
		_myValue = theDefaultValue;
		_myValueLabel = new Label("" + _myValue, theWidth, 12, color.colorValue);
		_myMin = -1000000;
		_myMax = 1000000;
	}

	/**
	 * @see ControllerInterfalce.updateInternalEvents
	 * @invisible
	 */
	public void updateInternalEvents(
	        PApplet theApplet) {
		if (isActive) {
			if (!ControlP5.keyHandler.isAltDown) {
				setValue(_myValue + (_myControlWindow.mouseY - _myControlWindow.pmouseY)*_myMultiplier);
			}
		}
	}
	

	/**
	 * @invisible
	 * @param theApplet
	 *            PApplet
	 */
	public void draw(
	        PApplet theApplet) {
		theApplet.pushMatrix();
		theApplet.translate(position().x(), position().y());
		theApplet.stroke(color.colorForeground);
		theApplet.fill(color.colorBackground);
		theApplet.rect(0, 0, width, height);
		theApplet.noStroke();
		_myCaptionLabel.draw(theApplet, 4, height + 4);
		_myValueLabel.draw(theApplet, 4, 4);
		theApplet.popMatrix();
	}

	/**
	 * @invisible
	 */
	public void mousePressed() {
		isActive = true;
	}

	/**
	 * @invisible
	 */
	public void mouseReleased() {
		isActive = false;
	}
	
	public void mouseReleasedOutside() {
		mouseReleased();
	}
	
	public void setMultiplier(float theMultiplier) {
		_myMultiplier = theMultiplier;
	}
	
	public float multiplier() {
		return _myMultiplier;
	}
	/**
	 * set the value of the numberbox.
	 * 
	 * @param theValue
	 *            float
	 */
	public void setValue(
	        float theValue) {
		_myValue = theValue;
		_myValue = Math.max(_myMin, Math.min(_myMax,_myValue));
		broadcast(FLOAT);
		_myValueLabel.set(adjustValue(_myValue));
	}
	
	public void setDirection(int theValue) {
		if(theValue>=0 && theValue<=DOWN) {
			_myNumberCount = theValue;
		}
	}

	public void update() {
		setValue(_myValue);
	}

	/**
	 * @invisible
	 * @param theElement
	 *            ControlP5XMLElement
	 */
	public void addToXMLElement(
	        ControlP5XMLElement theElement) {
		theElement.setAttribute("type", "numberbox");
	}

}
