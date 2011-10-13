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
import java.util.Vector;
import java.util.Hashtable;

/**
 * a radio button implementation.
 * 
 * @related ControlP5
 * @related Bang
 * @related Button
 * @related Knob
 * @related Numberbox
 * @related Radio
 * @related Slider
 * @related Tab
 * @related Textfield
 * @related Toggle
 * 
 * @example ControlP5radio
 * @nosuperclasses Controller
 * @related Controller
 */
public class RadioButton extends ControlGroup {

	Vector _myRadioToggles;

	protected int rowSpacing = 2;

	protected int columnSpacing = 2;

	protected int itemsPerRow = -1;

	protected boolean isMultipleChoice;

	/**
	 * a radioButton is a list of toggles that can be turned on or off.
	 * radioButton is of type ControllerGroup, therefore a controllerPlug can't
	 * be set. this means that an event from a radioButton can't be forwarded to
	 * a method other than controlEvent in a sketch.
	 * 
	 * a radioButton has 2 sets of values. radioButton.value() returns the value
	 * of the active radioButton item. radioButton.arrayValue() returns a float
	 * array that represents the active (1) and inactive (0) items of a
	 * radioButton.
	 * 
	 * @param theControlP5
	 * @param theParent
	 * @param theName
	 * @param theX
	 * @param theY
	 */
	public RadioButton(
	        final ControlP5 theControlP5,
	        final ControllerGroup theParent,
	        final String theName,
	        final int theX,
	        final int theY) {
		super(theControlP5, theParent, theName, theX, theY, 100, 9);
		isBarVisible = false;
		isBarToggle = false;
		_myRadioToggles = new Vector();
		setItemsPerRow(1);
	}

	public Toggle addItem(
	        final String theName,
	        final float theValue) {
		Toggle t = controlP5.addToggle(theName, 0, 0, 11, 11);
		t.captionLabel().style().marginLeft = t.width + 4;
		t.captionLabel().style().marginTop = -t.height - 2;
		return addItem(t, theValue);
	}

	public Toggle addItem(
	        final Toggle theToggle,
	        final float theValue) {
		theToggle.setGroup(this);
		theToggle.isMoveable = false;
		theToggle.setInternalValue(theValue);
		theToggle.isBroadcast = false;
		_myRadioToggles.add(theToggle);
		updateLayout();
		color().copyTo(theToggle);
		theToggle.addListener(this);
		updateValues(false);
		return theToggle;
	}

	public void removeItem(
	        final String theName) {
		for (int i = 0; i < _myRadioToggles.size(); i++) {
			if (((Toggle) _myRadioToggles.get(i)).name().equals(theName)) {
				((Toggle) _myRadioToggles.get(i)).removeListener(this);
				_myRadioToggles.remove(i);
			}
		}
		updateValues(false);
	}

	public Toggle getItem(
	        int theIndex) {
		return ((Toggle) _myRadioToggles.get(theIndex));
	}

	public void updateLayout() {
		int n = 0;
		int xx = 0;
		int yy = 0;
		for (int i = 0; i < _myRadioToggles.size(); i++) {
			Toggle t = ((Toggle) _myRadioToggles.get(i));
			t.position().y = yy;
			t.position().x = xx;

			xx += t.width + columnSpacing;
			n++;
			if (n == itemsPerRow) {
				n = 0;
				_myWidth = xx;
				yy += t.height + rowSpacing;
				xx = 0;
			} else {
				_myWidth = xx;
			}
		}
	}

	public void setItemsPerRow(
	        final int theValue) {
		itemsPerRow = theValue;
		updateLayout();
	}

	public void setSpacingColumn(
	        final int theSpacing) {
		columnSpacing = theSpacing;
		updateLayout();
	}

	public void setSpacingRow(
	        final int theSpacing) {
		rowSpacing = theSpacing;
		updateLayout();
	}

	/**
	 * deactivate all radioButton items.
	 * 
	 */
	public void deactivateAll() {
		for (int i = 0; i < _myRadioToggles.size(); i++) {
			((Toggle) _myRadioToggles.get(i)).deactivate();
		}
		_myValue = -1;
		updateValues(true);
	}

	/**
	 * controlEvent is called whenever a radioButton item is (de-)activated.
	 * this happens internally.
	 * 
	 */
	public void controlEvent(
	        ControlEvent theEvent) {
		if (!isMultipleChoice) {
			_myValue = -1;
			for (int i = 0; i < _myRadioToggles.size(); i++) {
				if (!((Toggle) _myRadioToggles.get(i)).equals(theEvent.controller())) {
					((Toggle) _myRadioToggles.get(i)).deactivate();
				} else {
					if (((Toggle) _myRadioToggles.get(i)).isOn) {
						_myValue = ((Toggle) _myRadioToggles.get(i)).internalValue();
					}
				}
			}
		}
		updateValues(true);

	}

	protected void updateValues(
	        boolean theBroadcastFlag) {
		_myArrayValue = new float[_myRadioToggles.size()];
		for (int i = 0; i < _myRadioToggles.size(); i++) {
			_myArrayValue[i] = ((Toggle) _myRadioToggles.get(i)).value();
		}
		if (theBroadcastFlag) {
			ControlEvent myEvent = new ControlEvent(this);
			controlP5.controlbroadcaster().broadcast(myEvent, ControlP5Constants.FLOAT);
		}
	}

}
