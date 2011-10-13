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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.Vector;

/**
 * @invisible
 */
public class ControlBroadcaster {

	private Vector _myControllerPlugList;

	private int _myControlEventType = ControlP5Constants.INVALID;

	private ControllerPlug _myControlEventPlug = null;

	private ControlP5 _myControlP5;

	private String _myEventMethod = "controlEvent";

	/**
	 * @invisible
	 * @param theControlP5
	 *            ControlP5
	 */
	protected ControlBroadcaster(
	        ControlP5 theControlP5) {
		_myControlP5 = theControlP5;
		_myControllerPlugList = new Vector();
		plug("", _myEventMethod);
	}

	/**
	 * @param theControllerName
	 *            String
	 * @param theTargetMethod
	 *            String
	 * @invisible
	 */
	public void plug(
	        final String theControllerName,
	        final String theTargetMethod) {
		if (theTargetMethod.equals(_myEventMethod)) {
			_myControlEventPlug = checkObject(_myControlP5.papplet, _myEventMethod, new Class[] { ControlEvent.class });
			if (_myControlEventPlug != null) {
				_myControlEventType = ControlP5Constants.METHOD;
			}
			return;
		}

		Controller myController = _myControlP5.controller(theControllerName);

		if (myController != null) {
			myController.setTarget(theTargetMethod);
			myController.setTargetObject(_myControlP5.papplet);

			ControllerPlug myControllerPlug = checkObject(_myControlP5.papplet, theTargetMethod,
			        ControlP5Constants.acceptClassList);
			if (myControllerPlug != null) {
				if (myController.controllerPlug() == null) {
					_myControllerPlugList.add(myControllerPlug);
					myController.setControllerPlug(myControllerPlug);
				} else {
					myController.controllerPlug().set(myControllerPlug.object(), myControllerPlug.name(),
					        myControllerPlug.type(), myControllerPlug.parameterType(),
					        ControlP5Constants.acceptClassList);
				}
			}
		} else {
			System.out.println("### Controller " + theControllerName + " does not exist.");
		}
	}

	/**
	 * @todo parameterCheck erweitern.
	 * @invisible
	 */
	protected static ControllerPlug checkObject(
	        final Object theObject,
	        final String theTargetName,
	        final Class[] theAcceptClassList) {
		Class myClass = theObject.getClass();
		Method[] myMethods = myClass.getDeclaredMethods();
		for (int i = 0; i < myMethods.length; i++) {
			if ((myMethods[i].getName()).equals(theTargetName)) {
				if (myMethods[i].getParameterTypes().length == 1) {
					for (int j = 0; j < theAcceptClassList.length; j++) {
						if (myMethods[i].getParameterTypes()[0] == theAcceptClassList[j]) {
							return new ControllerPlug(theObject, theTargetName, ControlP5Constants.METHOD, j,
							        theAcceptClassList);
						}
					}
				} else if (myMethods[i].getParameterTypes().length == 0) {
					return new ControllerPlug(theObject, theTargetName, ControlP5Constants.METHOD, -1,
					        theAcceptClassList);
				}
				break;
			}
		}
		Field[] myFields = myClass.getDeclaredFields();
		for (int i = 0; i < myFields.length; i++) {
			if ((myFields[i].getName()).equals(theTargetName)) {
				for (int j = 0; j < theAcceptClassList.length; j++) {
					if (myFields[i].getType() == theAcceptClassList[j]) {
						return new ControllerPlug(theObject, theTargetName, ControlP5Constants.FIELD, j,
						        theAcceptClassList);
					}
				}
				break;
			}
		}
		return null;
	}

