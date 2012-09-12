package glredbook11;

import glredbook10.GLSkeleton;

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
 * This program demonstrates the creation of a display list.
 * 
 * @author Kiet Le (Java port) Ported to JOGL 2.x by Claudio Eduardo Goes
 */
public class torus//
        extends GLSkeleton<GLJPanel>
        implements GLEventListener, KeyListener {
    private int theTorus;
    private KeyEvent key;
    private float angleX = 30f;
    private float angleY = 30f;

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

        torus demo = new torus();

        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("torus");
        frame.setSize(512, 512);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().add(demo.drawable);
        frame.setVisible(true);
        demo.drawable.requestFocusInWindow();
    }

    private void drawTorus(GL2 gl, int numc, int numt) {
        double s, t, x, y, z, twopi = 2 * Math.PI;
        for (int i = 0; i < numc; i++) {
            gl.glBegin(GL2.GL_QUAD_STRIP);
            for (int j = 0; j <= numt; j++) {
                for (int k = 1; k >= 0; k--) {
                    s = (i + k) % numc + 0.5;
                    t = j % numt;
                    x = (1 + 0.1 * Math.cos(s * twopi / numc))
                            * Math.cos(t * twopi / numt);
                    y = (1 + 0.1 * Math.cos(s * twopi / numc))
                            * Math.sin(t * twopi / numt);
                    z = 0.1 * Math.sin(s * twopi / numc);
                    gl.glVertex3d(x, y, z);
                }// k
            }// j
            gl.glEnd();
        }// i
    }

    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //
        theTorus = gl.glGenLists(1);
        gl.glNewList(theTorus, GL2.GL_COMPILE);
        drawTorus(gl, 8, 25);
        gl.glEndList();
        //
        gl.glShadeModel(GL2.GL_FLAT);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }

    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        GLU glu = new GLU();
        //
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        //
        if (key != null) {
            switch (key.getKeyCode()) {
            case KeyEvent.VK_X:
                gl.glRotated(angleX, 1f, 0f, 0f);
                break;
            case KeyEvent.VK_Y:
                gl.glRotated(angleY, 0f, 1f, 0f);
                break;
            case KeyEvent.VK_I:
                gl.glLoadIdentity();
                glu.gluLookAt(0, 0, 10, 0, 0, 0, 0, 1, 0);
                break;
            default:
                break;
            }
        }

        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glCallList(theTorus);
        gl.glPopMatrix();
        gl.glFlush();
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width,
            int height) {
        GL2 gl = drawable.getGL().getGL2();
        GLU glu = new GLU();
        //
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(30, (float) width / (float) height, 1.0, 100.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
        glu.gluLookAt(0, 0, 10, 0, 0, 0, 0, 1, 0);
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }

    public void keyTyped(KeyEvent key) {
    }

    public void keyPressed(KeyEvent key) {
        this.key = key;
        switch (key.getKeyCode()) {
        case KeyEvent.VK_ESCAPE:
            System.exit(0);
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
