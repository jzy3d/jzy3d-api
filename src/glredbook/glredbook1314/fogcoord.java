package glredbook1314;

import glredbook10.GLSkeleton;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;
 

/**
 * This program demonstrates the use of explicit fog coordinates. You can press
 * the keyboard and change the fog coordinate value at any vertex. You can also
 * switch between using explicit fog coordinates and the default fog generation
 * mode. <br>
 * Pressing the 'f' and 'b' keys move the viewer forward and backwards. Pressing
 * 'c' initiates the default fog generation. Pressing capital 'C' restores
 * explicit fog coordinates. Pressing '1', '2', '3', '8', '9', and '0' add or
 * subtract from the fog coordinate values at one of the three vertices of the
 * triangle.
 * 
 * @author Kiet Le (Java port) Ported to JOGL 2.x by Claudio Eduardo Goes
 */
public class fogcoord//
        extends GLSkeleton<GLCanvas>
        implements GLEventListener, KeyListener {
 
    private GLU glu;
    private float f1, f2, f3;
    private KeyEvent key;

    @Override
    protected GLCanvas createDrawable() {
        GLCapabilities caps = new GLCapabilities(null);
        caps.setSampleBuffers(true);
        //
        GLCanvas panel = new GLCanvas(caps);
        panel.addGLEventListener(this);
        panel.addKeyListener(this);
        return panel;
    }

    public static void main(String[] args) {
        // set argument 'NotFirstUIActionOnProcess' in the JNLP's application-desc tag for example
        // <application-desc main-class="demos.j2d.TextCube"/>
        //   <argument>NotFirstUIActionOnProcess</argument>
        // </application-desc>
        // boolean firstUIActionOnProcess = 0==args.length || !args[0].equals("NotFirstUIActionOnProcess") ;
        // GLProfile.initSingleton(firstUIActionOnProcess);
       // GLProfile.initSingleton(); // just lazy to touch all html/jnlp's

        GLCapabilities caps = new GLCapabilities(null);
        GLCanvas canvas = new GLCanvas(caps);

        fogcoord demo = new fogcoord();

        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("fogcoord");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);

        frame.getContentPane().add(demo.drawable);
        frame.setVisible(true);
        demo.drawable.requestFocusInWindow();
    }

    /*
     * Initialize fog
     */
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        glu = new GLU(); 
        //
        float fogColor[] = { 0.0f, 0.25f, 0.25f, 1.0f };
        f1 = 1.0f;
        f2 = 5.0f;
        f3 = 10.0f;

        gl.glEnable(GL2.GL_FOG);
        gl.glFogi(GL2.GL_FOG_MODE, GL2.GL_EXP);
        gl.glFogfv(GL2.GL_FOG_COLOR, fogColor, 0);
        gl.glFogf(GL2.GL_FOG_DENSITY, 0.25f);
        gl.glHint(GL2.GL_FOG_HINT, GL.GL_DONT_CARE);
        gl.glFogi(GL2.GL_FOG_COORDINATE_SOURCE, GL2.GL_FOG_COORDINATE);
        gl.glClearColor(0.0f, 0.25f, 0.25f, 1.0f); /* fog color */
    }

    /*
     * display() draws a triangle at an angle.
     */
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);

        if (key != null)
            switch (key.getKeyChar()) {
            case 'c':
                gl.glFogi(GL2.GL_FOG_COORDINATE_SOURCE,
                        GL2.GL_FRAGMENT_DEPTH);
                break;
            case 'C':
                gl.glFogi(GL2.GL_FOG_COORDINATE_SOURCE,
                        GL2.GL_FOG_COORDINATE);
                break;

            case 'b':
                gl.glMatrixMode(GL2.GL_MODELVIEW);
                gl.glTranslatef(0.0f, 0.0f, -0.25f);
                break;
            case 'f':
                gl.glMatrixMode(GL2.GL_MODELVIEW);
                gl.glTranslatef(0.0f, 0.0f, 0.25f);
                break;
            default:
                break;
            }

        gl.glColor3f(1.0f, 0.75f, 0.0f);
        gl.glBegin(GL2.GL_TRIANGLES);
        gl.glFogCoordf(f1);
        gl.glVertex3f(2.0f, -2.0f, 0.0f);
        gl.glFogCoordf(f2);
        gl.glVertex3f(-2.0f, 0.0f, -5.0f);
        gl.glFogCoordf(f3);
        gl.glVertex3f(0.0f, 2.0f, -10.0f);
        gl.glEnd();

        gl.glFlush();
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        glu.gluPerspective(45.0, 1.0, 0.25, 25.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glTranslatef(0.0f, 0.0f, -5.0f);
    }

    public void displayChanged(GLAutoDrawable drawable, //
            boolean modeChanged, boolean deviceChanged) {
    }

    public void keyTyped(KeyEvent key) {
    }

    public void keyPressed(KeyEvent key) {
        this.key = key;
        switch (key.getKeyChar()) {

        case KeyEvent.VK_ESCAPE:
            System.exit(0);
            break;

        case '1':
            f1 = f1 + 0.25f;
            break;
        case '2':
            f2 = f2 + 0.25f;
            break;
        case '3':
            f3 = f3 + 0.25f;
            break;
        case '8':
            if (f1 > 0.25)
                f1 = f1 - 0.25f;
            break;
        case '9':
            if (f2 > 0.25)
                f2 = f2 - 0.25f;

            break;
        case '0':
            if (f3 > 0.25)
                f3 = f3 - 0.25f;
            break;
        }

        super.refresh();
    }

    public void keyReleased(KeyEvent key) {
    }

    public void dispose(GLAutoDrawable arg0) {
         
    }
}//
