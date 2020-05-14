import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

import static com.sun.java.accessibility.util.AWTEventMonitor.addWindowListener;

/**
 * Class to manage the swing user interface
 */
public class Rendering extends JPanel {
    //Stores the list of points entered by the user
    private ArrayList<Point2D> points = new ArrayList<>();

    //JFrame representing the GUI window
    private JFrame frame;
    //Panel to place buttons and checkboxes on
    private JPanel panel;

    //Button to draw the render on the display
    private JButton render = new JButton("Render");

    //TODO - replace with radio buttons for flag/gouraud shading
    static JRadioButton flat = new JRadioButton("Flat");
    static JRadioButton gouraud = new JRadioButton("Gouraud");

    static Rendering faceRendering;

    public static void main(String[] args) {
        Rendering display = new Rendering();
        display.frame = new JFrame();

        //Initialise panel to place buttons on
        display.panel = new JPanel();
        display.panel.setLayout(new BoxLayout(display.panel, BoxLayout.PAGE_AXIS));

        //Add buttons to panel
        display.panel.add(display.render);

        //Adds radio buttons to JPanel
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(flat);
        buttonGroup.add(gouraud);
        display.panel.add(flat);
        display.panel.add(gouraud);
        flat.setSelected(true);

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
                points.clear();
                points.add(new Point(e.getX(), e.getY()));
                repaint();
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

                    //Create new window to display new synthetic face
                    faceRendering = new Rendering();
                    faceRendering.frame = new JFrame();
                    //Specifies layout of frame
                    faceRendering.frame.setTitle("Synthetic Face");
                    faceRendering.frame.setSize(1280, 720);
                    Container contentPane = faceRendering.frame.getContentPane();
                    contentPane.add(faceRendering, BorderLayout.CENTER);

                    //Sets frame to visible
                    faceRendering.frame.setVisible(true);

                    //Draws the frame
                    faceRendering.repaint();

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

        //If no points have been plotted
        if (points.isEmpty()) {
            //clear display and draw set of axes
            clearDisplay(g);
            g.setColor(Color.BLACK);
            return;
        }


        //Plots points where user has clicked
        for (Point2D p : points) {
            graphics2D.setColor(Color.BLACK);
            graphics2D.fillOval((int) (p.getX() - 5.0 / 2.0), (int) (p.getY() - 5.0 / 2.0), 5, 5);
        }

        //Sets colour to black
        g.setColor(Color.BLACK);
    }

    //Clears the display
    private void clearDisplay(Graphics g) {
        //Clears points by setting display to the default background colour
        g.setColor(frame.getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
    }

}