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


/**
 * This program demonstrates how to make and execute a display list. Note that
 * attributes, such as current color and matrix, are changed.
 * 
 * @author Kiet Le (Java port) Ported to JOGL 2.x by Claudio Eduardo Goes
 */
public class list//
        extends GLSkeleton<GLJPanel>
        implements GLEventListener, KeyListener {
    private int listName;

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
        list demo = new list();

        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("list");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 50);
        frame.setLocationRelativeTo(null);

        frame.getContentPane().add(demo.drawable);
        frame.setVisible(true);
        demo.drawable.requestFocusInWindow();
    }

    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //
        listName = gl.glGenLists(1);
        gl.glNewList(listName, GL2.GL_COMPILE);
        gl.glColor3f(1f, 0f, 0f);
        gl.glBegin(GL2.GL_TRIANGLES);
        gl.glVertex2f(0f, 0f);
        gl.glVertex2f(1f, 0f);
        gl.glVertex2f(0f, 1f);
        gl.glEnd();
        gl.glTranslatef(1.5f, 0f, 0f);
        gl.glEndList();
        gl.glShadeModel(GL2.GL_FLAT);
    }

    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glColor3f(0f, 1f, 0f);// has not affect
        for (int i = 0; i < 10; i++)
            gl.glCallList(listName);
        drawLine(gl); // * is this line green? NO! where is the line drawn?
        gl.glFlush();
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL2 gl = drawable.getGL().getGL2();
        GLU glu = new GLU();
        //
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        float aspect = 0f;
        if (w <= h) {
            aspect = (float) h / (float) w;
            glu.gluOrtho2D(0.0, 2.0, -0.5 * aspect, 1.5 * aspect);
        } else {
            aspect = (float) w / (float) h;
            glu.gluOrtho2D(0.0, 2.0 * aspect, -0.5, 1.5);
        }
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }

    private void drawLine(GL2 gl) {
        gl.glBegin(GL.GL_LINES);
        gl.glVertex2f(0f, 0.5f);
        gl.glVertex2f(15f, .05f);
        gl.glEnd();
    }

    public void keyTyped(KeyEvent arg0) {
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

    public void keyReleased(KeyEvent arg0) {
    }

    public void dispose(GLAutoDrawable arg0) {
         
    }
}
