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

import java.awt.event.KeyEvent;
import java.util.Hashtable;
import java.util.Observer;
import java.util.Vector;

import processing.core.PApplet;

/**
 * Controller is an abstract class that is extended by any available
 * controller within controlP5. this is the full documentation list for all
 * methods available for a controller. An event triggered by a controller will
 * be forwarded to the main program. If a void controlEvent(ControlEvent
 * theEvent) {} method is available, this method will be called.
 * <p>
 * A Controller can pass a change in value to the main program in 3 different
 * ways:
 * 
 * (1) add method controlEvent(ControlEvent theEvent) to your sketch. ControlP5
 * will automatically detect this method and will used it to forward any
 * controlEvent triggered by a controller - you can disable forwarding by using
 * setBroadcast(false) (see setBroadcast)
 * 
 * (2) each controller requires a unique name when being create. In case an
 * existing name is used for a newly created Controller, the existing one will
 * be overwritten. each unique name can be used to automatically link a
 * controller to either a method or a field within your program.
 * 
 * @see Bang
 * @see Button
 * @see Knob
 * @see Matrix
 * @see MultiList
 * @see Numberbox
 * @see Radio
 * @see ScrollList
 * @see Slider
 * @see Textarea
 * @see Textfield
 * @see Textlabel
 * @see Toggle
 * @see ControlGroup
 * @see ControlBehavior
 * @see ControlEvent
 * @example ControlP5basics
 */
public abstract class Controller implements ControllerInterface, CDrawable, ControlP5Constants {

	protected CVector3f position;

	protected CVector3f positionBuffer;

	protected CVector3f absolutePosition;

	protected ControllerInterface _myParent;

	protected String _myName;

	protected ControlWindow _myControlWindow;

	protected boolean isInside = false;

	protected boolean isMousePressed = false;

	protected ControlP5 controlP5;

	protected int width;

	protected int height;

	protected int _myId = -1;

	protected float _myValue;

	protected float _myDefaultValue;

	protected String _myStringValue = "";

	protected float[] _myArrayValue;

	protected Label _myCaptionLabel;

	protected Label _myValueLabel;

	protected boolean isLabelVisible = true;

	protected boolean isMoveable = true;

	protected boolean isBroadcast = true;

	protected boolean isVisible = true;

	protected boolean isActive = false;

	protected boolean isInit = false;

	protected Vector _myControlListener;

	protected CColor color = new CColor();

	protected float _myMin;

	protected float _myMax;

	protected float _myUnit;

	private ControllerPlug _myControllerPlug;

	protected String target;

	protected Object targetObject;

	protected ControlBehavior _myBehavior;

	protected boolean isBehavior;

	protected boolean isXMLsavable = true;

	protected Vector subelements;

	protected int myBroadcastType = FLOAT;

	protected boolean isUpdate = false;

	public static final int MOVE = 0;

	public static final int RELEASE = 1;

	public static final int PRESSED = 2;

	protected int _myDecimalPoints = 2;

	protected ControllerSprite sprite;

	protected boolean isSprite;

	/**
	 * @todo add distribution options for MOVE, RELEASE, and PRESSED.
	 *       setDecimalPoints: setDcimalPoints(6) does only show 2 digits after
	 *       the point
	 */
	protected Controller(
		final ControlP5 theControlP5,
		final ControllerGroup theParent,
		final String theName,
		final float theX,
		final float theY,
		final int theWidth,
		final int theHeight) {
		controlP5 = theControlP5;
		if (controlP5 == null) {
			isBroadcast = false;
		}
		_myName = theName;
		position = new CVector3f(theX, theY, 0);
		positionBuffer = new CVector3f(theX, theY, 0);
		setParent(theParent);
		if (theParent != null) {
			color.set(theParent.color);
		}
		else {
			color.set(controlP5.color);
		}
		width = theWidth;
		height = theHeight;
		_myCaptionLabel = new Label(theName, color.colorLabel);
		_myValueLabel = new Label("");
		_myControlListener = new Vector();
		subelements = new Vector();
		_myArrayValue = new float[0];
	}

	protected Controller(final int theX, final int theY) {
		position = new CVector3f(theX, theY, 0);
	}

	/**
	 * @invisible
	 */
	public final void init() {
		_myDefaultValue = _myValue;
		controlP5.controlbroadcaster().plug(_myName, _myName);
		initControllerValue();
		isInit = controlP5.isAutoInitialization;
		setValue(_myDefaultValue);
		isInit = true;
	}
	
