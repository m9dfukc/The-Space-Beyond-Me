package controlP5;

import processing.core.PApplet;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.util.Hashtable;
import java.util.Vector;
import java.awt.Component;

/**
 * the purpose of a control window is to outsource controllers so that they dont
 * need to be drawn into the actual processing window. to save cpu, a control
 * window is not updated when not active - in focus. for the same reason the
 * framerate is set to 15.
 * 
 * @example ControlP5window
 */
public class ControlWindow {

	int mouseX;

	int mouseY;

	int pmouseX;

	int pmouseY;

	boolean mousePressed;

	ControlP5 controlP5;

	boolean mouselock;

	public int background = 0x00000000;

	CColor color = new CColor();

	private String _myName = "main";

	protected PApplet _myApplet;

	private boolean isPAppletWindow;

	ControllerList _myTabs;

	boolean isVisible = true;

	boolean isInit = false;

	boolean isRemove = false;

	CDrawable _myDrawable;

	protected boolean isAutoDraw;

	boolean isUpdate;

	public final static int NORMAL = PAppletWindow.NORMAL;

	public final static int ECONOMIC = PAppletWindow.ECONOMIC;

	Vector _myControlWindowCanvas;

	Vector _myControlCanvas;

	protected boolean isMouseOver;

	/**
	 * @invisible
	 * @param theControlP5
	 *            ControlP5
	 * @param theApplet
	 *            PApplet
	 */
	public ControlWindow(final ControlP5 theControlP5, final PApplet theApplet) {
		controlP5 = theControlP5;
		_myApplet = theApplet;
		_myApplet.registerMouseEvent(this);
		isAutoDraw = true;
		init();
	}

	protected void init() {
		_myTabs = new ControllerList();
		_myControlWindowCanvas = new Vector();
		_myControlCanvas = new Vector();
		if (_myApplet instanceof PAppletWindow) {
			_myName = ((PAppletWindow) _myApplet).name();
			isPAppletWindow = true;
			((PAppletWindow) _myApplet).setControlWindow(this);
		}

		if (isPAppletWindow) {
			background = 0xff000000;
		}

		if (isInit == false) {
			if (_myApplet instanceof PAppletWindow) {
				_myApplet.registerKeyEvent(new ControlWindowKeyListener(this));
			} else {
				controlP5.keyHandler.update(this);
			}
		}

		_myTabs.add(new Tab(controlP5, this, "global"));
		_myTabs.add(new Tab(controlP5, this, "default"));

		activateTab((Tab) _myTabs.get(1));

		/*
		 * register a post event that will be called by processing after the
		 * draw method has been finished.
		 */
		if (_myApplet.g.getClass().getName().indexOf("PGraphics2D") > -1
				|| _myApplet.g.getClass().getName().indexOf("PGraphics3D") > -1) {
			System.out
					.println("### INFO you are using renderer "
							+ _myApplet.g.getClass().getName()
							+ "\n"
							+ "to draw controlP5 you have to call the controlP5.draw() method inside of\n"
							+ "your processing sketch draw() method.");
		} else {
			if (isInit == false) {
				_myApplet.registerPre(this);
				// _myApplet.registerPost(this);
				_myApplet.registerDraw(this);
			}
		}
		isInit = true;
	}

	/**
	 * get the current active tab of a control window.
	 * 
	 * @return Tab
	 */
	public Tab currentTab() {
		for (int i = 1; i < _myTabs.size(); i++) {
			if (((Tab) _myTabs.get(i)).isActive()) {
				return (Tab) _myTabs.get(i);
			}
		}
		return null;
	}

	/**
	 * activate a tab of a control window.
	 * 
	 * @param theTab
	 *            String
	 */
	public void activateTab(String theTab) {
		for (int i = 1; i < _myTabs.size(); i++) {
			if (((Tab) _myTabs.get(i)).name().equals(theTab)) {
				activateTab((Tab) _myTabs.get(i));
			}
		}
	}

	/**
	 * remove a tab from a control window.
	 * 
	 * @param theTab
	 *            Tab
	 */
	public void removeTab(Tab theTab) {
		_myTabs.remove(theTab);
	}

	/**
	 * add a tab to the control window.
	 * 
	 * @param theTab
	 *            Tab
	 * @return Tab
	 */
	public Tab add(Tab theTab) {
		_myTabs.add(theTab);
		return theTab;
	}

	public Tab addTab(String theTab) {
		return tab(theTab);
	}

	/**
	 * @invisible
	 * @param theTab
	 *            Tab
	 */
	protected void activateTab(Tab theTab) {
		for (int i = 1; i < _myTabs.size(); i++) {
			if (_myTabs.get(i) == theTab) {
				((Tab) _myTabs.get(i)).setActive(true);
			} else {
				((Tab) _myTabs.get(i)).setActive(false);
			}
		}
	}

