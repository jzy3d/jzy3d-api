package glredbook10;

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
 * This program demonstrates the use of OpenGL modeling transformations. Four
 * triangles are drawn, each with a different transformation.
 * 
 * @author Kiet Le (Java port) Ported to JOGL 2.x by Claudio Eduardo Goes
 */
public class model//
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
        model demo = new model();
        //
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("model");
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().add(demo.drawable);
        frame.setVisible(true);
        demo.drawable.requestFocusInWindow();
    }

    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glClearColor(0f, 0f, 0f, 0f);
        gl.glShadeModel(GL2.GL_FLAT);
    }

    /*
     * Clear the screen. For each triangle, set the current color and modify the
     * modelview matrix.
     */
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glColor3f(1f, 1f, 1f);
        //
        gl.glLoadIdentity();
        drawTriangle(gl);
        //
        gl.glEnable(GL2.GL_LINE_STIPPLE);
        gl.glLineStipple(1, (short) 0xf0f0);
        gl.glLoadIdentity();
        gl.glTranslatef(-20f, 0f, 0f);
        drawTriangle(gl);
        gl.glLineStipple(1, (short) 0xF00F);
        gl.glLoadIdentity();
        gl.glScalef(1.5f, 0.5f, 1.0f);
        drawTriangle(gl);
        gl.glLineStipple(1, (short) 0x8888);
        gl.glLoadIdentity();
        gl.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);
        drawTriangle(gl);
        gl.glDisable(GL2.GL_LINE_STIPPLE);
        //
        gl.glFlush();
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        if (w <= h)
            gl.glOrtho(-50.0, 50.0, -50.0 * (float) h / (float) w, 50.0
                    * (float) h / (float) w, -1.0, 1.0);
        else
            gl.glOrtho(-50.0 * (float) w / (float) h, 50.0 * (float) w
                    / (float) h, -50.0, 50.0, -1.0, 1.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }

    private void drawTriangle(GL2 gl) {
        gl.glBegin(GL.GL_LINE_LOOP);
        gl.glVertex2f(0.0f, 25.0f);
        gl.glVertex2f(25.0f, -25.0f);
        gl.glVertex2f(-25.0f, -25.0f);
        gl.glEnd();
    }

    public void keyTyped(KeyEvent key) {
        // TODO Auto-generated method stub
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
