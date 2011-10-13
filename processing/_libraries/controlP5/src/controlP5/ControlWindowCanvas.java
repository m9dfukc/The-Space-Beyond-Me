package controlP5;

import processing.core.PApplet;

/**
 * be warned, for now ControlWindowCanvas and ControlCanvas are EXPERIMENTAL and 
 * will undergo changes in the future!
 * 
 * use a controlWindowCanvas to draw your own content into a control window.
 * 
 * @example ControlP5canvas
 * @author andreas
 * 
 */
public abstract class ControlWindowCanvas {

	protected ControlWindow _myControlWindow;
	public final static int PRE = 0;
	public final static int POST = 1;
	protected int _myMode = PRE;

	/**
	 * controlWindowCanvas is an abstract class and therefore needs to be
	 * extended by your class. draw(PApplet theApplet) is the only method that
	 * needs to be overwritten.
	 */
	public abstract void draw(
	        PApplet theApplet);

	/**
	 * move a canvas to another controlWindow
	 * 
	 * @param theControlWindow
	 */
	public void moveTo(
	        ControlWindow theControlWindow) {
		if (_myControlWindow != null) {
			_myControlWindow.removeCanvas(this);
		}
		theControlWindow.addCanvas(this);
	}

	/**
	 * get the drawing mode of a ControlWindowCanvas. this can be PRE or POST.
	 * 
	 * @return
	 */
	public final int mode() {
		return _myMode;
	}

	/**
	 * set the drawing mode to PRE. PRE is the default.
	 */
	public final void pre() {
		setMode(PRE);
	}

	/**
	 * set the drawing mode to POST.
	 */
	public final void post() {
		setMode(POST);
	}

	/**
	 * @invisible
	 * @param theMode
	 */
	public final void setMode(
	        int theMode) {
		if (theMode == PRE) {
			_myMode = PRE;
		} else {
			_myMode = POST;
		}
	}

	protected final void setControlWindow(
	        ControlWindow theControlWindow) {
		_myControlWindow = theControlWindow;
	}

	public final ControlWindow window() {
		return _myControlWindow;
	}
	
}
