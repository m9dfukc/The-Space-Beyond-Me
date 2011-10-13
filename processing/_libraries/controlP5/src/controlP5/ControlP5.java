package controlP5;

/**
 * controlP5 is a processing library to create simple control GUIs.
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
import processing.core.PFont;

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Vector;
import java.util.Iterator;

/**
 * controlP5 is a processing and java library for creating simple control GUIs.
 * 
 * @example ControlP5basics
 */
public class ControlP5 implements ControlP5Constants {

	// projects using controlP5
	// http://www.danielsauter.com/showreel.php?id=59
	// http://www.raintone.com/2009/03/fractalwavetables-v2/
	// http://www.flickr.com/photos/jrosenk/3631041263/
	// http://www.gtdv.org/
	// http://0p0media.com/aurapixlab/
	// http://www.introspector.be/index.php?/research/dook/

	// @todo
	// (1) registerPost does not draw the controller anymore. now changing to
	// registerDraw, still draws controllers on top of the sketch.
	// (2) enable positioning of tabs
	// (3) file dialog:
	// http://java.sun.com/j2se/1.5.0/docs/api/java/awt/FileDialog.html

	public ControlWindow controlWindow;

	private Hashtable _myControllerMap;

	protected ControlBroadcaster _myControlBroadcaster;

	private String _myUrlPath = "";

	private String _myFilePath = "controlP5.xml";

	public CColor color = new CColor();

	protected Vector _myControlWindowList;

	protected static boolean isLock = false;

	protected boolean isAutoInitialization = false;

	protected boolean isGlobalControllersAlwaysVisible = true;

	/**
	 * @invisible
	 */
	public static final int standard58 = 0;

	/**
	 * @invisible
	 */
	public static final int standard56 = 1;

	/**
	 * @invisible
	 */
	public static final int synt24 = 2;

	/**
	 * @invisible
	 */
	public static final int grixel = 3;

	/**
	 * @invisible
	 */
	public static ControlWindowKeyHandler keyHandler;

	/**
	 * @invisible
	 */
	public static PApplet papplet;

	/**
	 * @invisible
	 */
	public static final String VERSION = "0.4.2";

	/**
	 * @invisible
	 */
	public static boolean isApplet;

	/**
	 * @invisible
	 */
	public static boolean DEBUG;

	protected ControlP5IOHandler _myControlP5IOHandler;

	protected boolean isTabEventsActive;

	protected boolean isUpdate;

	protected static boolean isControlFont;

	protected static ControlFont controlFont;

	// use blockDraw to prevent controlp5 to draw any elements. this is useful
	// when using clear() or load()
	protected boolean blockDraw;

	/**
	 * instantiate controlP5.
	 * 
	 * @param theParent
	 *        PApplet
	 */
	public ControlP5(final PApplet theParent) {
		// gui addons inspiration.
		// http://www.futureaudioworkshop.com/
		// 
		// add ControlIcon for custom icons with PImage
		papplet = theParent;
		init();
	}

	public ControlP5(final PApplet theParent, ControlFont theControlFont) {
		// gui addons inspiration.
		// http://www.futureaudioworkshop.com/
		// 
		// add ControlIcon for custom icons with PImage
		papplet = theParent;
		setControlFont(theControlFont);
		init();
	}

	protected void init() {
		welcome();
		isTabEventsActive = false;
		_myControlP5IOHandler = new ControlP5IOHandler(this);
		_myControlWindowList = new Vector();
		_myControlBroadcaster = new ControlBroadcaster(this);
		keyHandler = new ControlWindowKeyHandler(this);
		controlWindow = new ControlWindow(this, papplet);
		papplet.registerKeyEvent(new ControlWindowKeyListener(this));
		_myControllerMap = new Hashtable();
		_myControlWindowList.add(controlWindow);
		isApplet = papplet.online;
	}

	private void welcome() {
		System.out.println("ControlP5 "
			+ VERSION
			+ " "
			+ "infos, comments, questions at http://www.sojamo.de/libraries/controlP5");
	}

	public void setTabEventsActive(boolean theFlag) {
		isTabEventsActive = theFlag;
	}

	/**
	 * autoInitialization can be very handy when it comes to initializing
	 * values, e.g. you load a set of controllers from an xml file, then the
	 * values that are attached to the controllers will be reset to its state
	 * saved in the xml file. to turn of auto intialization, call
	 * setAutoInitialization(false) right after initializing controlP5 and
	 * before creating any controller.
	 * 
	 * @param theFlag
	 *        boolean
	 */
	public void setAutoInitialization(boolean theFlag) {
		isAutoInitialization = theFlag;
	}

