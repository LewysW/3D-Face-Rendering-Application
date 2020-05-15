import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import javax.swing.*;

import static com.sun.java.accessibility.util.AWTEventMonitor.addWindowListener;

/**
 * Class to manage the swing user interface
 */
public class Rendering extends JPanel {
    //Stores the list of points entered by the user
    private ArrayList<Point2D> points = new ArrayList<>();

    //Stores the coordinates of each corner of the triangle
    static private ArrayList<Point2D> trianglePoints = new ArrayList<>();

    //Stores label for each corner of triangle
    static private ArrayList<String> triangleLabels = new ArrayList<>();

    //JFrame representing the GUI window
    private JFrame frame;
    //Panel to place buttons and checkboxes on
    private JPanel panel;

    //Is the main frame
    private boolean mainFrame = false;

    private static final double WIDTH = 1280;
    private static final double HEIGHT = 720;

    //Button to draw the render on the display
    private JButton render = new JButton("Render");

    //Radio buttons to select the shading method
    static JRadioButton flat = new JRadioButton("Flat");
    static JRadioButton gouraud = new JRadioButton("Gouraud");

    //Radio buttons to select the perspective
    static JRadioButton orthographic = new JRadioButton("Orthographic");
    static JRadioButton perspective = new JRadioButton("Perspective");

    static Rendering faceRendering;

    //Reference faces
    static ArrayList<Face> faces = new ArrayList<>();

    //Synthetic face
    static Face syntheticFace;

    static String meshFile = "data/mesh.csv";
    static String shEVFile = "data/sh_ev.csv";
    static String txEVFile = "data/tx_ev.csv";
    static String faceFile0 = "000";
    static String faceFile1 = "001";
    static String faceFile2 = "002";
    static String faceFile3 = "003";

    public static void main(String[] args) {
        Rendering display = new Rendering();
        display.frame = new JFrame();
        display.mainFrame = true;

        //Initialise panel to place buttons on
        display.panel = new JPanel();
        display.panel.setLayout(new BoxLayout(display.panel, BoxLayout.PAGE_AXIS));

        //Add buttons to panel
        display.panel.add(display.render);

        //Shading label
        JLabel shadingLabel = new JLabel("Shading:");
        display.panel.add(shadingLabel);

        //Adds radio buttons to JPanel to select shading
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(flat);
        buttonGroup.add(gouraud);
        display.panel.add(flat);
        display.panel.add(gouraud);
        flat.setSelected(true);

        //Projection label
        JLabel projectionLabel = new JLabel("Projection:");
        display.panel.add(projectionLabel);

        //Adds radio buttons to JPanel to select shading
        ButtonGroup buttonGroup1 = new ButtonGroup();
        buttonGroup1.add(orthographic);
        buttonGroup1.add(perspective);
        display.panel.add(orthographic);
        display.panel.add(perspective);
        orthographic.setSelected(true);

        //Specifies layout of frame
        display.frame.setTitle("3D Rendering");
        display.frame.setSize(1280, 720);
        Container contentPane = display.frame.getContentPane();
        contentPane.add(display, BorderLayout.CENTER);

        //Specifies border of panel
        display.panel.setBorder(BorderFactory.createLineBorder(Color.gray));
        //Adds panel to frame
        contentPane.add(display.panel, BorderLayout.LINE_START);
        //Sets frame to visible
        display.frame.setVisible(true);

        //Define points of triangle to display:
        trianglePoints.add(new Point2D.Double(display.getWidth() / 2.0, 20));
        trianglePoints.add(new Point2D.Double(display.getWidth() / 8.0, display.getHeight() - 20));
        trianglePoints.add(new Point2D.Double(display.getWidth() - (display.getWidth() / 8.0), display.getHeight() - 20));

        //Define labels for corners of triangles
        triangleLabels.add("Face 1");
        triangleLabels.add("Face 2");
        triangleLabels.add("Face 3");

        //Parser to parse files
        FileParser fileParser = new FileParser(meshFile, shEVFile, txEVFile, faceFile0);

        //Load the three faces in
        faces.add(fileParser.loadFace(faceFile1));
        faces.add(fileParser.loadFace(faceFile2));
        faces.add(fileParser.loadFace(faceFile3));

        //Draws the frame
        display.repaint();

        //Ensures that program exits when window is closed
        display.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //Sets resizable to false for the window
        display.frame.setResizable(false);
    }


