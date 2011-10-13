package controlP5;

import java.util.Hashtable;
import java.util.Vector;

import processing.core.PApplet;

/**
 * @shortdesc the scroll list is a list of selectable items.
 * 
 * 
 * @example ControlP5scrollList
 * @nosuperclasses Controller
 * @related Controller
 * @related ControlGroup
 * @related ControllerGroup
 */
public class ListBox extends ControlGroup implements ControlListener {

	protected int _myAdjustedListHeight;

	protected int _myItemHeight = 13;

	protected Slider _myScrollbar;

	protected String _myName;

	protected float _myScrollValue = 0;

	protected boolean isScrollbarVisible = true;

	protected int _myHeight;

	protected Vector items;

	protected int spacing;

	protected boolean isMultipleChoice = false;

	/**
	 * @invisible
	 * @param theControlP5
	 *            ControlP5
	 * @param theGroup
	 *            ControllerGroup
	 * @param theName
	 *            String
	 * @param theX
	 *            int
	 * @param theY
	 *            int
	 * @param theW
	 *            int
	 * @param theH
	 *            int
	 */
	protected ListBox(
	        ControlP5 theControlP5,
	        ControllerGroup theGroup,
	        String theName,
	        int theX,
	        int theY,
	        int theW,
	        int theH) {
		super(theControlP5, theGroup, theName, theX, theY, theW, 9);
		_myWidth = theW;
		_myBackgroundHeight = theH;
		items = new Vector();
		_myAdjustedListHeight = (((int) (_myBackgroundHeight / _myItemHeight)) * _myItemHeight + 1) - 2;
		_myScrollbar = new Slider(controlP5, _myParent, theName + "Scroller", 0, 1, 1, _myWidth + 1, 0, 10,
		        _myAdjustedListHeight);
		_myName = theName;
		_myScrollbar.setBroadcast(false);
		_myScrollbar.setSliderMode(Slider.FLEXIBLE);
		_myScrollbar.setMoveable(false);
		_myScrollbar.setLabelVisible(false);
		_myScrollbar.setParent(this);
		add(_myScrollbar);
		_myScrollbar.addListener(this);
		_myScrollbar.setVisible(false);
		_myScrollbar.hide();
	}

	public void hideScrollbar() {
		isScrollbarVisible = false;
		_myScrollbar.hide();
	}

	public void showScrollbar() {
		isScrollbarVisible = true;
		if ((controllers.size() - 1) * _myItemHeight > _myAdjustedListHeight && isScrollbarVisible) {
			_myScrollbar.show();
		}
	}

	public boolean isScrollbarVisible() {
		return isScrollbarVisible;
	}

	/**
	 * scroll the scrollList remotely. values must range from 0 to 1.
	 * 
	 * @param theValue
	 */
	public void scroll(
	        float theValue) {
		if ((items.size()) * _myItemHeight > _myAdjustedListHeight) {
			_myScrollbar.setValue(PApplet.abs(1 - PApplet.min(PApplet.max(0, theValue), 1)));
		}
	}

	/**
	 * 
	 */
	private void scroll() {
		int myValue = 0;
		if ((items.size()) * _myItemHeight > _myAdjustedListHeight && isScrollbarVisible) {
			_myScrollbar.show();
			myValue = (int) (((_myScrollValue * (((items.size()) * _myItemHeight) - _myAdjustedListHeight)) / _myItemHeight))
			        * _myItemHeight;
			for (int i = 1; i < controllers.size(); i++) {
				int n = Math.abs(myValue / _myItemHeight) + (i- 1);
				n = Math.max(0, Math.min(n,items.size()-1));
				((Button)controllers.get(i)).captionLabel().set(((ListBoxItem) items.get(n)).text);
				((Button)controllers.get(i))._myValue = ((ListBoxItem) items.get(n)).value;
			}
		} else {
			_myScrollbar.hide();
			for (int i = 1; i < controllers.size(); i++) {
				int n = i - 1;
				((Button)controllers.get(i)).captionLabel().set(((ListBoxItem) items.get(n)).text);
				((Button)controllers.get(i))._myValue = ((ListBoxItem) items.get(n)).value;
			}
		}
	}

	public void setItemHeight(
	        int theHeight) {
		_myItemHeight = theHeight;
		_myAdjustedListHeight = (((int) (_myBackgroundHeight / _myItemHeight)) * _myItemHeight + spacing) - 2;
		_myScrollbar.setHeight((int)(_myAdjustedListHeight + 1));
		for (int i = 1; i < controllers.size(); i++) {
			((Button) controllers.get(i)).height = theHeight - 1;
			((Button) controllers.get(i)).position.y = (theHeight + spacing) * (i - 1);
		}
		setHeight(_myBackgroundHeight);
	}

