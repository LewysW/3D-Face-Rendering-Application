import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Face {
    ArrayList<Triangle> triangles = new ArrayList<>();

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

                //Generate a new 'synthetic' vertex using the three existing vertices and the weigths of each face
                syntheticVertex = apply(syntheticVertex, vertex0, weights.get(0));
                syntheticVertex = apply(syntheticVertex, vertex1, weights.get(1));
                syntheticVertex = apply(syntheticVertex, vertex2, weights.get(2));

                //Add the new 'synthetic' vertex to a new 'synthetic' triangle
                syntheticTriangle.vertices.add(syntheticVertex);
            }

            //Add the synthetic triangle to the triangles of the new face
            this.triangles.add(syntheticTriangle);
        }

        //Reverse sort triangles by average depth for use in painter's algorithm
        Collections.sort(triangles, new TriangleComparator().reversed());
    }

    /**
     * Apply influence of vertex v * weight to synthetic vertex
     * @param synthetic - new synthetic vertex
     * @param v - vertex of reference face
     * @param weight - weight to apply to reference face vertex
     * @return synthetic vertex with applied weight of vertex v
     */
    Vertex apply(Vertex synthetic, Vertex v, double weight) {
        synthetic.x += weight * v.x;
        synthetic.y += weight * v.y;
        synthetic.z += weight * v.z;

        synthetic.r += weight * v.r;
        synthetic.g += weight * v.g;
        synthetic.b += weight * v.b;

        return synthetic;
    }

    public class TriangleComparator implements Comparator<Triangle> {
        @Override
        public int compare(Triangle t1, Triangle t2) {
            return t1.averageDepth().compareTo(t2.averageDepth());
        }
    }

    void display(Graphics2D graphics2D, Shading shading, Projection projection) {
        System.out.println("Shading: " + shading.name());
        System.out.println("Projection: " + projection.name());
    }
}