	public void broadcast(
	        final ControlEvent theControlEvent,
	        final int theType) {
		if (theControlEvent.isTab() == false && theControlEvent.isGroup() == false) {
			if (theControlEvent.controller().controllerPlug() != null) {
				if (theType == ControlP5Constants.STRING) {
					callTarget(theControlEvent.controller().controllerPlug().name(), theControlEvent.stringValue());
				} else if (theType == ControlP5Constants.ARRAY) {
					
				} else {
					callTarget(theControlEvent.controller().controllerPlug().name(), theControlEvent.value());
				}
			}
		}
		if (_myControlEventType == ControlP5Constants.METHOD) {
			invokeMethod(_myControlEventPlug.object(), _myControlEventPlug.getMethod(),
			        new Object[] { theControlEvent });
		}

	}

	/**
	 * 
	 * @param theName
	 *            String
	 * @param theValue
	 *            float
	 */
	protected void callTarget(
	        final String theName,
	        final float theValue) {
		ControllerPlug p;
		for (int i = 0; i < _myControllerPlugList.size(); i++) {
			if (((ControllerPlug) _myControllerPlugList.get(i)).checkName(theName)) {
				p = (ControllerPlug) _myControllerPlugList.get(i);
				if (p.checkType(ControlP5Constants.METHOD)) {
					invokeMethod(p.object(), p.getMethod(), p.getMethodParameter(theValue));
				} else if (p.checkType(ControlP5Constants.FIELD)) {
					invokeField(p.object(), p.getField(), p.getFieldParameter(theValue));
				}
			}
		}
	}

	/**
	 * 
	 * @param theName
	 *            String
	 * @param theValue
	 *            String
	 */
	protected void callTarget(
	        final String theName,
	        final String theValue) {
		ControllerPlug p;
		for (int i = 0; i < _myControllerPlugList.size(); i++) {
			if (((ControllerPlug) _myControllerPlugList.get(i)).checkName(theName)) {
				p = (ControllerPlug) _myControllerPlugList.get(i);
				if (p.checkType(ControlP5Constants.METHOD)) {
					invokeMethod(p.object(), p.getMethod(), new Object[] { theValue });
				} else if (p.checkType(ControlP5Constants.FIELD)) {
					invokeField(p.object(), p.getField(), theValue);
				}
			}
		}
	}

	/**
	 * 
	 * @param theObject
	 *            Object
	 * @param theField
	 *            Field
	 * @param theParam
	 *            Object
	 */
	private void invokeField(
	        final Object theObject,
	        final Field theField,
	        final Object theParam) {
		try {
			theField.set(theObject, theParam);
		} catch (IllegalAccessException e) {
			System.out.println("### WARNING @ ControlBroadcaster.invokeField " + e);
		}
	}

	/**
	 * 
	 * @param theObject
	 *            Object
	 * @param theMethod
	 *            Method
	 * @param theParam
	 *            Object[]
	 */
	private void invokeMethod(
	        final Object theObject,
	        final Method theMethod,
	        final Object[] theParam) {
		try {
			if (theParam[0] == null) {
				theMethod.invoke(theObject, new Object[0]);
			} else {
				theMethod.invoke(theObject, theParam);
			}
		} catch (IllegalArgumentException e) {
			System.out.println("### ERROR @ ControlP5.invokeMethod " + theMethod.toString() + "  "
			        + theMethod.getName() + " " + theParam.length + " " + e);
			/**
			 * @todo thrown when plugging a String method/field.
			 */
		} catch (IllegalAccessException e) {
			printMethodError(theMethod, e);
		} catch (InvocationTargetException e) {
			printMethodError(theMethod, e);
		} catch (NullPointerException e) {
			printMethodError(theMethod, e);
		}

	}

	/**
	 * 
	 * @param theMethod
	 *            Method
	 * @param theException
	 *            Exception
	 */
	private void printMethodError(
	        Method theMethod,
	        Exception theException) {
		System.out.println("ERROR. an error occured while forwarding a Controller value\n "
		        + "to a method in your program. please check your code for any \n"
		        + "possible errors that might occur in this method .\n "
		        + "e.g. check for casting errors, possible nullpointers, array overflows ... .\n" + "method: "
		        + theMethod.getName() + "\n" + "exception:  " + theException);
	}

}
