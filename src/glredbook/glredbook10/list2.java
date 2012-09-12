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
 * This program demonstrates glGenList() and glPushAttrib(). The matrix and
 * color are restored, before the line is drawn.
 * 
 * @author Kiet Le (Java port) Ported to JOGL 2.x by Claudio Eduardo Goes
 */
public class list2//
        extends GLSkeleton<GLJPanel>
        implements GLEventListener, KeyListener {
    private GLU glu; 
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
        list2 demo = new list2();

        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("list2");
        frame.setSize(400, 50);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().add(demo.drawable);
        frame.setVisible(true);
        demo.drawable.requestFocusInWindow();
    }

    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        glu = new GLU();
        //
        float color_vector[] = { 1.0f, 0.0f, 0.0f };

        listName = gl.glGenLists(1);
        gl.glNewList(listName, GL2.GL_COMPILE);
        gl.glPushAttrib(GL2.GL_CURRENT_BIT);
        gl.glColor3fv(color_vector, 0);
        gl.glBegin(GL2.GL_TRIANGLES);
        gl.glVertex2f(0.0f, 0.0f);
        gl.glVertex2f(1.0f, 0.0f);
        gl.glVertex2f(0.0f, 1.0f);
        gl.glEnd();
        gl.glTranslatef(1.5f, 0.0f, 0.0f);
        gl.glPopAttrib();
        gl.glEndList();
        gl.glShadeModel(GL2.GL_FLAT);
    }

    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //
        float new_color[] = { 0.0f, 1.0f, 0.0f };

        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glColor3fv(new_color, 0);
        gl.glPushMatrix();
        for (int i = 0; i < 10; i++)
            gl.glCallList(listName);
        gl.glPopMatrix();
        drawLine(gl);
        gl.glFlush();
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        if (w <= h)
            glu.gluOrtho2D(0.0, 2.0, -0.5 * (float) h / (float) w, 1.5
                    * (float) h / (float) w);
        else
            glu.gluOrtho2D(0.0, 2.0 * (float) w / (float) h, -0.5, 1.5);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }

    private void drawLine(GL2 gl) {
        gl.glBegin(GL.GL_LINES);
        gl.glVertex2f(0.0f, 0.5f);
        gl.glVertex2f(15.0f, 0.5f);
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
