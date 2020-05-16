import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import static com.sun.java.accessibility.util.AWTEventMonitor.addWindowListener;

/**
 * Class to manage the swing user interface
 */
public class Rendering extends JPanel {
    //Stores the list of points entered by the user
    private ArrayList<Point2D> points = new ArrayList<>();

    //Stores the coordinates of each corner of the triangle
    static private ArrayList<Point2D> trianglePoints = new ArrayList<>();

    //JFrame representing the GUI window
    private JFrame frame;
    //Panel to place buttons and checkboxes on
    private JPanel panel;

    //Is the main frame
    private boolean mainFrame = false;

    //Resolution of display
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

    //Values for slider
    static final int MIN_FOCAL_LEN = 1;
    static final int MAX_FOCAL_LEN = 25;

    //Focal length
    static int focalLength = MIN_FOCAL_LEN;

    //Object to represent window to render synthesised face
    static Rendering faceRendering;

    //Stores list of reference faces loaded from files
    static ArrayList<Face> faces = new ArrayList<>();

    //Synthetic face
    static Face syntheticFace;

    //Stores shading method
    static Shading shading;
    //Stores projection method
    static Projection projection;

    //Store file names for files to be loaded in
    static String meshFile = "data/mesh.csv";
    static String shEVFile = "data/sh_ev.csv";
    static String txEVFile = "data/tx_ev.csv";
    static String faceFile0 = "000";
    static String faceFile1 = "001";
    static String faceFile2 = "002";
    static String faceFile3 = "003";

    /**
     * Main function to run program,
     * used to set up user interface
     * @param args
     */
    public static void main(String[] args) {
        //Parser to parse files
        FileParser fileParser = new FileParser(meshFile, shEVFile, txEVFile, faceFile0);

        //Load the three faces in
        faces.add(fileParser.loadFace(faceFile1));
        faces.add(fileParser.loadFace(faceFile2));
        faces.add(fileParser.loadFace(faceFile3));

        //Create new object to represent the window to display the synthesised face in
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

        //Sets initial values for shading mode and projeciton mode
        shading = (flat.isSelected()) ? Shading.FLAT : Shading.GOURAUD;
        projection = (orthographic.isSelected()) ? Projection.ORTHOGRAPHIC : Projection.PERSPECTIVE;

        //Label for focal length slider
        JLabel sliderLabel = new JLabel("Focal Length:");
        display.panel.add(sliderLabel);

        //Slider for specifying focal length
        JSlider focalLengthSlider = new JSlider(JSlider.HORIZONTAL, MIN_FOCAL_LEN, MAX_FOCAL_LEN, MIN_FOCAL_LEN);
        focalLengthSlider.addChangeListener(new SliderListener());
        focalLengthSlider.setMajorTickSpacing(5);
        focalLengthSlider.setPaintTicks(true);
        focalLengthSlider.setPaintLabels(true);
        display.panel.add(focalLengthSlider);

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
        trianglePoints.add(new Point2D.Double(display.getWidth() / 2.0, 150));
        trianglePoints.add(new Point2D.Double(display.getWidth() / 8.0, display.getHeight() - 100));
        trianglePoints.add(new Point2D.Double(display.getWidth() - (display.getWidth() / 8.0), display.getHeight() - 100));

        //Draws the frame
        display.repaint();

        //Ensures that program exits when window is closed
        display.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //Sets resizable to false for the window
        display.frame.setResizable(false);
    }