	/**
	 * by default controlP5 draws any controller on top of any drawing done in
	 * the draw() function (this doesnt apply to P3D where controlP5.draw() has
	 * to be called manually in the sketch's draw() function ). to turn off the
	 * auto drawing of controlP5, use controlP5.setAutoDraw(false). now you can
	 * call controlP5.draw() any time whenever controllers should be drawn into
	 * the sketch.
	 * 
	 * @param theFlag
	 *        boolean
	 */
	public void setAutoDraw(boolean theFlag) {
		if (isAutoDraw() && theFlag == false) {
			controlWindow.papplet().unregisterDraw(controlWindow);
		}
		if (isAutoDraw() == false && theFlag == true) {
			controlWindow.papplet().registerDraw(controlWindow);
		}
		controlWindow.isAutoDraw = theFlag;
	}

	/**
	 * check if the autoDraw function for the main window is enabled(true) or
	 * disabled(false).
	 * 
	 * @return boolean
	 */
	public boolean isAutoDraw() {
		return controlWindow.isAutoDraw;
	}

	/**
	 * @invisible
	 * @return ControlBroadcaster
	 */
	public ControlBroadcaster controlbroadcaster() {
		return _myControlBroadcaster;
	}

	/**
	 * add a tab to controlP5. by default the tab will be added to the main
	 * window.
	 * 
	 * @param theName
	 *        String
	 * @return Tasaveb
	 */
	public Tab addTab(String theName) {
		return addTab(controlWindow, theName);
	}

	public Tab addTab(PApplet theWindow, String theName) {
		return addTab(controlWindow, theName);
	}

	public Tab addTab(ControlWindow theWindow, String theName) {
		for (int i = 0; i < theWindow.tabs().size(); i++) {
			if (theWindow.tabs().get(i).name().equals(theName)) {
				return (Tab) theWindow.tabs().get(i);
			}
		}
		Tab myTab = new Tab(this, theWindow, theName);
		theWindow.tabs().add(myTab);
		return myTab;
	}

	/**
	 * get a tab by name.
	 * 
	 * @param theName
	 *        String
	 * @return Tab
	 */
	public Tab tab(String theName) {
		return getTab(theName);
	}

	/**
	 * get a tab by name.
	 * 
	 * @param theName
	 *        String
	 * @return Tab
	 */
	public Tab getTab(String theName) {
		for (int j = 0; j < _myControlWindowList.size(); j++) {
			for (int i = 0; i < ((ControlWindow) _myControlWindowList.get(j)).tabs().size(); i++) {
				if (((Tab) ((ControlWindow) _myControlWindowList.get(j)).tabs().get(i)).name().equals(theName)) {
					return (Tab) ((ControlWindow) _myControlWindowList.get(j)).tabs().get(i);
				}
			}
		}
		Tab myTab = addTab(theName);
		return myTab;
	}

	/**
	 * get a tab by name from a specific controlwindow.
	 * 
	 * @param theWindow
	 *        ControlWindow
	 * @param theName
	 *        String
	 * @return Tab
	 */
	public Tab tab(ControlWindow theWindow, String theName) {
		return getTab(theWindow, theName);
	}

	/**
	 * get a tab by name from a specific controlwindow.
	 * 
	 * @param theWindow
	 *        ControlWindow
	 * @param theName
	 *        String
	 * @return Tab
	 */
	public Tab getTab(ControlWindow theWindow, String theName) {
		for (int i = 0; i < theWindow.tabs().size(); i++) {
			if (((Tab) theWindow.tabs().get(i)).name().equals(theName)) {
				return (Tab) theWindow.tabs().get(i);
			}
		}
		Tab myTab = theWindow.add(new Tab(this, theWindow, theName));
		return myTab;
	}

	/**
	 * 
	 * a button to controlP5. by default it will be added to the default tab of
	 * the main window.
	 * 
	 * @param theName
	 *        String
	 * @param theValue
	 *        float
	 * @param theX
	 *        int
	 * @param theY
	 *        int
	 * @param theW
	 *        int
	 * @param theH
	 *        int
	 * @return Button
	 */
	public Button addButton(
		final String theName,
		final float theValue,
		final int theX,
		final int theY,
		final int theW,
		final int theH) {
		Button myController = new Button(
			this,
			(ControllerGroup) controlWindow.tabs().get(1),
			theName,
			theValue,
			theX,
			theY,
			theW,
			theH);
		register(myController);
		return myController;
	}

	/**
	 * add a bang to controlP5. by default it will be added to the default tab
	 * of the main window.
	 * 
	 * @param theName
	 *        String
	 * @param theX
	 *        int
	 * @param theY
	 *        int
	 * @param theWidth
	 *        int
	 * @param theHeight
	 *        int
	 * @return Bang
	 */
	public Bang addBang(final String theName, final int theX, final int theY, final int theWidth, final int theHeight) {
		Bang myController = new Bang(this, (Tab) controlWindow.tabs().get(1), theName, theX, theY, theWidth, theHeight);
		register(myController);
		return myController;
	}

