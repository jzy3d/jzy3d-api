package glredbook11;

import glredbook10.GLSkeleton;

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
 * This program demonstrates polygon offset to draw a shaded polygon and its
 * wireframe counterpart without ugly visual artifacts ("stitching").
 * 
 * @author Kiet Le (Java port) Ported to JOGL 2.x by Claudio Eduardo Goes
 */
public class polyoff//
        extends GLSkeleton<GLJPanel>
        implements GLEventListener, KeyListener, MouseListener {
    private GLU glu;
    private GLUT glut;

    private int list;
    private int spinx = 0;
    private int spiny = 0;
    private float tdist = 0.0f;
    private float polyfactor = 1.0f;
    private float polyunits = 1.0f;

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
        polyoff demo = new polyoff();

        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("polyoff");
        frame.setSize(400, 400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().add(demo.drawable);
        frame.setVisible(true);
        demo.drawable.requestFocusInWindow();
    }

    /*
     * specify initial properties create display list with sphere initialize
     * lighting and depth buffer
     */
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        glu = new GLU();
        glut = new GLUT();
        //

        float light_ambient[] = { 0.0f, 0.0f, 0.0f, 1.0f };
        float light_diffuse[] = { 1.0f, 1.0f, 1.0f, 1.0f };
        float light_specular[] = { 1.0f, 1.0f, 1.0f, 1.0f };
        float light_position[] = { 1.0f, 1.0f, 1.0f, 0.0f };

        float global_ambient[] = { 0.2f, 0.2f, 0.2f, 1.0f };

        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        list = gl.glGenLists(1);
        gl.glNewList(list, GL2.GL_COMPILE);
        glut.glutSolidSphere(1.0, 20, 12);
        gl.glEndList();

        gl.glEnable(GL.GL_DEPTH_TEST);

        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, light_ambient, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, light_diffuse, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, light_specular, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, light_position, 0);
        gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, global_ambient, 0);
    }

    /*
     * display() draws two spheres, one with a gray, diffuse material, the other
     * sphere with a magenta material with a specular highlight.
     */
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //
        /* clear all pixels */
        // double mat_ambient[] = { 0.8, 0.8, 0.8, 1.0 };
        // double mat_diffuse[] = { 1.0, 0.0, 0.5, 1.0 };
        // double mat_specular[] = { 1.0, 1.0, 1.0, 1.0 };
        float gray[] = { 0.8f, 0.8f, 0.8f, 1.0f };
        float black[] = { 0.0f, 0.0f, 0.0f, 1.0f };

        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glPushMatrix();
        gl.glTranslated(0.0, 0.0, tdist);
        gl.glRotated((double) spinx, 1.0, 0.0, 0.0);
        gl.glRotated((double) spiny, 0.0, 1.0, 0.0);

        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR & GL2.GL_DIFFUSE, gray, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, black, 0);
        gl.glMaterialf(GL.GL_FRONT, GL2.GL_SHININESS, 0.0f);
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);
        gl.glEnable(GL2.GL_POLYGON_OFFSET_FILL);
        gl.glPolygonOffset(polyfactor, polyunits);
        gl.glCallList(list);
        gl.glDisable(GL2.GL_POLYGON_OFFSET_FILL);

        gl.glDisable(GL2.GL_LIGHTING);
        gl.glDisable(GL2.GL_LIGHT0);
        gl.glColor3d(1.0, 1.0, 1.0);
        gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2.GL_LINE);
        gl.glCallList(list);
        gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2.GL_FILL);

        gl.glPopMatrix();
        gl.glFlush();

    }

    /* call when window is resized */
    public void reshape(GLAutoDrawable drawable, int x, int y, int width,
            int height) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45.0, (double) width / (double) height, 1.0, 10.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
        glu.gluLookAt(0.0, 0.0, 5.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }

    public void keyTyped(KeyEvent key) {

    }

    public void keyPressed(KeyEvent key) {
        switch (key.getKeyChar()) {
        case 't':
            if (tdist < 4.0)
                tdist = (tdist + 0.5f);
            break;
        case 'T':
            if (tdist > -5.0)
                tdist = (tdist - 0.5f);
            break;
        case 'F':
            polyfactor = polyfactor + 0.1f;
            System.out.println("polyfactor is " + polyfactor);
            break;
        case 'f':
            polyfactor = polyfactor - 0.1f;
            System.out.println("polyfactor is " + polyfactor);
            break;
        case 'U':
            polyunits = polyunits + 1.0f;
            System.out.println("polyunits is " + polyunits);
            break;
        case 'u':
            polyunits = polyunits - 1.0f;
            System.out.println("polyunits is " + polyunits);
            break;
        default:
            break;
        }
        super.refresh();
    }

    public void keyReleased(KeyEvent key) {
    }

    public void mouseClicked(MouseEvent mouse) {
    }

    public void mousePressed(MouseEvent mouse) {
        switch (mouse.getButton()) {
        case MouseEvent.BUTTON1:
            spinx = (spinx + 5) % 360;
            break;
        case MouseEvent.BUTTON2:
            spiny = (spiny + 5) % 360;
            break;

        default:
            break;
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
