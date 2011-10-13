package controlP5;

import processing.core.PApplet;

/**
 * a knob. description tbd.
 * 
 * @example ControlP5knob
 * @nosuperclasses Controller
 * @related Controller
 */
public class Knob extends Controller {
	
	/*
	 * @todo
	 * make it look like a synthesizer knob.
	 * add option for only-one-cyle and multi-cycle.
	 */
	
	int cnt;

	float _myDiameter;

	float _myRadius;
	
	float _myOffsetAngle = PI;
	
	float _myAngle = _myOffsetAngle;
	
	

	/**
	 * @invisible
	 * @param theControlP5
	 *            ControlP5
	 * @param theParent
	 *            ControllerGroup
	 * @param theName
	 *            String
	 * @param theMin
	 *            float
	 * @param theMax
	 *            float
	 * @param theDefaultValue
	 *            float
	 * @param theX
	 *            int
	 * @param theY
	 *            int
	 * @param theWidth
	 *            int
	 */
	public Knob(
	        ControlP5 theControlP5,
	        ControllerGroup theParent,
	        String theName,
	        float theMin,
	        float theMax,
	        float theDefaultValue,
	        int theX,
	        int theY,
	        int theWidth) {
		super(theControlP5, theParent, theName, theX, theY, theWidth, theWidth);
		_myValue = theDefaultValue;
		setMin(theMin);
		setMax(theMax);
		_myDiameter = theWidth;
		_myRadius = _myDiameter / 2;
		_myUnit = (_myMax - _myMin) / ControlP5Constants.TWO_PI;
	}

	/**
	 * @see ControllerInterfalce.updateInternalEvents
	 * @invisible
	 */
	public void updateInternalEvents(
	        PApplet theApplet) {
		if (isMousePressed && !controlP5.keyHandler.isAltDown) {
			_myAngle = (float) Math.atan2(_myControlWindow.mouseY - _myRadius - _myParent.absolutePosition().y()
			        - position.y(), _myControlWindow.mouseX - _myRadius - _myParent.absolutePosition().x()
			        - position.x());
			setValue(((_myAngle + _myOffsetAngle) * _myUnit)+_myMin);
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
		theApplet.translate(position().x() + _myRadius, position().y() + _myRadius);
		theApplet.fill(color.colorBackground);
		theApplet.ellipse(-_myRadius, -_myRadius, width, height);
		// !!! add strokeWeight(1) so that the knob lines are
		// not affected by other strokeWeights. check if the strokeWeight
		// can be reset to its previous value after rendering the knob.
		float myPreviousStrokeWeight = theApplet.g.strokeWeight;
		theApplet.strokeWeight(1);
		theApplet.pushMatrix();
		theApplet.stroke(color.colorForeground);
		theApplet.line(-_myRadius / 2, 0, -_myRadius, 0);

		theApplet.rotate(_myAngle);
		theApplet.stroke(color.colorActive);
		theApplet.line(0, 0, _myRadius, 0);
		theApplet.popMatrix();

		theApplet.noStroke();
		theApplet.pushMatrix();
		theApplet.translate(-_myRadius, -_myRadius);
		_myCaptionLabel.draw(theApplet, 4, height + 4);
		theApplet.popMatrix();
		theApplet.popMatrix();
		theApplet.strokeWeight(myPreviousStrokeWeight );
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
	public void mousePressed() {}

	/**
	 * @invisible
	 */
	public void mouseReleased() {
		isActive = true;
		broadcast(FLOAT);
	}

	/**
	 * set the minimum level of the knob.
	 * 
	 * @param theValue
	 *            float
	 */
	public void setMin(
	        float theValue) {
		_myMin = theValue;
	}

	/**
	 * set the maximum level of the knob.
	 * 
	 * @param theValue
	 *            float
	 */
	public void setMax(
	        float theValue) {
		_myMax = theValue;
	}

	public void setValue(
	        float theValue) {
		_myAngle = ((theValue-_myMin) / _myUnit) - _myOffsetAngle;
		_myValue = theValue;
		broadcast(FLOAT);
	}

	public void update() {
		setValue(_myValue);
	}
	
	public void setOffsetAngle(float theValue) {
		_myOffsetAngle = theValue;
	}
	/**
	 * @invisible
	 * @param theElement
	 *            ControlP5XMLElement
	 */
	public void addToXMLElement(
	        ControlP5XMLElement theElement) {
		theElement.setAttribute("type", "knob");
		theElement.setAttribute("min", new Float(min()));
		theElement.setAttribute("max", new Float(max()));
	}

}