	/**
	 * add a toggle to controlP5. by default it will be added to the default tab
	 * of the main window.
	 * 
	 * @param theName
	 *        String
	 * @param theDefaultValue
	 *        boolean
	 * @param theX
	 *        float
	 * @param theY
	 *        float
	 * @param theWidth
	 *        int
	 * @param theHeight
	 *        int
	 * @return Toggle
	 */
	public Toggle addToggle(
		final String theName,
		final boolean theDefaultValue,
		final float theX,
		final float theY,
		final int theWidth,
		final int theHeight) {
		Toggle myController = new Toggle(this, (Tab) controlWindow.tabs().get(1), theName, (theDefaultValue == true)
			? 1f
			: 0f, theX, theY, theWidth, theHeight);
		register(myController);
		return myController;

	}

	/**
	 * add a toggle to controlP5. by default it will be added to the default tab
	 * of the main window.
	 * 
	 * @param theName
	 *        String
	 * @param theDefaultValue
	 *        boolean
	 * @param theX
	 *        float
	 * @param theY
	 *        float
	 * @param theWidth
	 *        int
	 * @param theHeight
	 *        int
	 * @return Toggle
	 * @related Toggle
	 */
	public Toggle addToggle(
		final String theName,
		final float theX,
		final float theY,
		final int theWidth,
		final int theHeight) {
		Toggle myController = new Toggle(
			this,
			(Tab) controlWindow.tabs().get(1),
			theName,
			Float.NaN,
			theX,
			theY,
			theWidth,
			theHeight);
		register(myController);
		return myController;

	}

	public Matrix addMatrix(
		final String theName,
		final int theCellX,
		final int theCellY,
		final int theX,
		final int theY,
		final int theWidth,
		final int theHeight) {
		Matrix myController = new Matrix(
			this,
			(Tab) controlWindow.tabs().get(1),
			theName,
			theCellX,
			theCellY,
			theX,
			theY,
			theWidth,
			theHeight);
		register(myController);
		return myController;
	}

	/**
	 * add a slider to controlP5. by default it will be added to the default tab
	 * of the main window.
	 * 
	 * @param theName
	 *        String
	 * @param theMin
	 *        float
	 * @param theMax
	 *        float
	 * @param theDefaultValue
	 *        float
	 * @param theX
	 *        int
	 * @param theY
	 *        int
	 * @param theW
	 *        int
	 * @param theH
	 *        int
	 * @return Slider
	 */
	public Slider addSlider(
		String theName,
		float theMin,
		float theMax,
		float theDefaultValue,
		int theX,
		int theY,
		int theW,
		int theH) {
		Slider myController = new Slider(
			this,
			(ControllerGroup) controlWindow.tabs().get(1),
			theName,
			theMin,
			theMax,
			theDefaultValue,
			theX,
			theY,
			theW,
			theH);
		register(myController);
		return myController;
	}

	/**
	 * add a slider to controlP5. by default it will be added to the default tab
	 * of the main window.
	 * 
	 * @param theName
	 *        String
	 * @param theMin
	 *        float
	 * @param theMax
	 *        float
	 * @param theX
	 *        int
	 * @param theY
	 *        int
	 * @param theWidth
	 *        int
	 * @param theHeight
	 *        int
	 * @return Slider
	 */
	public Slider addSlider(
		final String theName,
		final float theMin,
		final float theMax,
		final int theX,
		final int theY,
		final int theWidth,
		final int theHeight) {
		return addSlider(theName, theMin, theMax, Float.NaN, theX, theY, theWidth, theHeight);
	}

	public Range addRange(
		String theName,
		float theMin,
		float theMax,
		float theDefaultMinValue,
		float theDefaultMaxValue,
		int theX,
		int theY,
		int theW,
		int theH) {
		Range myController = new Range(
			this,
			(ControllerGroup) controlWindow.tabs().get(1),
			theName,
			theMin,
			theMax,
			theDefaultMinValue,
			theDefaultMaxValue,
			theX,
			theY,
			theW,
			theH);
		register(myController);
		return myController;
	}

	/**
	 * add a slider to controlP5. by default it will be added to the default tab
	 * of the main window.
	 * 
	 * @param theName
	 *        String
	 * @param theMin
	 *        float
	 * @param theMax
	 *        float
	 * @param theX
	 *        int
	 * @param theY
	 *        int
	 * @param theWidth
	 *        int
	 * @param theHeight
	 *        int
	 * @return Slider
	 */
	public Range addRange(
		final String theName,
		final float theMin,
		final float theMax,
		final int theX,
		final int theY,
		final int theWidth,
		final int theHeight) {
		return addRange(theName, theMin, theMax, theMin, theMax, theX, theY, theWidth, theHeight);
	}

