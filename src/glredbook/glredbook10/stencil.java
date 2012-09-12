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
 * This program demonstrates use of the stencil buffer for masking
 * nonrectangular regions. Whenever the window is redrawn, a value of 1 is drawn
 * into a diamond-shaped region in the stencil buffer. Elsewhere in the stencil
 * buffer, the value is 0. Then a blue sphere is drawn where the stencil value
 * is 1, and yellow torii are drawn where the stencil value is not 1.
 * 
 * @author Kiet Le (Java port) Ported to JOGL 2.x by Claudio Eduardo Goes
 */
public class stencil//
        extends GLSkeleton<GLJPanel>
        implements GLEventListener, KeyListener {
    private GLU glu;
    private GLUT glut;

    private static final int YELLOWMAT = 1;
    private static final int BLUEMAT = 2;

    @Override
    protected GLJPanel createDrawable() {
        GLCapabilities caps = new GLCapabilities(null);
        caps.setStencilBits(8);
        //
        GLJPanel panel = new GLJPanel(caps);
        panel.addGLEventListener(this);
        panel.addKeyListener(this);
        return panel;
    }

    public static void main(String[] args) {
        stencil demo = new stencil();
        //
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("stencil");
        frame.setSize(400, 400);
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
        float yellow_diffuse[] = new float[] { 0.7f, 0.7f, 0.0f, 1.0f };
        float yellow_specular[] = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
        float blue_diffuse[] = new float[] { 0.1f, 0.1f, 0.7f, 1.0f };
        float blue_specular[] = new float[] { 0.1f, 1.0f, 1.0f, 1.0f };
        float position_one[] = new float[] { 1.0f, 1.0f, 1.0f, 0.0f };
        //
        gl.glNewList(YELLOWMAT, GL2.GL_COMPILE);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_DIFFUSE, yellow_diffuse, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, yellow_specular, 0);
        gl.glMaterialf(GL.GL_FRONT, GL2.GL_SHININESS, 64.0f);
        gl.glEndList();

        gl.glNewList(BLUEMAT, GL2.GL_COMPILE);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_DIFFUSE, blue_diffuse, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, blue_specular, 0);
        gl.glMaterialf(GL.GL_FRONT, GL2.GL_SHININESS, 45.0f);
        gl.glEndList();

        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, position_one, 0);

        gl.glEnable(GL2.GL_LIGHT0);
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glDepthFunc(GL.GL_LESS);
        gl.glEnable(GL.GL_DEPTH_TEST);

        gl.glClearStencil(0x0);
        gl.glEnable(GL2.GL_STENCIL_TEST);

    }

    /*
     * Draw a sphere in a diamond-shaped section in the middle of a window with
     * 2 torii.
     */
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        /* draw blue sphere where the stencil is 1 */
        gl.glStencilFunc(GL.GL_EQUAL, 0x1, 0x1);
        gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_KEEP);
        gl.glCallList(BLUEMAT);
        glut.glutSolidSphere(0.5, 20, 20);

        /* draw the tori where the stencil is not 1 */
        gl.glStencilFunc(GL.GL_NOTEQUAL, 0x1, 0x1);
        gl.glPushMatrix();
        gl.glRotatef(45.0f, 0.0f, 0.0f, 1.0f);
        gl.glRotatef(45.0f, 0.0f, 1.0f, 0.0f);
        gl.glCallList(YELLOWMAT);
        glut.glutSolidTorus(0.275, 0.85, 20, 20);
        gl.glPushMatrix();
        gl.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
        glut.glutSolidTorus(0.275, 0.85, 20, 20);
        gl.glPopMatrix();
        gl.glPopMatrix();
    }

    /*
     * Whenever the window is reshaped, redefine the coordinate system and
     * redraw the stencil area.
     */
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glViewport(0, 0, w, h);
        gl.glClear(GL2.GL_STENCIL_BUFFER_BIT);
        /* create a diamond shaped stencil area */
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(-3.0, 3.0, -3.0, 3.0, -1.0, 1.0);
        // if (w <= h) glu.gluOrtho2D(-3.0, 3.0, -3.0 * (float) h / (float) w,
        // 3.0 * (float) h / (float) w);
        // else glu.gluOrtho2D(-3.0 * (float) w / (float) h, //
        // 3.0 * (float) w / (float) h, -3.0, 3.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();

        gl.glStencilFunc(GL.GL_ALWAYS, 0x1, 0x1);
        gl.glStencilOp(GL2.GL_REPLACE, GL2.GL_REPLACE, GL2.GL_REPLACE);
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex2f(-1.0f, 0.0f);
        gl.glVertex2f(0.0f, 1.0f);
        gl.glVertex2f(1.0f, 0.0f);
        gl.glVertex2f(0.0f, -1.0f);
        gl.glEnd();

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45.0, (float) w / (float) h, 3.0, 7.0);
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
        default:
            break;
        }
    }

    public void keyReleased(KeyEvent key) {
    }

    public void dispose(GLAutoDrawable arg0) {
         
    }

}
