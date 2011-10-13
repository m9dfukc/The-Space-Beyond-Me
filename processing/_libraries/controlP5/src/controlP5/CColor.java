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

/**
 * @invisible
 */
public class CColor {
	
	/*
	 * @TODO
	 * refering to this thread http://processing.org/discourse/yabb2/YaBB.pl?num=1246553397/2#2
	 * the color handling is not very user friendly and should be revised.
	 * naming: take out color from the fields
	 * make fields public or add getter for each color
	 * how to change/edit only the alpha channel of a color?
	*/
	protected int colorBackground = 0xff003652;

	protected int colorForeground = 0xff00698c;

	protected int colorActive = 0xff0699C4;

	protected int colorLabel = 0xffffffff;

	protected int colorValue = 0xffffffff;

	protected void set(
	        CColor theColor) {
		colorBackground = theColor.colorBackground;
		colorForeground = theColor.colorForeground;
		colorActive = theColor.colorActive;
		colorLabel = theColor.colorLabel;
		colorValue = theColor.colorValue;
	}

	protected void copyTo(
	        ControllerInterface theControl) {
		theControl.setColorBackground(colorBackground);
		theControl.setColorForeground(colorForeground);
		theControl.setColorActive(colorActive);
		theControl.setColorLabel(colorLabel);
	}

	
	public String toString() {
		return ("colorBackground " + (colorBackground >> 16 & 0xff) + "-" + (colorBackground >> 8 & 0xff) + "-"
		        + (colorBackground >> 0 & 0xff) + "\n" + "colorForeground " + (colorForeground >> 16 & 0xff) + "-"
		        + (colorForeground >> 8 & 0xff) + "-" + (colorForeground >> 0 & 0xff) + "\n" + "colorActive "
		        + (colorActive >> 16 & 0xff) + "-" + (colorActive >> 8 & 0xff) + "-" + (colorActive >> 0 & 0xff) + "\n"
		        + "colorLabel " + (colorLabel >> 16 & 0xff) + "-" + (colorLabel >> 8 & 0xff) + "-"
		        + (colorLabel >> 0 & 0xff) + "\n" + "colorValue " + (colorValue >> 16 & 0xff) + "-"
		        + (colorValue >> 8 & 0xff) + "-" + (colorValue >> 0 & 0xff));
	}

	public CColor() {}

	public CColor(
	        CColor theColor) {
		set(theColor);
	}

	public boolean equals(
	        CColor theColor) {
		if (colorBackground == theColor.colorBackground && colorForeground == theColor.colorForeground
		        && colorActive == theColor.colorActive && colorLabel == theColor.colorLabel
		        && colorValue == theColor.colorValue) {
			return true;
		}
		return false;
	}
}
