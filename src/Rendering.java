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

    //Button to clear the display
    private JButton clear = new JButton("Clear");
    //Button to draw the render on the display
    private JButton render = new JButton("Render");

    //TODO - replace with radio buttons for flag/gouraud shading
    static JRadioButton flat = new JRadioButton("Flat");
    static JRadioButton gouraud = new JRadioButton("Gouraud");

    public static void main(String[] args) {
        Rendering render = new Rendering();
        render.frame = new JFrame();

        //Initialise panel to place buttons on
        render.panel = new JPanel();
        render.panel.setLayout(new BoxLayout(render.panel, BoxLayout.PAGE_AXIS));

        //Add buttons to panel
        render.panel.add(render.clear);
        render.panel.add(render.render);

        //Adds radio buttons to JPanel
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(flat);
        buttonGroup.add(gouraud);
        render.panel.add(flat);
        render.panel.add(gouraud);

        //Specifies layout of frame
        render.frame.setTitle("3D Rendering");
        render.frame.setSize(1280, 720);
        Container contentPane = render.frame.getContentPane();
        contentPane.add(render, BorderLayout.CENTER);

        //Specifies border of panel
        render.panel.setBorder(BorderFactory.createLineBorder(Color.gray));
        //Adds panel to frame
        contentPane.add(render.panel, BorderLayout.LINE_START);
        //Sets frame to visible
        render.frame.setVisible(true);

        //Draws the frame
        render.repaint();

        //Ensures that program exits when window is closed
        render.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //Sets resizable to false for the window
        render.frame.setResizable(false);
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
                points.add(new Point(e.getX(), e.getY()));
                repaint();
            }
        });

        //Clears the render and plotted points from the display and repaints
        clear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                //Removes points from array list points
                points.clear();
                repaint();
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