    public Rendering() {
        //Exits program on window being closed
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        //Adds points to the display when clicked
        addMouseListener(new MouseAdapter() {// provides empty implementation of all
            // MouseListener`s methods, allowing us to
            // override only those which interests us
            @Override //I override only one method for presentation
            public void mousePressed(MouseEvent e) {
                //If point lies within triangle, clear display and plot it
                Point2D point = new Point2D.Double(e.getX(), e.getY());
                if (isWithinTriangle(trianglePoints, point)) {
                    points.clear();
                    points.add(point);
                    repaint();
                }
            }
        });


        //Action listener for the render button to display the synthetic face
        render.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                //If a point on the display has been selected
                if (!points.isEmpty()) {
                    //Close previous synthetic face if displayed
                    if (faceRendering != null) {
                        faceRendering.setVisible(false);
                        faceRendering.frame.dispose();
                    }

                    //Get weights for each face determined by point in triangle
                    ArrayList<Double> weights = Triangle.interpolate(points.get(0), trianglePoints);
                    syntheticFace = new Face(faces, weights);

                    //Create new window to display new synthetic face
                    faceRendering = new Rendering();
                    faceRendering.frame = new JFrame();
                    //Specifies layout of frame
                    faceRendering.frame.setTitle("Synthetic Face");
                    faceRendering.frame.setSize((int) WIDTH, (int) HEIGHT);
                    Container contentPane = faceRendering.frame.getContentPane();
                    contentPane.add(faceRendering, BorderLayout.CENTER);

                    //Sets frame to visible
                    faceRendering.frame.setVisible(true);

                    //Sets resizable to false for the window
                    faceRendering.frame.setResizable(false);
                }
            }
        });
    }

    //Paints the display
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D graphics2D = (Graphics2D) g;

        //If JFrame is the main frame
        if (mainFrame) {
            //If no points have been plotted
            if (points.isEmpty()) {
                //clear display and draw triangle
                displayTriangle(graphics2D, trianglePoints, triangleLabels);
                return;
            }


            displayTriangle(graphics2D, trianglePoints, triangleLabels);
            plotPoints(graphics2D, points);
        //If frame is secondary frame
        } else if (syntheticFace != null) {
            Shading shading = (flat.isSelected()) ? Shading.FLAT : Shading.GOURAUD;
            Projection projection = (orthographic.isSelected()) ? Projection.ORTHOGRAPHIC : Projection.PERSPECTIVE;

            System.out.println("Displaying synthetic face!");
            syntheticFace.display(graphics2D, shading, projection, 0, WIDTH, HEIGHT);
        }
    }

    //Return whether point clicked by user lies within the triangle
    private boolean isWithinTriangle(ArrayList<Point2D> trianglePoints, Point2D point) {
        double p0x = trianglePoints.get(0).getX();
        double p0y = trianglePoints.get(0).getY();

        double p1x = trianglePoints.get(1).getX();
        double p1y = trianglePoints.get(1).getY();

        double p2x = trianglePoints.get(2).getX();
        double p2y = trianglePoints.get(2).getY();

        double px = point.getX();
        double py = point.getY();

        //Citation:
        //Find if point is within triangle:
        //https://stackoverflow.com/questions/2049582/how-to-determine-if-a-point-is-in-a-2d-triangle

        double area = 0.5 *(-p1y*p2x + p0y*(-p1x + p2x) + p0x*(p1y - p2y) + p1x*p2y);
        double s = 1/(2*area)*(p0y*p2x - p0x*p2y + (p2y - p0y)*px + (p0x - p2x)*py);
        double t = 1/(2*area)*(p0x*p1y - p0y*p1x + (p0y - p1y)*px + (p1x - p0x)*py);

        return (s > 0) && (t > 0) && (1 - s - t > 0);

        //End citation
    }

    //Label the points of the triangle
    private void labelPoints(Graphics2D graphics2D, ArrayList<Point2D> points, ArrayList<String> labels) {
        graphics2D.drawString(labels.get(0), (int) points.get(0).getX() - 20, (int) points.get(0).getY() - 5);
        graphics2D.drawString(labels.get(1), (int) points.get(1).getX() - 45, (int) points.get(1).getY());
        graphics2D.drawString(labels.get(2), (int) points.get(2).getX() + 5, (int) points.get(2).getY());
    }

    //Display the triangle
    private void displayTriangle(Graphics2D graphics2D, ArrayList<Point2D> points, ArrayList<String> labels) {
        //plotPoints(graphics2D, points);
        labelPoints(graphics2D, points, labels);
        drawEdges(graphics2D, points);
    }

    //Plot points on the display
    private void plotPoints(Graphics2D graphics2D, ArrayList<Point2D> points) {
        for (Point2D p : points) {
            graphics2D.setColor(Color.BLACK);
            graphics2D.fillOval((int) (p.getX() - 5.0 / 2.0), (int) (p.getY() - 5.0 / 2.0), 5, 5);
        }
    }

    //Draw the edges between the corners of the triangle
    private void drawEdges(Graphics2D graphics2D, ArrayList<Point2D> points) {
        Path2D path = new Path2D.Double();

        //Move to top corner of triangle
        path.moveTo(points.get(0).getX(), points.get(0).getY());

        //Draw line from top corner to bottom left and bottom right corners
        path.lineTo(points.get(1).getX(), points.get(1).getY());
        path.lineTo(points.get(2).getX(), points.get(2).getY());
        path.lineTo(points.get(0).getX(), points.get(0).getY());

        graphics2D.draw(path);
    }

    //Clears the display
    private void clearDisplay(Graphics g) {
        //Clears points by setting display to the default background colour
        g.setColor(frame.getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
    }

}