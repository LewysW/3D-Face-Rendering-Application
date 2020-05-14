import java.util.ArrayList;

public class Face {
    ArrayList<Triangle> triangles = new ArrayList<>();

    Face(ArrayList<Triangle> triangles) {
        this.triangles = triangles;
    }

    Face(ArrayList<Face> faces, ArrayList<Double> weights) {

    }
}
