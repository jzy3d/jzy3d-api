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
 * This program demonstrates using display lists to call different line
 * stipples.
 * 
 * @author Kiet Le (Java port) Ported to JOGL 2.x by Claudio Eduardo Goes
 */
public class linelist
        extends GLSkeleton<GLJPanel>
        implements GLEventListener, KeyListener {
    private int offset;

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
        linelist demo  = new linelist();
         
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("linelist");
        frame.setSize(400, 150);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().add(demo.drawable);
        frame.setVisible(true);
        demo.drawable.requestFocusInWindow();
    }

    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //
        /* background to be cleared to black */
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glShadeModel(GL2.GL_FLAT);

        offset = gl.glGenLists(3);
        gl.glNewList(offset, GL2.GL_COMPILE);
        gl.glDisable(GL2.GL_LINE_STIPPLE);
        gl.glEndList();
        gl.glNewList(offset + 1, GL2.GL_COMPILE);
        gl.glEnable(GL2.GL_LINE_STIPPLE);
        gl.glLineStipple(1, (short) 0x0F0F);
        gl.glEndList();
        gl.glNewList(offset + 2, GL2.GL_COMPILE);
        gl.glEnable(GL2.GL_LINE_STIPPLE);
        gl.glLineStipple(1, (short) 0x1111);
        gl.glEndList();
    }

    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);

        /* draw all lines in white */
        gl.glColor3f(1.0f, 1.0f, 1.0f);

        gl.glCallList(offset);
        drawOneLine(gl, 50.0f, 125.0f, 350.0f, 125.0f);
        gl.glCallList(offset + 1);
        drawOneLine(gl, 50.0f, 100.0f, 350.0f, 100.0f);
        gl.glCallList(offset + 2);
        drawOneLine(gl, 50.0f, 75.0f, 350.0f, 75.0f);
        gl.glCallList(offset + 1);
        drawOneLine(gl, 50.0f, 50.0f, 350.f, 50.0f);
        gl.glCallList(offset);
        drawOneLine(gl, 50.0f, 25.0f, 350.0f, 25.0f);
        gl.glFlush();
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(0, 400, 0, 200, -1, 1);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }

    private void drawOneLine(GL2 gl, float x1, float y1, float x2, float y2) {
        gl.glBegin(GL.GL_LINES);
        gl.glVertex2f(x1, y1);
        gl.glVertex2f(x2, y2);
        gl.glEnd();
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
