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
 * This program demonstrates use of OpenGL feedback. First, a lighting
 * environment is set up and a few lines are drawn. Then feedback mode is
 * entered, and the same lines are drawn. The results in the feedback buffer are
 * printed.
 * 
 * @author Kiet Le (Java port) Ported to JOGL 2.x by Claudio Eduardo Goes
 */
public class feedback//
        extends GLSkeleton<GLJPanel>
        implements GLEventListener, KeyListener {

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
        feedback demo = new feedback();

        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("feedback");
        frame.setSize(200, 200);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().add(demo.drawable);
        frame.setVisible(true);
        demo.drawable.requestFocusInWindow();
    }

    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);
    }

    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //
        float feedBuffer[] = new float[1024];
        FloatBuffer feedBuf = GLBuffers.newDirectFloatBuffer(1024);
        int size;

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(0.0, 100.0, 0.0, 100.0, 0.0, 1.0);

        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        drawGeometry(gl, GL2.GL_RENDER);

        gl.glFeedbackBuffer(1024, GL2.GL_3D_COLOR, feedBuf);
        gl.glRenderMode(GL2.GL_FEEDBACK);
        drawGeometry(gl, GL2.GL_FEEDBACK);

        size = gl.glRenderMode(GL2.GL_RENDER);
        feedBuf.get(feedBuffer);
        printBuffer(gl, size, feedBuffer);
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width,
            int height) {
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }

    /*
     * Draw a few lines and two points, one of which will be clipped. If in
     * feedback mode, a passthrough token is issued between the each primitive.
     */
    void drawGeometry(GL2 gl, int mode) {
        gl.glBegin(GL.GL_LINE_STRIP);
        gl.glNormal3f(0.0f, 0.0f, 1.0f);
        gl.glVertex3f(30.0f, 30.0f, 0.0f);
        gl.glVertex3f(50.0f, 60.0f, 0.0f);
        gl.glVertex3f(70.0f, 40.0f, 0.0f);
        gl.glEnd();
        if (mode == GL2.GL_FEEDBACK)
            gl.glPassThrough(1.0f);
        gl.glBegin(GL.GL_POINTS);
        gl.glVertex3f(-100.0f, -100.0f, -100.0f); /* will be clipped */
        gl.glEnd();
        if (mode == GL2.GL_FEEDBACK)
            gl.glPassThrough(2.0f);
        gl.glBegin(GL.GL_POINTS);
        gl.glNormal3f(0.0f, 0.0f, 1.0f);
        gl.glVertex3f(50.0f, 50.0f, 0.0f);
        gl.glEnd();
    }

    /* Write contents of one vertex to stdout. */
    void print3DcolorVertex(int size, int count, float[] buffer) {
        int i;

        System.out.println("  ");
        for (i = 0; i < 7; i++) {
            System.out.println(" " + buffer[size - count]);
            count = count - 1;
        }
        System.out.println();
    }

    /* Write contents of entire buffer. (Parse tokens!) */
    private void printBuffer(GL2 gl, int size, float[] buffer) {
        int count;
        float token;

        count = size;
        while (count > 0) {
            token = buffer[size - count];
            count--;
            if (token == GL2.GL_PASS_THROUGH_TOKEN) {
                System.out.println("GL.GL_PASS_THROUGH_TOKEN");
                System.out.println("\t " + buffer[size - count]);
                count--;
            } else if (token == GL2.GL_POINT_TOKEN) {
                System.out.println("GL.GL_POINT_TOKEN");
                print3DcolorVertex(size, count, buffer);
            } else if (token == GL2.GL_LINE_TOKEN) {
                System.out.println("GL.GL_LINE_TOKEN ");
                print3DcolorVertex(size, count, buffer);
                print3DcolorVertex(size, count, buffer);
            } else if (token == GL2.GL_LINE_RESET_TOKEN) {
                System.out.println("GL.GL_LINE_RESET_TOKEN ");
                print3DcolorVertex(size, count, buffer);
                print3DcolorVertex(size, count, buffer);
            }
        }
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