    /**
     * Constructor for
     */
    private Rendering() {
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
                //Update shading and projection
                shading = (flat.isSelected()) ? Shading.FLAT : Shading.GOURAUD;
                projection = (orthographic.isSelected()) ? Projection.ORTHOGRAPHIC : Projection.PERSPECTIVE;

                //If point lies within triangle, clear display and plot it
                Point2D point = new Point2D.Double(e.getX(), e.getY());
                if (mainFrame && isWithinTriangle(trianglePoints, point)) {
                    points.clear();
                    points.add(point);
                    repaint();
                }
            }
        });


        //Action listener for the render button to display the synthetic face
        render.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                //Update shading and projection
                shading = (flat.isSelected()) ? Shading.FLAT : Shading.GOURAUD;
                projection = (orthographic.isSelected()) ? Projection.ORTHOGRAPHIC : Projection.PERSPECTIVE;

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
                }
            }
        });
    }

    /**
     * Repaints the display
     * @param g - graphics object used to paint display
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D graphics2D = (Graphics2D) g;

        //If JFrame is the main frame
        if (mainFrame) {
            //Display and draw triangle for user to click within
            displayTriangle(graphics2D, trianglePoints, faces);

            //If user has clicked within triangle
            if (!points.isEmpty()) {
                //Plot point point in triangle where user clicked
                plotPoints(graphics2D, points);
            }

        //If synthetic face has been generated
        } else if (syntheticFace != null) {
            //Display the synthetic face in the second window
            syntheticFace.display(graphics2D, shading, projection, focalLength, WIDTH, HEIGHT, -250, -300, 14);
        }
    }

    /**
     * Return whether point lies within triangle
     * @param trianglePoints - vertices of triangle
     * @param point - point to check
     * @return whether point lies within triangle
     */
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

        //Conditions for point being within triangle
        return (s > 0) && (t > 0) && (1 - s - t > 0);

        //End citation
    }

    /**
     * Display the triangle
     * @param graphics2D - to draw triangle
     * @param points - vertices of triangle
     * @param faces - faces to display on vertices of triangle
     */
    private void displayTriangle(Graphics2D graphics2D, ArrayList<Point2D> points, ArrayList<Face> faces) {
        //Draw edges between vertices
        drawEdges(graphics2D, points);
        //Display faces at vertcies of triangle
        displayFaces(graphics2D, faces);
    }

    /**
     * Plot points on screen
     * @param graphics2D - to draw points
     * @param points - points to plot
     */
    private void plotPoints(Graphics2D graphics2D, ArrayList<Point2D> points) {
        //For each point
        for (Point2D p : points) {
            //Set colour to black
            graphics2D.setColor(Color.BLACK);
            //Fill oval representing point
            graphics2D.fillOval((int) (p.getX() - 5.0 / 2.0), (int) (p.getY() - 5.0 / 2.0), 5, 5);
        }
    }

    /**
     * Draw edges of triangle
     * @param graphics2D - to draw path
     * @param points - vertices of triangle
     */
    private void drawEdges(Graphics2D graphics2D, ArrayList<Point2D> points) {
        //Create new path
        Path2D path = new Path2D.Double();

        //Move to top vertex of triangle
        path.moveTo(points.get(0).getX(), points.get(0).getY());

        //Draw line to bottom left vertex of triangle
        path.lineTo(points.get(1).getX(), points.get(1).getY());
        //Draw line to bottom right vertex of triangle
        path.lineTo(points.get(2).getX(), points.get(2).getY());
        //Draw line back to top vertex of triangle
        path.lineTo(points.get(0).getX(), points.get(0).getY());

        //Render complete path
        graphics2D.draw(path);
    }

    /**
     * Display list of faces in corners of triangle
     * @param graphics2D - to display faces
     * @param faces - to display
     */
    private void displayFaces(Graphics2D graphics2D, ArrayList<Face> faces) {
        //Display reference faces using flat shading and orthographic perspective to reduce execution time to render
        faces.get(0).display(graphics2D, Shading.FLAT, Projection.ORTHOGRAPHIC, focalLength, WIDTH, HEIGHT, -215, -300, 5);
        faces.get(1).display(graphics2D, Shading.FLAT, Projection.ORTHOGRAPHIC, focalLength, WIDTH, HEIGHT, 200, 100, 5);
        faces.get(2).display(graphics2D, Shading.FLAT, Projection.ORTHOGRAPHIC, focalLength, WIDTH, HEIGHT, -600, 100, 5);
    }

    /**
     * Listener for UI slider
     */
    static class SliderListener implements ChangeListener {
        /**
         * Run if slider's state has been changed
         * @param e - event to represent slider being adjusted
         */
        public void stateChanged(ChangeEvent e) {
            //Get value of slider
            JSlider source = (JSlider)e.getSource();
            //If slider is not currently being adjusted
            if (!source.getValueIsAdjusting()) {
                //Update value of focal length
                // (scaled up by a factor of 10,000 to be of high enough precision when multiplying points)
                focalLength = source.getValue() * 10000;
            }
        }
    }

}