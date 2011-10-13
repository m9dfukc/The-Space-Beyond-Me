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
 * a bang controller triggers an event when pressed. It can only be applied to a method
 * assigned to it and not to fields. for a full documentation of this
 * controller see the <a href="./controller_class_controller.htm">controller</a>
 * class.
 * 
 * @example ControlP5bang
 * @nosuperclasses Controller
 * @related Controller
 */
public class Bang extends Controller {

	protected int cnt;

	
	protected int triggerId = PRESSED;

	/**
	 * @param theControlP5
	 *            ControlP5
	 * @param theParent
	 *            ControllerGroup
	 * @param theName
	 *            String
	 * @param theX
	 *            float
	 * @param theY
	 *            float
	 * @param theWidth
	 *            int
	 * @param theHeight
	 *            int
	 * @invisible
	 */
	protected Bang(
	        ControlP5 theControlP5,
	        ControllerGroup theParent,
	        String theName,
	        float theX,
	        float theY,
	        int theWidth,
	        int theHeight) {
		super(theControlP5, theParent, theName, theX, theY, theWidth, theHeight);
		_myValue = 1;
	}

	/**
	 * @invisible
	 * @param theApplet
	 *            PApplet
	 */
	public void draw(
	        PApplet theApplet) {
		theApplet.pushMatrix();
		theApplet.translate(position.x, position.y);

		if (isActive) {
			theApplet.fill(color.colorActive);
		} else {
			theApplet.fill(color.colorForeground);
		}

		if (cnt < 0) {
			theApplet.fill(color.colorForeground);
			cnt++;
		}

		theApplet.rect(0, 0, width, height);
		_myCaptionLabel.draw(theApplet, 0, height + 4);
		theApplet.popMatrix();
	}

	/**
	 * @invisible
	 */
	protected void onEnter() {
		cnt = 0;
		isActive = true;
	}

	/**
	 * @invisible
	 */
	protected void onLeave() {
		isActive = false;
	}

	/**
	 * @invisible
	 */
	protected void mousePressed() {
		if (triggerId == PRESSED) {
			cnt = -3;
			isActive = true;
			update();
		}
	}

	/**
	 * @invisible
	 */
	protected void mouseReleased() {
		if (triggerId == RELEASE) {
			cnt = -3;
			isActive = true;
			update();
		}
	}
	
	protected void mouseReleasedOutside() {
		// if _myTriggerId==RELEASE, the event is not
		// triggered when mouse is released outside, since
		// the event would be triggered for any mouse
		// release even though the controller is not acitve.
		// therefore mouseReleased() is not called in here. 
		onLeave();
	} 

	/**
	 * by default a bang is triggered when the mouse is pressed. use
	 * setTriggerEvent(Bang.PRESSED) or
	 * setTriggerEvent(Bang.RELEASE) to define the action for triggering
	 * a bang. currently only Bang.PRESSED and Bang.RELEASE are supported. 
	 * 
	 * @param theEventID
	 */
	public void setTriggerEvent(
	        int theEventID) {
		triggerId = theEventID;
	}

	/**
	 * set the value of the bang controller. since bang can be true or false,
	 * false=0 and true=1
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
		theElement.setAttribute("type", "bang");
	}

}