	/**
	 * add a numberbox to controlP5. by default it will be added to the default
	 * tab of the main window.
	 * 
	 * @param theName
	 *        String
	 * @param theX
	 *        float
	 * @param theY
	 *        float
	 * @param theWidth
	 *        int
	 * @param theHeight
	 *        int
	 * @return Numberbox
	 * @related Numberbox
	 */
	public Numberbox addNumberbox(
		final String theName,
		final int theX,
		final int theY,
		final int theWidth,
		final int theHeight) {
		return addNumberbox(theName, Float.NaN, theX, theY, theWidth, theHeight);
	}

	/**
	 * add a numberbox to controlP5. by default it will be added to the default
	 * tab of the main window.
	 * 
	 * @param theName
	 *        String
	 * @param theDefaultValue
	 *        int
	 * @param theX
	 *        float
	 * @param theY
	 *        float
	 * @param theWidth
	 *        int
	 * @param theHeight
	 *        int
	 * @return Numberbox
	 * @related Numberbox
	 */
	public Numberbox addNumberbox(
		final String theName,
		final float theDefaultValue,
		final int theX,
		final int theY,
		final int theWidth,
		final int theHeight) {
		Numberbox myController = new Numberbox(
			this,
			(Tab) controlWindow.tabs().get(1),
			theName,
			theDefaultValue,
			theX,
			theY,
			theWidth,
			theHeight);
		register(myController);
		return myController;
	}

	/**
	 * add a knob to your controlP5 setup.
	 * 
	 * @param theName
	 *        String
	 * @param theMin
	 *        float
	 * @param theMax
	 *        float
	 * @param theX
	 *        float
	 * @param theY
	 *        float
	 * @param theDiameter
	 *        int
	 * @return Knob
	 * @related Knob
	 */
	public Knob addKnob(
		final String theName,
		final float theMin,
		final float theMax,
		final int theX,
		final int theY,
		final int theDiameter) {
		return addKnob(theName, theMin, theMax, Float.NaN, theX, theY, theDiameter);
	}

	public MultiList addMultiList(
		final String theName,
		final int theX,
		final int theY,
		final int theWidth,
		final int theHeight) {
		MultiList myMultiList = new MultiList(
			this,
			(Tab) controlWindow.tabs().get(1),
			theName,
			theX,
			theY,
			theWidth,
			theHeight);
		register(myMultiList);
		return myMultiList;
	}

	/**
	 * add a knob to controlP5. by default it will be added to the default tab
	 * of the main window.
	 * 
	 * @param theName
	 *        String
	 * @param theMin
	 *        float
	 * @param theMax
	 *        float
	 * @param theDefaultValue
	 *        float
	 * @param theX
	 *        float
	 * @param theY
	 *        float
	 * @param theDiameter
	 *        int
	 * @return Knob
	 * @related Knob
	 */
	public Knob addKnob(
		final String theName,
		final float theMin,
		final float theMax,
		final float theDefaultValue,
		final int theX,
		final int theY,
		final int theDiameter) {
		Knob myController = new Knob(
			this,
			(Tab) controlWindow.tabs().get(1),
			theName,
			theMin,
			theMax,
			theDefaultValue,
			theX,
			theY,
			theDiameter);
		register(myController);
		return myController;
	}

	/**
	 * add a textlabel to controlP5. by default it will be added to the default
	 * tab of the main window.
	 * 
	 * @param theName
	 *        String
	 * @param theText
	 *        String
	 * @param theX
	 *        int
	 * @param theY
	 *        int
	 * @param theW
	 *        int
	 * @param theH
	 *        int
	 * @return Textlabel
	 */
	public Textarea addTextarea(
		final String theName,
		final String theText,
		final int theX,
		final int theY,
		final int theW,
		final int theH) {
		Textarea myController = new Textarea(
			this,
			(Tab) controlWindow.tabs().get(1),
			theName,
			theText,
			theX,
			theY,
			theW,
			theH);
		register(myController);
		return myController;
	}

	public Textlabel addTextlabel(final String theName, final String theText, final int theX, final int theY) {
		Textlabel myController = new Textlabel(this, (Tab) controlWindow.tabs().get(1), theName, theText, theX, theY);
		register(myController);
		return myController;
	}

	/**
	 * add a textfield to controlP5. by default it will be added to the default
	 * tab of the main window.
	 * 
	 * @param theName
	 *        String
	 * @param theX
	 *        int
	 * @param theY
	 *        int
	 * @param theW
	 *        int
	 * @param theH
	 *        int
	 * @return Textfield
	 */
	public Textfield addTextfield(final String theName, final int theX, final int theY, final int theW, final int theH) {
		Textfield myController = new Textfield(
			this,
			(Tab) controlWindow.tabs().get(1),
			theName,
			"",
			theX,
			theY,
			theW,
			theH);
		register(myController);
		return myController;
	}

