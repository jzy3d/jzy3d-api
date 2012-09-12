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

import com.jogamp.opengl.util.gl2.GLUT;

/**
 * This program draws 5 red teapots, each at a different z distance from the
 * eye, in different types of fog. Pressing the left mouse button chooses
 * between 3 types of fog: exponential, exponential squared, and linear. In this
 * program, there is a fixed density value, as well as fixed start and end
 * values for the linear fog.
 * 
 * @author Kiet Le (Java port) Ported to JOGL 2.x by Claudio Eduardo Goes
 */
public class fog
        extends GLSkeleton<GLJPanel>
        implements GLEventListener, KeyListener, MouseListener{
    private GLUT glut; 
    private int fogMode;

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
        fog demo = new fog();

        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("fog");
        frame.setSize(450, 150);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().add(demo.drawable);
        frame.setVisible(true);
        demo.drawable.requestFocusInWindow();
    }

    /*
     * Initialize z-buffer, projection matrix, light source, and lighting model.
     * Do not specify a material property here.
     */
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2(); 
        glut = new GLUT();
        //
        float position[] = { 0.0f, 3.0f, 3.0f, 0.0f };
        float local_view[] = { 0.0f };

        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDepthFunc(GL.GL_LESS);

        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, position, 0);
        gl.glLightModelfv(GL2.GL_LIGHT_MODEL_LOCAL_VIEWER, local_view, 0);

        gl.glFrontFace(GL.GL_CW);
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);
        gl.glEnable(GL2.GL_AUTO_NORMAL);
        gl.glEnable(GL2.GL_NORMALIZE);
        gl.glEnable(GL2.GL_FOG);
        {
            float fogColor[] = { 0.5f, 0.5f, 0.5f, 1.0f };

            fogMode = GL2.GL_EXP;
            gl.glFogi(GL2.GL_FOG_MODE, fogMode);
            gl.glFogfv(GL2.GL_FOG_COLOR, fogColor, 0);
            gl.glFogf(GL2.GL_FOG_DENSITY, 0.35f);
            gl.glHint(GL2.GL_FOG_HINT, GL.GL_DONT_CARE);

            gl.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        }
    }

    /*
     * display() draws 5 teapots at different z positions.
     */
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        //
        if (fogMode == GL2.GL_EXP2) {
            gl.glFogf(GL2.GL_FOG_START, 1.0f);
            gl.glFogf(GL2.GL_FOG_END, 5.0f);
        }
        gl.glFogi(GL2.GL_FOG_MODE, fogMode);

        renderRedTeapot(gl, -4.0f, -0.5f, -1.0f);
        renderRedTeapot(gl, -2.0f, -0.5f, -2.0f);
        renderRedTeapot(gl, 0.0f, -0.5f, -3.0f);
        renderRedTeapot(gl, 2.0f, -0.5f, -4.0f);
        renderRedTeapot(gl, 4.0f, -0.5f, -5.0f);
        gl.glFlush();
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        if (w <= (h * 3))
            gl.glOrtho(-6.0, 6.0, -2.0 * ((float) h * 3) / (float) w, 2.0
                    * ((float) h * 3) / (float) w, 0.0, 10.0);
        else
            gl.glOrtho(-6.0 * (float) w / ((float) h * 3), //
                    6.0 * (float) w / ((float) h * 3), -2.0, 2.0, 0.0, 10.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }

    private void cycleFog() {
        if (fogMode == GL2.GL_EXP) {
            fogMode = GL2.GL_EXP2;
            System.out.println("Fog mode is GL.GL_EXP2");
        } else if (fogMode == GL2.GL_EXP2) {
            fogMode = GL.GL_LINEAR;
            System.out.println("Fog mode is GL.GL_LINEAR\n");
        } else if (fogMode == GL.GL_LINEAR) {
            fogMode = GL2.GL_EXP;
            System.out.println("Fog mode is GL.GL_EXP\n");
        }
    }

    private void renderRedTeapot(GL2 gl, float x, float y, float z) {
        float mat[] = new float[4];

        gl.glPushMatrix();
        gl.glTranslatef(x, y, z);
        mat[0] = 0.1745f;
        mat[1] = 0.01175f;
        mat[2] = 0.01175f;
        mat[3] = 1.0f;
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, mat, 0);
        mat[0] = 0.61424f;
        mat[1] = 0.04136f;
        mat[2] = 0.04136f;
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_DIFFUSE, mat, 0);
        mat[0] = 0.727811f;
        mat[1] = 0.626959f;
        mat[2] = 0.626959f;
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, mat, 0);
        gl.glMaterialf(GL.GL_FRONT, GL2.GL_SHININESS, 0.6f * 128.0f);
        glut.glutSolidTeapot(1.0);
        gl.glPopMatrix();
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

    public void mouseClicked(MouseEvent mouse) {
    }

    public void mousePressed(MouseEvent mouse) {
        cycleFog();
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
