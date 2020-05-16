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

    void scale(double width, double height) {
        x = width / 2 + (x * width / 2);
        y = height / 2 + (y * height / 2);
    }

    void centre(double displayWidth, double displayHeight) {
        double middleX = displayWidth / 2;
        double middleY = displayHeight / 2;

        x += middleX;
        y += middleY;
    }

    void flip() {
        y = -y;
    }

    void shift(double shiftX, double shiftY) {
        x += shiftX;
        y += shiftY;
    }
}