	/**
	 * add a radio list to controlP5. by default it will be added to the default
	 * tab of the main window.
	 * 
	 * @param theName
	 *        String
	 * @param theX
	 *        int
	 * @param theY
	 *        int
	 * @return Radio
	 */
	public Radio addRadio(final String theName, final int theX, final int theY) {
		Radio myController = new Radio(this, (Tab) controlWindow.tabs().get(1), theName, theX, theY);
		register(myController);
		return myController;
	}

	public Radio addRadio(
		final String theName,
		final int theX,
		final int theY,
		final int theWidth,
		final int theHeight,
		final int theLineSpacing) {
		Radio myController = new Radio(
			this,
			(Tab) controlWindow.tabs().get(1),
			theName,
			theX,
			theY,
			theWidth,
			theHeight,
			theLineSpacing);
		register(myController);
		return myController;
	}

	public RadioButton addRadioButton(final String theName, final int theX, final int theY) {
		RadioButton myController = new RadioButton(this, (Tab) controlWindow.tabs().get(1), theName, theX, theY);
		register(myController);
		return myController;
	}

	public CheckBox addCheckBox(final String theName, final int theX, final int theY) {
		CheckBox myController = new CheckBox(this, (Tab) controlWindow.tabs().get(1), theName, theX, theY);
		register(myController);
		return myController;
	}

	/**
	 * add a scroll list to controlP5. by default it will be added to the
	 * default tab of the main window.
	 * 
	 * @param theName
	 *        String
	 * @param theX
	 *        int
	 * @param theY
	 *        int
	 * @param theW
	 *        int
	 * @param theH
	 *        int
	 * @return ScrollList
	 */
	public ScrollList addScrollList(final String theName, final int theX, final int theY, final int theW, final int theH) {
		ScrollList myController = new ScrollList(
			this,
			(Tab) controlWindow.tabs().get(1),
			theName,
			theX,
			theY,
			theW,
			theH);
		register(myController);
		return myController;
	}

	public ListBox addListBox(final String theName, final int theX, final int theY, final int theW, final int theH) {
		ListBox myController = new ListBox(this, (Tab) controlWindow.tabs().get(1), theName, theX, theY, theW, theH);
		register(myController);
		return myController;
	}

	/**
	 * add a group to controlP5. by default it will be added to the default tab
	 * of the main window.
	 * 
	 * @param theName
	 *        String
	 * @param theX
	 *        int
	 * @param theY
	 *        int
	 * @return ControlGroup
	 */
	public ControlGroup addGroup(String theName, int theX, int theY, int theW) {
		ControlGroup myController = new ControlGroup(
			this,
			(ControllerGroup) controlWindow.tabs().get(1),
			theName,
			theX,
			theY,
			theW,
			9);
		register(myController);
		return myController;
	}

	public ControlGroup addGroup(String theName, int theX, int theY) {
		return addGroup(theName, theX, theY, 99);
	}

	/**
	 * @invisible
	 * @param theController
	 *        ControllerInterface
	 */
	public void register(ControllerInterface theController) {
		checkName(theController.name());
		_myControllerMap.put(theController.name(), theController);
		theController.init();
	}

	public ControllerInterface[] getControllerList() {
		ControllerInterface[] myControllerList = new ControllerInterface[_myControllerMap.size()];
		_myControllerMap.values().toArray(myControllerList);
		return myControllerList;
	}

	protected void deactivateControllers() {
		if (getControllerList() != null) {
			ControllerInterface[] n = getControllerList();
			for (int i = 0; i < n.length; i++) {
				if (n[i] instanceof Textfield) {
					((Textfield) n[i]).setFocus(false);
				}
			}
		}
	}

	/**
	 * @invisible
	 */
	protected void clear() {
		for (int i = _myControlWindowList.size() - 1; i >= 0; i--) {
			((ControlWindow) _myControlWindowList.get(i)).clear();
		}

		for (int i = _myControlWindowList.size() - 1; i >= 0; i--) {
			_myControlWindowList.remove(i);
		}

		_myControllerMap.clear();
		// controlWindow.init();
	}

	/**
	 * remove a controlWindow and all its contained controllers.
	 * 
	 * @param theWindow
	 *        ControlWindow
	 */
	protected void remove(ControlWindow theWindow) {
		theWindow.remove();
		_myControlWindowList.remove(theWindow);
	}

	/**
	 * remove a controller by instance.
	 * 
	 * @param theController
	 *        ControllerInterface
	 */
	protected void remove(ControllerInterface theController) {
		_myControllerMap.remove(theController.name());
	}

	/**
	 * remove a controlP5 element such as a controller, group, or tab by name.
	 * 
	 * @param theString
	 *        String
	 */
	public void remove(String theString) {
		if (controller(theString) != null) {
			controller(theString).remove();
		}

		if (group(theString) != null) {
			group(theString).remove();
		}

		for (int j = 0; j < _myControlWindowList.size(); j++) {
			for (int i = 0; i < ((ControlWindow) _myControlWindowList.get(j)).tabs().size(); i++) {
				if (((Tab) ((ControlWindow) _myControlWindowList.get(j)).tabs().get(i)).name().equals(theString)) {
					((Tab) ((ControlWindow) _myControlWindowList.get(j)).tabs().get(i)).remove();
				}
			}
		}

	}

