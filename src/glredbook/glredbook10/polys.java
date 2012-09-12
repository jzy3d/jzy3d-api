package glredbook10;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLJPanel;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;


/**
 * This program demonstrates polygon stippling.
 * 
 * @author Kiet Le (Java port) Ported to JOGL 2.x by Claudio Eduardo Goes (Java conversin)
 */
public class polys//
        extends GLSkeleton<GLJPanel>
        implements GLEventListener, KeyListener {
    private GLU glu;
    private byte fly[] = { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03,
            (byte) 0x80, (byte) 0x01, (byte) 0xC0, (byte) 0x06, (byte) 0xC0,
            (byte) 0x03, (byte) 0x60, (byte) 0x04, (byte) 0x60, (byte) 0x06,
            (byte) 0x20, (byte) 0x04, (byte) 0x30, (byte) 0x0C, (byte) 0x20,
            (byte) 0x04, (byte) 0x18, (byte) 0x18, (byte) 0x20, (byte) 0x04,
            (byte) 0x0C, (byte) 0x30, (byte) 0x20, (byte) 0x04, (byte) 0x06,
            (byte) 0x60, (byte) 0x20, (byte) 0x44, (byte) 0x03, (byte) 0xC0,
            (byte) 0x22, (byte) 0x44, (byte) 0x01, (byte) 0x80, (byte) 0x22,
            (byte) 0x44, (byte) 0x01, (byte) 0x80, (byte) 0x22, (byte) 0x44,
            (byte) 0x01, (byte) 0x80, (byte) 0x22, (byte) 0x44, (byte) 0x01,
            (byte) 0x80, (byte) 0x22, (byte) 0x44, (byte) 0x01, (byte) 0x80,
            (byte) 0x22, (byte) 0x44, (byte) 0x01, (byte) 0x80, (byte) 0x22,
            (byte) 0x66, (byte) 0x01, (byte) 0x80, (byte) 0x66, (byte) 0x33,
            (byte) 0x01, (byte) 0x80, (byte) 0xCC, (byte) 0x19, (byte) 0x81,
            (byte) 0x81, (byte) 0x98, (byte) 0x0C, (byte) 0xC1, (byte) 0x83,
            (byte) 0x30, (byte) 0x07, (byte) 0xe1, (byte) 0x87, (byte) 0xe0,
            (byte) 0x03, (byte) 0x3f, (byte) 0xfc, (byte) 0xc0, (byte) 0x03,
            (byte) 0x31, (byte) 0x8c, (byte) 0xc0, (byte) 0x03, (byte) 0x33,
            (byte) 0xcc, (byte) 0xc0, (byte) 0x06, (byte) 0x64, (byte) 0x26,
            (byte) 0x60, (byte) 0x0c, (byte) 0xcc, (byte) 0x33, (byte) 0x30,
            (byte) 0x18, (byte) 0xcc, (byte) 0x33, (byte) 0x18, (byte) 0x10,
            (byte) 0xc4, (byte) 0x23, (byte) 0x08, (byte) 0x10, (byte) 0x63,
            (byte) 0xC6, (byte) 0x08, (byte) 0x10, (byte) 0x30, (byte) 0x0c,
            (byte) 0x08, (byte) 0x10, (byte) 0x18, (byte) 0x18, (byte) 0x08,
            (byte) 0x10, (byte) 0x00, (byte) 0x00, (byte) 0x08 };

    private byte halftone[] = { (byte) 0xAA, (byte) 0xAA, (byte) 0xAA,
            (byte) 0xAA, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0x55,
            (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x55,
            (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0xAA, (byte) 0xAA,
            (byte) 0xAA, (byte) 0xAA, (byte) 0x55, (byte) 0x55, (byte) 0x55,
            (byte) 0x55, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA,
            (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0xAA,
            (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x55, (byte) 0x55,
            (byte) 0x55, (byte) 0x55, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA,
            (byte) 0xAA, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0x55,
            (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x55,
            (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0xAA, (byte) 0xAA,
            (byte) 0xAA, (byte) 0xAA, (byte) 0x55, (byte) 0x55, (byte) 0x55,
            (byte) 0x55, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA,
            (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0xAA,
            (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x55, (byte) 0x55,
            (byte) 0x55, (byte) 0x55, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA,
            (byte) 0xAA, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0x55,
            (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x55,
            (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0xAA, (byte) 0xAA,
            (byte) 0xAA, (byte) 0xAA, (byte) 0x55, (byte) 0x55, (byte) 0x55,
            (byte) 0x55, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA,
            (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0xAA,
            (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x55, (byte) 0x55,
            (byte) 0x55, (byte) 0x55, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA,
            (byte) 0xAA, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0x55 };

    @Override
    protected GLJPanel createDrawable() {
        GLCapabilities caps = new GLCapabilities(null);
        //
        GLJPanel panel = new GLJPanel(caps);
        panel.addGLEventListener(this);
        panel.addKeyListener(this);
        return panel;
    }

    public static void main(String[] args) {
        polys demo = new polys();
        //
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("polys");
        frame.setSize(350, 150);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().add(demo.drawable);
        frame.setVisible(true);
        demo.drawable.requestFocusInWindow();
    }

    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        glu = new GLU();
        //
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glShadeModel(GL2.GL_FLAT);
    }

    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glColor3f(1.0f, 1.0f, 1.0f);

        /* draw one solid, unstippled rectangle, */
        /* then two stippled rectangles */
        gl.glRectf(25.0f, 25.0f, 125.0f, 125.0f);
        gl.glEnable(GL2.GL_POLYGON_STIPPLE);
        gl.glPolygonStipple(fly, 0);
        gl.glRectf(125.0f, 25.0f, 225.0f, 125.0f);
        gl.glPolygonStipple(halftone, 0);
        gl.glRectf(225.0f, 25.0f, 325.0f, 125.0f);
        gl.glDisable(GL2.GL_POLYGON_STIPPLE);

        gl.glFlush();
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluOrtho2D(0.0, (double) w, 0.0, (double) h);
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

    public void dispose(GLAutoDrawable arg0) {
         
    }

}
