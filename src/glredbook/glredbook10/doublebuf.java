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
import javax.swing.JFrame;

import com.jogamp.opengl.util.FPSAnimator;

/**
 * This is a simple double buffered program. Pressing the left mouse button
 * rotates the rectangle. Pressing the middle mouse button stops the rotation.
 * 
 * @author Kiet Le (Java port) Ported to JOGL 2.x by Claudio Eduardo Goes
 */
public class doublebuf//
        extends GLSkeleton<GLJPanel>
        implements GLEventListener, KeyListener, MouseListener {
    private float spin = 0f, spinDelta = 0f; 

    @Override
    protected GLJPanel createDrawable() {
        GLCapabilities caps = new GLCapabilities(null);
        //
        GLJPanel panel = new GLJPanel(caps);
        panel.setDoubleBuffered(true);
        panel.addGLEventListener(this);
        panel.addKeyListener(this);
        panel.addMouseListener(this);
        return panel;
    }

    public static void main(String[] args) {

        doublebuf demo = new doublebuf();

        FPSAnimator animator = new FPSAnimator(demo.drawable, 60);
        demo.setAnimator(animator);
        //
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("doublebuf");
        frame.setSize(512, 256);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().add(demo.drawable);
        frame.setVisible(true);
        demo.drawable.requestFocusInWindow();
        animator.start();
    }

    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glShadeModel(GL2.GL_FLAT);
    }

    public synchronized void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glPushMatrix();
        gl.glRotatef(spin, 0.0f, 0.0f, 1.0f);
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glRectf(-25.0f, -25.0f, 25.0f, 25.0f);
        gl.glPopMatrix();

        gl.glFlush();

        spinDisplay();
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL2 gl = drawable.getGL().getGL2();

        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        float aspect = 0;
        if (w <= h) {
            aspect = (float) h / (float) w;
            gl.glOrtho(-50.0, 50.0, -50.0 * aspect, 50.0 * aspect, //
                    -1.0, 1.0);
        } else {
            aspect = (float) w / (float) h;
            gl.glOrtho(-50.0 * aspect, 50.0 * aspect, -50.0, 50.0, //
                    -1.0, 1.0);
        }
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }

    private void spinDisplay() {
        spin = spin + spinDelta;
        if (spin > 360f)
            spin = spin - 360;
    }

    public void keyTyped(KeyEvent key) {
    }

    public void keyPressed(KeyEvent key) {
        switch (key.getKeyCode()) {
        case KeyEvent.VK_ESCAPE:
            super.runExit();
        default:
            break;
        }
    }

    public void keyReleased(KeyEvent key) {
    }

    public void mouseClicked(MouseEvent key) {
    }

    public void mousePressed(MouseEvent mouse) {
        switch (mouse.getButton()) {
        case MouseEvent.BUTTON1:
            spinDelta = 2f;
            break;
        case MouseEvent.BUTTON2:
        case MouseEvent.BUTTON3:
            spinDelta = 0f;
            break;
        }//

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

}//
