import java.awt.*;
import java.util.ArrayList;

public class Face {
    ArrayList<Triangle> triangles = new ArrayList<>();

    Face(ArrayList<Triangle> triangles) {
        this.triangles = triangles;
    }

    Face(ArrayList<Face> faces, ArrayList<Double> weights) {
        int numTriangles = faces.get(0).triangles.size();

        int i = 0;

        for (int t = 0; t < numTriangles; t++) {
            int numVertices = faces.get(0).triangles.get(0).vertices.size();
            Triangle syntheticTriangle = new Triangle();

            for (int v = 0; v < numVertices; v++) {
                Vertex syntheticVertex = new Vertex(0, 0, 0, 0, 0, 0);

                Vertex vertex0 = faces.get(0).triangles.get(t).vertices.get(v);
                Vertex vertex1 = faces.get(1).triangles.get(t).vertices.get(v);
                Vertex vertex2 = faces.get(2).triangles.get(t).vertices.get(v);

                syntheticVertex = apply(syntheticVertex, vertex0, weights.get(0));
                syntheticVertex = apply(syntheticVertex, vertex1, weights.get(1));
                syntheticVertex = apply(syntheticVertex, vertex2, weights.get(2));

                syntheticTriangle.vertices.add(syntheticVertex);
            }

            this.triangles.add(syntheticTriangle);
        }
        
        System.out.println("Triangles.size(): " + triangles.size());
    }

    Vertex apply(Vertex synthetic, Vertex v, double weight) {
        synthetic.x += weight * v.x;
        synthetic.y += weight * v.y;
        synthetic.z += weight * v.z;

        synthetic.r += weight * v.r;
        synthetic.g += weight * v.g;
        synthetic.b += weight * v.b;

        return synthetic;
    }
}
