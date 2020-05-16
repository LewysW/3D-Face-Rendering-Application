import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import static java.lang.System.exit;

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
    public void draw(Graphics2D graphics2D, double displayWidth, double displayHeight, Shading shading, Projection projection, int focalLength, double shiftX, double shiftY, double scale) {
        ArrayList<Vertex> coords = new ArrayList<>();

        for (Vertex v : vertices) {
            Vertex vertex;

            vertex = new Vertex(v.x, v.y, v.z,v.r, v.g, v.b);

            if (projection == Projection.PERSPECTIVE) {
                //TODO - scale x and y properly using z
               // vertex.x = focalLength * (vertex.x / (vertex.z));
                //vertex.y = focalLength * (vertex.y / (vertex.z));

                vertex.x = focalLength * (vertex.x / vertex.z);
                vertex.y = focalLength * (vertex.y / vertex.z);
            }


            //Flips image
            vertex.flip();
            //Scales image
            vertex.scale((displayWidth / 16) * scale, (displayHeight / 9) * scale);

            //Centres image
            vertex.centre(displayWidth, displayHeight);


            //Shifts image by specified x and y (when displaying reference face)
            vertex.shift(shiftX, shiftY);

            coords.add(vertex);
        }

        int[] x = new int[vertices.size()];
        int[] y = new int[vertices.size()];

        //Get coords as primitive array to initialise polygon
        for (int v = 0; v < coords.size(); v++) {
            x[v] = (int) coords.get(v).x;
            y[v] = (int) coords.get(v).y;
        }

        Polygon polygon = new Polygon(x, y, vertices.size());

        if (shading == Shading.FLAT) {
            shadeFlat(graphics2D, coords, polygon);
        } else if (shading == Shading.GOURAUD) {
           shadeGouraud(graphics2D, coords, polygon);
        }
    }

    /**
     * Shades a polygon using the average colour of the 3 vertices
     * @param graphics2D - to render polygon
     * @param vertices - list of vertices of triangle
     */
    private void shadeFlat(Graphics2D graphics2D, ArrayList<Vertex> vertices, Polygon polygon) {
        int[] x = new int[3];
        int[] y = new int[3];
        Color[] colours = new Color[3];
        double r = 0;
        double g = 0;
        double b = 0;

        int i = 0;
        for (Vertex v : vertices) {
            r += v.r;
            g += v.g;
            b += v.b;

            colours[i] = new Color((float) v.r / 255, (float) v.g / 255, (float) v.b / 255);

            x[i] = (int) v.x;
            y[i++] = (int) v.y;
        }

        r /= vertices.size();
        g /= vertices.size();
        b /= vertices.size();

        graphics2D.setColor(new Color((float) r / 255, (float) g / 255, (float) b / 255));
       // graphics2D.drawPolygon(x, y, 3);
        graphics2D.fillPolygon(polygon);
    }

    /**
     * Shades a polygon by filling in each point using the interpolation of each of the three vertices' colours
     * does this using GradientPaint rather than pixel by pixel due to time to process
     * @param graphics2D
     * @param vertices
     * @param polygon
     */
    private void shadeGouraud(Graphics2D graphics2D, ArrayList<Vertex> vertices, Polygon polygon) {
        ArrayList<Color> colours = new ArrayList<>();
        float x0 = (float) vertices.get(0).x;
        float y0 = (float) vertices.get(0).y;
        float x1 = (float) vertices.get(1).x;
        float y1 = (float) vertices.get(1).y;
        float x2 = (float) vertices.get(2).x;
        float y2 = (float) vertices.get(2).y;


        //Store colour for each vertex
        for (Vertex v : vertices) {
            Color color = new Color((float) v.r / 255, (float) v.g / 255, (float) v.b / 255);
            colours.add(color);
        }

        //Generate gradient from each vertex to each other vertex, starting at colour of the first vertex
        // and transition to colour of second vertex
        GradientPaint gradient1 = new GradientPaint(x0, y0, colours.get(0), x1, y1, colours.get(1));
        GradientPaint gradient2 = new GradientPaint(x1, y1, colours.get(1), x2, y2, colours.get(2));
        GradientPaint gradient3 = new GradientPaint(x2, y2, colours.get(2), x0, y0, colours.get(0));

        //Apply first gradient
        graphics2D.setPaint(gradient1);
        graphics2D.fillPolygon(polygon);
        //Apply second gradient
        graphics2D.setPaint(gradient2);
        graphics2D.fillPolygon(polygon);
        //Apply third gradient
        graphics2D.setPaint(gradient3);
        graphics2D.fillPolygon(polygon);
        return;
    }

}