	protected void updateFont(ControlFont theControlFont) {
		_myCaptionLabel.updateFont(theControlFont);
		_myValueLabel.updateFont(theControlFont);
	}

	/**
	 * with setBehavior you can add a ControlBehavior to a controller. A
	 * ControlBehavior can be used to e.g. automatically change state, function,
	 * position, etc.
	 * 
	 * @example ControlP5behavior
	 * @param theBehavior ControlBehavior
	 */
	public void setBehavior(final ControlBehavior theBehavior) {
		isBehavior = true;
		_myBehavior = theBehavior;
		_myBehavior.init(this);
	}

	/**
	 * remove the behavior from the controller.
	 */
	public void removeBehavior() {
		isBehavior = false;
		_myBehavior = null;
	}

	/**
	 * get the behavior of the controller.
	 * 
	 * @return ControlBehavior
	 */
	public ControlBehavior behavior() {
		return _myBehavior;
	}

	/**
	 * returns the default value.
	 * 
	 * @return float
	 */
	public float defaultValue() {
		return _myDefaultValue;
	}

	/**
	 * set the default value.
	 * 
	 * @param theValue float
	 */
	public void setDefaultValue(final float theValue) {
		_myDefaultValue = theValue;
	}

	/**
	 * enable or prevent the controller to be moveable. By default a controller
	 * is moveable.
	 * 
	 * @param theValue boolean
	 */
	public void setMoveable(final boolean theValue) {
		isMoveable = theValue;
	}

	/**
	 * show or hide the labels of a controller.
	 * 
	 * @param theValue boolean
	 */
	public void setLabelVisible(final boolean theValue) {
		isLabelVisible = theValue;
	}

	/**
	 * @invisible
	 */
	private void initControllerValue() {
		if (controllerPlug() != null) {
			if (Float.isNaN(defaultValue())) {
				if (controllerPlug().value() == null) {
					setDefaultValue(min());
				}
				else {
					float myInitValue = 0;
					if (controllerPlug().value() instanceof Boolean) {
						// ESCA-JAVA0278:
						final boolean myBoolean = new Boolean(controllerPlug().value().toString()).booleanValue();
						// ESCA-JAVA0081:
						myInitValue = (myBoolean == true) ? 1f : 0f;

					}
					else if (controllerPlug().value() instanceof Float) {
						myInitValue = (new Float(controllerPlug().value().toString())).floatValue();
					}
					else if (controllerPlug().value() instanceof Integer) {
						myInitValue = (new Integer(controllerPlug().value().toString())).intValue();
					}
					else if (controllerPlug().value() instanceof String) {
						_myStringValue = controllerPlug().value().toString();
					}
					setDefaultValue(myInitValue);
				}
			}
		}
		else {
			if (Float.isNaN(defaultValue())) {
				setDefaultValue(min());
			}
		}
		_myValue = _myDefaultValue;
	}

	/**
	 * Use setBroadcast to enable and disable the broadcasting of changes in a
	 * controller's value. By default any value changes are forwarded to
	 * function controlEvent inside your program. use setBroadcast(false) to
	 * disable forwarding.
	 * 
	 * @param theFlag boolean
	 */
	public void setBroadcast(final boolean theFlag) {
		isBroadcast = theFlag;
	}

	/**
	 * check if broadcasting is enabled or disabled for a controller. Every
	 * event relevant for a value change will be broadcasted to any of the
	 * value-listeners. By default broadcasting for a controller is enabled.
	 * 
	 * @return boolean
	 */
	public boolean isBroadcast() {
		return isBroadcast;
	}

	/**
	 * get the position of a controller. e.g. Controller.position().x();
	 * 
	 * @return CVector3f
	 */
	public CVector3f position() {
		return position;
	}

	/**
	 * set the position of a controller. The position of a controller is
	 * relative.
	 * 
	 * @param theX float
	 * @param theY float
	 */
	public void setPosition(final float theX, final float theY) {
		position.x = theX;
		position.y = theY;
	}

	/*
	 * set the position of a controller for the z-Axis is not yet implemented,
	 * since 2D renderer do not allow translation in the z-Axis. moving a
	 * controller in the z-axis would affect the picking behavior of a
	 * controller, which currently is optimized for z=0. moving a controller on
	 * the z-Axis would give wrong results for mouseEvents applied to a
	 * controller since 3d-picking is not implemented.
	 */