	public void setHeight(
	        int theHeight) {
		_myBackgroundHeight = theHeight;
		_myAdjustedListHeight = (((int) (_myBackgroundHeight / _myItemHeight)) * _myItemHeight + spacing) - 2;

		int pn = controllers.size() - 1;
		int n = (int) (_myBackgroundHeight / _myItemHeight);
		boolean isScrollbarRequired = _myBackgroundHeight < (_myItemHeight + spacing) * items.size();

		if (pn > n) { // make listbox smaller
			for (int i = 0; i < pn - n; i++) {
				String buttonName = _myName + "Button" + (n + i);
				controllers.remove(controlP5.controller(buttonName));
				controlP5.remove(buttonName);
			}
			if (isScrollbarRequired) {
				_myScrollbar.show();
				scroll();
			} else {
				_myScrollbar.hide();
			}
		}

		if (pn < n) { // make listbox bigger
			int nn = Math.min(n, items.size());
			nn -= pn;
			addListButton(nn);
			if (isScrollbarRequired) {
				_myScrollbar.show();
				scroll();
			} else {
				_myScrollbar.hide();
			}
		}

		_myScrollbar.setHeight(_myAdjustedListHeight + 1);
		scroll();
	}

	public void setWidth(
	        int theWidth) {
		_myWidth = theWidth;
		for (int i = 1; i < controllers.size(); i++) {
			((Button) controllers.get(i)).width = theWidth;
		}
		_myScrollbar.position.x = _myWidth + 1;
	}

	protected void addListButton(
	        int theNum) {
		for (int i = 0; i < theNum; i++) {
			int myLength = controllers.size() - 1;
			if ((myLength * _myItemHeight) < _myBackgroundHeight) {
				Button b = new Button(controlP5, (ControllerGroup) this, _myName + "Button" + myLength, myLength, 0,
				        myLength * _myItemHeight, _myWidth, _myItemHeight - 1, false);
				b.setMoveable(false);
				add(b);
				controlP5.register(b);
				b.setBroadcast(false);
				b.addListener(this);
			}
		}
		updateScroll();
	}

	private void updateScroll() {
		_myScrollValue = _myScrollbar.value();
		_myScrollbar.setValue(_myScrollValue);
		if ((items.size() - 1) * _myItemHeight > _myAdjustedListHeight && isScrollbarVisible) {
			_myScrollbar.show();
		}
		scroll();
	}

	/**
	 * add an item to the scrollList.
	 * 
	 * @param theName
	 *            String
	 * @param theValue
	 *            int
	 */
	public void addItem(
	        String theName,
	        int theValue) {
		items.add(new ListBoxItem(theName, theValue));
		addListButton(1);
	}

	/**
	 * remove an item from the scroll list.
	 * 
	 * @param theItemName
	 *            String
	 */
	public void removeItem(
	        String theItemName) {
		try {
			for (int i = items.size() - 1; i >= 0; i--) {
				if (((ListBoxItem) items.get(i)).name.equals(theItemName)) {
					items.remove(i);
				}
			}
			if ((controllers.size() - 1) > items.size()) {
				String buttonName = ((Button) controllers.get(controllers.size() - 1)).name();
				controllers.remove(controlP5.controller(buttonName));
				controlP5.remove(buttonName);
			}
			updateScroll();
		} catch (Exception e) {
			if (ControlP5.DEBUG) {
				System.out.println("ScrollList.removeItem exception:" + e);
			}
		}
	}

	/**
	 * @invisible
	 * @param theEvent
	 *            ControlEvent
	 */
	public void controlEvent(
	        ControlEvent theEvent) {
		if (theEvent.controller() instanceof Button) {
			try {
				_myValue = theEvent.controller().value();
				ControlEvent myEvent = new ControlEvent(this);
				controlP5.controlbroadcaster().broadcast(myEvent, ControlP5Constants.FLOAT);
			} catch (Exception e) {
				// in case the itemHeight conflicts with
				// the listHeight, an java.lang.ArrayIndexOutOfBoundsException
				// might occur.
				// !!! fix
			}
		} else {
			_myScrollValue = -(1 - theEvent.value());
			scroll();
		}

	}

	/**
	 * @invisible
	 * @param theElement
	 *            ControlP5XMLElement
	 */
	public void addToXMLElement(
	        ControlP5XMLElement theElement) {
		theElement.setAttribute("type", "listBox");
	}

	class ListBoxItem {
		String name;
		String text;
		int value;
		boolean isActive;

		ListBoxItem(
		        String theName,
		        int theValue) {
			name = theName;
			text = theName;
			value = theValue;
		}
	}
}
