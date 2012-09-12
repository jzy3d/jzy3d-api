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

import com.jogamp.opengl.util.gl2.GLUT;

/**
 * This program shows how to composite modeling transformations to draw
 * translated and rotated models. Interaction: pressing the d and y keys (day
 * and year) alters the rotation of the planet around the sun.
 * 
 * @author Kiet Le (Java port) Ported to JOGL 2.x by Claudio Eduardo Goes
 */
public class planet//
        extends GLSkeleton<GLJPanel>
        implements GLEventListener, KeyListener {
    private GLU glu;
    private GLUT glut; 
    private int year = 0, day = 0;

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
        planet demo = new planet();
        //
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("planet");
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().add(demo.drawable);
        frame.setVisible(true);
        demo.drawable.requestFocusInWindow();
    }

    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        glu = new GLU();
        glut = new GLUT();
        //
        gl.glShadeModel(GL2.GL_FLAT);
    }

    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glColor3f(1.0f, 1.0f, 1.0f);

        /* draw sun */
        gl.glPushMatrix();
        glut.glutWireSphere(1.0, 20, 16);
        /* draw smaller planet */
        gl.glRotatef((float) year, 0.0f, 1.0f, 0.0f);
        gl.glTranslatef(2.0f, 0.0f, 0.0f);
        gl.glRotatef((float) day, 0.0f, 1.0f, 0.0f);
        glut.glutWireSphere(0.2, 10, 10);
        gl.glPopMatrix();

        gl.glFlush();
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(60.0, (float) w / (float) h, 1.0, 20.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glTranslatef(0.0f, 0.0f, -5.0f);
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
        case KeyEvent.VK_LEFT:
        case KeyEvent.VK_A:
            yearSubtract();
            break;
        case KeyEvent.VK_RIGHT:
        case KeyEvent.VK_D:
            yearAdd();
            break;
        case KeyEvent.VK_UP:
        case KeyEvent.VK_W:
            dayAdd();
            break;
        case KeyEvent.VK_DOWN:
        case KeyEvent.VK_S:
            daySubtract();
            break;

        default:
            break;
        }
        super.refresh();
    }

    public void keyReleased(KeyEvent key) {
    }

    private void dayAdd() {
        day = (day + 10) % 360;
    }

    private void daySubtract() {
        day = (day - 10) % 360;
    }

    private void yearAdd() {
        year = (year + 5) % 360;
    }

    private void yearSubtract() {
        year = (year - 5) % 360;
    }

    public void dispose(GLAutoDrawable arg0) {
         
    }

}
