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
 * ControlListener is an interface that can be implemented by
 * a custom class. add the controlListener to a controller
 * with addListner()
 *
 * @related Controller
 * @example ControlP5listener
 */
public interface ControlListener {

    /**
     * ControlListener is an interface that can be implemented by
     * a custom class. add the controlListener to a controller
     * with Controller.addListner()
     *
     * @param theEvent ControlEvent
     * @related Controller
     * @example ControlP5listener
     */
    public void controlEvent(ControlEvent theEvent);

}
