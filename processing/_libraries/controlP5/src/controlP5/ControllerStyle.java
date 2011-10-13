package controlP5;

/*
 * 
 */
public class ControllerStyle {
	
	public int paddingTop = 0;
	public int paddingRight = 0;
	public int paddingBottom = 0;
	public int paddingLeft = 0;
	public int marginTop = 0;
	public int marginRight = 0;
	public int marginBottom = 0;
	public int marginLeft = 0;
	public int background;
	public int backgroundWidth = -1;
	public int backgroundHeight = -1;
	public int color;
	
	/*
	 * 
	 */
	public void margin(int theValue) {
		marginTop = theValue;
		marginRight = theValue;
		marginBottom = theValue;
		marginLeft = theValue;
	}
	
	/*
	 * 
	 */
	public void padding(int theValue) {
		paddingTop = theValue;
		paddingRight = theValue;
		paddingBottom = theValue;
		paddingLeft = theValue;
	}
	
	/*
	 * 
	 */
	public void margin(int theTop, int theRight, int theBottom, int theLeft) {
		marginTop = theTop;
		marginRight = theRight;
		marginBottom = theBottom;
		marginLeft = theLeft;
	}
	
	/*
	 * 
	 */
	public void padding(int theTop, int theRight, int theBottom, int theLeft) {
		paddingTop = theTop;
		paddingRight = theRight;
		paddingBottom = theBottom;
		paddingLeft = theLeft;
	}
	
	/*
	 * 
	 */
	public void moveMargin(int theTop, int theRight, int theBottom, int theLeft) {
		marginTop += theTop;
		marginRight += theRight;
		marginBottom += theBottom;
		marginLeft += theLeft;
	}
	
	/*
	 * 
	 */
	public void movePadding(int theTop, int theRight, int theBottom, int theLeft) {
		paddingTop += theTop;
		paddingRight += theRight;
		paddingBottom += theBottom;
		paddingLeft += theLeft;
	}
}
