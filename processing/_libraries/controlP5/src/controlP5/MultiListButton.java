package controlP5;

import controlP5.*;
import java.util.Hashtable;
import java.util.Vector;

import processing.core.PApplet;

/**
 * @example ControlP5multiList
 * @nosuperclasses Controller
 * @related Controller
 */
public class MultiListButton extends Button implements MultiListInterface {

	MultiListInterface parent;

	MultiList root;

	CRect _myRect;

	/**
	 * 
	 * @param theProperties
	 *            ControllerProperties
	 * @param theParent
	 *            MultiListInterface
	 * @param theRoot
	 *            MultiList
	 */
	protected MultiListButton(
	        ControlP5 theControlP5,
	        String theName,
	        float theValue,
	        int theX,
	        int theY,
	        int theWidth,
	        int theHeight,
	        MultiListInterface theParent,
	        MultiList theRoot) {
		super(theControlP5, (ControllerGroup) theRoot.parent(), theName, theValue, theX, theY, theWidth, theHeight);
		parent = theParent;
		root = theRoot;
		isXMLsavable = false;
		updateRect(position().x(), position().y(), width, height);
	}

	public void remove() {
		int myYoffset = 0;
		for (int i = 0; i < parent.subelements().size(); i++) {
			if (((MultiListButton) parent.subelements().get(i)) == this) {
				myYoffset = height + 1;
			}
			((MultiListButton) parent.subelements().get(i)).updateLocation(0, -myYoffset);
		}

		if (_myParent != null) {
			removeListener(root);
			_myParent.remove(this);
		}
		if (controlP5 != null) {
			removeListener(root);
			controlP5.remove(this);
		}
		for (int i = 0; i < subelements.size(); i++) {
			((MultiListButton) subelements.get(i)).remove();
		}
	}

	/**
	 * @invisible
	 * @return Vector
	 */
	public Vector subelements() {
		return subelements;
	}

	/**
	 * @invisible
	 * @param theX
	 *            float
	 * @param theY
	 *            float
	 * @param theW
	 *            float
	 * @param theH
	 *            float
	 */
	public void updateRect(
	        float theX,
	        float theY,
	        float theW,
	        float theH) {
		_myRect = new CRect(theX, theY, theW, theH);
	}

	/**
	 * @invisible
	 * @param theX
	 *            float
	 * @param theY
	 *            float
	 */
	public void updateLocation(
	        float theX,
	        float theY) {
		position().x += theX;
		position().y += theY;
		updateRect(position().x, position().y, width, height);
		for (int i = 0; i < subelements.size(); i++) {
			((MultiListInterface) subelements.get(i)).updateLocation(theX, theY);
		}
	}

	/**
	 * set the width of a multlist button.
	 * 
	 * @param theWidth
	 *            int
	 */
	public Controller setWidth(
	        int theWidth) {
		width = theWidth;
		updateLocation(0, 0);
		return this;
	}

	/**
	 * set the height of a multlist button.
	 * 
	 * @param theHeight
	 *            int
	 */
	public Controller setHeight(
	        int theHeight) {
		int difHeight = height;
		height = theHeight;
		difHeight = height - difHeight;
		int myYoffset = 0;
		for (int i = 0; i < parent.subelements().size(); i++) {
			((MultiListButton) parent.subelements().get(i)).updateLocation(0, myYoffset);
			if (((MultiListButton) parent.subelements().get(i)) == this) {
				myYoffset = difHeight;
			}
		}
		updateLocation(0, 0);
		return this;
	}

	/**
	 * add a new button to the sublist of this multilist button.
	 * 
	 * @param theName
	 *            String
	 * @param theValue
	 *            int
	 * @return MultiListButton
	 */
	public MultiListButton add(
	        String theName,
	        float theValue) {
		int myHeight = -(height + 1);
		for (int i = 0; i < subelements().size(); i++) {
			myHeight += ((MultiListButton) subelements().get(i)).height + 1;
		}

		MultiListButton b = new MultiListButton(controlP5, theName, theValue, (int) position().x + (width + 1),
		        (int) position().y + (height + 1) + myHeight, (int) width, (int) height, this, root);
		b.isMoveable = false;
		b.hide();
		controlP5.register(b);
		b.addListener(root);
		subelements.add(b);
		updateRect(position().x() + (width + 1), position().y(), width, (height + 1) + myHeight);
		return b;
	}

	/**
	 * @invisible
	 */
	protected void onEnter() {
		if (!root.isUpdateLocation) {
			isActive = true;
			root.occupied(true);
			root.mostRecent = this;
			parent.close(this);
			open();
		}
	}
	
	
	/**
	 * @invisible
	 */
	protected void onLeave() {
		if (!parent.observe() && !root.isUpdateLocation && root.mostRecent == this) {
			isActive = false;
			root.occupied(false);
		}
	}



	public void mouseReleasedOutside() {
		// !!! other than in the Button class,
		// calling mouseReleased here conflicts with 
		// 
		//mouseReleased(); 
	}

	/**
	 * @invisible
	 * @param theMousePosition
	 *            CVector3f
	 * @return boolean
	 */
	public boolean observe() {
		return CRect.inside(_myRect, _myControlWindow.mouseX, _myControlWindow.mouseY);
	}

	/**
	 * @invisible
	 * @param theInterface
	 *            MultiListInterface
	 */
	public void close(
	        MultiListInterface theInterface) {
		for (int i = 0; i < subelements.size(); i++) {
			if (theInterface != (MultiListInterface) subelements.get(i)) {
				((MultiListInterface) subelements.get(i)).close();
			}
		}

	}

	/**
	 * @invisible
	 */
	public void close() {
		for (int i = 0; i < subelements.size(); i++) {
			((MultiListButton) subelements.get(i)).close();
			((MultiListButton) subelements.get(i)).hide();
		}
	}

	/**
	 * @invisible
	 */
	public void open() {
		for (int i = 0; i < subelements.size(); i++) {
			((MultiListButton) subelements.get(i)).show();
		}
	}

	/**
	 * @invisible
	 * @param theElement
	 *            ControlP5XMLElement
	 */
	public void addToXMLElement(
	        ControlP5XMLElement theElement) {
		ControlP5XMLElement myXMLElement = new ControlP5XMLElement(new Hashtable(), true, false);
		myXMLElement.setName("mlbutton");
		myXMLElement.setAttribute("name", name());
		myXMLElement.setAttribute("parent", parent.name());
		theElement.addChild(myXMLElement);
		for (int i = 0; i < subelements.size(); i++) {
			((MultiListInterface) subelements.get(i)).addToXMLElement(theElement);
		}
	}
}
