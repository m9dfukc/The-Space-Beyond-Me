package controlP5;

import processing.core.PApplet;

/**
 * a slider is either used horizontally or vertically. when adding a slider to controlP5, 
 * the width is compared versus the height. width is bigger, you get a horizontal slider,
 * height is bigger, you get a vertical slider.
 * a slider can have a fixed slide controller (one end of the slider is fixed to the left or
 * bottom side of the controller), or a flexible slide control (a knob that you can drag).
 * 
 * 
 * @example ControlP5slider
 * @nosuperclasses Controller
 * @related Controller
 */
public class Slider extends Controller {

	protected static final int HORIZONTAL = 0;

	protected static final int VERTICAL = 1;

	private int _myDirection;

	public final static int FIX = 1;

	public final static int FLEXIBLE = 0;

	protected int _mySliderMode = FIX;

	protected float _myValuePosition;

	protected float _mySliderbarSize = 0;
	
	protected int steps = -1;
	
	/*
	 * @todo currently the slider value goes up and down linear,
	 * provide an option to make it logarithmic, potential, curved.
	 * */
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
	 * @param theHeight
	 *            int
	 */
	public Slider(
	        ControlP5 theControlP5,
	        ControllerGroup theParent,
	        String theName,
	        float theMin,
	        float theMax,
	        float theDefaultValue,
	        int theX,
	        int theY,
	        int theWidth,
	        int theHeight) {
		super(theControlP5, theParent, theName, theX, theY, theWidth, theHeight);
		_myCaptionLabel = new Label(theName, color.colorLabel);
		_myMin = theMin;
		_myMax = theMax;
		// initialize the valueLabel with the longest string available, which is
		// either theMax or theMin.
		_myValueLabel = new Label("" + (((adjustValue(_myMax)).length() > (adjustValue(_myMin)).length()) ? adjustValue(_myMax):adjustValue(_myMin)), color.colorValue);
		// after initializing valueLabel, set the value to
		// the default value.		
		_myValueLabel.set(""+adjustValue(_myValue));
		_myValue = theDefaultValue;
		
		setSliderMode(FIX);
		_myDirection = (width > height) ? HORIZONTAL : VERTICAL;
		valueLabel();
	}

	/**
	 * use the slider mode to set the mode of the slider bar, which can be
	 * Slider.FLEXIBLE or Slider.FIX
	 * 
	 * @param theMode
	 *            int
	 */
	public void setSliderMode(
	        int theMode) {
		_mySliderMode = theMode;
		if (_mySliderMode == FLEXIBLE) {
			_mySliderbarSize = 10;
		} else {
			_mySliderbarSize = 0;
		}
		_myUnit = (_myMax - _myMin) / ((width > height) ? width - _mySliderbarSize : height - _mySliderbarSize);
		setValue(_myValue);
	}

	/**
	 * @see ControllerInterfalce.updateInternalEvents
	 * @invisible
	 */
	public void updateInternalEvents(
	        PApplet theApplet) {
		if (isVisible) {
			if (isMousePressed && !ControlP5.keyHandler.isAltDown) {
				if (_myDirection == HORIZONTAL) {
					setValue(_myMin + (_myControlWindow.mouseX - (_myParent.absolutePosition().x() + position.x))
					        * _myUnit);
				} else {
					setValue(_myMin
					        + (-(_myControlWindow.mouseY - (_myParent.absolutePosition().y() + position.y) - height))
					        * _myUnit);
				}
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
		if (isVisible) {
			theApplet.pushMatrix();
			theApplet.translate(position().x(), position().y());
			theApplet.fill(color.colorBackground);
			theApplet.noStroke();
			theApplet.rect(0, 0, width, height);
			theApplet.fill(isInside ? color.colorActive : color.colorForeground);
			if (_myDirection == HORIZONTAL) {
				if (_mySliderMode == FIX) {
					theApplet.rect(0, 0, _myValuePosition, height);
				} else {
					theApplet.rect(_myValuePosition, 0, _mySliderbarSize, height);
				}
			} else {
				if (_mySliderMode == FIX) {
					theApplet.rect(0, height, width, -_myValuePosition);
				} else {
					theApplet.rect(0, height - _myValuePosition - _mySliderbarSize, width, _mySliderbarSize);
				}
			}

			if (isLabelVisible) {
				if (_myDirection == HORIZONTAL) {
					_myCaptionLabel.draw(theApplet, width + 3, height / 2 - 3);
					_myValueLabel.draw(theApplet, 3, height / 2 - 3);
				} else {
					_myCaptionLabel.draw(theApplet, 0, height + 3);
					_myValueLabel.draw(theApplet, width + 4, -(int) _myValuePosition + height - 8);
				}
			}

			theApplet.popMatrix();
		}
	}

	/**
	 * set the value of the slider.
	 * 
	 * @param theValue
	 *            float
	 */
	public void setValue(
	        float theValue) {
		_myValue = theValue;
//		if(steps>0) {
//			float myRange = _myValue/(Math.abs(_myMax) - Math.abs(_myMin));
//			_myValue = (int)((myRange * steps*10)/((steps*10)/steps))/(float)steps;
//			_myValue *= myRange;
//		}
//		System.out.println(_myValue+" / "+(int)((_myValue * steps*10)/((steps*10)/steps)));
		_myValue = (_myValue <= _myMin) ? _myMin : _myValue;
		_myValue = (_myValue >= _myMax) ? _myMax : _myValue;
		_myValuePosition = ((_myValue - _myMin) / _myUnit);
		_myValueLabel.set(adjustValue(_myValue));
		
		broadcast(FLOAT);
	}
	
//	public void setSteps(int theSteps) {
//		steps = theSteps;
//	}
//	
	
	
	public void update() {
		setValue(_myValue);
	}

	/**
	 * set the minimum value of the slider.
	 * 
	 * @param theValue
	 *            float
	 */
	public void setMin(
	        float theValue) {
		_myMin = theValue;
		setSliderMode(_mySliderMode);
	}

	/**
	 * set the maximum value of the slider.
	 * 
	 * @param theValue
	 *            float
	 */
	public void setMax(
	        float theValue) {
		_myMax = theValue;
		setSliderMode(_mySliderMode);
	}

	/**
	 * set the width of the slider.
	 * 
	 * @param theValue
	 *            int
	 */
	public Controller setWidth(
	        int theValue) {
		width = theValue;
		setSliderMode(_mySliderMode);
		return this;
	}

	/**
	 * set the height of the slider.
	 * 
	 * @param theValue
	 *            int
	 */
	public Controller setHeight(
	        int theValue) {
		height = theValue;
		setSliderMode(_mySliderMode);
		return this;
	}

	/**
	 * @invisible
	 * @param theElement
	 *            ControlP5XMLElement
	 */
	public void addToXMLElement(
	        ControlP5XMLElement theElement) {
		theElement.setAttribute("type", "slider");
		theElement.setAttribute("min", new Float(min()));
		theElement.setAttribute("max", new Float(max()));
	}

}
