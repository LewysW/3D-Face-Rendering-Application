import java.awt.*;
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

    //TODO - need to interpolate points in 3D space as well
//    //https://en.wikipedia.org/wiki/Barycentric_coordinate_system#Barycentric_coordinates_on_triangles
//    static ArrayList<Double> interpolate(Vertex vertex, ArrayList<Vertex> vertices) {
//
//    }
}
