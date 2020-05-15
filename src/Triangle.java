import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Triangle {
    ArrayList<Vertex> vertices = new ArrayList<>();

    //https://en.wikipedia.org/wiki/Barycentric_coordinate_system#Barycentric_coordinates_on_triangles

    /**
     * Interpolate points of triangle 2D space
     * @param point - (x, y)
     * @param points - (xi, yi) where i is in domaind {1, 2, 3}
     * @return list of doubles - lambda1, lambda2, lambda3 (the 'weight' of each point i)
     */
    static ArrayList<Double> interpolate(Point2D point, ArrayList<Point2D> points) {
        //Get coords of point in triangle
        double x = point.getX();
        double y = point.getY();
        //Get coords of first point
        double x1 = points.get(0).getX();
        double y1 = points.get(0).getY();
        //Get coords of second point
        double x2 = points.get(1).getX();
        double y2 = points.get(1).getY();
        //Get coords of third points
        double x3 = points.get(2).getX();
        double y3 = points.get(2).getY();

        double determinate = (y2 - y3) * (x1 - x3) + (x3 - x2) * (y1 - y3);
        double lambda1 = ((y2 - y3) * (x - x3) + (x3 - x2) * (y - y3)) / determinate;
        double lambda2 = ((y3 - y1) * (x - x3) + (x1 - x3) * (y - y3)) / determinate;
        double lambda3 = 1 - lambda1 - lambda2;

        ArrayList<Double> weights = new ArrayList<>();
        weights.add(lambda1);
        weights.add(lambda2);
        weights.add(lambda3);
        return weights;
    }

    //TODO - need to interpolate points in 3D space as well for the Gouraud shading
//    //https://en.wikipedia.org/wiki/Barycentric_coordinate_system#Barycentric_coordinates_on_triangles
//    static ArrayList<Double> interpolate(Vertex vertex, ArrayList<Vertex> vertices) {
//
//    }

    /**
     * Calculates average depth of three vertices
     * @return average depth of vertices
     */
    Double averageDepth() {
        double z = 0;

        for (Vertex v : vertices) {
            z += v.z;
        }

        z /= vertices.size();
        return z;
    }

    //Draw the edges between the corners of the triangle in 2D
    public void draw(Graphics2D graphics2D, double displayWidth, double displayHeight, Shading shading, double shiftX, double shiftY, double scale) {
        ArrayList<Vertex> coords = new ArrayList<>();
        int numVertices = 3;

        for (Vertex v : vertices) {
            Vertex vertex = new Vertex(v.x, v.y, v.z,v.r, v.g, v.b);
            //Flips image
            vertex.flip();
            //Scales image
            vertex.scale(displayWidth * scale / 2, displayHeight * scale / 2);
            //Centres image
            vertex.centre(displayWidth, displayHeight);
            //Shifts image by specified x and y (when displaying reference face)
            vertex.shift(shiftX, shiftY);

            coords.add(vertex);
        }

        if (shading == Shading.FLAT) {
            int[] x = new int[3];
            int[] y = new int[3];
            double r = 0;
            double g = 0;
            double b = 0;

            int i = 0;
            for (Vertex v : coords) {
                x[i] = (int) v.x;
                y[i++] = (int) v.y;
                r += v.r;
                g += v.g;
                b += v.b;
            }

            r /= numVertices;
            g /= numVertices;
            b /= numVertices;

            graphics2D.setColor(new Color((float) r / 255, (float) g / 255, (float) b / 255));
            graphics2D.drawPolygon(x, y, 3);
            graphics2D.fillPolygon(x, y , 3);
        }

//        //Move to top corner of triangle
//        path.moveTo(coords.get(0).x, coords.get(0).y);
//
//        //Draw line from top corner to bottom left and bottom right corners
//        path.lineTo(coords.get(1).x, coords.get(1).y);
//        path.lineTo(coords.get(2).x, coords.get(2).y);
//        path.lineTo(coords.get(0).x, coords.get(0).y);
//
//        graphics2D.draw(path);
    }


}
