package glredbook11;
 
import glredbook10.GLSkeleton;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLJPanel;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;
 

/**
 * When the left mouse button is pressed, this program reads the mouse position
 * and determines two 3D points from which it was transformed. Very little is
 * displayed.
 * 
 * @author Kiet Le (Java port) Ported to JOGL 2.x by Claudio Eduardo Goes
 */
public class unproject//
        extends GLSkeleton<GLJPanel>
        implements GLEventListener, KeyListener, MouseListener {
    private GLU glu;  
    private MouseEvent mouse;

    @Override
    protected GLJPanel createDrawable() {
        GLCapabilities caps = new GLCapabilities(null);
        //
        GLJPanel panel = new GLJPanel(caps);
        panel.addGLEventListener(this);
        panel.addKeyListener(this);
        panel.addMouseListener(this);
        return panel;
    }

    public static void main(String[] args) {
        unproject demo = new unproject();

        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("unproject");
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().add(demo.drawable);
        frame.setVisible(true);
        demo.drawable.requestFocusInWindow();
    }

    public void init(GLAutoDrawable drawable) {
//        GL2 gl = drawable.getGL().getGL2();
        glu = new GLU(); 
    }

    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);

        int viewport[] = new int[4];
        double mvmatrix[] = new double[16];
        double projmatrix[] = new double[16];
        int realy = 0;// GL y coord pos
        double wcoord[] = new double[4];// wx, wy, wz;// returned xyz coords
        if (mouse != null) {
            int x = mouse.getX(), y = mouse.getY();
            switch (mouse.getButton()) {
            case MouseEvent.BUTTON1:
                gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
                gl.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, mvmatrix, 0);
                gl.glGetDoublev(GL2.GL_PROJECTION_MATRIX, projmatrix, 0);
                /* note viewport[3] is height of window in pixels */
                realy = viewport[3] - (int) y - 1;
                System.out.println("Coordinates at cursor are (" + x + ", "
                        + realy);
                glu.gluUnProject((double) x, (double) realy, 0.0, //
                        mvmatrix, 0,//
                        projmatrix, 0, //
                        viewport, 0, //
                        wcoord, 0);
                System.out
                        .println("World coords at z=0.0 are ( " //
                                + wcoord[0] + ", " + wcoord[1] + ", "
                                + wcoord[2] + ")");
                glu.gluUnProject((double) x, (double) realy, 1.0, //
                        mvmatrix, 0,//
                        projmatrix, 0,//
                        viewport, 0, //
                        wcoord, 0);
                System.out
                        .println("World coords at z=1.0 are (" //
                                + wcoord[0] + ", " + wcoord[1] + ", "
                                + wcoord[2] + ")");
                break;
            case MouseEvent.BUTTON2:
                break;
            default:
                break;
            }
        }

        gl.glFlush();
    }

    /* Change these values for a different transformation */
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45.0, (float) w / (float) h, 1.0, 100.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }

    public void keyTyped(KeyEvent key) {
    }

    public void keyPressed(KeyEvent key) {

        switch (key.getKeyCode()) {
        case KeyEvent.VK_ESCAPE:
            System.exit(0);
            break;

        default:
            break;
        }

    }

    public void keyReleased(KeyEvent key) {
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        mouse = e;
        super.refresh();
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void dispose(GLAutoDrawable arg0) {
         
    }

}
