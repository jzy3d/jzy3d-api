package glredbook10;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLJPanel;
import javax.swing.JFrame;

import com.jogamp.opengl.util.GLBuffers;

/**
 * This program uses evaluators to draw a Bezier curve.
 * 
 * @author Kiet Le (Java port) Ported to JOGL 2.x by Claudio Eduardo Goes
 */
public class bezcurve//
        extends GLSkeleton<GLJPanel>
        implements GLEventListener, KeyListener {
    private float ctrlpoints[][] = new float[][] { { -4.0f, -4.0f, 0.0f },
            { -2.0f, 4.0f, 0.0f }, { 2.0f, -4.0f, 0.0f }, { 4.0f, 4.0f, 0.0f } };
    private FloatBuffer ctrlpointBuf;

    // = GLBuffers.newDirectFloatBuffer(ctrlpoints[0].length * ctrlpoints.length);

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

        bezcurve demo = new bezcurve();

        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("bezcurve");
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().add(demo.drawable);
        frame.setVisible(true);
        demo.drawable.requestFocusInWindow();
    }

    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        // need to convert 2d array to buffer type
        ctrlpointBuf = GLBuffers.newDirectFloatBuffer(ctrlpoints[0].length
                * ctrlpoints.length);
        for (int i = 0; i < ctrlpoints.length; i++) {
            ctrlpointBuf.put(ctrlpoints[i]);
        }
        ctrlpointBuf.rewind();
        //
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glShadeModel(GL2.GL_FLAT);
        gl.glMap1f(GL2.GL_MAP1_VERTEX_3, 0.0f, 1.0f, 3, 4, ctrlpointBuf);
        gl.glEnable(GL2.GL_MAP1_VERTEX_3);
    }

    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glBegin(GL.GL_LINE_STRIP);
        for (int i = 0; i <= 30; i++) {
            gl.glEvalCoord1f((float) i / (float) 30.0);
        }
        gl.glEnd();
        /* The following code displays the control points as dots. */
        gl.glPointSize(5.0f);
        gl.glColor3f(1.0f, 1.0f, 0.0f);
        gl.glBegin(GL.GL_POINTS);
        for (int i = 0; i < 4; i++) {
            gl.glVertex3fv(ctrlpointBuf);
            ctrlpointBuf.position(i * 3);
        }
        gl.glEnd();
        gl.glFlush();
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        if (w <= h) //
            gl.glOrtho(-5.0, 5.0, -5.0 * (float) h / (float) w, //
                    5.0 * (float) h / (float) w, -5.0, 5.0);
        else
            gl.glOrtho(-5.0 * (float) w / (float) h, //
                    5.0 * (float) w / (float) h,//
                    -5.0, 5.0, -5.0, 5.0);
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

    public void dispose(GLAutoDrawable arg0) {
         
    }

}
