package controlP5;

public class CheckBox extends RadioButton {

	public CheckBox(
	        final ControlP5 theControlP5,
	        final ControllerGroup theParent,
	        final String theName,
	        final int theX,
	        final int theY) {
		super(theControlP5,theParent,theName,theX,theY);
		isMultipleChoice = true;
	}
}
