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
 * ControlGroup extends ControllerGroup, for a list and documentation for
 * available methods see the <a
 * href="./controllergroup_class_controllergroup.htm">ControllerGroup</a>
 * documentation.
 * 
 * use a controlGroup to bundle several controllers. controlGroups can be closed
 * and opened to keep the screen organized.
 * 
 * @example ControlP5group
 * @nosuperclasses ControllerGroup
 * @related ControllerGroup
 */
public class ControlGroup extends ControllerGroup implements ControlListener {

	// TODO implement control events for controlGroups.
	// http://processing.org/discourse/yabb_beta/YaBB.cgi?board=LibraryProblems;action=display;num=1228715265;start=0#0
	protected Button _myCloseButton;

	protected int _myBackgroundHeight = 0;

	protected int _myBackgroundColor = 0x00ffffff;

	protected boolean isEventActive = false;

	protected boolean isBarVisible = true;

	/**
	 * 
	 * @invisible
	 * @param theControlP5
	 *            ControlP5
	 * @param theParent
	 *            ControllerGroup
	 * @param theName
	 *            String
	 * @param theX
	 *            int
	 * @param theY
	 *            int
	 */
	public ControlGroup(
	        ControlP5 theControlP5,
	        ControllerGroup theParent,
	        String theName,
	        int theX,
	        int theY,
	        int theW,
	        int theH) {
		super(theControlP5, theParent, theName, theX, theY);
		_myValueLabel = new Label("-");
		_myWidth = theW;
		_myHeight = theH;
	}

	/**
	 * @invisible
	 */
	public void mousePressed() {
		if (isBarToggle) {
			if (!controlP5.keyHandler.isAltDown) {
				isOpen = !isOpen;
				_myValueLabel.set(isOpen ? "-" : "+");

				if (isEventActive) {
					controlP5.controlbroadcaster().broadcast(new ControlEvent(this), ControlP5Constants.METHOD);
				}
			}
		}
	}

	/**
	 * activate or deactivate the Event status of a tab.
	 * 
	 * @param theFlag
	 *            boolean
	 */
	public ControlGroup activateEvent(
	        boolean theFlag) {
		isEventActive = theFlag;
		return this;
	}

	public int getBackgroundHeight() {
		return _myBackgroundHeight;
	}

	public void setBackgroundHeight(
	        int theHeight) {
		_myBackgroundHeight = theHeight;
	}

	public void setBackgroundColor(
	        int theColor) {
		_myBackgroundColor = theColor;
	}

	public void setBarHeight(
	        int theHeight) {
		_myHeight = theHeight;
	}

	protected void preDraw(
	        PApplet theApplet) {
		if (isOpen) {
			theApplet.fill(_myBackgroundColor);
			theApplet.rect(0, 0, _myWidth, _myBackgroundHeight);
		}
	}

	protected void postDraw(
	        PApplet theApplet) {
		if (isBarVisible) {
			theApplet.fill(isInside ? color.colorForeground : color.colorBackground);
			theApplet.rect(0, -1, _myWidth, -_myHeight);
			_myLabel.draw(theApplet, 2, -_myHeight);
			_myValueLabel.draw(theApplet, _myWidth - 9, -_myHeight);
		}
	}

	/**
	 * @invisible
	 * @param theElement
	 *            ControlP5XMLElement
	 */
	public void addToXMLElement(
	        ControlP5XMLElement theElement) {
		theElement.setName("group");
		theElement.setAttribute("width", new Integer(_myWidth));
		theElement.setAttribute("height", new Integer(_myHeight));
		for (int i = 0; i < controllers.size(); i++) {
			if (((ControllerInterface) controllers.get(i)).isXMLsavable()) {
				theElement.addChild(((ControllerInterface) controllers.get(i)).getAsXML());
			}
		}
	}

	/**
	 * add a close button to the controlbar of this controlGroup.
	 */
	public void addCloseButton() {
		if (_myCloseButton == null) {
			_myCloseButton = new Button(controlP5, this, name() + "close", 1, _myWidth + 1, -10, 12, 9);
			_myCloseButton.setLabel("X");
			_myCloseButton.addListener(this);
		}
	}

	/**
	 * remove the close button.
	 */
	public void removeCloseButton() {
		if (_myCloseButton == null) {
			_myCloseButton.remove();
		}
		_myCloseButton = null;
	}

	public void hideBar() {
		isBarVisible = false;
	}

	public void showBar() {
		isBarVisible = true;
	}

	public boolean isBarVisible() {
		return isBarVisible;
	}

	/**
	 * @invisible
	 * @param theEvent
	 *            ControlEvent
	 */
	public void controlEvent(
	        ControlEvent theEvent) {
		if (theEvent.controller().name().equals(name() + "close")) {
			hide();
		}
	}

	/**
	 * !!! experimental, see ControllerGroup.value()
	 * 
	 * @invisible
	 * @return String
	 */
	public String stringValue() {
		return "" + _myValue;
	}

	/**
	 * !!! experimental, see ControllerGroup.value()
	 * 
	 * @invisible
	 * @return float
	 */
	public float value() {
		return _myValue;
	}

	public float[] arrayValue() {
		return _myArrayValue;
	}

	protected void setArrayValue(
	        float[] theArray) {
		_myArrayValue = theArray;
	}
}
