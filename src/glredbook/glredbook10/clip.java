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
 * This program demonstrates arbitrary clipping planes.
 * 
 * @author Kiet Le (Java port) Ported to JOGL 2.x by Claudio Eduardo Goes
 */
public class clip//
        extends GLSkeleton<GLJPanel>
        implements GLEventListener, KeyListener {
    private GLU glu;
    private GLUT glut;

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

        clip demo = new clip();

        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("clip");
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
        gl.glClearColor(0, 0, 0, 0);
        gl.glShadeModel(GL2.GL_FLAT);
    }

    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //  
        double eqn[] = { 0.0, 1.0, 0.0, 0.0 };
        double eqn2[] = { 1.0, 0.0, 0.0, 0.0 };

        gl.glClear(GL.GL_COLOR_BUFFER_BIT);

        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glPushMatrix();
        gl.glTranslatef(0.0f, 0.0f, -5.0f);

        /* clip lower half -- y < 0 */
        gl.glClipPlane(GL2.GL_CLIP_PLANE0, eqn, 0);
        gl.glEnable(GL2.GL_CLIP_PLANE0);
        /* clip left half -- x < 0 */
        gl.glClipPlane(GL2.GL_CLIP_PLANE1, eqn2, 0);
        gl.glEnable(GL2.GL_CLIP_PLANE1);

        gl.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
        glut.glutWireSphere(1.0, 20, 16);
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
        // TODO Auto-generated method stub
    }

    public void dispose(GLAutoDrawable arg0) {
         
    }
}
