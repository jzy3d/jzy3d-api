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
 * This program demonstrates the same scene as dof.c, but without use of the
 * accumulation buffer, so everything is in focus.
 * 
 * @author Kiet Le (Java port) Ported to JOGL 2.x by Claudio Eduardo Goes
 */
public class dofnot//
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

        dofnot demo = new dofnot();

        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("dofnot");
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
        float ambient[] = { 0.0f, 0.0f, 0.0f, 1.0f };
        float diffuse[] = { 1.0f, 1.0f, 1.0f, 1.0f };
        float specular[] = { 1.0f, 1.0f, 1.0f, 1.0f };
        float position[] = { 0.0f, 3.0f, 3.0f, 0.0f };

        float lmodel_ambient[] = { 0.2f, 0.2f, 0.2f, 1.0f };
        float local_view[] = { 0.0f };

        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDepthFunc(GL.GL_LESS);

        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, ambient, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, diffuse, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, position, 0);

        gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, lmodel_ambient, 0);
        gl.glLightModelfv(GL2.GL_LIGHT_MODEL_LOCAL_VIEWER, local_view, 0);

        gl.glFrontFace(GL.GL_CW);
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);
        gl.glEnable(GL2.GL_AUTO_NORMAL);
        gl.glEnable(GL2.GL_NORMALIZE);

        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }

    /*
     * display() draws 5 teapots into the accumulation buffer several times;
     * each time with a jittered perspective. The focal point is at z = 5.0, so
     * the gold teapot will stay in focus. The amount of jitter is adjusted by
     * the magnitude of the accPerspective() jitter; in this example, 0.33. In
     * this example, the teapots are drawn 8 times. See jitter.h
     */
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        gl.glPushMatrix();
        /* ruby, gold, silver, emerald, and cyan teapots */
        renderTeapot(gl, -1.1f, -0.5f, -4.5f, 0.1745f, 0.01175f, 0.01175f,
                0.61424f, 0.04136f, 0.04136f, 0.727811f, 0.626959f, 0.626959f,
                0.6f);
        renderTeapot(gl, -0.5f, -0.5f, -5.0f, 0.24725f, 0.1995f, 0.0745f,
                0.75164f, 0.60648f, 0.22648f, 0.628281f, 0.555802f, 0.366065f,
                0.4f);
        renderTeapot(gl, 0.2f, -0.5f, -5.5f, 0.19225f, 0.19225f, 0.19225f,
                0.50754f, 0.50754f, 0.50754f, 0.508273f, 0.508273f, 0.508273f,
                0.4f);
        renderTeapot(gl, 1.0f, -0.5f, -6.0f, 0.0215f, 0.1745f, 0.0215f,
                0.07568f, 0.61424f, 0.07568f, 0.633f, 0.727811f, 0.633f, 0.6f);
        renderTeapot(gl, 1.8f, -0.5f, -6.5f, 0.0f, 0.1f, 0.06f, 0.0f,
                0.5098039f, 0.50980392f, 0.50196078f, 0.50196078f, 0.50196078f,
                .25f);

        gl.glPopMatrix();
        gl.glFlush();
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45.0, (float) w / (float) h, 1.0, 15.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }

    private void renderTeapot(GL2 gl, float x, float y, float z, float ambr,
            float ambg, float ambb, float difr, float difg, float difb,
            float specr, float specg, float specb, float shine) {
        float mat[] = new float[4];

        gl.glPushMatrix();
        gl.glTranslatef(x, y, z);
        mat[0] = ambr;
        mat[1] = ambg;
        mat[2] = ambb;
        mat[3] = 1.0f;
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, mat, 0);
        mat[0] = difr;
        mat[1] = difg;
        mat[2] = difb;
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_DIFFUSE, mat, 0);
        mat[0] = specr;
        mat[1] = specg;
        mat[2] = specb;
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, mat, 0);
        gl.glMaterialf(GL.GL_FRONT, GL2.GL_SHININESS, shine * 128.0f);
        glut.glutSolidTeapot(0.5);
        gl.glPopMatrix();
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
