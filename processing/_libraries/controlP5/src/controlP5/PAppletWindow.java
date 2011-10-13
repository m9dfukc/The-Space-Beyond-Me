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
import java.awt.event.WindowEvent;
import java.awt.Frame;
import java.awt.Color;
import java.awt.event.WindowListener;
import java.awt.Insets;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.Component;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

/**
 * PAppletWIndow description.
 * 
 * @invisible
 */

public class PAppletWindow extends PApplet implements WindowListener, ComponentListener {

	protected int width = 600;

	protected int height = 200;

	protected int x = 100;

	protected int y = 100;

	protected String _myName;
	
	protected  String _myTitle;
	
	protected  boolean isCoordinates = true; 

	protected boolean isLoop = true;

	protected ControlWindow controlWindow;

	protected ControlP5 controlP5;

	public final static int NORMAL = 0;

	public final static int ECONOMIC = 1;

	protected int _myMode = NORMAL;

	protected String _myRenderer = "";

	protected int _myFrameRate = 15;

	public PAppletWindow() {
		super();
	}

	public PAppletWindow(
	        final String theName,
	        final int theWidth,
	        final int theHeight) {
		this(theName, theWidth, theHeight, "", 15);
	}

	/**
	 * @param theName
	 *            String
	 * @param theWidth
	 *            int
	 * @param theHeight
	 *            int
	 */
	public PAppletWindow(
	        final String theName,
	        final int theWidth,
	        final int theHeight,
	        final String theRenderer,
	        final int theFrameRate) {
		super();
		_myName = theName;
		_myTitle = theName;
		width = theWidth;
		height = theHeight;
		_myFrameRate = theFrameRate;
		_myRenderer = theRenderer;
		launch();
	}

	/**
	 * @param theName
	 *            String
	 * @param theX
	 *            int
	 * @param theY
	 *            int
	 * @param theWidth
	 *            int
	 * @param theHeight
	 *            int
	 */
	public PAppletWindow(
	        final String theName,
	        final int theX,
	        final int theY,
	        final int theWidth,
	        final int theHeight) {
		this(theName, theX, theY, theWidth, theHeight, "", 15);
	}

	public PAppletWindow(
	        final String theName,
	        final int theX,
	        final int theY,
	        final int theWidth,
	        final int theHeight,
	        final String theRenderer,
	        final int theFrameRate) {
		super();
		_myName = theName;
		_myTitle = theName;
		width = theWidth;
		height = theHeight;
		x = theX;
		y = theY;
		_myFrameRate = theFrameRate;
		_myRenderer = theRenderer;
		launch();
	}
	
	public void setParent(ControlP5 theControlP5) {
		controlP5 = theControlP5;
	}
	
	
	public void pause() {
		controlWindow.isAutoDraw = false;
	}

	public void play() {
		controlWindow.isAutoDraw = true;
	}

	/**
	 * @return String
	 */
	public String name() {
		return _myName;
	}

	/**
	 * show/hide the controller window.
	 * 
	 * @param theValue
	 *            boolean
	 */
	protected void visible(
	        boolean theValue) {
		// frame.setVisible(theValue);
		// frame.pack();
		if (theValue == true) {
			frame.show();
		} else {
			frame.hide();
		}
	}

	/**
	 * resize controller window.
	 * 
	 * @param theValue
	 *            boolean
	 */
	protected void resizeable(
	        boolean theValue) {
		frame.setResizable(theValue);
	}

	/**
	 * @invisible
	 */
	public void setup() {
		try {
			Thread.sleep(100);
		} catch (Exception e) {
		}
		if (_myRenderer.length() == 0) {
			size(width, height);
//			System.out.println("default renderer");
		} else {
			size(width, height, _myRenderer);
//			System.out.println("using renderer " + _myRenderer);
		}
		try {
			Thread.sleep(100);
		} catch (Exception e) {

		}
		/*
		 * method framrate is called frameRate from processing version 0117 on.
		 * therefore check for backwards compatibility.
		 */
		String myFramerate = "frameRate";
		Method[] myMethods = this.getClass().getMethods();
		for (int i = 0; i < myMethods.length; i++) {
			if (myMethods[i].getName().toLowerCase().equals("framerate")) {
				myFramerate = myMethods[i].getName();
				break;
			}
		}

		try {
			Method m = this.getClass().getMethod(myFramerate, new Class[] { float.class });
			m.invoke(this, new Object[] { new Float(_myFrameRate) });
		} catch (NoSuchMethodException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}
	}

	protected void setControlWindow(
	        ControlWindow theWindow) {
		controlWindow = theWindow;
	}

	/**
	 * @invisible
	 */
	public void draw() {}
	
	
	public void setTitle(String theTitle) {
		_myTitle = theTitle;
		updateTitle();
	}
	
	protected void updateTitle() {
		String m = _myTitle;
		if(isCoordinates) {
			m += " x:" + x + " y:" + y + "   " + width + "x" + height;
		}
		frame.setTitle(m);
	} 
	