	/**
	 * get a controller by name. you will have to cast the controller.
	 * 
	 * @param theName
	 *        String
	 * @return Controller
	 */
	public Controller controller(String theName) {
		if (_myControllerMap.containsKey(theName)) {
			if (_myControllerMap.get(theName) instanceof Controller) {
				return (Controller) _myControllerMap.get(theName);
			}
		}
		return null;
	}

	/**
	 * get a group by name
	 * 
	 * @param theGroupName
	 *        String
	 * @return ControllerGroup
	 */
	public ControllerGroup group(String theGroupName) {
		return getGroup(theGroupName);
	}

	/**
	 * get a group by name.
	 * 
	 * @param theGroupName
	 *        String
	 * @return ControllerGroup
	 */
	public ControllerGroup getGroup(String theGroupName) {
		if (_myControllerMap.containsKey(theGroupName)) {
			if (_myControllerMap.get(theGroupName) instanceof ControllerGroup) {
				return (ControllerGroup) _myControllerMap.get(theGroupName);
			}
		}
		return null;
	}

	public void draw() {
		if (blockDraw == false) {
			controlWindow.draw();
		}
	}

	/**
	 * create a new ControlWindow.
	 * 
	 * @param theWindowName
	 *        String
	 * @param theWidth
	 *        int
	 * @param theHeight
	 *        int
	 * @return ControlWindow
	 * @related ControlWindow
	 */
	public ControlWindow addControlWindow(final String theWindowName, final int theWidth, final int theHeight) {
		return addControlWindow(theWindowName, 400, 200, theWidth, theHeight, "", 15);
	}

	public ControlWindow addControlWindow(
		final String theWindowName,
		final int theX,
		final int theY,
		final int theWidth,
		final int theHeight) {
		return addControlWindow(theWindowName, theX, theY, theWidth, theHeight, "", 15);
	}

	public ControlWindow addControlWindow(
		final String theWindowName,
		final int theX,
		final int theY,
		final int theWidth,
		final int theHeight,
		final int theFrameRate) {
		return addControlWindow(theWindowName, theX, theY, theWidth, theHeight, "", theFrameRate);
	}

	/**
	 * create a new ControlWindow.
	 * 
	 * @param theWindowName
	 *        String
	 * @param theX
	 *        int
	 * @param theY
	 *        int
	 * @param theWidth
	 *        int
	 * @param theHeight
	 *        int
	 * @return ControlWindow
	 * @related ControlWindow
	 */
	public ControlWindow addControlWindow(
		final String theWindowName,
		final int theX,
		final int theY,
		final int theWidth,
		final int theHeight,
		String theRenderer,
		int theFrameRate) {
		for (int i = 0; i < _myControlWindowList.size(); i++) {
			if (((ControlWindow) _myControlWindowList.get(i)).name().equals(theWindowName)) {
				System.out.println("### WARNING ###\n"
					+ "### ControlWindow with name "
					+ theWindowName
					+ " already exists. overwritten!");
			}
		}
		PAppletWindow myPAppletWindow = new PAppletWindow(
			theWindowName,
			theX,
			theY,
			theWidth,
			theHeight,
			theRenderer,
			theFrameRate);
		myPAppletWindow.setParent(this);
		myPAppletWindow.setMode(PAppletWindow.ECONOMIC);
		ControlWindow myControlWindow = new ControlWindow(this, myPAppletWindow);
		_myControlWindowList.add(myControlWindow);
		return myControlWindow;
	}

	public ControlWindow window(PApplet theApplet) {
		if (theApplet.equals(papplet)) {
			return controlWindow;
		}
		// !!! check for another window in case theApplet is of type
		// PAppletWindow.
		return controlWindow;
	}

	/**
	 * get a ControlWindow by name.
	 * 
	 * @param theName
	 *        String
	 * @return ControlWindow
	 * @related ControlWindow
	 */
	public ControlWindow window(String theWindowName) {
		for (int i = 0; i < _myControlWindowList.size(); i++) {
			if (((ControlWindow) _myControlWindowList.get(i)).name().equals(theWindowName)) {
				return (ControlWindow) _myControlWindowList.get(i);
			}
		}
		System.out.println("### WARNING ###\n"
			+ "### ControlWindow "
			+ theWindowName
			+ " does not exist. returning null.");
		return null;
	}

	private boolean checkName(String theName) {
		if (_myControllerMap.containsKey(theName)) {
			System.out.println("### WARNING. controller with name \""
				+ theName
				+ "\" already exists. overwriting reference of existing controller.");
			return true;
		}
		return false;
	}

