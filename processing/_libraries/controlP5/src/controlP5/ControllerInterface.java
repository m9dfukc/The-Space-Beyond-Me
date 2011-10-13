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
import java.awt.event.KeyEvent;

/**
 * @invisible
 */
public interface ControllerInterface {

	/**
	 * @invisible
	 */
	public void init();

	/**
	 * @invisible
	 * @return CVector3f
	 */
	public CVector3f position();

	/**
	 * @invisible
	 * @param theX
	 *            float
	 * @param theY
	 *            float
	 */
	public void setPosition(
	        float theX,
	        float theY);

	/**
	 * @invisible
	 * @return CVector3f
	 */
	public CVector3f absolutePosition();

	/**
	 * @invisible
	 */
	public void updateAbsolutePosition();

	/**
	 * @invisible
	 */
	public void update();

	/**
	 * @invisible
	 */
	public void setUpdate(
	        boolean theFlag);

	/**
	 * @invisible
	 * @return boolean
	 */
	public boolean isUpdate();

	/**
	 * @invisible
	 */
	public void updateEvents();

	/**
	 * @invisible
	 */
	public void continuousUpdateEvents();

	/**
	 * a method for putting input events like e.g. mouse or keyboard events and
	 * queries. this has been taken out of the draw method for better
	 * overwriting capability.
	 * 
	 * @invisible
	 */
	public void updateInternalEvents(PApplet theApplet);

	/**
	 * @invisible
	 * @param theApplet
	 *            PApplet
	 */
	public void draw(
	        PApplet theApplet);

	/**
	 * @invisible
	 * @param theElement
	 *            ControllerInterface
	 */
	public void add(
	        ControllerInterface theElement);

	/**
	 * @invisible
	 * @param theElement
	 *            ControllerInterface
	 */
	public void remove(
	        ControllerInterface theElement);

	/**
	 * @invisible
	 */
	public void remove();

	/**
	 * @invisible
	 * @return String
	 */
	public String name();

	/**
	 * @invisible
	 * @return ControlWindow
	 */
	public ControlWindow getWindow();

	/**
	 * @invisible
	 * @return Tab
	 */
	public Tab getTab();

	/**
	 * @invisible
	 * @param theStatus
	 *            boolean
	 * @return boolean
	 */
	public boolean setMousePressed(
	        boolean theStatus);

	/**
	 * @invisible
	 * @param theEvent
	 *            KeyEvent
	 */
	public void keyEvent(
	        KeyEvent theEvent);

	/**
	 * @invisible
	 * @param theValue
	 *            int
	 */
	public void setId(
	        int theValue);

	/**
	 * @invisible
	 * @return int
	 */
	public int id();

	/**
	 * @invisible
	 * @param theString
	 *            String
	 */
	public void setLabel(
	        String theString);

	/**
	 * @invisible
	 * @param theColor
	 *            int
	 */
	public void setColorActive(
	        int theColor);

	/**
	 * @invisible
	 * @param theColor
	 *            int
	 */
	public void setColorForeground(
	        int theColor);

	/**
	 * @invisible
	 * @param theColor
	 *            int
	 */
	public void setColorBackground(
	        int theColor);

	/**
	 * @invisible
	 * @param theColor
	 *            int
	 */
	public void setColorLabel(
	        int theColor);

	/**
	 * @invisible
	 * @param theColor
	 *            int
	 */
	public void setColorValue(
	        int theColor);

	public CColor color();

	/**
	 * @invisible
	 * @param theXMLElement
	 *            ControlP5XMLElement
	 */
	public void addToXMLElement(
	        ControlP5XMLElement theXMLElement);

	/**
	 * @invisible
	 * @return ControlP5XMLElement
	 */
	public ControlP5XMLElement getAsXML();

	/**
	 * @invisible
	 */
	public void show();

	/**
	 * @invisible
	 */
	public void hide();

	/**
	 * @invisible
	 * @return boolean
	 */
	public boolean isVisible();

	/**
	 * @invisible
	 * @param theGroup
	 *            ControlGroup
	 * @param theTab
	 *            Tab
	 * @param theWindow
	 *            ControlWindow
	 */
	public void moveTo(
	        ControlGroup theGroup,
	        Tab theTab,
	        ControlWindow theWindow);

	public float value();

	public String stringValue();

	public boolean isXMLsavable();

}
