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
import java.lang.reflect.Method;
import java.lang.reflect.Field;


/**
 * @invisible
 */
public class ControllerPlug {

  private Object _myObject;

  private String _myName;

  private Method _myMethod;

  private Field _myField;

  private int _myType = ControlP5Constants.INVALID;

  private Class _myParameterClass;

  private int _myParameterType = -1;

  private Object _myValue = null;

  public ControllerPlug(
      final Object theObject,
      final String theName,
      final int theType,
      final int theParameterType,
      Class[] theAcceptClassList) {
    set(theObject, theName, theType, theParameterType, theAcceptClassList);
  }


  public void set(
      final Object theObject,
      final String theName,
      final int theType,
      final int theParameterType,
      final Class[] theAcceptClassList) {
    _myObject = theObject;
    _myName = theName;
    _myType = theType;
    _myParameterType = theParameterType;
    Class myClass = theObject.getClass();

    /* check for methods */
    if (_myType == ControlP5Constants.METHOD) {
      try {
        Method[] myMethods = myClass.getDeclaredMethods();
        for (int i = 0; i < myMethods.length; i++) {
          if ((myMethods[i].getName()).equals(theName)) {
            if (myMethods[i].getParameterTypes().length == 1) {
              for (int j = 0; j < theAcceptClassList.length; j++) {
                if (myMethods[i].getParameterTypes()[0] == theAcceptClassList[j]) {
                  _myParameterClass = myMethods[i].getParameterTypes()[0];
                  break;
                }
              }
            } else if (myMethods[i].getParameterTypes().length == 0) {
              _myParameterClass = null;
              break;
            }
            break;
          }
        }
        Class[] myArgs = (_myParameterClass == null) ? new Class[] {}
                         : new Class[] {_myParameterClass};
        _myMethod = myClass.getDeclaredMethod(_myName, myArgs);
        _myMethod.setAccessible(true);
      } catch (SecurityException e) {
        printSecurityWarning(e);
      } catch (NoSuchMethodException e) {
        System.out.println("### ERROR @ ControllerPlug. " + e);
      }

      /* check for fields */
    } else if (_myType == ControlP5Constants.FIELD) {
      for (int i = 0; i < myClass.getDeclaredFields().length; i++) {
        if (myClass.getDeclaredFields()[i].getName().equals(_myName)) {
          _myParameterClass = myClass.getDeclaredFields()[i].getType();
        }
      }
      if (_myParameterClass != null) {
        /**
         * note. when running in applet mode.
         * for some reason setAccessible(true) works for methods
         * but not for fields. theAccessControlException is thrown.
         * therefore, make fields in your code public.
         */
        try {
          _myField = myClass.getDeclaredField(_myName);
          try {
            _myField.setAccessible(true);
          } catch (java.security.AccessControlException e) {
            printSecurityWarning(e);
          }
          try {
            _myValue = (_myField.get(theObject));
          } catch (Exception ex) {
            printSecurityWarning(ex);
          }
        } catch (NoSuchFieldException e) {
          System.out.println("### ERROR @ ControllerPlug. " + e);
        }
      }
    }
  }


  private void printSecurityWarning(Exception e) {
//		 AccessControlException required for applets.
    if (!ControlP5.isApplet) {
      System.out.println("### WARNING AccessControlException.\n"
          + "you are probably running in applet mode.\n"
          + "make sure fields and methods which you want to \n"
          + "access in your processing code are public.\n"
          + e);
      ControlP5.isApplet = true;
    }
  }


  protected Object value() {
    return _myValue;
  }


  protected boolean checkType(int theType) {
    return _myType == theType;
  }


  protected boolean checkName(String theName) {
    return (_myName.equals(theName));
  }


  protected Object object() {
    return _myObject;
  }


  protected String name() {
    return _myName;
  }


  protected int type() {
    return _myType;
  }


  protected int parameterType() {
    return _myParameterType;
  }


  private Object get(float theValue) {
    if (_myParameterClass == float.class) {
      return new Float(theValue);
    } else if (_myParameterClass == int.class) {
      return new Integer((int) theValue);
    } else if (_myParameterClass == boolean.class) {
      return (theValue > 0.5) ? new Boolean(true) : new Boolean(false);
    } else {
      return null;
    }
  }


  protected Object getFieldParameter(float theValue) {
    return get(theValue);
  }


  protected Object[] getMethodParameter(float theValue) {
    return new Object[] {get(theValue)};
  }


  protected Method getMethod() {
    return _myMethod;
  }


  protected Field getField() {
    return _myField;
  }


  protected Class classType() {
    return _myParameterClass;
  }

}
