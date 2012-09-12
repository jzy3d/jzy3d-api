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
import javax.swing.JFrame;
 

/**
 * This is a simple, introductory OpenGL program in Java using the
 * javax.media.opengl extension library.
 * 
 * @author Kiet Le (Java port) Ported to JOGL 2.x by Claudio Eduardo Goes
 */
public class hello //
        extends GLSkeleton<GLJPanel>
        implements GLEventListener, KeyListener {

    @Override
    protected GLJPanel createDrawable() {
        // 0. optionally set additional canvas capabilities
        GLCapabilities caps = new GLCapabilities(null);
        caps.setSampleBuffers(true);// enable sample buffers for aliasing
        caps.setNumSamples(2);
        //
        // 1. create canvas with the desired optional capabilities above
        GLJPanel panel = new GLJPanel(caps);
        panel.addGLEventListener(this);
        panel.addKeyListener(this);
        return panel;
    }

    public static void main(String[] args) {
        hello demo = new hello();

        /*
         * 2. Declare initial window size, position, and set frame's close
         * behavior. Open window with "hello" in its title bar. Call
         * initialization routines. Register callback function to display
         * graphics. Enter main loop and process events.
         */
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("hello");
        frame.setSize(400, 400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 3. add canvas to frame, set it visible, and have input focus on the
        // canvas
        frame.getContentPane().add(demo.drawable);
        frame.setVisible(true);
        demo.drawable.requestFocusInWindow();
    }

    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        /* select clearing color (background) color */
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        /* initialize viewing values */
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(0.0, 1.0, 0.0, 1.0, -1.0, 1.0);
    }

    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //
        /* clear all pixels */
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);

        /*
         * draw white polygon (rectangle) with corners at (0.25, 0.25, 0.0) and
         * (0.75, 0.75, 0.0)
         */
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glBegin(GL2.GL_POLYGON);
        gl.glVertex3f(0.25f, 0.25f, 0.0f);
        gl.glVertex3f(0.75f, 0.25f, 0.0f);
        gl.glVertex3f(0.75f, 0.75f, 0.0f);
        gl.glVertex3f(0.25f, 0.75f, 0.0f);
        gl.glEnd();

        /*
         * don't wait! start processing buffered OpenGL routines
         */
        gl.glFlush();

    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width,
            int height) {  
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }

    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub

    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
            System.exit(0);

    }

    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub

    }

    public void dispose(GLAutoDrawable arg0) {
         
    }

}