	/**
	 * set the path / filename of the xml file your controlP5 setup will be
	 * saved to.
	 * 
	 * @param theFilename
	 *        String
	 * @related setUrlPath ( )
	 * @related save ( )
	 * @related load ( )
	 * @related loadUrl ( )
	 */
	public void setFilePath(String theFilePath) {
		if (theFilePath == null) {
			theFilePath = "";
		}
		_myFilePath = theFilePath;
	}

	/**
	 * you can set an url an e.g. index.php file on a server where you want to
	 * save your controlP5 setup to.
	 * 
	 * @param theUrlPath
	 *        String
	 * @related setFilePath ( )
	 * @related loadUrl ( )
	 * @related load ( )
	 * @related save ( )
	 */
	public void setUrlPath(String theUrlPath) {
		setUrlPath(theUrlPath, "controlP5.xml");
	}

	/**
	 * you can set an url e.g. an index.php file on a server where you want to
	 * save your controlP5 setup to.
	 * 
	 * @param theUrlPath
	 *        String
	 * @param theFilename
	 *        String
	 * @related setFilePath ( )
	 * @related loadUrl ( )
	 * @related load ( )
	 * @related save ( )
	 * 
	 */
	public void setUrlPath(String theUrlPath, String theFilename) {
		if (theUrlPath == null) {
			theUrlPath = "";
			return;
		}
		theUrlPath = _myControlP5IOHandler.replace(theUrlPath, "&amp;", "&");
		if (theUrlPath.indexOf('?') == -1) {
			theUrlPath += '?';
		}
		else {
			if (!theUrlPath.endsWith("&") && !theUrlPath.endsWith("?")) {
				theUrlPath += "&";
			}
		}
		_myUrlPath = theUrlPath + "filename=" + theFilename;
	}

	/**
	 * get the current file path where your controlP5 setup will be save to on
	 * your local disk.
	 * 
	 * @return String
	 */
	public String filePath() {
		return _myFilePath;
	}

	/**
	 * get the current url path where your controlP5 setup will be save to a
	 * remote server e.g. a webserver.
	 * 
	 * @return String
	 */
	public String urlPath() {
		return _myUrlPath;
	}

	/**
	 * set the active state color of tabs and controllers.
	 * 
	 * @param theColor
	 *        int
	 */
	public void setColorActive(int theColor) {
		color.colorActive = theColor;
		for (Enumeration e = _myControlWindowList.elements(); e.hasMoreElements();) {
			ControlWindow myControlWindow = (ControlWindow) e.nextElement();
			myControlWindow.setColorActive(theColor);
		}
	}

	/**
	 * set the foreground color of tabs and controllers.
	 * 
	 * @param theColor
	 *        int
	 */
	public void setColorForeground(int theColor) {
		color.colorForeground = theColor;
		for (Enumeration e = _myControlWindowList.elements(); e.hasMoreElements();) {
			ControlWindow myControlWindow = (ControlWindow) e.nextElement();
			myControlWindow.setColorForeground(theColor);
		}
	}

	/**
	 * set the backgorund color of tabs and controllers.
	 * 
	 * @param theColor
	 *        int
	 */
	public void setColorBackground(int theColor) {
		color.colorBackground = theColor;
		for (Enumeration e = _myControlWindowList.elements(); e.hasMoreElements();) {
			ControlWindow myControlWindow = (ControlWindow) e.nextElement();
			myControlWindow.setColorBackground(theColor);
		}
	}

	/**
	 * set the label color of tabs and controllers.
	 * 
	 * @param theColor
	 *        int
	 */
	public void setColorLabel(int theColor) {
		color.colorLabel = theColor;
		for (Enumeration e = _myControlWindowList.elements(); e.hasMoreElements();) {
			ControlWindow myControlWindow = (ControlWindow) e.nextElement();
			myControlWindow.setColorLabel(theColor);
		}
	}

	/**
	 * set the value color of controllers.
	 * 
	 * @param theColor
	 *        int
	 */
	public void setColorValue(int theColor) {
		color.colorValue = theColor;
		for (Enumeration e = _myControlWindowList.elements(); e.hasMoreElements();) {
			ControlWindow myControlWindow = (ControlWindow) e.nextElement();
			myControlWindow.setColorValue(theColor);
		}
	}

	protected Vector controlWindows() {
		return _myControlWindowList;
	}

	/**
	 * lock ControlP5 to disable moving Controllers around.
	 * 
	 */
	public void lock() {
		isLock = true;
	}

	/**
	 * unlock ControlP5 to enable moving Controllers around.
	 * 
	 */
	public void unlock() {
		isLock = false;
	}