	/**
	 * get the absolute position of a controller. if a controller is the child
	 * of another controller, the absolute position is the sum of its parent(s)
	 * position(s).
	 * 
	 * @return CVector3f
	 */
	public CVector3f absolutePosition() {
		return absolutePosition;
	}

	/**
	 * @invisible
	 */
	public void updateAbsolutePosition() {
	}

	/**
	 * continuousUpdateEvents is used for internal updates of a controller. this
	 * method is final and can't be overwritten.
	 * 
	 * @invisible
	 */
	public final void continuousUpdateEvents() {
		if (isBehavior) {
			if (_myBehavior.isActive() && !isMousePressed) {
				_myBehavior.update();
			}
		}
	}

	/**
	 * updateEvents is used for internal updates of a controller. this method is
	 * final and can't be overwritten.
	 * 
	 * @invisible
	 */
	public final void updateEvents() {
		if (isVisible && (isMousePressed == _myControlWindow.mouselock)) {
			if (isMousePressed && controlP5.keyHandler.isAltDown && isMoveable) {
				positionBuffer.x += _myControlWindow.mouseX - _myControlWindow.pmouseX;
				positionBuffer.y += _myControlWindow.mouseY - _myControlWindow.pmouseY;
				if (controlP5.keyHandler.isShiftDown) {
					position.x = ((int) (positionBuffer.x) / 10) * 10;
					position.y = ((int) (positionBuffer.y) / 10) * 10;
				}
				else {
					position.set(positionBuffer);
				}
			}
			else {
				if (isInside) {
					_myControlWindow.isMouseOver = true;
				}
				if (inside()) {
					if (!isInside) {
						isInside = true;
						onEnter();
					}
				}
				else {
					if (isInside && !isMousePressed) {
						onLeave();
						isInside = false;
					}
				}
			}
		}
	}

	/**
	 * TODO
	 * 
	 * @see ControllerInterface.updateInternalEvents
	 * @invisible
	 */
	public void updateInternalEvents(final PApplet theApplet) {
	}

	/**
	 * @invisible
	 * @param theApplet PApplet
	 */
	public void draw(final PApplet theApplet) {
		if (inside()) {
			theApplet.fill(255, 0, 0);
		}
		else {
			theApplet.fill(255);
		}

		theApplet.pushMatrix();
		theApplet.translate(position.x(), position.y(), position.z());
		theApplet.rect(0, 0, width, height);
		theApplet.popMatrix();
	}

	/**
	 * @invisible
	 * @param theElement ControllerInterface
	 */
	public void add(final ControllerInterface theElement) {
		System.out.println("### ERROR @ Controller.register() not supported.");
	}

	/**
	 * @invisible
	 * @param theElement ControllerInterface
	 */
	public void remove(final ControllerInterface theElement) {
		System.out.println("### ERROR @ Controller.remove() not supported.");
	}

	/**
	 * remove a controller from controlP5.
	 */
	public void remove() {
		if (_myParent != null) {
			_myParent.remove(this);
		}
		if (controlP5 != null) {
			controlP5.remove(this);
		}
	}

	/**
	 * returns the name of the controller.
	 * 
	 * @return String
	 */
	public String name() {
		return _myName;
	}

	/**
	 * move a controller to another tab. The tab is defined by parameter
	 * theTabName. if controlP5 can't find a tab with such name, controlP5 will
	 * create this tab and add it to the main window.
	 * 
	 * @param theTabName String
	 * 
	 */
	public void moveTo(final String theTabName) {
		setTab(theTabName);
		for (int i = 0; i < subelements.size(); i++) {
			((Controller) subelements.get(i)).moveTo(theTabName);
		}
	}

	/**
	 * move a controller to another tab indicated by parameter theTab.
	 * 
	 * @param theTab
	 */
	public void moveTo(final Tab theTab) {
		setTab(theTab.getWindow(), theTab.name());
		for (int i = 0; i < subelements.size(); i++) {
			((Controller) subelements.get(i)).moveTo(theTab);
		}
	}

	/**
	 * move a controller to the default tab inside the main window.
	 * 
	 * @param theApplet
	 */
	public void moveTo(final PApplet theApplet) {
		setTab("default");
		for (int i = 0; i < subelements.size(); i++) {
			((Controller) subelements.get(i)).moveTo(theApplet);
		}
	}

