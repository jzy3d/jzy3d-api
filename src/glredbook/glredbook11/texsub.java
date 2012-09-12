package glredbook11;

import glredbook10.GLSkeleton;

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
 * 1.0. If the s key is pressed, a texture subimage is used to alter the
 * original texture. If the r key is pressed, the original texture is restored.
 * 
 * @author Kiet Le (Java port) Ported to JOGL 2.x by Claudio Eduardo Goes
 */
public class texsub
        extends GLSkeleton<GLJPanel>
        implements GLEventListener, KeyListener {
    private GLU glu;
    private KeyEvent key;
    //
    private static final int rgba = 4;
    private static final int checkImageWidth = 64;
    private static final int checkImageHeight = 64;
    private static final int subImageWidth = 16;
    private static final int subImageHeight = 16;
    // private byte checkImage[][][];
    // private byte otherImage[][][];
    private ByteBuffer checkImageBuf = //
    GLBuffers.newDirectByteBuffer(checkImageWidth * checkImageHeight * rgba);
    private ByteBuffer subImageBuf = //
    GLBuffers.newDirectByteBuffer(subImageWidth * subImageHeight * rgba);
    private int[] texName = new int[2];

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

        texsub demo = new texsub();

        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("texsub");
        frame.setSize(400, 400);
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
        gl.glShadeModel(GL2.GL_FLAT);
        gl.glEnable(GL.GL_DEPTH_TEST);

        makeCheckImages();

        gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);

        gl.glGenTextures(2, texName, 0);
        gl.glBindTexture(GL2.GL_TEXTURE_2D, texName[0]);
        gl
                .glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S,
                        GL2.GL_REPEAT);
        gl
                .glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T,
                        GL2.GL_REPEAT);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER,
                GL.GL_NEAREST);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER,
                GL.GL_NEAREST);
        gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_RGBA, checkImageWidth,
                checkImageHeight, 0, GL2.GL_RGBA, GL.GL_UNSIGNED_BYTE,
                checkImageBuf);
    }

    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //
        if (key != null) {
            switch (key.getKeyCode()) {
            case KeyEvent.VK_S:
                gl.glBindTexture(GL2.GL_TEXTURE_2D, texName[0]);
                gl.glTexSubImage2D(GL2.GL_TEXTURE_2D, 0, 12, 44, subImageWidth,
                        subImageHeight, GL2.GL_RGBA, GL.GL_UNSIGNED_BYTE,
                        subImageBuf);
                break;
            case KeyEvent.VK_R:
                gl.glBindTexture(GL2.GL_TEXTURE_2D, texName[0]);
                gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_RGBA,
                        checkImageWidth, checkImageHeight, 0, GL2.GL_RGBA,
                        GL.GL_UNSIGNED_BYTE, checkImageBuf);
                break;

            default:
                break;
            }
            key = null;
        }

        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        gl.glEnable(GL2.GL_TEXTURE_2D);
        gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_DECAL);
        gl.glBindTexture(GL2.GL_TEXTURE_2D, texName[0]);

        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2d(0.0, 0.0);
        gl.glVertex3d(-2.0, -1.0, 0.0);
        gl.glTexCoord2d(0.0, 1.0);
        gl.glVertex3d(-2.0, 1.0, 0.0);
        gl.glTexCoord2d(1.0, 1.0);
        gl.glVertex3d(0.0, 1.0, 0.0);
        gl.glTexCoord2d(1.0, 0.0);
        gl.glVertex3d(0.0, -1.0, 0.0);

        gl.glTexCoord2d(0.0, 0.0);
        gl.glVertex3d(1.0, -1.0, 0.0);
        gl.glTexCoord2d(0.0, 1.0);
        gl.glVertex3d(1.0, 1.0, 0.0);
        gl.glTexCoord2d(1.0, 1.0);
        gl.glVertex3d(2.41421, 1.0, -1.41421);
        gl.glTexCoord2d(1.0, 0.0);
        gl.glVertex3d(2.41421, -1.0, -1.41421);
        gl.glEnd();
        gl.glFlush();

        gl.glDisable(GL2.GL_TEXTURE_2D);
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(60.0, (float) w / (float) h, 1.0, 30.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glTranslated(0.0, 0.0, -3.6);
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }

    private void makeCheckImages() {
        byte c = 0x00;

        for (int i = 0; i < checkImageWidth; i++) {
            for (int j = 0; j < checkImageHeight; j++) {

                c = (byte) ((((byte) ((i & 0x8) == 0 ? 0x00 : 0xff)//
                ^ (byte) ((j & 0x8) == 0 ? 0x00 : 0xff))));
                // checkImage[i][j][0] = (byte) c;
                // checkImage[i][j][1] = (byte) c;
                // checkImage[i][j][2] = (byte) c;
                checkImageBuf.put((byte) c);
                checkImageBuf.put((byte) c);
                checkImageBuf.put((byte) c);
                checkImageBuf.put((byte) 0xff);
            }
        }

        for (int i = 0; i < subImageHeight; i++) {
            for (int j = 0; j < subImageWidth; j++) {
                c = (byte) ((((byte) ((i & 0x4) == 0 ? 0x00 : 0xff)//
                ^ (byte) ((j & 0x4) == 0 ? 0x00 : 0xff))));
                subImageBuf.put((byte) c);
                subImageBuf.put((byte) 0);
                subImageBuf.put((byte) 0);
                subImageBuf.put((byte) 0xff);
            }
        }
        checkImageBuf.rewind();
        subImageBuf.rewind();
    }

    public void keyTyped(KeyEvent key) {
    }

    public void keyPressed(KeyEvent key) {
        this.key = key;
        switch (key.getKeyCode()) {
        case KeyEvent.VK_ESCAPE:
            System.exit(0);
        default:
            break;
        }
        super.refresh();
    }

    public void keyReleased(KeyEvent key) {
    }

    public void dispose(GLAutoDrawable arg0) {
         
    }
}
