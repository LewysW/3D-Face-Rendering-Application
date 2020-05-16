/**
 * Represents vertex of triangle
 */
public class Vertex {
    //Coordinate
    double x, y, z;
    //RGB colours
    double r, g, b;

    /**
     * Constructor to set coordinates and colours
     * @param x - x coordinate
     * @param y - y coordinate
     * @param z - z coordinate
     * @param r - red colour value
     * @param g - green colour value
     * @param b - blue colour value
     */
    public Vertex(double x, double y, double z, double r, double g, double b) {
        //Coordinates
        this.x = x;
        this.y = y;
        this.z = z;

        //Colours
        this.r = r;
        this.g = g;
        this.b = b;
    }

    /**
     * Scale coordinates by factor
     * @param width - width of screen
     * @param height - height of screen
     * @param factor
     */
    void scale(double width, double height, double factor) {
        //Weight width and height by aspect ratio and scale factor
        width = (width / 16) * factor;
        height = (height / 9) * factor;

        //Scale coordinates by screen size
        x = width / 2 + (x * width / 2);
        y = height / 2 + (y * height / 2);
    }

    /**
     * Centre the coordinate
     * @param displayWidth - width
     * @param displayHeight - height
     */
    void centre(double displayWidth, double displayHeight) {
        x = (displayWidth + x) / 2;
        y = (displayHeight + y) / 2;
    }

    /**
     * Flip vertically
     */
    void flip() {
        y = -y;
    }

    /**
     * Shift coordinate by specified horizontal and vertical amount
     * @param shiftX - amount to shift x by
     * @param shiftY - amount to shift y by
     */
    void shift(double shiftX, double shiftY) {
        x += shiftX;
        y += shiftY;
    }
}
