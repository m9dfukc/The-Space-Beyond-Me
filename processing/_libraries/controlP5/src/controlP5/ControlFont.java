package controlP5;

import processing.core.PFont;

/**
 * ControlFont is a container for a PFont that
 * can be used for customizing the font of a label.
 * Fonts other than the pixel fonts provided by
 * ControlP5 can for now only be used for TextLabels 
 * and Controller Labels. Textarea and Textfield are 
 * not supported.
 * 
 * @author andreas
 *
 */
public class ControlFont {
	protected int fontSize;
	protected PFont font;
	protected boolean isControlFont;
	protected boolean isSmooth;
	
	/**
	 * create a controlFont and pass a reference to
	 * a PFont. fontsize needs to be defined as second parameter.
	 * 
	 * @param theFont
	 * @param theFontSize
	 */
	public ControlFont(PFont theFont, int theFontSize) {
		font = theFont;
		fontSize = theFontSize;
		isControlFont = true;
		font.smooth = true;
	}
	
	
	protected boolean isActive() {
		return isControlFont;
	}
	
	protected boolean setActive(boolean theFlag) {
		isControlFont = theFlag;
		return isControlFont;
	}
	
	public void setSmooth(boolean theFlag) {
		isSmooth = theFlag;
		font.smooth = isSmooth;
	} 
	
	public boolean isSmooth() {
		return isSmooth;
	}
	
	public PFont getPFont() {
		return font;
	}
	
	
	public int size() {
		return fontSize;
	}
	
	public void setSize(int theSize) {
		fontSize = theSize;
	}
	
}
