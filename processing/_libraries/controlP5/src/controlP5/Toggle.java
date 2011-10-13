package controlP5;

import processing.core.PApplet;

/**
 * a toggle can have two states, true and false, where true has the value 1 and
 * false is 0.
 * 
 * @example ControlP5toggle
 * @nosuperclasses Controller
 * @related Controller
 */
public class Toggle extends Controller {

	int cnt;

	protected boolean isOn = false;

	protected float internalValue = -1;

	/**
	 * @invisible
	 * @param theControlP5
	 *            ControlP5
	 * @param theParent
	 *            Tab
	 * @param theName
	 *            String
	 * @param theValue
	 *            float
	 * @param theX
	 *            float
	 * @param theY
	 *            float
	 * @param theWidth
	 *            int
	 * @param theHeight
	 *            int
	 */
	public Toggle(
	        ControlP5 theControlP5,
	        Tab theParent,
	        String theName,
	        float theValue,
	        float theX,
	        float theY,
	        int theWidth,
	        int theHeight) {
		super(theControlP5, theParent, theName, theX, theY, theWidth, theHeight);
		_myValue = theValue;
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
		if (isActive) {
			theApplet.fill(color.colorActive);
		} else {
			theApplet.fill(isOn ? color.colorActive : color.colorForeground);
		}
		theApplet.rect(0, 0, width, height);
		if(isLabelVisible) {
			_myCaptionLabel.draw(theApplet, 0, height + 4);
		}
		theApplet.popMatrix();
	}

	protected void onEnter() {
		isActive = true;
	}

	protected void onLeave() {
		isActive = false;
	}

	/**
	 * @invisible
	 */
	public void mousePressed() {
		setState(!isOn);
		isActive = false;
	}

	/**
	 * @invisible
	 * @param theValue
	 *            float
	 */
	public void setValue(
	        float theValue) {
		if (theValue == 0) {
			setState(false);
		} else {
			setState(true);
		}
	}

	public void update() {
		setValue(_myValue);
	}

	/**
	 * set the state of the toggle, which can be true or false.
	 * 
	 * @param theFlag
	 *            boolean
	 */
	public void setState(
	        boolean theFlag) {
		isOn = theFlag;
		_myValue = (isOn == false) ? 0 : 1;
		broadcast(FLOAT);
	}

	public boolean getState() {
		return isOn;
	}

	protected void deactivate() {
		isOn = false;
		_myValue = (isOn == false) ? 0 : 1;
	}

	protected void activate() {
		isOn = true;
		_myValue = (isOn == false) ? 0 : 1;
	}
	
	protected void toggle() {
		if(isOn) {
			deactivate();
		} else {
			activate();
		}
	}
	/**
	 * @invisible
	 * @param theElement
	 *            ControlP5XMLElement
	 */
	public void addToXMLElement(
	        ControlP5XMLElement theElement) {
		theElement.setAttribute("type", "toggle");
	}

	/**
	 * by default a toggle returns 0 (for off) and 1 (for on). the internal
	 * value variable can be used to store an additional value for a toggle
	 * event.
	 * 
	 * @param theInternalValue
	 */
	public void setInternalValue(
	        float theInternalValue) {
		internalValue = theInternalValue;
	}

	public float internalValue() {
		return internalValue;
	}

}