	/**
	 * move a controller to a defined tab inside the main window.
	 * 
	 * @param theApplet
	 * @param theTabName
	 */
	public void moveTo(final PApplet theApplet, final String theTabName) {
		setTab(theTabName);
		for (int i = 0; i < subelements.size(); i++) {
			((Controller) subelements.get(i)).moveTo(theApplet, theTabName);
		}
	}

	/**
	 * move a controller to the default tab of a control window - other than the
	 * main window.
	 * 
	 * @param theControlWindow
	 */
	public void moveTo(final ControlWindow theControlWindow) {
		setTab(theControlWindow, "default");
		for (int i = 0; i < subelements.size(); i++) {
			((Controller) subelements.get(i)).moveTo(theControlWindow);
		}
	}

	public void moveTo(final ControlWindow theControlWindow, final String theTabName) {
		setTab(theControlWindow, theTabName);
		for (int i = 0; i < subelements.size(); i++) {
			((Controller) subelements.get(i)).moveTo(theControlWindow, theTabName);
		}
	}

	public void moveTo(final ControlGroup theGroup, final Tab theTab, ControlWindow theControlWindow) {
		for (int i = 0; i < subelements.size(); i++) {
			((Controller) subelements.get(i)).moveTo(theGroup, theTab, theControlWindow);
		}

		if (theGroup != null) {
			setGroup(theGroup);
			return;
		}

		if (theControlWindow == null) {
			theControlWindow = controlP5.controlWindow;
		}

		setTab(theControlWindow, theTab.name());
	}

	public void moveTo(final ControlGroup theGroup) {
		if (theGroup != null) {
			setGroup(theGroup);
			return;
		}

	}

	/**
	 * set the tab of the controller.
	 * 
	 * @param theName
	 *        String
	 */
	public void setTab(final String theName) {
		setParent(controlP5.getTab(theName));
		for (int i = 0; i < subelements.size(); i++) {
			((Controller) subelements.get(i)).setTab(theName);
		}
	}

	public void setTab(final ControlWindow theWindow, final String theName) {
		setParent(controlP5.getTab(theWindow, theName));
		for (int i = 0; i < subelements.size(); i++) {
			((Controller) subelements.get(i)).setTab(theWindow, theName);
		}
	}

	/**
	 * set the group of the controller.
	 * 
	 * @param theName
	 *        String
	 */
	public void setGroup(final String theName) {
		setParent(controlP5.getGroup(theName));
		for (int i = 0; i < subelements.size(); i++) {
			((Controller) subelements.get(i)).setGroup(theName);
		}
	}

	public void setGroup(final ControllerGroup theGroup) {
		setParent(theGroup);
		for (int i = 0; i < subelements.size(); i++) {
			((Controller) subelements.get(i)).setGroup(theGroup);
		}
	}

	/**
	 * @deprecated
	 * @param theApplet
	 *        PApplet
	 */
	public void setWindow(final PApplet theApplet) {
		moveTo(theApplet);
	}

	/**
	 * @deprecated
	 * @param theWindwo
	 *        ControlWindow
	 */
	public ControlWindow setWindow(final ControlWindow theWindow) {
		moveTo(theWindow);
		return _myControlWindow;
	}

	/**
	 * get the instance of the tab the controller belongs to.
	 * 
	 * @return Tab
	 */
	public Tab getTab() {
		if (_myParent instanceof Tab) {
			return (Tab) _myParent;
		}
		return _myParent.getTab();
	}

	/**
	 * set the parent of a parent of a controller. this method is only meant for
	 * internal use.this method is final and can't be overwritten.
	 * 
	 * @invisible
	 * @param theParent ControllerInterface
	 */
	public final void setParent(final ControllerInterface theParent) {
		if (_myParent != null) {
			_myParent.remove(this);
		}
		absolutePosition = new CVector3f(position);
		if (theParent != null) {
			_myParent = theParent;
			_myParent.add(this);
			absolutePosition.add(_myParent.position());
			_myControlWindow = _myParent.getWindow();
		}
	}

	/**
	 * get the parent of a controller.
	 * 
	 * @return
	 */
	public ControllerInterface parent() {
		return _myParent;
	}

	/**
	 * get the control window of the controller
	 * 
	 * @return ControlWindow
	 */
	public ControlWindow getWindow() {
		return _myControlWindow;
	}