	/**
	 * @invisible
	 * @return ControllerList
	 */
	public ControllerList tabs() {
		return _myTabs;
	}

	/**
	 * get a tab by name of a control window
	 * 
	 * @param theTabName
	 *            String
	 * @return Tab
	 */
	public Tab tab(String theTabName) {
		return controlP5.tab(this, theTabName);
	}

	/**
	 * remove a control window from controlP5.
	 */
	public void remove() {
		for (int i = _myTabs.size() - 1; i >= 0; i--) {
			((Tab) _myTabs.get(i)).remove();
		}
		_myTabs.clear();
		_myTabs.clearDrawable();
		// controlP5._myControlWindowList.remove(this);
	}

	/**
	 * clear the control window, delete all controllers from a control window.
	 */
	public void clear() {
		remove();
		if (_myApplet instanceof PAppletWindow) {
			_myApplet.stop();
			((PAppletWindow) _myApplet).dispose();
			_myApplet = null;
			System.gc();
		}

	}
	
	protected void updateFont(ControlFont theControlFont) {
		for (int i = 0; i < _myTabs.size(); i++) {
			((Tab) _myTabs.get(i)).updateFont(theControlFont);
		}
	}

	/**
	 * @invisible
	 */
	public void updateEvents() {
		isMouseOver = false;
		((ControllerInterface) _myTabs.get(0)).updateEvents();
		for (int i = 1; i < _myTabs.size(); i++) {
			((Tab) _myTabs.get(i)).continuousUpdateEvents();
			if (((Tab) _myTabs.get(i)).isActive()
					&& ((Tab) _myTabs.get(i)).isVisible()) {
				((ControllerInterface) _myTabs.get(i)).updateEvents();
			}
		}
	}

	/**
	 * returns true if the mouse is inside a controller. !!! doesnt work for
	 * groups yet.
	 * 
	 * @return
	 */
	public boolean isMouseOver() {
		return isMouseOver;
	}

	/**
	 * update all controllers contained in the control window if update is
	 * enabled.
	 */
	public void update() {
		((ControllerInterface) _myTabs.get(0)).update();
		for (int i = 1; i < _myTabs.size(); i++) {
			((Tab) _myTabs.get(i)).update();
		}
	}

	/**
	 * enable or disable the update function of a control window.
	 * 
	 * @param theFlag
	 *            boolean
	 */
	public void setUpdate(boolean theFlag) {
		isUpdate = theFlag;
		for (int i = 0; i < _myTabs.size(); i++) {
			((ControllerInterface) _myTabs.get(i)).setUpdate(theFlag);
		}
	}

	/**
	 * check the update status of a control window.
	 * 
	 * @return boolean
	 */
	public boolean isUpdate() {
		return isUpdate;
	}

	public void addCanvas(ControlWindowCanvas theCanvas) {
		_myControlWindowCanvas.add(theCanvas);
		theCanvas.setControlWindow(this);
	}

	public void removeCanvas(ControlWindowCanvas theCanvas) {
		_myControlWindowCanvas.remove(theCanvas);
	}

	/**
	 * @invisible
	 */
	public void pre() {
		if (isVisible) {

			if (isPAppletWindow) {
				_myApplet.background(background);
			}
		}
	}

	/**
	 * @invisible
	 */
	public void draw() {
		if (controlP5.blockDraw == false) {
			updateEvents();
			if (isVisible) {
				int myRectMode = _myApplet.g.rectMode;
				int myEllipseMode = _myApplet.g.ellipseMode;
				_myApplet.rectMode(_myApplet.CORNER);
				_myApplet.ellipseMode(_myApplet.CORNER);

				if (_myDrawable != null) {
					_myDrawable.draw(_myApplet);
				}

				for (int i = 0; i < _myControlWindowCanvas.size(); i++) {
					if (((ControlWindowCanvas) _myControlWindowCanvas.get(i))
							.mode() == ControlWindowCanvas.PRE) {
						((ControlWindowCanvas) _myControlWindowCanvas.get(i))
								.draw(_myApplet);
					}
				}

				// if (isPAppletWindow) {
				// _myApplet.background(background);
				// }
				_myApplet.noStroke();
				_myApplet.noFill();
				int myOffsetX = 0;
				int myOffsetY = 0;
				int myHeight = 0;
				for (int i = 1; i < _myTabs.size(); i++) {
					if (((Tab) _myTabs.get(i)).isVisible()) {
						if (myHeight < ((Tab) _myTabs.get(i)).height()) {
							myHeight = ((Tab) _myTabs.get(i)).height();
						}
						if (myOffsetX > component().getWidth()
								- ((Tab) _myTabs.get(i)).width()) {
							myOffsetY += myHeight + 1;
							myOffsetX = 0;
							myHeight = 0;
						}

						((Tab) _myTabs.get(i)).setOffset(myOffsetX, myOffsetY);
						if (((Tab) _myTabs.get(i)).updateLabel()) {
							((Tab) _myTabs.get(i)).drawLabel(_myApplet);
						}
						if (((Tab) _myTabs.get(i)).isActive()) {
							((Tab) _myTabs.get(i)).draw(_myApplet);
						}
						myOffsetX += ((Tab) _myTabs.get(i)).width();
					}
				}
				((ControllerInterface) _myTabs.get(0)).draw(_myApplet);

				for (int i = 0; i < _myControlWindowCanvas.size(); i++) {
					if (((ControlWindowCanvas) _myControlWindowCanvas.get(i))
							.mode() == ControlWindowCanvas.POST) {
						((ControlWindowCanvas) _myControlWindowCanvas.get(i))
								.draw(_myApplet);
					}
				}

				pmouseX = mouseX;
				pmouseY = mouseY;
				_myApplet.rectMode(myRectMode);
				_myApplet.ellipseMode(myEllipseMode);
			}
		}
	}