	/**
	 * save controlP5 settings to your local disk or to a remote server. a file
	 * controlP5.xml will be written to the data folder of your sketch. you can
	 * set another file path with method setFilePath(). to save a file to a
	 * remote server set the url with setUrlPath() e.g.
	 * setUrlPath("http://yourdomain.com/controlP5/upload.php");
	 * 
	 * @shortdesc save controlP5 settings to your local disk or to a remote
	 *            server.
	 * @param theFilename
	 *        String
	 * @return boolean
	 * @related setFilePath ( )
	 * @related setUrlPath ( )
	 * @related load ( )
	 * @related loadUrl ( )
	 * @todo saving in application mode does write the xml file into a folder
	 *       data on top level, but does not load from there. therefore loading
	 *       in application mode one would have to use the inputstreamreader
	 *       used before switching to loadStrings.
	 */
	public boolean save(String theFilePath) {
		return _myControlP5IOHandler.save(this, theFilePath);
	}

	/**
	 * save controlP5 settings to your local disk or to a remote server. a file
	 * controlP5.xml will be written to the data folder of your sketch. you can
	 * set another file path with method setFilePath(). to save a file to a
	 * remote server set the url with setUrlPath() e.g.
	 * setUrlPath("http://yourdomain.com/controlP5/upload.php");
	 * 
	 * @shortdesc save controlP5 settings to your local disk or to a remote
	 *            server.
	 * @return boolean
	 * @related load ( )
	 * @related setUrlPath ( )
	 * @related setFilePath ( )
	 */
	public boolean save() {
		if (_myFilePath != null) {
			return _myControlP5IOHandler.save(this, _myFilePath);
		}
		else {
			return false;
		}
	}

	/**
	 * load an xml file, containing a controlP5 setup
	 * 
	 * @param theFileName
	 */
	public boolean load(String theFileName) {
		blockDraw = true;
		clear();
		System.out.println("loading.." + theFileName);
		String[] myStrings = papplet.loadStrings(theFileName);
		String myString = "";
		for (int i = 0; i < myStrings.length; i++) {
			myString += myStrings[i];
		}
		if (myString.length() == 0) {
			return false;
		}

		try {
			Thread.sleep(200);
		}
		catch (Exception e) {}
		_myControlP5IOHandler.parse(myString);
		blockDraw = false;
		return true;
	}

	/**
	 * get the current version of controlP5
	 * 
	 * @return String
	 */
	public String version() {
		return VERSION;
	}

	/**
	 * show all controllers and tabs in your sketch.
	 */

	public void show() {
		controlWindow.show();
	}

	public boolean isVisible() {
		return controlWindow.isVisible();
	}

	/**
	 * hide all controllers and tabs in your sketch.
	 */
	public void hide() {
		controlWindow.hide();
	}

	public void update() {
		for (Enumeration e = _myControlWindowList.elements(); e.hasMoreElements();) {
			ControlWindow myControlWindow = (ControlWindow) e.nextElement();
			myControlWindow.update();
		}
	}

	public boolean isUpdate() {
		return isUpdate;
	}

	public void setUpdate(boolean theFlag) {
		isUpdate = theFlag;
		for (Enumeration e = _myControlWindowList.elements(); e.hasMoreElements();) {
			ControlWindow myControlWindow = (ControlWindow) e.nextElement();
			myControlWindow.setUpdate(theFlag);
		}
	}

	/**
	 * forces each available controller to broadcast its value. deprecated. use
	 * .update() instead.
	 * 
	 * @deprecated
	 */
	public void trigger() {
		Iterator iter = _myControllerMap.keySet().iterator();
		while (iter.hasNext()) { // or your for as well
			Object key = iter.next();
			if (_myControllerMap.get(key) instanceof Controller) {
				((Controller) _myControllerMap.get(key)).trigger();
			}
		}
	}

	public boolean setControlFont(ControlFont theControlFont) {
		controlFont = theControlFont;
		isControlFont = true;
		updateFont(controlFont);
		return isControlFont;
	}

	public boolean setControlFont(PFont thePFont, int theFontSize) {
		controlFont = new ControlFont(thePFont, theFontSize);
		isControlFont = true;
		updateFont(controlFont);
		return isControlFont;
	}
	
	public boolean setControlFont(PFont thePFont) {
		controlFont = new ControlFont(thePFont, thePFont.size);
		isControlFont = true;
		updateFont(controlFont);
		return isControlFont;
	}

	protected void updateFont(ControlFont theControlFont) {
		for (Enumeration e = _myControlWindowList.elements(); e.hasMoreElements();) {
			ControlWindow myControlWindow = (ControlWindow) e.nextElement();
			myControlWindow.updateFont(theControlFont);
		}
	}

	public static ControlFont getControlFont() {
		return controlFont;
	}

}

// new controllers
// http://www.cambridgeincolour.com/tutorials/photoshop-curves.htm
// http://images.google.com/images?q=synthmaker
// http://en.wikipedia.org/wiki/Pie_menu
// 
// inspiration
// http://www.explodingart.com/arb/Andrew_R._Brown/Code/Code.html

