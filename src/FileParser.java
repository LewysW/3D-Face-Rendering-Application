import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Parses required files
 */
public class FileParser {
    //Indices for shapes and textures of faces
    private ArrayList<ArrayList<Double>> meshIndices;
    //Shape weighting to apply
    private ArrayList<Double> sh_EV;
    //Texture weighting to apply
    private ArrayList<Double> tx_EV;
    //Average shape from file sh_000.csv
    private ArrayList<ArrayList<Double>> averageShape;
    //Average texture from file tx_000.csv
    private ArrayList<ArrayList<Double>> averageTexture;

    /**
     * Constructor for FileParser class
     * @param meshFile - index file for face mesh
     * @param sh_EV_File - shape weighting file
     * @param tx_EV_File - texture weighting file
     * @param averageFace - average face file 000
     */
    public FileParser(String meshFile, String sh_EV_File, String tx_EV_File, String averageFace) {
        //Load data from files
        this.meshIndices = loadCSV(meshFile);
        this.sh_EV = loadEVFile(sh_EV_File);
        this.tx_EV = loadEVFile(tx_EV_File);
        this.averageShape = loadCSV("data/sh_" + averageFace + ".csv");
        this.averageTexture = loadCSV("data/tx_" + averageFace + ".csv");
    }

    /**
     * Load a csv file
     * @param fileName - to load
     * @return - matrix (2D arraylist) of double representing file data
     */
    public ArrayList<ArrayList<Double>> loadCSV(String fileName) {
        //Matrix of file data
        ArrayList<ArrayList<Double>>  fileData = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line;

            while ((line = br.readLine()) != null) {
                ArrayList<Double> rowData = new ArrayList<>();
                for (String s : line.split(",")) {
                    rowData.add(Double.parseDouble(s));
                }

                fileData.add(rowData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileData;
    }

    /**
     * Load EV file
     * @param evFile - to load
     * @return matrix of data from EV files
     */
    public ArrayList<Double> loadEVFile(String evFile) {
        //Matrix of file data
        ArrayList<Double> evData = new ArrayList<>();

        try {
            //Get buffer reader to file
            BufferedReader br = new BufferedReader(new FileReader(evFile));
            String line;

            //Read each line
            while ((line = br.readLine()) != null) {
                //Add weighting to matrix
                evData.add(Double.parseDouble(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return evData;
    }

    /**
     * Load in a face file
     * @param fileNum - of face to load
     * @return Face object representing data stored in file
     */
    public Face loadFace(String fileNum) {
        //File number (0 - 199)
        int n = Integer.parseInt(fileNum);

        //Construct file name of shape and texture files
        String shFile = "data/sh_" + fileNum + ".csv";
        String txFile = "data/tx_" + fileNum + ".csv";

        //Load shape and texture data from files
        ArrayList<ArrayList<Double>> shFileData = loadCSV(shFile);
        ArrayList<ArrayList<Double>> txFileData = loadCSV(txFile);

        //Stores triangles of face
        ArrayList<Triangle> triangles = new ArrayList<>();

        //For each row in shape and texture data
        for (int row = 0; row < shFileData.size(); row++) {
            //Store shape and texture data
            ArrayList<Double> shapes = new ArrayList<>();
            ArrayList<Double> textures = new ArrayList<>();

            //For each row in file
            for (int col = 0; col < shFileData.get(row).size(); col++) {
                //Multiply value in sh_00n.csv file with nth weight in sh_EV file
                Double newShape = shFileData.get(row).get(col) * sh_EV.get(n);
                //Multiply value in tx_00n.csv file with nth weight in tx_EV file
                Double newTexture = txFileData.get(row).get(col) * tx_EV.get(n);

                //Sum shape and and texture values with values in the average face file (sh_000.csv and tx_000.csv)
                newShape += averageShape.get(row).get(col);
                newTexture += averageTexture.get(row).get(col);

                //Add vertex coordinate (x, y, or z)
                shapes.add(newShape);
                //Add colour (r, g, or b) value of vertex
                textures.add(newTexture);
            }

            //Add (x, y, z) coordinate to list of shapes
            shFileData.set(row, shapes);
            //Add row (rgb value)
            txFileData.set(row, textures);
        }

        //Use mesh file values to index into values from sh_00n.csv and tx_00n.csv to generate vertices and triangles
        for (ArrayList<Double> row : meshIndices) {
            //Create new triangle
            Triangle triangle = new Triangle();

            //For each row of indices in mesh file corresponding to a triangle
            for (Double d : row) {
                //Get the index of the coordinates of the current vertex in the row
                int index = (int) Math.floor(d) - 1;

                //Get the x,y,z coordinates of the current vertex
                double x = shFileData.get(index).get(0);
                double y = shFileData.get(index).get(1);
                double z = shFileData.get(index).get(2);

                //Get the RGB values of the current vertex
                double r = txFileData.get(index).get(0);
                double g = txFileData.get(index).get(1);
                double b = txFileData.get(index).get(2);

                //Add vertex to triangle with coordinate (x,y,z) and colour (r,g,b)
                triangle.vertices.add(new Vertex(x, y, z, r, g, b));
            }

            //Add triangle to list of triangles in face
            triangles.add(triangle);
        }

        //Normalise coordinates of triangles
        normalise(triangles);

        //Return face initialised with triangles
        return new Face(triangles);
    }

    /**
     * Normalise x and y coords to be in range -1 to 1, and z coords to be in range 0 to 1
     * @param triangles
     */
    void normalise(ArrayList<Triangle> triangles) {
        //Max and min value for x, y, and z
        double x_max = 0;
        double x_min = 0;
        double y_max = 0;
        double y_min = 0;
        double z_max = 0;
        double z_min = 0;

        //Average depth of triangle
        double average;

        //Calculate max and min for each coordinate
        for (Triangle t : triangles) {
            for (Vertex v : t.vertices) {
                if (v.x > x_max) {
                    x_max = v.x;
                }

                if (v.x < x_min) {
                    x_min = v.x;
                }

                if (v.y > y_max) {
                    y_max = v.y;
                }

                if (v.y < y_min) {
                    y_min = v.y;
                }

                //Max z coordinate is the maximum average z of the three vertices in the triangle
                if ((average = t.averageDepth()) > z_max) {
                    z_max = average;
                }

                //Max z coordinate is the maximum average z of the three vertices in the triangle
                if (average < z_min) {
                    z_min = average;
                }
            }
        }

        //normalise coords using max and min values
        for (Triangle t : triangles) {
            double z = t.averageDepth();
            for (Vertex v : t.vertices) {
                //Scale x and y to be in range -1 to 1 (-1 is left and top of screen, 1 is right and bottom of screen)
                v.x = 2 * (v.x - x_min) / (x_max - x_min) - 1;
                v.y = 2 * (v.y - y_min) / (y_max - y_min) - 1;

                //Scale z to be in range 0 to 1 (0 is screen image is projected on and 1 is back of view frustrum)
                v.z = (z - z_min) / (z_max - z_min);
            }

        }
    }
}
