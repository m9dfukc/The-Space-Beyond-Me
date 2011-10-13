package controlP5;

/**
 * controlP5 is a processing and java library for creating simple control GUIs.
 *
 *  2007 by Andreas Schlegel
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307 USA
 *
 * @author Andreas Schlegel (http://www.sojamo.de)
 *
 */

import processing.core.PApplet;

/**
 * a button executes after release and can be applied to methods and functions.
 * for a full documentation of this controller see the <a
 * href="./controller_class_controller.htm">controller</a> class.
 * 
 * @example ControlP5button
 * @nosuperclasses Controller
 * @related Controller
 */
public class Button extends Controller {

	int cnt;

	protected boolean isPressed;

	protected boolean isOn;

	/**
	 * @param theControlP5
	 *            ControlP5
	 * @param theParent
	 *            ControllerGroup
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
	 * @invisible
	 */
	protected Button(
	        ControlP5 theControlP5,
	        ControllerGroup theParent,
	        String theName,
	        float theDefaultValue,
	        int theX,
	        int theY,
	        int theWidth,
	        int theHeight) {
		super(theControlP5, theParent, theName, theX, theY, theWidth, theHeight);
		_myValue = theDefaultValue;
	}

	protected Button(
	        ControlP5 theControlP5,
	        ControllerGroup theParent,
	        String theName,
	        float theDefaultValue,
	        int theX,
	        int theY,
	        int theWidth,
	        int theHeight,
	        boolean theBroadcastFlag) {
		super(theControlP5, theParent, theName, theX, theY, theWidth, theHeight);
		_myValue = theDefaultValue;
	}

	public Button() {
		super(null, null, null, 0, 0, 1, 1);
	}

	public Button(
	        ControlP5 theControlP5,
	        String theName) {
		super(theControlP5, theControlP5.tab("default"), theName, 0, 0, 1, 1);
	}


	/**
	 * @invisible
	 * @example ControlP5button
	 * @param theApplet
	 *            PApplet
	 */
	public void draw(
	        PApplet theApplet) {
		theApplet.pushMatrix();
		theApplet.translate(position.x, position.y);
		if (!isSprite) {
			if (isInside || isOn) {
				theApplet.fill(color.colorActive);
			} else {
				theApplet.fill(color.colorBackground);
			}

			theApplet.rect(0, 0, width, height);
			_myCaptionLabel.draw(theApplet, 4, height / 2 - 3);
		} else {
			if (isActive) {
				if (isPressed) {
					sprite.setState(2);
				} else {
					sprite.setState(1);
				}
			} else {
				sprite.setState(0);
			}
			theApplet.fill(0);
			sprite.draw(theApplet);
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
		isActive = isInside;
		isPressed = true;
	}

	/**
	 * @invisible
	 */
	public void mouseReleased() {
		isPressed = false;
		if (isActive) {
			isActive = false;
			// added. if a tab changes due to a mousePressed
			// and tab changes, 'isInside' has to be set to false
			isInside = false;
			setValue(_myValue);
		}
	}

	public void mouseReleasedOutside() {
		mouseReleased();
	}

	/**
	 * set the value of the button controller.
	 * 
	 * @param theValue
	 *            float
	 */
	public void setValue(
	        float theValue) {
		_myValue = theValue;
		broadcast(FLOAT);
	}

	/**
	 * @invisible
	 */
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
		theElement.setAttribute("type", "button");
	}

}
