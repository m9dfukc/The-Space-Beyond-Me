package controlP5;

import controlP5.*;
import processing.core.PApplet;
import java.util.Vector;

/**
 * @invisible
 */
public interface MultiListInterface {

    void close();

    void open();

    void close(MultiListInterface theInterface);

    boolean observe();

    void updateLocation(float theX, float theY);

    public void draw(PApplet theApplet);

    void addToXMLElement(ControlP5XMLElement theElement);

    String name();

    Vector subelements();
}
