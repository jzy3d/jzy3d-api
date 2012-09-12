package glredbook10;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.nio.ByteBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLJPanel;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;

import com.jogamp.opengl.util.GLBuffers;

/**
 * This program texture maps a checkerboard image onto two rectangles. This
 * program clamps the texture, if the texture coordinates fall outside 0.0 and
 * 1.0.
 * 
 * @author Kiet Le (Java port) Ported to JOGL 2.x by Claudio Eduardo Goes
 */
public class checker //
        extends GLSkeleton<GLJPanel>
        implements GLEventListener, KeyListener {
    private GLU glu;
    /* Create checkerboard texture */
    private static final int checkImageWidth = 64;
    private static final int checkImageHeight = 64;
    private static final int color = 3;
    // private byte checkImage[][][] = new
    // byte[checkImageWidth][checkImageHeight][color];
    private ByteBuffer checkImageBuf = //
    GLBuffers.newDirectByteBuffer(checkImageHeight * checkImageWidth * color);

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

        checker demo = new checker();

        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("checker");
        frame.setSize(500, 500);
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
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDepthFunc(GL.GL_LESS);

        makeCheckImage();

        gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);
        gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, color, checkImageWidth,
                checkImageHeight, 0, GL2.GL_RGB, GL.GL_UNSIGNED_BYTE,
                checkImageBuf);// checkImage[0][0][0]);
        gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
        gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
        gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER,
                GL.GL_NEAREST);
        gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER,
                GL.GL_NEAREST);
        gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_DECAL);
        gl.glEnable(GL2.GL_TEXTURE_2D);
        gl.glShadeModel(GL2.GL_FLAT);
    }

    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3f(-2.0f, -1.0f, 0.0f);
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex3f(-2.0f, 1.0f, 0.0f);
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex3f(0.0f, 1.0f, 0.0f);
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3f(0.0f, -1.0f, 0.0f);

        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3f(1.0f, -1.0f, 0.0f);
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex3f(1.0f, 1.0f, 0.0f);
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex3f(2.41421f, 1.0f, -1.41421f);
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3f(2.41421f, -1.0f, -1.41421f);
        gl.glEnd();

        gl.glFlush();
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        
        glu.gluPerspective(60.0, 1.0 * (float) w / (float) h, 1.0, 30.0);

        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();

        gl.glTranslatef(0.0f, 0.0f, -3.6f);
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }

    /*
     * 3D array won't be used. I left it here for you to see.
     */
    private void makeCheckImage() {
        byte c = (byte) 0xFF;

        for (int i = 0; i < checkImageWidth; i++) {
            for (int j = 0; j < checkImageHeight; j++) {
                // c = ((((i & 0x8) == 0) ^ ((j & 0x8)) == 0)) * 255;
                c = (byte) ((((byte) ((i & 0x8) == 0 ? 0x00 : 0xff)//
                ^ (byte) ((j & 0x8) == 0 ? 0x00 : 0xff))));

                // checkImage[i][j][0] = (byte) c;
                // checkImage[i][j][1] = (byte) c;
                // checkImage[i][j][2] = (byte) c;
                checkImageBuf.put((byte) c);
                checkImageBuf.put((byte) c);
                checkImageBuf.put((byte) c);
            }
        }
        checkImageBuf.rewind();
    }//

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
