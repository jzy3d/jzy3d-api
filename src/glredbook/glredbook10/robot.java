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
 * translated and rotated hierarchical models. Interaction: pressing the s
 * and e keys (shoulder and elbow) alters the rotation of the robot arm.
 *
 * @author Kiet Le (Java port) Ported to JOGL 2.x by Claudio Eduardo Goes
 */
public class robot//
        extends GLSkeleton<GLJPanel>
        implements GLEventListener, KeyListener {
    private GLU glu;
    private GLUT glut; 
    private static int shoulder = 0, elbow = 0;

    //
    @Override
    protected GLJPanel createDrawable() {
        GLCapabilities caps = new GLCapabilities(null);
        //
        GLJPanel panel = new GLJPanel(caps);
        panel.addGLEventListener(this);
        panel.addKeyListener(this);
        return panel;
    }

    public void run() {
        robot demo = new robot();
        //
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("robot");
        frame.setSize(400, 400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().add(demo.drawable);
        frame.setVisible(true);
        demo.drawable.requestFocusInWindow();
    }

    public static void main(String[] args) {
        new robot().run();
    }

    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        glu = new GLU();
        glut = new GLUT();
        //
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glShadeModel(GL2.GL_FLAT);
    }

    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glColor3f(1.0f, 1.0f, 1.0f);

        gl.glPushMatrix();
        gl.glTranslatef(-1.0f, 0.0f, 0.0f);
        gl.glRotatef((float) shoulder, 0.0f, 0.0f, 1.0f);
        gl.glTranslatef(1.0f, 0.0f, 0.0f);
        // gl.glPushMatrix();
        gl.glScalef(2.0f, 0.4f, 1.0f);
        glut.glutWireCube(1.0f);
        // gl.glPopMatrix();

        gl.glTranslatef(1.0f, 0.0f, 0.0f);
        gl.glRotatef((float) elbow, 0.0f, 0.0f, 1.0f);
        gl.glTranslatef(1.0f, 0.0f, 0.0f);
        // gl.glPushMatrix();
        gl.glScalef(2.0f, 0.4f, 1.0f);
        glut.glutWireCube(1.0f);
        // gl.glPopMatrix();

        gl.glPopMatrix();

        gl.glFlush();
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(65.0, (float) w / (float) h, 1.0, 20.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glTranslatef(0.0f, 0.0f, -5.0f);
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }

    private void elbowAdd() {
        elbow = (elbow + 5) % 360;
    }

    private void elbowSubtract() {
        elbow = (elbow - 5) % 360;
    }

    private void shoulderAdd() {
        shoulder = (shoulder + 5) % 360;
    }

    private void shoulderSubtract() {
        shoulder = (shoulder - 5) % 360;
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
            shoulderSubtract();
            break;
        case KeyEvent.VK_RIGHT:
        case KeyEvent.VK_D:
            shoulderAdd();
            break;
        case KeyEvent.VK_UP:
        case KeyEvent.VK_W:
            elbowAdd();
            break;
        case KeyEvent.VK_DOWN:
        case KeyEvent.VK_S:
            elbowSubtract();
            break;

        default:
            break;
        }
        super.refresh();
    }

    public void keyReleased(KeyEvent key) {
    }

    public void dispose(GLAutoDrawable arg0) {
         
    }

}
