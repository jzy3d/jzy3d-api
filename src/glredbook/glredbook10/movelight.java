package glredbook10;

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

import com.jogamp.opengl.util.gl2.GLUT;

/**
 * This program demonstrates when to issue lighting and transformation commands
 * to render a model with a light which is moved by a modeling transformation
 * (rotate or translate). The light position is reset after the modeling
 * transformation is called. The eye position does not change. <br>
 * <br>
 * A sphere is drawn using a grey material characteristic. A single light source
 * illuminates the object. <br>
 * <br>
 * Interaction: pressing the left or middle mouse button alters the modeling
 * transformation (x rotation) by 30 degrees. The scene is then redrawn with the
 * light in a new position.
 * 
 * @author Kiet Le (Java port) Ported to JOGL 2.x by Claudio Eduardo Goes
 */
public class movelight//
        extends GLSkeleton<GLJPanel>
        implements GLEventListener, KeyListener, MouseListener {
    private GLU glu;
    private GLUT glut;
 
    private static int spin = 0;

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
        movelight demo = new movelight();
        //
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("movelight");
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
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);
        gl.glDepthFunc(GL.GL_LESS);
        gl.glEnable(GL.GL_DEPTH_TEST);
    }

    /*
     * Here is where the light position is reset after the modeling
     * transformation (glRotated) is called. This places the light at a new
     * position in world coordinates. The cube represents the position of the
     * light.
     */
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //
        float position[] = { 0.0f, 0.0f, 1.5f, 1.0f };

        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glPushMatrix();
        gl.glTranslatef(0.0f, 0.0f, -5.0f);

        gl.glPushMatrix();
        gl.glRotated((double) spin, 1.0, 0.0, 0.0);
        gl.glRotated(0.0, 1.0, 0.0, 0.0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, position, 0);

        gl.glTranslated(0.0, 0.0, 1.5);
        gl.glDisable(GL2.GL_LIGHTING);
        gl.glColor3f(0.0f, 1.0f, 1.0f);
        glut.glutWireCube(0.1f);
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glPopMatrix();

        glut.glutSolidTorus(0.275f, 0.85f, 20, 20);
        gl.glPopMatrix();
        gl.glFlush();
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(40.0, (float) w / (float) h, 1.0, 20.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }

    private void move_light() {
        spin = (spin + 30) % 360;
    }

    public void keyTyped(KeyEvent key) {
    }

    public void keyPressed(KeyEvent key) {
        char ch = key.getKeyChar();
        switch (ch) {
        case KeyEvent.VK_ESCAPE:
            System.exit(0);
            break;

        default:
            break;
        }
    }

    public void keyReleased(KeyEvent key) {
    }

    public void mouseClicked(MouseEvent mouse) {
    }

    public void mousePressed(MouseEvent mouse) {
        if (mouse.getButton() == MouseEvent.BUTTON1) //
            move_light();
        super.refresh();
    }

    public void mouseReleased(MouseEvent mouse) {
    }

    public void mouseEntered(MouseEvent mouse) {
    }

    public void mouseExited(MouseEvent mouse) {
    }

    public void dispose(GLAutoDrawable arg0) {
         
    }

}
