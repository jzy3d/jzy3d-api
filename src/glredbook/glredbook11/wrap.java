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
 * program demonstrates the wrapping modes, if the texture coordinates fall
 * outside 0.0 and 1.0. Interaction: Pressing the 's' and 'S' keys switch the
 * wrapping between clamping and repeating for the s parameter. The 't' and 'T'
 * keys control the wrapping for the t parameter. If running this program on
 * OpenGL 1.0, texture objects are not used.
 * 
 * @author Kiet Le (Java port) Ported to JOGL 2.x by Claudio Eduardo Goes
 */
public class wrap//
        extends GLSkeleton<GLJPanel>
        implements GLEventListener, KeyListener {
    private GLU glu;

    private int texName[] = new int[1];
    private static final int checkImageWidth = 64;
    private static final int checkImageHeight = 64;
    private static final int rgba = 4;
    // private byte[][][] checkImage;
    private ByteBuffer checkImageBuf = //
    GLBuffers.newDirectByteBuffer(checkImageHeight * checkImageWidth * rgba);
    private KeyEvent key;

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
        wrap demo = new wrap();

        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("wrap");
        frame.setSize(250, 250);
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
        System.out.println("" + GL.GL_VERSION);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glShadeModel(GL2.GL_FLAT);
        gl.glEnable(GL.GL_DEPTH_TEST);

        makeCheckImage();
        gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);

        gl.glGenTextures(1, texName, 0);
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
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        gl.glEnable(GL2.GL_TEXTURE_2D);
        if (key != null) {
            switch (key.getKeyChar()) {
            case 's':
                gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S,
                        GL2.GL_CLAMP);
                break;
            case 'S':
                gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S,
                        GL2.GL_REPEAT);
                break;
            case 't':
                gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T,
                        GL2.GL_CLAMP);
                break;
            case 'T':
                gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T,
                        GL2.GL_REPEAT);
                break;
            default:
                break;
            }
        }
        gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_DECAL);
        gl.glBindTexture(GL2.GL_TEXTURE_2D, texName[0]);

        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2d(0.0, 0.0);
        gl.glVertex3d(-2.0, -1.0, 0.0);
        gl.glTexCoord2d(0.0, 3.0);
        gl.glVertex3d(-2.0, 1.0, 0.0);
        gl.glTexCoord2d(3.0, 3.0);
        gl.glVertex3d(0.0, 1.0, 0.0);
        gl.glTexCoord2d(3.0, 0.0);
        gl.glVertex3d(0.0, -1.0, 0.0);

        gl.glTexCoord2d(0.0, 0.0);
        gl.glVertex3d(1.0, -1.0, 0.0);
        gl.glTexCoord2d(0.0, 3.0);
        gl.glVertex3d(1.0, 1.0, 0.0);
        gl.glTexCoord2d(3.0, 3.0);
        gl.glVertex3d(2.41421, 1.0, -1.41421);
        gl.glTexCoord2d(3.0, 0.0);
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

    private void makeCheckImage() {
        // byte c = (~(i & 0x8) ^ ~(j & 0x8)) * 255;
        byte c = 0;
        for (int i = 0; i < checkImageHeight; i++) {
            for (int j = 0; j < checkImageWidth; j++) {
                // c = ((((i&0x8)==0)^((j&0x8))==0))*255;C' version
                c = (byte) ((((byte) ((i & 0x8) == 0 ? 0x00 : 0xff)//
                ^ (byte) ((j & 0x8) == 0 ? 0x00 : 0xff))));
                System.out.print("" + (byte) c + " ");
                // checkImage[i][j][0] = ( byte) c;
                // checkImage[i][j][1] = ( byte) c;
                // checkImage[i][j][2] = ( byte) c;
                // checkImage[i][j][3] = ( byte) 0xff;
                checkImageBuf.put((byte) c);
                checkImageBuf.put((byte) c);
                checkImageBuf.put((byte) c);
                checkImageBuf.put((byte) 0xff);
            }
            System.out.println();
        }
        checkImageBuf.rewind();
    }

    public void keyTyped(KeyEvent key) {
    }

    public void keyPressed(KeyEvent key) {
        this.key = key;
        switch (key.getKeyCode()) {
        case KeyEvent.VK_ESCAPE:
            System.exit(0);
            break;

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
