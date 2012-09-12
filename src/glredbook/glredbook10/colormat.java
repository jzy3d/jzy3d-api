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
 * After initialization, the program will be in ColorMaterial mode. Pressing the
 * mouse buttons will change the color of the diffuse reflection.
 * 
 * @author Kiet Le (Java port) Ported to JOGL 2.x by Claudio Eduardo Goes
 */
public class colormat//
        extends GLSkeleton<GLJPanel>
        implements GLEventListener, KeyListener, MouseListener {
    private GLUT glut;
    private float diffuseMaterial[] = { 0.5f, 0.5f, 0.5f, 1.0f };
    private boolean diffuseColorChanged = false;


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

        colormat demo = new colormat();

        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("colormat");
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().add(demo.drawable);
        frame.setVisible(true);
        demo.drawable.requestFocusInWindow();
    }//
    /*
     * Initialize values for material property, light source, lighting model,
     * and depth buffer.
     */
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2(); 
        glut = new GLUT();
        //
        float mat_specular[] = { 1.0f, 1.0f, 1.0f, 1.0f };
        float light_position[] = { 1.0f, 1.0f, 1.0f, 0.0f };

        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_DIFFUSE, diffuseMaterial, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, mat_specular, 0);
        gl.glMaterialf(GL.GL_FRONT, GL2.GL_SHININESS, 25.0f);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, light_position, 0);

        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);
        gl.glDepthFunc(GL.GL_LESS);
        gl.glEnable(GL.GL_DEPTH_TEST);

        gl.glColorMaterial(GL.GL_FRONT, GL2.GL_DIFFUSE);
        gl.glEnable(GL2.GL_COLOR_MATERIAL);
    }

    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        if (diffuseColorChanged) {
            gl.glColor4fv(diffuseMaterial, 0);
            diffuseColorChanged = !diffuseColorChanged;
        }

        glut.glutSolidSphere(1.0f, 10, 10);
        gl.glFlush();
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        if (w <= h)
            gl.glOrtho(-1.5, 1.5, -1.5 * (float) h / (float) w, 1.5 * (float) h
                    / (float) w, -10.0, 10.0);
        else
            gl.glOrtho(-1.5 * (float) w / (float) h, 1.5 * (float) w
                    / (float) h, -1.5, 1.5, -10.0, 10.0);

        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }

    private void changeRedDiffuse() {
        diffuseMaterial[0] += 0.1;
        if (diffuseMaterial[0] > 1.0)
            diffuseMaterial[0] = 0.0f;
        // gl.glColor4fv(diffuseMaterial, 0);
    }

    private void changeGreenDiffuse() {
        diffuseMaterial[1] += 0.1;
        if (diffuseMaterial[1] > 1.0)
            diffuseMaterial[1] = 0.0f;
        // gl.glColor4fv(diffuseMaterial, 0);
    }

    private void changeBlueDiffuse() {
        diffuseMaterial[2] += 0.1;
        if (diffuseMaterial[2] > 1.0)
            diffuseMaterial[2] = 0.0f;
        // gl.glColor4fv(diffuseMaterial, 0);
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
        switch (mouse.getButton()) {
        case MouseEvent.BUTTON1:
            changeRedDiffuse();
            diffuseColorChanged = true;
            break;
        case MouseEvent.BUTTON2:
            changeGreenDiffuse();
            diffuseColorChanged = true;
            break;
        case MouseEvent.BUTTON3:
            changeBlueDiffuse();
            diffuseColorChanged = true;
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
