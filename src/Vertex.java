import java.awt.*;
import java.awt.geom.Point2D;

public class Vertex {
    double x, y, z;
    double r, g, b;

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

    void scale(double width, double height, double scale) {
        width = (width / 16) * scale;
        height = (height / 9) * scale;

        x = width / 2 + (x * width / 2);
        y = height / 2 + (y * height / 2);
    }

    void centre(double displayWidth, double displayHeight) {
        x = (displayWidth + x) / 2;
        y = (displayHeight + y) / 2;
    }

    void flip() {
        y = -y;
    }

    void shift(double shiftX, double shiftY) {
        x += shiftX;
        y += shiftY;
    }
}
