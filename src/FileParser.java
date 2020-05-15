import java.awt.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import static java.lang.System.exit;

public class FileParser {
    private ArrayList<ArrayList<Double>> meshIndices;
    private ArrayList<Double> sh_EV;
    private ArrayList<Double> tx_EV;
    private ArrayList<ArrayList<Double>> averageShape;
    private ArrayList<ArrayList<Double>> averageTexture;

    public FileParser(String meshFile, String sh_EV_File, String tx_EV_File, String averageFace) {
        this.meshIndices = loadCSV(meshFile);
        this.sh_EV = loadEVFile(sh_EV_File);
        this.tx_EV = loadEVFile(tx_EV_File);
        this.averageShape = loadCSV("data/sh_" + averageFace + ".csv");
        this.averageTexture = loadCSV("data/tx_" + averageFace + ".csv");
        System.out.println("Size of mesh indices: " + meshIndices.size());

        System.out.println("Size of sh_EV: " + sh_EV.size());

        //TODO - write function to parse face file
    }

    public ArrayList<ArrayList<Double>> loadCSV(String fileName) {
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

    public ArrayList<Double> loadEVFile(String evFile) {
        ArrayList<Double> evData = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(evFile));
            String line;

            while ((line = br.readLine()) != null) {
                evData.add(Double.parseDouble(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return evData;
    }

    public Face loadFace(String fileNum) {
        int n = Integer.parseInt(fileNum);
        String shFile = "data/sh_" + fileNum + ".csv";
        String txFile = "data/tx_" + fileNum + ".csv";
        ArrayList<ArrayList<Double>> shFileData = loadCSV(shFile);
        ArrayList<ArrayList<Double>> txFileData = loadCSV(txFile);
        ArrayList<Triangle> triangles = new ArrayList<>();

        //Add to average values in sh_000.csv and tx_000.csv and multiply by weigths in sh_EV.csv and tx_EV.csv
        for (int row = 0; row < shFileData.size(); row++) {
            ArrayList<Double> shapes = new ArrayList<>();
            ArrayList<Double> textures = new ArrayList<>();
            for (int col = 0; col < shFileData.get(row).size(); col++) {
                //Sum coordinate value in sh_00n.csv with sh_000.csv and tx_00n.csv with tx_000.csv
                Double newShape = shFileData.get(row).get(col) * sh_EV.get(n);
                Double newTexture = txFileData.get(row).get(col) * tx_EV.get(n);

                //Multiply values for sh_00n and tx_00n with nth weight in sh_EV.csv and tx_EV.csv respectively
                newShape += averageShape.get(row).get(col);
                newTexture += averageTexture.get(row).get(col);

                shapes.add(newShape);
                textures.add(newTexture);
            }

            shFileData.set(row, shapes);
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

                triangle.vertices.add(new Vertex(x, y, z, r, g, b));
            }

            triangles.add(triangle);
        }

        return new Face(triangles);
    }
}