	/**
	 * @invisible
	 * @param theDrawable
	 *            CDrawable
	 */
	public void setContext(CDrawable theDrawable) {
		_myDrawable = theDrawable;
	}

	/**
	 * get the name of the control window.
	 * 
	 * @return String
	 */
	public String name() {
		return _myName;
	}

	/**
	 * @invisible
	 * @param theMouseEvent
	 *            MouseEvent
	 */
	public void mouseEvent(MouseEvent theMouseEvent) {
		mouseX = theMouseEvent.getX();
		mouseY = theMouseEvent.getY();
		if (isVisible) {
			if (theMouseEvent.getID() == MouseEvent.MOUSE_PRESSED) {
				mousePressed = true;
				for (int i = 0; i < _myTabs.size(); i++) {
					if (((ControllerInterface) _myTabs.get(i))
							.setMousePressed(true)) {
						mouselock = true;
						if (controlP5.DEBUG) {
							System.out.println("### mouselock = " + mouselock);
						}
						return;
					}
				}

			}
			if (theMouseEvent.getID() == MouseEvent.MOUSE_RELEASED) {
				mousePressed = false;
				mouselock = false;
				for (int i = 0; i < _myTabs.size(); i++) {
					((ControllerInterface) _myTabs.get(i))
							.setMousePressed(false);
				}
			}
		}
	}

	public void multitouch(int[][] theCoordinates) {
		for (int n = 0; n < theCoordinates.length; n++) {
			mouseX = theCoordinates[n][0];
			mouseY = theCoordinates[n][1];
			if (isVisible) {
				if (theCoordinates[n][2] == MouseEvent.MOUSE_PRESSED) {
					mousePressed = true;
					for (int i = 0; i < _myTabs.size(); i++) {
						if (((ControllerInterface) _myTabs.get(i))
								.setMousePressed(true)) {
							mouselock = true;
							if (controlP5.DEBUG) {
								System.out.println("### mouselock = "
										+ mouselock);
							}
							return;
						}
					}

				}
				if (theCoordinates[n][2] == MouseEvent.MOUSE_RELEASED) {
					mousePressed = false;
					mouselock = false;
					for (int i = 0; i < _myTabs.size(); i++) {
						((ControllerInterface) _myTabs.get(i))
								.setMousePressed(false);
					}
				}
			}
		}
	}

	public boolean isMousePressed() {
		return mousePressed;
	}

	/**
	 * @invisible
	 * @param theKeyEvent
	 *            KeyEvent
	 */
	public void keyEvent(KeyEvent theKeyEvent) {
		for (int i = 0; i < _myTabs.size(); i++) {
			((ControllerInterface) _myTabs.get(i)).keyEvent(theKeyEvent);
		}
	}

	/**
	 * set the color for the controller while active.
	 * 
	 * @invisible
	 * @param theColor
	 *            int
	 */
	public void setColorActive(int theColor) {
		color.colorActive = theColor;
		for (int i = 0; i < tabs().size(); i++) {
			((Tab) tabs().get(i)).setColorActive(theColor);
		}
	}

	/**
	 * set the foreground color of the controller.
	 * 
	 * @invisible
	 * @param theColor
	 *            int
	 */
	public void setColorForeground(int theColor) {
		color.colorForeground = theColor;
		for (int i = 0; i < tabs().size(); i++) {
			((Tab) tabs().get(i)).setColorForeground(theColor);
		}
	}