	/**
	 * check if the mouse is inside the area of a controller.
	 * 
	 * @invisible
	 * @return boolean
	 */
	protected boolean inside() {
		return (_myControlWindow.mouseX > position.x() + _myParent.absolutePosition().x()
			&& _myControlWindow.mouseX < position.x() + _myParent.absolutePosition().x() + width
			&& _myControlWindow.mouseY > position.y() + _myParent.absolutePosition().y() && _myControlWindow.mouseY < position
			.y()
			+ _myParent.absolutePosition().y()
			+ height);
	}

	/**
	 * returns true or false and indicates if the mouse is inside the area of a
	 * controller.
	 * 
	 * @return boolean
	 */
	public boolean isInside() {
		return isInside;
	}

	/**
	 * returns true or false if the mouse has been pressed.
	 * 
	 * @return
	 */
	public boolean isMousePressed() {
		return isMousePressed;
	}

	protected void onEnter() {
	}

	protected void onLeave() {
	}

	protected void mousePressed() {
	}

	protected void mouseReleased() {
	}

	protected void mouseReleasedOutside() {
	}

	/**
	 * 
	 * @invisible
	 * @param theStatus boolean
	 * @return boolean
	 */
	public final boolean setMousePressed(final boolean theStatus) {
		if (!isVisible) {
			return false;
		}
		if (theStatus == true) {
			if (isInside) {
				isMousePressed = true;
				if (!controlP5.keyHandler.isAltDown) {
					mousePressed();
				}
				return true;
			}
		}
		else {
			if (isMousePressed == true && inside()) {
				isMousePressed = false;
				if (!controlP5.keyHandler.isAltDown) {
					mouseReleased();
				}
			}
			if (!inside()) {
				isInside = false;
				isMousePressed = false;
				mouseReleasedOutside();
			}
		}
		return false;
	}

	/**
	 * @invisible
	 * @param KeyEvent theEvent
	 */
	public void keyEvent(final KeyEvent theEvent) {
	}

	/**
	 * set the id of the controller.
	 * 
	 * @param int theId
	 * 
	 */
	public void setId(final int theId) {
		_myId = theId;
	}

	/**
	 * get the id of the controller.
	 * 
	 * @return int
	 */
	public int id() {
		return _myId;
	}

	protected ControllerPlug controllerPlug() {
		return _myControllerPlug;
	}

	protected void setTarget(final String theTarget) {
		target = theTarget;
	}

	protected void setTargetObject(final Object theTarget) {
		targetObject = theTarget;
	}

	protected void setControllerPlug(final ControllerPlug thePlug) {
		_myControllerPlug = thePlug;
	}

	/**
	 * set the value of the controller.
	 * 
	 * @param theValue
	 *        float
	 */
	public abstract void setValue(float theValue);

	public void setArrayValue(float[] theArray) {
		_myArrayValue = theArray;
	}

	/**
	 * set the value of the controller without sending the broadcast event. this
	 * function is final.
	 * 
	 * @param theValue
	 *        float
	 */
	public final void changeValue(float theValue) {
		boolean br = this.isBroadcast;
		this.isBroadcast = false;
		setValue(theValue);
		this.isBroadcast = br;
	}

	/**
	 * updates the value of the controller without having to set the value
	 * explicitly. update does not visually update the controller. the updating
	 * status can be set with setUpdate(true/false) and checked with isUpdate().
	 * 
	 * @related setUpdate ( )
	 */
	public void update() {
	}

	/**
	 * disable the update function for a controller.
	 * 
	 * @related update ( )
	 * @param theFlag
	 *        boolean
	 */
	public void setUpdate(final boolean theFlag) {
		isUpdate = theFlag;
	}

	/**
	 * enable the update function for a controller.
	 * 
	 * @related setUpdate ( )
	 * @return boolean
	 */
	public boolean isUpdate() {
		return isUpdate;
	}

	/**
	 * trigger the event of a controller. deprecated. use .update() instead.
	 * 
	 * @deprecated
	 */
	public void trigger() {
		setValue(value());
	}

	/**
	 * get the current value of the controller.
	 * 
	 * @return float
	 */
	public float value() {
		return _myValue;
	}

	/**
	 * set the current string value of a controller.
	 * 
	 * @return String
	 */
	public String stringValue() {
		return _myStringValue;
	}

