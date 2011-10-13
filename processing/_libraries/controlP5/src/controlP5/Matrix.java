package controlP5;

import processing.core.PApplet;

/**
 * a matrix is a 2d array with one pointer that traverses through the matrix
 * with a timed interval. if an item of a matrix-colum is active, the x and y
 * position of the corresponding cell is returned. see the example for more
 * information.
 * 
 * @example ControlP5matrix
 * @nosuperclasses Controller
 * @related Controller
 */
public class Matrix extends Controller {

	protected int cnt;

	protected int[][] myMarkers;

	protected int stepX;

	protected int stepY;

	protected int cellX;

	protected int cellY;

	protected boolean isPressed;

	protected int _myCellX;

	protected int _myCellY;

	protected int sum;

	protected long _myTime;

	protected long _myInterval;

	protected int currentX = -1;

	protected int currentY = -1;

	/**
	 * @invisible
	 * @param theControlP5
	 *            ControlP5
	 * @param theParent
	 *            ControllerGroup
	 * @param theName
	 *            String
	 * @param theCellX
	 *            int
	 * @param theCellY
	 *            int
	 * @param theX
	 *            int
	 * @param theY
	 *            int
	 * @param theWidth
	 *            int
	 * @param theHeight
	 *            int
	 */
	public Matrix(
	        ControlP5 theControlP5,
	        ControllerGroup theParent,
	        String theName,
	        int theCellX,
	        int theCellY,
	        int theX,
	        int theY,
	        int theWidth,
	        int theHeight) {
		super(theControlP5, theParent, theName, theX, theY, theWidth, theHeight);

		_myCellX = theCellX;

		_myCellY = theCellY;

		sum = _myCellX * _myCellY;

		stepX = width / _myCellX;

		stepY = height / _myCellY;

		myMarkers = new int[_myCellX][_myCellY];

		for (int x = 0; x < _myCellX; x++) {
			for (int y = 0; y < _myCellY; y++) {
				myMarkers[x][y] = -1;
			}
		}
		_myTime = System.currentTimeMillis();
		_myInterval = 100;
	}

	/**
	 * set the speed of intervals in millis iterating through the matrix.
	 * 
	 * @param theInterval
	 *            long
	 */
	public void setInterval(
	        long theInterval) {
		_myInterval = theInterval;
	}

	/**
	 * @see ControllerInterfalce.updateInternalEvents
	 * @invisible
	 */
	public void updateInternalEvents(
	        PApplet theApplet) {
		if (System.currentTimeMillis() > _myTime + _myInterval) {
			cnt += 1;
			cnt %= _myCellX;
			_myTime = System.currentTimeMillis();
			for (int i = 0; i < _myCellY; i++) {
				if (myMarkers[cnt][i] == 1) {
					_myValue = 0;
					_myValue = (cnt << 0) + (i << 8);
					setValue(_myValue);
				}
			}
		}

		if (isInside) {
			if (isPressed) {
				int tX = (int) ((theApplet.mouseX - position.x) / stepX);
				int tY = (int) ((theApplet.mouseY - position.y) / stepY);
				if (tX != currentX || tY != currentY) {
					boolean isMarkerActive = (myMarkers[tX][tY] == 1) ? true : false;
					for (int i = 0; i < _myCellY; i++) {
						myMarkers[tX][i] = 0;
					}
					if (!isMarkerActive) {
						myMarkers[tX][tY] = 1;
					}
					currentX = tX;
					currentY = tY;
				}
			}
		}
	}

	/**
	 * @invisible
	 * @param theApplet
	 *            PApplet
	 */
	public void draw(
	        PApplet theApplet) {

		theApplet.noStroke();
		theApplet.pushMatrix();
		theApplet.translate(position.x, position.y);
		theApplet.fill(color.colorBackground);
		theApplet.rect(0,0, width, height);
		theApplet.noStroke();
		if (isInside()) {
			theApplet.fill(color.colorForeground);
			theApplet.rect((int) ((theApplet.mouseX - position.x) / stepX) * stepX,
			        (int) ((theApplet.mouseY - position.y) / stepY) * stepY, stepX, stepY);
		}
		theApplet.stroke(color.colorActive);
		theApplet.line(cnt * stepX, 0, cnt * stepX, height);

		for (int x = 0; x < _myCellX; x++) {
			for (int y = 0; y < _myCellY; y++) {
				if (myMarkers[x][y] == 1) {
					theApplet.line(x * stepX,y * stepY + stepY / 2, x * stepX
					        + stepX, y * stepY + stepY / 2);

				}
			}
		}
		theApplet.popMatrix();
	}

	
	protected void onEnter() {
		isActive = true;
	}

	protected void onLeave() {
		isActive = false;
	}

	/**
	 * @invisible
	 */
	public void mousePressed() {
		isActive = isInside;
		if (isInside) {
			isPressed = true;
		}
	}

	/**
	 * @invisible
	 */
	public void mouseReleased() {
		if (isActive) {
			isActive = false;
		}
		isPressed = false;
		currentX = -1;
		currentY = -1;
	}

	public void setValue(
	        float theValue) {
		_myValue = theValue;
		broadcast(FLOAT);
	}

	public void update() {
		setValue(_myValue);
	}
	
	public void set(int theX, int theY, boolean theValue) {
		myMarkers[theX][theY] = (theValue==true) ? 1:0;
	}
	
	public static int getX(
	        int thePosition) {
		return ((thePosition >> 0) & 0xff);
	}

	public static int getY(
	        int thePosition) {
		return ((thePosition >> 8) & 0xff);
	}

	public static int getX(
	        float thePosition) {
		return (((int) thePosition >> 0) & 0xff);
	}

	public static int getY(
	        float thePosition) {
		return (((int) thePosition >> 8) & 0xff);
	}

	/**
	 * @invisible
	 * @param theElement
	 *            ControlP5XMLElement
	 */
	public void addToXMLElement(
	        ControlP5XMLElement theElement) {
		theElement.setAttribute("type", "matrix");
	}

}