	public String title() {
		return _myTitle;
	} 
	
	public void showCoordinates() {
		isCoordinates = true;
		updateTitle();
	}
	
	public void hideCoordinates() {
		isCoordinates = false;
		updateTitle();
	}
	
	/**
	 * @invisible
	 * @param e
	 *            WindowEvent
	 */
	public void windowActivated(
	        WindowEvent e) {
		isLoop = true;
		loop();
		try {
		controlP5.deactivateControllers();
		} catch(NullPointerException nullPointer) {
		}
	}

	public void keyPressed(
	        KeyEvent theKeyEvent) {
		controlP5.papplet.keyPressed(theKeyEvent);
		controlP5.keyHandler.keyEvent(theKeyEvent, this.controlWindow, false);
	}

	public void keyReleased(
	        KeyEvent theKeyEvent) {
		controlP5.papplet.keyReleased(theKeyEvent);
		controlP5.keyHandler.keyEvent(theKeyEvent, this.controlWindow, false);
	}

	public void keyTyped(
	        KeyEvent theKeyEvent) {
		controlP5.papplet.keyTyped(theKeyEvent);
		controlP5.keyHandler.keyEvent(theKeyEvent, this.controlWindow, false);
	}
 
	/**
	 * @invisible
	 * @param e
	 *            WindowEvent
	 */
	public void windowClosed(
	        WindowEvent e) {
//	 System.out.println("window closed");
	}

	/**
	 * @invisible
	 * @param e
	 *            WindowEvent
	 */
	public void windowClosing(
	        WindowEvent e) {
		controlWindow.remove();
		controlWindow._myApplet.stop();
		dispose();
	}

	/**
	 * @invisible
	 * @param e
	 *            WindowEvent
	 */
	public void windowDeactivated(
	        WindowEvent e) {
		if (_myMode == ECONOMIC) {
			isLoop = false;
			noLoop();
		}
	}

	/**
	 * @invisible
	 * @param e
	 *            WindowEvent
	 */
	public void windowDeiconified(
	        WindowEvent e) {}

	/**
	 * @invisible
	 * @param e
	 *            WindowEvent
	 */
	public void windowIconified(
	        WindowEvent e) {}

	/**
	 * @invisible
	 * @param e
	 *            WindowEvent
	 */
	public void windowOpened(
	        WindowEvent e) {}

	/**
	 * @invisible
	 * @param e
	 *            ComponentEvent
	 */
	public void componentHidden(
	        ComponentEvent e) {}

	/**
	 * @invisible
	 * @param e
	 *            ComponentEvent
	 */
	public void componentMoved(
	        ComponentEvent e) {
		Component c = e.getComponent();
		x = c.getLocation().x;
		y = c.getLocation().y;
		updateTitle();
	}

	/**
	 * @invisible
	 * @param e
	 *            ComponentEvent
	 */
	public void componentResized(
	        ComponentEvent e) {
		Component c = e.getComponent();
		// System.out.println("componentResized event from " +
		// c.getClass().getName() + "; new size: " + c.getSize().width
		// + ", " + c.getSize().height);
	}

	/**
	 * @invisible
	 * @param e
	 *            ComponentEvent
	 */
	public void componentShown(
	        ComponentEvent e) {
	// System.out.println("componentShown event from " +
	// e.getComponent().getClass().getName());
	}

	public void setMode(
	        int theValue) {
		if (theValue == ECONOMIC) {
			_myMode = ECONOMIC;
			return;
		}
		_myMode = NORMAL;
	}

	protected void dispose() {
		controlWindow._myApplet.stop();
		stop();
		removeAll();
		frame.removeAll();
		frame.dispose();
	}

	private void launch() {
		GraphicsDevice displayDevice = null;
		if (displayDevice == null) {
			GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
			displayDevice = environment.getDefaultScreenDevice();
		}

		frame = new Frame(displayDevice.getDefaultConfiguration());

		// remove the grow box by default
		// users who want it back can call frame.setResizable(true)
		frame.setResizable(false);
		init();

		frame.pack(); // get insets. get more.
		Insets insets = frame.getInsets();

		int windowW = Math.max(width, MIN_WINDOW_WIDTH) + insets.left + insets.right;
		int windowH = Math.max(height, MIN_WINDOW_HEIGHT) + insets.top + insets.bottom;

		frame.setSize(windowW, windowH);
		frame.setLayout(null);
		frame.add(this);
		frame.setBackground(Color.black);
		int usableWindowH = windowH - insets.top - insets.bottom;
		setBounds((windowW - width) / 2, insets.top + (usableWindowH - height) / 2, width, height);

		frame.addWindowListener(this);
		frame.addComponentListener(this);
		frame.setName(_myName);
		frame.setTitle(_myName + " x:" + x + " y:" + y + "   w:" + width + " h:" + height);
		frame.setVisible(true);
		frame.setLocation(x,y);
		requestFocus();
	}

}