	public float[] arrayValue() {
		return _myArrayValue;
	}

	/**
	 * @invisible
	 * @return CColor
	 */
	public CColor color() {
		return color;
	}

	public CColor getColor() {
		return color;
	}

	/**
	 * set the label of the controller.
	 * 
	 * @param theLabel
	 *        String
	 * @return Controller
	 */
	public void setLabel(final String theLabel) {
		_myCaptionLabel.setFixedSize(false);
		_myCaptionLabel.set(theLabel);
		_myCaptionLabel.setFixedSize(true);
	}

	/**
	 * set or change the content of the caption label of a controller.
	 * 
	 * @param theLabel
	 */
	public void setCaptionLabel(final String theLabel) {
		_myCaptionLabel.setFixedSize(false);
		_myCaptionLabel.set(theLabel);
		_myCaptionLabel.setFixedSize(true);
	}

	/**
	 * set or change the value of the value label of a controller. (this is
	 * cheating, but maybe useful for some cases.)
	 * 
	 * @param theLabel
	 */
	public void setValueLabel(final String theLabel) {
		_myValueLabel.setFixedSize(false);
		_myValueLabel.set(theLabel);
		_myValueLabel.setFixedSize(true);
	}

	/**
	 * get the label of the controller.
	 * 
	 * @return String
	 */
	public String label() {
		return _myCaptionLabel.toString();
	}

	/**
	 * add a listener to the controller.
	 * 
	 * @related ControlListener
	 * @param theListener
	 *        ControlListener
	 */
	public void addListener(final ControlListener theListener) {
		_myControlListener.add(theListener);
	}

	/**
	 * remove a listener from the controller.
	 * 
	 * @related ControlListener
	 * @param theListener
	 *        ControlListener
	 */
	public void removeListener(final ControlListener theListener) {
		_myControlListener.remove(theListener);
	}

	/**
	 * @invisible
	 * @return int
	 */
	public int listenerSize() {
		return _myControlListener.size();
	}

	protected void broadcast(int theType) {
		theType = myBroadcastType;
		final ControlEvent myEvent = new ControlEvent(this);
		for (int i = 0; i < listenerSize(); i++) {
			((ControlListener) _myControlListener.get(i)).controlEvent(myEvent);
		}
		if (isBroadcast && isInit) {
			controlP5.controlbroadcaster().broadcast(myEvent, theType);
		}
		isInit = true;
	}

	/**
	 * check if the controller is visible.
	 * 
	 * @return boolean
	 */
	public boolean isVisible() {
		return isVisible;
	}

	/**
	 * set the visibility of a controller.
	 * 
	 * @param theFlag
	 *        boolean
	 */
	public void setVisible(final boolean theFlag) {
		isVisible = theFlag;
		if (theFlag == false) {
			isActive = false;
		}
	}

	/**
	 * hide the controller, make it invisible.
	 */
	public void hide() {
		isVisible = false;
		isActive = false;
	}

	/**
	 * show the controller. make it visible.
	 */
	public void show() {
		isVisible = true;
	}

	/**
	 * set the color for the controller while active.
	 * 
	 * @param theColor
	 *        int
	 */
	public void setColorActive(final int theColor) {
		color.colorActive = theColor;
	}

	/**
	 * set the foreground color of the controller.
	 * 
	 * @param theColor
	 *        int
	 */
	public void setColorForeground(final int theColor) {
		color.colorForeground = theColor;
	}

	/**
	 * set the background color of the controller.
	 * 
	 * @param theColor
	 *        int
	 */
	public void setColorBackground(final int theColor) {
		color.colorBackground = theColor;
	}

	/**
	 * set the color of the text label of the controller.
	 * 
	 * @param theColor
	 *        int
	 */
	public void setColorLabel(final int theColor) {
		color.colorLabel = theColor;
		_myCaptionLabel.set(_myCaptionLabel.toString(), color.colorLabel);
	}

	/**
	 * set the color of the value label of the controller.
	 * 
	 * @param theColor
	 *        int
	 */
	public void setColorValue(final int theColor) {
		color.colorValue = theColor;
		if (_myValueLabel != null) {
			_myValueLabel.set(_myValueLabel.toString(), color.colorValue);
		}
	}

	public Label captionLabel() {
		return _myCaptionLabel;
	}

	public Label valueLabel() {
		return _myValueLabel;
	}

