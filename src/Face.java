import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Represent faces
 */
public class Face {
    //List of triangles making up a face
    private ArrayList<Triangle> triangles = new ArrayList<>();

    Face(ArrayList<Triangle> triangles) {
        this.triangles = triangles;
    }

    /**
     * Generates the new triangles for the synthetic face
     * using the three reference faces and their weights
     * @param faces - reference faces
     * @param weights - influence of each face based on interpolation determined by click
     */
    Face(ArrayList<Face> faces, ArrayList<Double> weights) {
        int numTriangles = faces.get(0).triangles.size();

        System.out.println(weights);
        //For each triangle in provided faces
        for (int t = 0; t < numTriangles; t++) {
            int numVertices = faces.get(0).triangles.get(0).vertices.size();
            Triangle syntheticTriangle = new Triangle();

            //For each vertex in the current triangle
            for (int v = 0; v < numVertices; v++) {
                Vertex syntheticVertex = new Vertex(0, 0, 0, 0, 0, 0);

                //Get the current vertex for each face
                Vertex vertex0 = faces.get(0).triangles.get(t).vertices.get(v);
                Vertex vertex1 = faces.get(1).triangles.get(t).vertices.get(v);
                Vertex vertex2 = faces.get(2).triangles.get(t).vertices.get(v);

                //Generate a new 'synthetic' vertex using the three existing vertices and the weights of each face
                syntheticVertex = applyWeight(syntheticVertex, vertex0, weights.get(0));
                syntheticVertex = applyWeight(syntheticVertex, vertex1, weights.get(1));
                syntheticVertex = applyWeight(syntheticVertex, vertex2, weights.get(2));

                //Add the new 'synthetic' vertex to a new 'synthetic' triangle
                syntheticTriangle.vertices.add(syntheticVertex);
            }

            //Add the synthetic triangle to the triangles of the new face
            this.triangles.add(syntheticTriangle);
        }
    }

    /**
     * Apply influence of vertex v to synthetic vertex
     * @param synthetic - new synthetic vertex
     * @param v - vertex of reference face
     * @param weight - weight to apply to reference face vertex
     * @return synthetic vertex with applied weight of vertex v
     */
    Vertex applyWeight(Vertex synthetic, Vertex v, double weight) {
        synthetic.x += weight * v.x;
        synthetic.y += weight * v.y;
        synthetic.z += weight * v.z;

        synthetic.r += weight * v.r;
        synthetic.g += weight * v.g;
        synthetic.b += weight * v.b;

        return synthetic;
    }

    /**
     * Compares two triangles by average depth for sorting
     */
    public class TriangleComparator implements Comparator<Triangle> {
        @Override
        public int compare(Triangle t1, Triangle t2) {
            return t1.averageDepth().compareTo(t2.averageDepth());
        }
    }

    /**
     * Displays the face by displaying each triangle
     * @param graphics2D - to display triangles
     * @param shading - shading type (flat or gouraud)
     * @param projection - projection type (orthographic or perspective)
     * @param focalLength - focal length (distance from camera to image)
     * @param width - of screen
     * @param height - of screen
     * @param shiftX - how much to shift image horizontally
     * @param shiftY - how much to shift image vertically
     * @param scale - how much to scale size of image to fit nicely on screen
     */
    void display(Graphics2D graphics2D, Shading shading, Projection projection, int focalLength, double width,
                 double height, double shiftX, double shiftY, double scale) {
        //Copy triangles to avoid overwriting data during transformation
        ArrayList<Triangle> trianglesToDisplay = (ArrayList<Triangle>) triangles.clone();

        //Sort triangles by average depth for use in painter's algorithm
        Collections.sort(trianglesToDisplay, new TriangleComparator());

        //Draw each triangle
        for (Triangle t : trianglesToDisplay) {
            t.draw(graphics2D, width, height, shading, projection, focalLength, shiftX, shiftY, scale);
        }
    }
}
