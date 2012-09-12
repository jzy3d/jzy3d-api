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
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;

import com.jogamp.opengl.util.gl2.GLUT;

/**
 * This program draws filled polygons with antialiased edges. The special
 * GL_SRC_ALPHA_SATURATE blending function is used. Pressing the left mouse
 * button turns the antialiasing on and off.
 * 
 * @author Kiet Le (Java port) Ported to JOGL 2.x by Claudio Eduardo Goes
 */
public class antipoly //
        extends GLSkeleton<GLJPanel>
        implements GLEventListener, KeyListener, MouseListener {
    private GLU glu;
    private GLUT glut;
    private boolean polySmooth;


    @Override
    protected GLJPanel createDrawable() {
        GLCapabilities caps = new GLCapabilities(null);
        //
        GLJPanel panel = new GLJPanel(caps);
        panel.addGLEventListener(this);
        panel.addKeyListener(this);
        panel.addMouseListener(this);
        return panel;
    }

    public static void main(String[] args) {

        antipoly demo = new antipoly();

        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("antipoly");
        frame.setSize(200, 200);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().add(demo.drawable);
        frame.setVisible(true);
        demo.drawable.requestFocusInWindow();
    }

    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        glu = new GLU();
        glut = new GLUT();
        //
        float mat_ambient[] = { 0.0f, 0.0f, 0.0f, 1.00f };
        float mat_specular[] = { 1.0f, 1.0f, 1.0f, 1.00f };
        float mat_shininess[] = { 15.0f };

        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, mat_ambient, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, mat_specular, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SHININESS, mat_shininess, 0);

        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);
        gl.glEnable(GL.GL_BLEND);
        gl.glCullFace(GL.GL_BACK);
        gl.glEnable(GL.GL_CULL_FACE);
        gl.glEnable(GL2.GL_POLYGON_SMOOTH);
        polySmooth = true;

        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }

    /*
     * Note: polygons must be drawn from back to front for proper blending.
     */
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //
        float position[] = { 0.0f, 0.0f, 1.0f, 0.0f };
        float mat_cube1[] = { 0.75f, 0.75f, 0.0f, 1.0f };
        float mat_cube2[] = { 0.0f, 0.75f, 0.75f, 1.0f };

        if (polySmooth)
            gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        else
            gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        toggleSmooth(gl);

        gl.glPushMatrix();

        gl.glTranslatef(0.0f, 0.0f, -8.0f);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, position, 0);

        gl.glBlendFunc(GL2.GL_SRC_ALPHA_SATURATE, GL.GL_ONE);

        gl.glPushMatrix();
        gl.glRotatef(30.0f, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(60.0f, 0.0f, 1.0f, 0.0f);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_DIFFUSE, mat_cube1, 0);
        glut.glutSolidCube(1.0f);
        gl.glPopMatrix();

        gl.glTranslatef(0.0f, 0.0f, -2.0f);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_DIFFUSE, mat_cube2, 0);
        gl.glRotatef(30.0f, 0.0f, 1.0f, 0.0f);
        gl.glRotatef(60.0f, 1.0f, 0.0f, 0.0f);
        glut.glutSolidCube(1.0f);
        gl.glPopMatrix();

        gl.glFlush();
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(30.0, (float) w / (float) h, 1.0, 20.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }

    private void toggleSmooth(GL2 gl) {
        if (polySmooth) {
            gl.glDisable(GL.GL_BLEND);
            gl.glDisable(GL2.GL_POLYGON_SMOOTH);
            gl.glEnable(GL.GL_DEPTH_TEST);
        } else {
            gl.glEnable(GL.GL_BLEND);
            gl.glEnable(GL2.GL_POLYGON_SMOOTH);
            gl.glDisable(GL.GL_DEPTH_TEST);
        }
    }

    public void keyTyped(KeyEvent key) {
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
    }

    public void mouseClicked(MouseEvent mouse) {
    }

    public void mousePressed(MouseEvent mouse) {
        if (mouse.getButton() == MouseEvent.BUTTON1) {
            polySmooth = !polySmooth;
            System.out.println(polySmooth);
        }

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

}
