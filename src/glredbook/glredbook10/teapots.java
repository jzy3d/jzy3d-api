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

import com.jogamp.opengl.util.gl2.GLUT;

/**
 * This program demonstrates lots of material properties. A single light source
 * illuminates the objects.
 * 
 * @author Kiet Le (Java port) Ported to JOGL 2.x by Claudio Eduardo Goes
 */
public class teapots//
        extends GLSkeleton<GLJPanel>
        implements GLEventListener, KeyListener {
    private GLUT glut;

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

        teapots demo = new teapots();
        //
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("teapots");
        frame.setSize(500, 600);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().add(demo.drawable);
        frame.setVisible(true);
        demo.drawable.requestFocusInWindow();

    }

    /*
     * Initialize depth buffer, projection matrix, light source, and lighting
     * model. Do not specify a material property here.
     */
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        glut = new GLUT();
        //
        float ambient[] = { 0.0f, 0.0f, 0.0f, 1.0f };
        float diffuse[] = { 1.0f, 1.0f, 1.0f, 1.0f };
        float specular[] = { 1.0f, 1.0f, 1.0f, 1.0f };
        float position[] = { 0.0f, 3.0f, 3.0f, 0.0f };

        float lmodel_ambient[] = { 0.2f, 0.2f, 0.2f, 1.0f };
        float local_view[] = { 0.0f };

        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, ambient, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, diffuse, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, position, 0);
        gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, lmodel_ambient, 0);
        gl.glLightModelfv(GL2.GL_LIGHT_MODEL_LOCAL_VIEWER, local_view, 0);

        gl.glFrontFace(GL.GL_CW);
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);
        gl.glEnable(GL2.GL_AUTO_NORMAL);
        gl.glEnable(GL2.GL_NORMALIZE);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDepthFunc(GL.GL_LESS);
    }

    /*
     * First column: emerald, jade, obsidian, pearl, ruby, turquoise 2nd column:
     * brass, bronze, chrome, copper, gold, silver 3rd column: black, cyan,
     * green, red, white, yellow plastic 4th column: black, cyan, green, red,
     * white, yellow rubber
     */
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        renderTeapot(gl, 2.0, 17.0, 0.0215, 0.1745, 0.0215, 0.07568, 0.61424,
                0.07568, 0.633, 0.727811, 0.633, 0.6);
        renderTeapot(gl, 2.0, 14.0, 0.135, 0.2225, 0.1575, 0.54, 0.89, 0.63,
                0.316228, 0.316228, 0.316228, 0.1);
        renderTeapot(gl, 2.0, 11.0, 0.05375, 0.05, 0.06625, 0.18275, 0.17,
                0.22525, 0.332741, 0.328634, 0.346435, 0.3);
        renderTeapot(gl, 2.0, 8.0, 0.25, 0.20725, 0.20725, 1, 0.829, 0.829,
                0.296648, 0.296648, 0.296648, 0.088);
        renderTeapot(gl, 2.0, 5.0, 0.1745, 0.01175, 0.01175, 0.61424, 0.04136,
                0.04136, 0.727811, 0.626959, 0.626959, 0.6);
        renderTeapot(gl, 2.0, 2.0, 0.1, 0.18725, 0.1745, 0.396, 0.74151,
                0.69102, 0.297254, 0.30829, 0.306678, 0.1);
        renderTeapot(gl, 6.0, 17.0, 0.329412, 0.223529, 0.027451, 0.780392,
                0.568627, 0.113725, 0.992157, 0.941176, 0.807843, 0.21794872);
        renderTeapot(gl, 6.0, 14.0, 0.2125, 0.1275, 0.054, 0.714, 0.4284,
                0.18144, 0.393548, 0.271906, 0.166721, 0.2);
        renderTeapot(gl, 6.0, 11.0, 0.25, 0.25, 0.25, 0.4, 0.4, 0.4, 0.774597,
                0.774597, 0.774597, 0.6);
        renderTeapot(gl, 6.0, 8.0, 0.19125, 0.0735, 0.0225, 0.7038, 0.27048,
                0.0828, 0.256777, 0.137622, 0.086014, 0.1);
        renderTeapot(gl, 6.0, 5.0, 0.24725, 0.1995, 0.0745, 0.75164, 0.60648,
                0.22648, 0.628281, 0.555802, 0.366065, 0.4);
        renderTeapot(gl, 6.0, 2.0, 0.19225, 0.19225, 0.19225, 0.50754, 0.50754,
                0.50754, 0.508273, 0.508273, 0.508273, 0.4);
        renderTeapot(gl, 10.0, 17.0, 0.0, 0.0, 0.0, 0.01, 0.01, 0.01, 0.50,
                0.50, 0.50, .25);
        renderTeapot(gl, 10.0, 14.0, 0.0, 0.1, 0.06, 0.0, 0.50980392,
                0.50980392, 0.50196078, 0.50196078, 0.50196078, .25);
        renderTeapot(gl, 10.0, 11.0, 0.0, 0.0, 0.0, 0.1, 0.35, 0.1, 0.45, 0.55,
                0.45, .25);
        renderTeapot(gl, 10.0, 8.0, 0.0, 0.0, 0.0, 0.5, 0.0, 0.0, 0.7, 0.6,
                0.6, .25);
        renderTeapot(gl, 10.0, 5.0, 0.0, 0.0, 0.0, 0.55, 0.55, 0.55, 0.70,
                0.70, 0.70, .25);
        renderTeapot(gl, 10.0, 2.0, 0.0, 0.0, 0.0, 0.5, 0.5, 0.0, 0.60, 0.60,
                0.50, .25);
        renderTeapot(gl, 14.0, 17.0, 0.02, 0.02, 0.02, 0.01, 0.01, 0.01, 0.4,
                0.4, 0.4, .078125);
        renderTeapot(gl, 14.0, 14.0, 0.0, 0.05, 0.05, 0.4, 0.5, 0.5, 0.04, 0.7,
                0.7, .078125);
        renderTeapot(gl, 14.0, 11.0, 0.0, 0.05, 0.0, 0.4, 0.5, 0.4, 0.04, 0.7,
                0.04, .078125);
        renderTeapot(gl, 14.0, 8.0, 0.05, 0.0, 0.0, 0.5, 0.4, 0.4, 0.7, 0.04,
                0.04, .078125);
        renderTeapot(gl, 14.0, 5.0, 0.05, 0.05, 0.05, 0.5, 0.5, 0.5, 0.7, 0.7,
                0.7, .078125);
        renderTeapot(gl, 14.0, 2.0, 0.05, 0.05, 0.0, 0.5, 0.5, 0.4, 0.7, 0.7,
                0.04, .078125);
        gl.glFlush();
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        if (w <= h)
            gl.glOrtho(0.0, 16.0, 0.0, 16.0 * (float) h / (float) w, -10.0,
                    10.0);
        else
            gl.glOrtho(0.0, 16.0 * (float) w / (float) h, 0.0, 16.0, -10.0,
                    10.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }

    /*
     * Move object into position. Use 3rd through 12th parameters to specify the
     * material property. Draw a teapot.
     */
    private void renderTeapot(GL2 gl, double x, double y, double ambr,
            double ambg, double ambb, double difr, double difg, double difb,
            double specr, double specg, double specb, double shine) {
        float mat[] = new float[4];

        gl.glPushMatrix();
        gl.glTranslatef((float) x, (float) y, 0.0f);
        mat[0] = (float) ambr;
        mat[1] = (float) ambg;
        mat[2] = (float) ambb;
        mat[3] = 1.0f;
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, mat, 0);
        mat[0] = (float) difr;
        mat[1] = (float) difg;
        mat[2] = (float) difb;
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_DIFFUSE, mat, 0);
        mat[0] = (float) specr;
        mat[1] = (float) specg;
        mat[2] = (float) specb;
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, mat, 0);
        gl.glMaterialf(GL.GL_FRONT, GL2.GL_SHININESS, (float) (shine * 128.0f));
        glut.glutSolidTeapot(1.0f);
        gl.glPopMatrix();
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

    public void dispose(GLAutoDrawable arg0) {
         
    }
}
