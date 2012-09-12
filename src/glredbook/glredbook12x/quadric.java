package glredbook12x;

import glredbook10.GLSkeleton;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLJPanel;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;
import javax.swing.JFrame;

/**
 * This program demonstrates the use of some of the gluQuadric* routines.
 * Quadric objects are created with some quadric properties and the callback
 * routine to handle errors. Note that the cylinder has no top or bottom and the
 * circle has a hole in it.
 * 
 * @author Kiet Le (Java port) Ported to JOGL 2.x by Claudio Eduardo Goes
 */

public class quadric //
        extends GLSkeleton<GLJPanel>
        implements GLEventListener, KeyListener {
    private GLU glu;

    private int startList;

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
        GLCapabilities caps = new GLCapabilities(null);
        GLJPanel canvas = new GLJPanel(caps);

        quadric demo = new quadric();
        canvas.addGLEventListener(demo);
        if (demo instanceof KeyListener)
            canvas.addKeyListener(demo);
        // explicit cast for class not impl'ing listeners
        // to make it compile,
        if (demo instanceof MouseListener)
            canvas.addMouseListener((MouseListener) demo);
        if (demo instanceof MouseMotionListener)
            canvas.addMouseMotionListener((MouseMotionListener) demo);
        if (demo instanceof MouseWheelListener)
            canvas.addMouseWheelListener((MouseWheelListener) demo);

        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("quadric");
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().add(canvas);
        frame.setVisible(true);
        canvas.requestFocusInWindow();
    }

    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        glu = new GLU();
        //
        GLUquadric qobj;
        float mat_ambient[] = { 0.5f, 0.5f, 0.5f, 1.0f };
        float mat_specular[] = { 1.0f, 1.0f, 1.0f, 1.0f };
        float mat_shininess[] = { 50.0f };
        float light_position[] = { 1.0f, 1.0f, 1.0f, 0.0f };
        float model_ambient[] = { 0.5f, 0.5f, 0.5f, 1.0f };

        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, mat_ambient, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, mat_specular, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SHININESS, mat_shininess, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, light_position, 0);
        gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, model_ambient, 0);

        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);
        gl.glEnable(GL.GL_DEPTH_TEST);

        /*
         * Create 4 display lists, each with a different quadric object.
         * Different drawing styles and surface normal specifications are
         * demonstrated.
         */
        startList = gl.glGenLists(4);
        qobj = glu.gluNewQuadric();

        /*
         * glu.gluQuadricCallback(qobj, GLU.GLU_ERROR, errorCallback); <br>
         * Quadric call backs have yet been implemented in JOGL. But this
         * program still work.
         */
        glu.gluQuadricDrawStyle(qobj, GLU.GLU_FILL); /* smooth shaded */
        glu.gluQuadricNormals(qobj, GLU.GLU_SMOOTH);
        gl.glNewList(startList, GL2.GL_COMPILE);
        glu.gluSphere(qobj, 0.75, 15, 10);
        gl.glEndList();

        glu.gluQuadricDrawStyle(qobj, GLU.GLU_FILL); /* flat shaded */
        glu.gluQuadricNormals(qobj, GLU.GLU_FLAT);
        gl.glNewList(startList + 1, GL2.GL_COMPILE);
        glu.gluCylinder(qobj, 0.5, 0.3, 1.0, 15, 5);
        gl.glEndList();

        glu.gluQuadricDrawStyle(qobj, GLU.GLU_LINE); /*
                                                         * all polygons
                                                         * wireframe
                                                         */
        glu.gluQuadricNormals(qobj, GLU.GLU_NONE);
        gl.glNewList(startList + 2, GL2.GL_COMPILE);
        glu.gluDisk(qobj, 0.25, 1.0, 20, 4);
        gl.glEndList();

        glu.gluQuadricDrawStyle(qobj, GLU.GLU_SILHOUETTE); /* boundary only */
        glu.gluQuadricNormals(qobj, GLU.GLU_NONE);
        gl.glNewList(startList + 3, GL2.GL_COMPILE);
        glu.gluPartialDisk(qobj, 0.0, 1.0, 20, 4, 0.0, 225.0);
        gl.glEndList();

    }

    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glPushMatrix();

        gl.glEnable(GL2.GL_LIGHTING);
        gl.glShadeModel(GL2.GL_SMOOTH);
        gl.glTranslatef(-1.0f, -1.0f, 0.0f);
        gl.glCallList(startList);

        gl.glShadeModel(GL2.GL_FLAT);
        gl.glTranslatef(0.0f, 2.0f, 0.0f);
        gl.glPushMatrix();
        gl.glRotatef(300.0f, 1.0f, 0.0f, 0.0f);
        gl.glCallList(startList + 1);
        gl.glPopMatrix();

        gl.glDisable(GL2.GL_LIGHTING);
        gl.glColor3f(0.0f, 1.0f, 1.0f);
        gl.glTranslatef(2.0f, -2.0f, 0.0f);
        gl.glCallList(startList + 2);

        gl.glColor3f(1.0f, 1.0f, 0.0f);
        gl.glTranslatef(0.0f, 2.0f, 0.0f);
        gl.glCallList(startList + 3);

        gl.glPopMatrix();
        gl.glFlush();

    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        if (w <= h)
            gl.glOrtho(-2.5, 2.5, -2.5 * (float) h / (float) w, 2.5 * (float) h
                    / (float) w, -10.0, 10.0);
        else
            gl.glOrtho(-2.5 * (float) w / (float) h, 2.5 * (float) w
                    / (float) h, -2.5, 2.5, -10.0, 10.0);
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
            System.out.println("nothin pressed.");
            break;
        }
    }

    public void keyReleased(KeyEvent key) {
    }

    public void dispose(GLAutoDrawable arg0) {
         
    }

}
