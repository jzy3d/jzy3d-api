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

/**
 * Draws the bitmapped letter F on the screen (several times). This demonstrates
 * use of the glBitmap() call.
 * 
 * @author Kiet Le (Java port) Ported to JOGL 2.x by Claudio Eduardo Goes
 */
public class drawf//
        extends GLSkeleton<GLJPanel>
        implements GLEventListener, KeyListener {
    private byte rasters[] = new byte[] { (byte) 0xc0, (byte) 0x00,
            (byte) 0xc0, (byte) 0x00, (byte) 0xc0, (byte) 0x00, (byte) 0xc0,
            (byte) 0x00, (byte) 0xc0, (byte) 0x00, (byte) 0xff, (byte) 0x00,
            (byte) 0xff, (byte) 0x00, (byte) 0xc0, (byte) 0x00, (byte) 0xc0,
            (byte) 0x00, (byte) 0xc0, (byte) 0x00, (byte) 0xff, (byte) 0xc0,
            (byte) 0xff, (byte) 0xc0 };

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

        drawf demo = new drawf();

        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("drawf");
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().add(demo.drawable);
        frame.setVisible(true);
        demo.drawable.requestFocusInWindow();
    }

    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }

    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glRasterPos2f(20.5f, 20.5f);
        gl.glBitmap(10, 12, 0.0f, 0.0f, 12.0f, 0.0f, rasters, 0);
        gl.glBitmap(10, 12, 0.0f, 0.0f, 12.0f, 0.0f, rasters, 0);
        gl.glBitmap(10, 12, 0.0f, 0.0f, 12.0f, 0.0f, rasters, 0);
        gl.glFlush();
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(0, w, 0, h, -1.0, 1.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }

    public void keyTyped(KeyEvent key) {
        // TODO Auto-generated method stub
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
        // TODO Auto-generated method stub
    }

    public void dispose(GLAutoDrawable arg0) {
         
    }

}