	/**
	 * @invisible get the minmum value of the controller.
	 * @return float
	 */
	public float max() {
		return _myMax;
	}

	/**
	 * @invisible get the maximum value of the controller.
	 * @return float
	 */
	public float min() {
		return _myMin;
	}

	/**
	 * set the minimum level of the Controller.
	 * 
	 * @param theValue
	 *        float
	 */
	public void setMin(float theValue) {
		_myMin = theValue;
	}

	/**
	 * set the maximum level of the Controller.
	 * 
	 * @param theValue
	 *        float
	 */
	public void setMax(float theValue) {
		_myMax = theValue;
	}

	public Controller setWidth(int theWidth) {
		width = theWidth;
		return this;
	}

	public Controller setHeight(int theHeight) {
		height = theHeight;
		return this;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	/**
	 * sets the decimal precision of a controller's float value displayed. the
	 * precision does not apply to the returned float value.
	 * 
	 * @param theValue
	 */
	public void setDecimalPrecision(int theValue) {
		_myDecimalPoints = theValue;
		_myValueLabel = new Label(""
			+ (((adjustValue(_myMax)).length() > (adjustValue(_myMin)).length())
				? adjustValue(_myMax)
				: adjustValue(_myMin)), color.colorValue);
		_myValueLabel.set("" + adjustValue(_myValue));
	}

	/**
	 * @param theValue
	 *        float
	 * @return String
	 */
	protected String adjustValue(final float theValue) {
		return adjustValue(theValue, _myDecimalPoints);
	}

	/**
	 * !!! does not work properly yet!
	 * 
	 * @param theValue
	 * @param theFloatPrecision
	 * @return
	 */
	protected String adjustValue(final float theValue, final int theFloatPrecision) {

		int myFloatNumberLength = theFloatPrecision + 1;
		if (controllerPlug() != null) {
			if (controllerPlug().classType() == int.class) {
				myFloatNumberLength = 0;
			}
		}
		String myLabelValue = "" + theValue;
		int myIndex = myLabelValue.indexOf('.');
		if (myIndex > 0) {

			if (theFloatPrecision == 0) {
				myIndex--;
			}
			myLabelValue = myLabelValue.substring(0, (int) Math.min(myLabelValue.length(), myIndex
				+ myFloatNumberLength));

			final int n = (myLabelValue.length() - myIndex);
			if (n < myFloatNumberLength) {
				for (int i = 0; i < myFloatNumberLength - n; i++) {
					myLabelValue += "0";
				}
			}
		}
		else {
			myLabelValue += ".";
			for (int i = 0; i < myFloatNumberLength; i++) {
				myLabelValue += "0";
			}
		}

		return myLabelValue;
	}

	public ControlWindow controlWindow() {
		return _myControlWindow;
	}

	public void setSprite(ControllerSprite theSprite) {
		sprite = theSprite;
		width = sprite.width();
		height = sprite.height();
		isSprite = true;
	}

	public void enableSprite() {
		if (sprite != null) {
			isSprite = true;
		}
	}

	public void disableSprite() {
		isSprite = false;
	}

	/**
	 * @invisible
	 * @return boolean
	 */
	public boolean isXMLsavable() {
		return isXMLsavable;
	}

	/**
	 * @invisible
	 * @return ControlP5XMLElement
	 */
	public ControlP5XMLElement getAsXML() {
		final ControlP5XMLElement myXMLElement = new ControlP5XMLElement(new Hashtable(), true, false);
		myXMLElement.setName("controller");
		myXMLElement.setAttribute("name", name());
		myXMLElement.setAttribute("label", _myCaptionLabel.toString());
		myXMLElement.setAttribute("id", new Integer(id()));
		myXMLElement.setAttribute("value", new Float(value()));
		myXMLElement.setAttribute("x", new Float(position().x()));
		myXMLElement.setAttribute("y", new Float(position().y()));
		myXMLElement.setAttribute("width", new Integer(width));
		myXMLElement.setAttribute("height", new Integer(height));
		myXMLElement.setAttribute("visible", new Float(isVisible == true ? 1 : 0));
		myXMLElement.setAttribute("moveable", new Float(isMoveable == true ? 1 : 0));
		addToXMLElement(myXMLElement);
		// ControlP5XMLElement.checkColor(theProperties, properties(),
		// myXMLElement);
		return myXMLElement;
	}

}
