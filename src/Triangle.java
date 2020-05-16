import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Represents a triangle in the face
 */
public class Triangle {
    //Represents the vertices in the triangle
    ArrayList<Vertex> vertices = new ArrayList<>();

    //https://en.wikipedia.org/wiki/Barycentric_coordinate_system#Barycentric_coordinates_on_triangles

    /**
     * Interpolate points of triangle 2D space using Barycentric coordinates
     * @param point - (x, y)
     * @param points - (xi, yi) where i is in domain {1, 2, 3}
     * @return list of doubles - lambda1, lambda2, lambda3 (the 'weight' of each point i)
     */
    static ArrayList<Double> interpolate(Point2D point, ArrayList<Point2D> points) {
        //Get coords of point in triangle
        double x = point.getX();
        double y = point.getY();
        //Get coords of first vertex
        double x1 = points.get(0).getX();
        double y1 = points.get(0).getY();
        //Get coords of second vertex
        double x2 = points.get(1).getX();
        double y2 = points.get(1).getY();
        //Get coords of third vertex
        double x3 = points.get(2).getX();
        double y3 = points.get(2).getY();

        //Calculate determinate of matrix
        double determinate = (y2 - y3) * (x1 - x3) + (x3 - x2) * (y1 - y3);

        //Calculate barycentric coordinates for each vertex of triangle
        double lambda1 = ((y2 - y3) * (x - x3) + (x3 - x2) * (y - y3)) / determinate;
        double lambda2 = ((y3 - y1) * (x - x3) + (x1 - x3) * (y - y3)) / determinate;
        double lambda3 = 1 - lambda1 - lambda2;

        ArrayList<Double> weights = new ArrayList<>();
        //Add each coordinate to list of weights
        weights.add(lambda1);
        weights.add(lambda2);
        weights.add(lambda3);
        return weights;
    }

    /**
     * Calculates average depth of three vertices
     * @return average depth of vertices
     */
    Double averageDepth() {
        double z = 0;

        //Sum vertices z coords
        for (Vertex v : vertices) {
            z += v.z;
        }

        //Calculate average z
        z /= vertices.size();
        return z;
    }

    /**
     * Draws triangle of face
     * @param graphics2D - used to draw
     * @param displayWidth - width of display
     * @param displayHeight - height of display
     * @param shading - shading technique in use
     * @param projection - projection technique in use
     * @param focalLength - focal length between camera and screen
     * @param shiftX - shift image horizontally by this much
     * @param shiftY - shift image vertically by this much
     * @param scale - scale image size by this much if orthogonal
     */
    public void draw(Graphics2D graphics2D, double displayWidth, double displayHeight, Shading shading, Projection projection, int focalLength, double shiftX, double shiftY, double scale) {
        ArrayList<Vertex> coords = new ArrayList<>();

        //For each vertex in triangle
        for (Vertex v : vertices) {
            Vertex vertex = new Vertex(v.x, v.y, v.z,v.r, v.g, v.b);

            //Shift z coord by focal length such that when z = 0, it equals the depth of the screen on which to project the face
            vertex.z += focalLength;

            //If using perspective projection
            if (projection == Projection.PERSPECTIVE) {
               // System.out.println("f/z = r/R:" + focalLength + "/" + (vertex.z));
                double x = vertex.x;
                double y = vertex.y;

                //Calculate projected x coordinate
                vertex.x = focalLength * (vertex.x / vertex.z);
                //Calculate projected y coordinate
                vertex.y = focalLength * (vertex.y / vertex.z);

                //Scale of image determined by focal length
                scale = (double) focalLength / 10000;
               // System.out.println("x/X = y/Y = r/R: " + vertex.x / x + " = " + vertex.y / y + " = " + (double) focalLength / vertex.z);
            }

            //Flips image to be the correct way up
            vertex.flip();

            //Scales image to fit display (based on focal length if perspective projection)
            vertex.scale(displayWidth, displayHeight, scale);

            //Centres image
            vertex.centre(displayWidth, displayHeight);

            //Shifts image by specified x and y (when displaying reference face to fit on triangle)
            vertex.shift(shiftX, shiftY);

            //Adds processed triangle to list of coords
            coords.add(vertex);
        }

        //Generate list of integers to store vertices of triangle (Polygon object can only take lists of ints)
        int[] x = new int[vertices.size()];
        int[] y = new int[vertices.size()];

        //Get coords as primitive array to initialise polygon
        for (int v = 0; v < coords.size(); v++) {
            x[v] = (int) coords.get(v).x;
            y[v] = (int) coords.get(v).y;
        }

        //Create polygon object to represent triangle
        Polygon polygon = new Polygon(x, y, vertices.size());

        //If shading method is flat
        if (shading == Shading.FLAT) {
            //Then shade the polygon using the flat shading method
            shadeFlat(graphics2D, coords, polygon);

        //Otherwise if it is Gouraud
        } else if (shading == Shading.GOURAUD) {
            // Shade the polygon using Gouraud shading
           shadeGouraud(graphics2D, coords, polygon);
        }
    }

    /**
     * Shades a polygon using the average colour of the 3 vertices
     * @param graphics2D - to render polygon
     * @param vertices - list of vertices of triangle
     */
    private void shadeFlat(Graphics2D graphics2D, ArrayList<Vertex> vertices, Polygon polygon) {
        double r = 0;
        double g = 0;
        double b = 0;

        for (Vertex v : vertices) {
            //Sum RGB values of vertices
            r += v.r;
            g += v.g;
            b += v.b;
        }

        //Find average colour of triangle
        r /= vertices.size();
        g /= vertices.size();
        b /= vertices.size();

        //Set the colour to this average colour
        graphics2D.setColor(new Color((float) r / 255, (float) g / 255, (float) b / 255));
       //Draw the polygon with that colour
        graphics2D.fillPolygon(polygon);
    }

    /**
     * Shades a polygon by filling in each point using the interpolation of each of the three vertices' colours
     * does this using GradientPaint rather than pixel by pixel due to performance
     * @param graphics2D - to draw polygon
     * @param vertices - of triangle
     * @param polygon - to be drawn
     */
    private void shadeGouraud(Graphics2D graphics2D, ArrayList<Vertex> vertices, Polygon polygon) {
        ArrayList<Color> colours = new ArrayList<>();
        //Get coordinate of each vertex
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
        // and transition to colour of second vertex:

        //Generate gradient c0 to c1 for point (x0, y0) to point (x1, y1)
        GradientPaint gradient1 = new GradientPaint(x0, y0, colours.get(0), x1, y1, colours.get(1));
        //Generate gradient c1 to c2 for point (x1, y1) to point (x2, y2)
        GradientPaint gradient2 = new GradientPaint(x1, y1, colours.get(1), x2, y2, colours.get(2));
        //Generate gradient c2 to c0 for point (x2, y2) to point (x0, y0)
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
    }

}
