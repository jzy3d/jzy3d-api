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
 *
 * @author Kiet Le (Java port) Ported to JOGL 2.x by Claudio Eduardo Goes
 */

public class aargb//
        extends GLSkeleton<GLJPanel>
        implements GLEventListener, KeyListener {
    private float rotAngle = 0f;
    private boolean rotate = false;

    @Override
    protected GLJPanel createDrawable() {
        GLCapabilities caps = new GLCapabilities(null);
        caps.setNumSamples(2);
        caps.setSampleBuffers(true);
        //
        GLJPanel panel = new GLJPanel(caps);
        panel.addGLEventListener(this);
        panel.addKeyListener(this);
        return panel;
    }

    public static void main(String[] args) {
        aargb demo = new aargb();

        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("aargb");
        frame.setSize(512, 512);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().add(demo.drawable);
        frame.setVisible(true);
        demo.drawable.requestFocusInWindow();
    }

    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //
        float values[] = new float[2];
        gl.glGetFloatv(GL2.GL_LINE_WIDTH_GRANULARITY, values, 0);
        System.out
                .println("GL.GL_LINE_WIDTH_GRANULARITY value is " + values[0]);
        gl.glGetFloatv(GL2.GL_LINE_WIDTH_RANGE, values, 0);
        System.out.println("GL.GL_LINE_WIDTH_RANGE values are " + values[0]
                + ", " + values[1]);
        gl.glEnable(GL2.GL_LINE_SMOOTH);
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glHint(GL2.GL_LINE_SMOOTH_HINT, GL.GL_DONT_CARE);
        gl.glLineWidth(1.5f);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }

    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glColor3f(0.0f, 1.0f, 0.0f);
        gl.glPushMatrix();
        gl.glRotatef(-rotAngle, 0.0f, 0.0f, 0.1f);
        gl.glBegin(GL.GL_LINES);
        gl.glVertex2f(-0.5f, 0.5f);
        gl.glVertex2f(0.5f, -0.5f);
        gl.glEnd();
        gl.glPopMatrix();
        gl.glColor3f(0.0f, 0.0f, 1.0f);
        gl.glPushMatrix();
        gl.glRotatef(rotAngle, 0.0f, 0.0f, 0.1f);
        gl.glBegin(GL.GL_LINES);
        gl.glVertex2f(0.5f, 0.5f);
        gl.glVertex2f(-0.5f, -0.5f);
        gl.glEnd();
        gl.glPopMatrix();
        gl.glFlush();
        if (rotate)
            rotAngle += 1f;
        if (rotAngle >= 360f)
            rotAngle = 0f;
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL2 gl = drawable.getGL().getGL2();
        GLU glu = new GLU();
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        if (w <= h) //
            glu.gluOrtho2D(-1.0, 1.0, -1.0 * (float) h / (float) w, //
                    1.0 * (float) h / (float) w);
        else
            glu.gluOrtho2D(-1.0 * (float) w / (float) h, //
                    1.0 * (float) w / (float) h, -1.0, 1.0);
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
        case KeyEvent.VK_R:
            rotate = !rotate;
            super.refresh();
        default:
            break;
        }
    }

    public void keyReleased(KeyEvent key) {
    }

    public void dispose(GLAutoDrawable arg0) {
         
    }
}
