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
 * This program demonstrates the use of the GL lighting model. A sphere is drawn
 * using a magenta diffuse reflective and white specular material property. A
 * single light source illuminates the object. This program illustrates lighting
 * in color map mode.
 * 
 * @author Kiet Le (Java port) Ported to JOGL 2.x by Claudio Eduardo Goes
 */
public class maplight//
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
        maplight demo = new maplight();

        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("maplight");
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().add(demo.drawable);
        frame.setVisible(true);
        demo.drawable.requestFocusInWindow();
    }

    /*
     * Initialize material property, light source, and lighting model.
     */
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        glut = new GLUT();
        //
        float light_position[] = { 1.0f, 1.0f, 1.0f, 0.0f };
        float mat_colormap[] = { 16.0f, 48.0f, 79.0f };
        float mat_shininess[] = { 10.0f };

        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_COLOR_INDEXES, mat_colormap, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SHININESS, mat_shininess, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, light_position, 0);

        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);

        gl.glDepthFunc(GL.GL_LESS);
        gl.glEnable(GL.GL_DEPTH_TEST);

        /*
         * jogl does not implement color index mode index color code for (int i =
         * 0; i < 32; i++) { // glut. auxSetOneColor (16 + i, 1.0 * (i/32.0),
         * 0.0, 1.0 * (i/32.0)); // glut.auxSetOneColor (48 + i, 1.0, 1.0 *
         * (i/32.0), 1.0); } gl.glClearIndex(0);
         */
    }

    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        glut.glutSolidSphere(1.0f, 20, 20);
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
