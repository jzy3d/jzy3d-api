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
import javax.media.opengl.glu.GLUquadric;
import javax.swing.JFrame;

/**
 * This program demonstrates the use of the quadrics Utility Library routines to
 * draw circles and arcs.
 * 
 * @author Kiet Le (Java port) Ported to JOGL 2.x by Claudio Eduardo Goes
 */
public class disk//
        extends GLSkeleton<GLJPanel>
        implements GLEventListener, KeyListener {
    private GLU glu;
    private GLUquadric quadObj;

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

        disk demo = new disk();
        //
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("disk");
        frame.setSize(512, 512);
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

        quadObj = glu.gluNewQuadric();
        gl.glShadeModel(GL2.GL_FLAT);
    }

    /*
     * Clear the screen. For each triangle, set the current color and modify the
     * modelview matrix.
     */
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);

        gl.glPushMatrix();
        glu.gluQuadricDrawStyle(quadObj, GLU.GLU_FILL);
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glTranslatef(10.0f, 10.0f, 0.0f);
        glu.gluDisk(quadObj, 0.0, 5.0, 10, 2);
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glColor3f(1.0f, 1.0f, 0.0f);
        gl.glTranslatef(20.0f, 20.0f, 0.0f);
        glu.gluPartialDisk(quadObj, 0.0, 5.0, 10, 3, 30.0, 120.0);
        gl.glPopMatrix();

        gl.glPushMatrix();
        glu.gluQuadricDrawStyle(quadObj, GLU.GLU_SILHOUETTE);
        gl.glColor3f(0.0f, 1.0f, 1.0f);
        gl.glTranslatef(30.0f, 30.0f, 0.0f);
        glu.gluPartialDisk(quadObj, 0.0, 5.0, 10, 3, 135.0, 270.0);
        gl.glPopMatrix();

        gl.glPushMatrix();
        glu.gluQuadricDrawStyle(quadObj, GLU.GLU_LINE);
        gl.glColor3f(1.0f, 0.0f, 1.0f);
        gl.glTranslatef(40.0f, 40.0f, 0.0f);
        glu.gluDisk(quadObj, 2.0, 5.0, 10, 10);
        gl.glPopMatrix();
        gl.glFlush();
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        if (w <= h)
            gl.glOrtho(0.0, 50.0, 0.0, 50.0 * (float) h / (float) w, -1.0, 1.0);
        else
            gl.glOrtho(0.0, 50.0 * (float) w / (float) h, 0.0, 50.0, -1.0, 1.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }

    public void keyTyped(KeyEvent key) {
    }

    public void keyPressed(KeyEvent key) {
        switch (key.getKeyChar()) {
        case KeyEvent.VK_ESCAPE:
            System.exit(0);
            break;

        default:
            break;
        }
    }

    public void keyReleased(KeyEvent key) {
    }

    public void run() {
    }

    public void dispose(GLAutoDrawable arg0) {
         
    }

}