	/**
	 * set the background color of the controller.
	 * 
	 * @invisible
	 * @param theColor
	 *            int
	 */
	public void setColorBackground(int theColor) {
		color.colorBackground = theColor;
		for (int i = 0; i < tabs().size(); i++) {
			((Tab) tabs().get(i)).setColorBackground(theColor);
		}
	}

	/**
	 * set the color of the text label of the controller.
	 * 
	 * @invisible
	 * @param theColor
	 *            int
	 */
	public void setColorLabel(int theColor) {
		color.colorLabel = theColor;
		for (int i = 0; i < tabs().size(); i++) {
			((Tab) tabs().get(i)).setColorLabel(theColor);
		}
	}

	/**
	 * set the color of the values.
	 * 
	 * @param theColor
	 *            int
	 */
	public void setColorValue(int theColor) {
		color.colorValue = theColor;
		for (int i = 0; i < tabs().size(); i++) {
			((Tab) tabs().get(i)).setColorValue(theColor);
		}
	}

	/**
	 * set the background color of the control window.
	 * 
	 * @param theValue
	 *            int
	 */
	public void setBackground(int theValue) {
		background = theValue;
	}

	/**
	 * get the papplet instance of the ControlWindow.
	 * 
	 * @invisible
	 * @return PApplet
	 */
	public PApplet papplet() {
		return _myApplet;
	}

	public Component component() {
		return papplet();
	}

	/**
	 * set the title of a control window. only applies to control windows of
	 * type PAppletWindow.
	 */
	public void setTitle(String theTitle) {
		if (_myApplet instanceof PAppletWindow) {
			((PAppletWindow) _myApplet).setTitle(theTitle);
		}
	}

	/**
	 * shows the xy coordinates displayed in the title of a control window. only
	 * applies to control windows of type PAppletWindow.
	 * 
	 * @param theFlag
	 */
	public void showCoordinates() {
		if (_myApplet instanceof PAppletWindow) {
			((PAppletWindow) _myApplet).showCoordinates();
		}
	}

	/**
	 * hide the xy coordinates displayed in the title of a control window. only
	 * applies to control windows of type PAppletWindow.
	 * 
	 * @param theFlag
	 */
	public void hideCoordinates() {
		if (_myApplet instanceof PAppletWindow) {
			((PAppletWindow) _myApplet).hideCoordinates();
		}
	}

	/**
	 * hide the controllers and tabs of the ControlWindow.
	 */
	public void hide() {
		isVisible = false;
		if (isPAppletWindow) {
			((PAppletWindow) _myApplet).visible(false);
		}
	}

	/**
	 * @deprecated
	 * @param theMode
	 */
	public void setMode(int theMode) {
		setUpdateMode(theMode);
	}

	/**
	 * set the draw mode of a control window. a separate control window is only
	 * updated when in focus. to update the context of the window continuously,
	 * use yourControlWindow.setUpdateMode(ControlWindow.NORMAL); otherwise use
	 * yourControlWindow.setUpdateMode(ControlWindow.ECONOMIC); for an economic,
	 * less cpu intensive update.
	 * 
	 * @param theMode
	 */
	public void setUpdateMode(int theMode) {
		if (isPAppletWindow) {
			((PAppletWindow) _myApplet).setMode(theMode);
		}
	}

	/**
	 * set the frame rate of the control window.
	 * 
	 * @param theFrameRate
	 */
	public void frameRate(int theFrameRate) {
		_myApplet.frameRate(theFrameRate);
	}

	/**
	 * show the controllers and tabs of the ControlWindow.
	 */
	public void show() {
		isVisible = true;
		if (isPAppletWindow) {
			((PAppletWindow) _myApplet).visible(true);
		}

	}

	/**
	 * check if the content of the control window is visible.
	 * 
	 * @return boolean
	 */
	public boolean isVisible() {
		return isVisible;
	}

	protected ControlP5XMLElement getAsXML() {
		ControlP5XMLElement myXMLElement = new ControlP5XMLElement(
				new Hashtable(), true, false);
		myXMLElement.setName("window");
		myXMLElement.setAttribute("class", _myApplet.getClass().getName());
		myXMLElement.setAttribute("name", name());
		myXMLElement.setAttribute("width", "" + _myApplet.width);
		myXMLElement.setAttribute("height", "" + _myApplet.height);
		myXMLElement.setAttribute("background", ControlP5IOHandler
				.intToString(background));
		if (_myApplet.getClass().getName().indexOf("controlP5.PAppletWindow") != -1) {
			myXMLElement.setAttribute("x", "" + ((PAppletWindow) _myApplet).x);
			myXMLElement.setAttribute("y", "" + ((PAppletWindow) _myApplet).y);
		}
		return myXMLElement;
	}

}
