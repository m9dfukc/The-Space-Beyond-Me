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

import java.util.Vector;

/**
 * @invisible
 */
public class ControllerList {

  Vector _myControllers;

  Vector _myDrawables;

  public ControllerList() {
    _myControllers = new Vector();
    _myDrawables = new Vector();
  }

  public void add(ControllerInterface theController) {
    if(_myControllers.indexOf(theController)<0) {
      _myControllers.add(theController);
    }
  }

  protected void remove(ControllerInterface theController) {
    _myControllers.remove(theController);
  }

  protected void addDrawable(CDrawable theController) {
    if(_myDrawables.indexOf(theController)<0) {
      _myDrawables.add(theController);
    }
  }

  protected void removeDrawable(CDrawable theController) {
    _myDrawables.remove(theController);
  }


  public ControllerInterface get(int theIndex) {
    return (ControllerInterface)_myControllers.get(theIndex);
  }

  public CDrawable getDrawable(int theIndex) {
    return (CDrawable)_myDrawables.get(theIndex);
  }

  public int sizeDrawable() {
    return  _myDrawables.size();
  }

  public int size() {
    return _myControllers.size();
  }

  protected void clear() {
    _myControllers.clear();
  }

  protected void clearDrawable() {
    _myDrawables.clear();
  }


}
