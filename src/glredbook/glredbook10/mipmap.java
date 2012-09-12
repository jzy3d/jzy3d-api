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

import com.jogamp.common.nio.Buffers;

/**
 * This program demonstrates using mipmaps for texture maps. To overtly show the
 * effect of mipmaps, each mipmap reduction level has a solidly colored,
 * contrasting texture image. Thus, the quadrilateral which is drawn is drawn
 * with several different colors.
 * 
 * @author Kiet Le (Java port) Ported to JOGL 2.x by Claudio Eduardo Goes
 */
public class mipmap//
        extends GLSkeleton<GLJPanel>
        implements GLEventListener, KeyListener {
    private GLU glu;
    private static final int color = 3;
    // private byte mipmapImage32[][][] = new byte[32][32][color];
    // private byte mipmapImage16[][][] = new byte[16][16][color];
    // private byte mipmapImage8[][][] = new byte[8][8][color];
    // private byte mipmapImage4[][][] = new byte[4][4][color];
    // private byte mipmapImage2[][][] = new byte[2][2][color];
    // private byte mipmapImage1[][][] = new byte[1][1][color];
    private ByteBuffer mipmapImage32Buf = Buffers.newDirectByteBuffer(32 * 32 * color);
    private ByteBuffer mipmapImage16Buf = Buffers.newDirectByteBuffer(16 * 16 * color);
    private ByteBuffer mipmapImage8Buf = Buffers.newDirectByteBuffer(8 * 8 * color);
    private ByteBuffer mipmapImage4Buf = Buffers.newDirectByteBuffer(4 * 4 * color);
    private ByteBuffer mipmapImage2Buf = Buffers.newDirectByteBuffer(2 * 2 * color);
    private ByteBuffer mipmapImage1Buf = Buffers.newDirectByteBuffer(1 * 1 * color);

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
        mipmap demo = new mipmap();
        //
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("mipmap");
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
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDepthFunc(GL.GL_LESS);
        gl.glShadeModel(GL2.GL_FLAT);

        gl.glTranslatef(0.0f, 0.0f, -3.6f);

        makeImages();

        gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);
        gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, 3, 32, 32, 0, GL2.GL_RGB,
                GL.GL_UNSIGNED_BYTE, mipmapImage32Buf);
        gl.glTexImage2D(GL2.GL_TEXTURE_2D, 1, 3, 16, 16, 0, GL2.GL_RGB,
                GL.GL_UNSIGNED_BYTE, mipmapImage16Buf);
        gl.glTexImage2D(GL2.GL_TEXTURE_2D, 2, 3, 8, 8, 0, GL2.GL_RGB,
                GL.GL_UNSIGNED_BYTE, mipmapImage8Buf);
        gl.glTexImage2D(GL2.GL_TEXTURE_2D, 3, 3, 4, 4, 0, GL2.GL_RGB,
                GL.GL_UNSIGNED_BYTE, mipmapImage4Buf);
        gl.glTexImage2D(GL2.GL_TEXTURE_2D, 4, 3, 2, 2, 0, GL2.GL_RGB,
                GL.GL_UNSIGNED_BYTE, mipmapImage2Buf);
        gl.glTexImage2D(GL2.GL_TEXTURE_2D, 5, 3, 1, 1, 0, GL2.GL_RGB,
                GL.GL_UNSIGNED_BYTE, mipmapImage1Buf);
        gl.glTexParameterf(GL2.GL_TEXTURE_2D, //
                GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
        gl.glTexParameterf(GL2.GL_TEXTURE_2D, //
                GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
        gl.glTexParameterf(GL2.GL_TEXTURE_2D, //
                GL2.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
        gl.glTexParameterf(GL2.GL_TEXTURE_2D, //
                GL2.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST_MIPMAP_NEAREST);
        gl.glTexEnvf(GL2.GL_TEXTURE_ENV, //
                GL2.GL_TEXTURE_ENV_MODE, GL2.GL_DECAL);
        gl.glEnable(GL2.GL_TEXTURE_2D);
    }

    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3f(-2.0f, -1.0f, 0.0f);
        gl.glTexCoord2f(0.0f, 8.0f);
        gl.glVertex3f(-2.0f, 1.0f, 0.0f);
        gl.glTexCoord2f(8.0f, 8.0f);
        gl.glVertex3f(2000.0f, 1.0f, -6000.0f);
        gl.glTexCoord2f(8.0f, 0.0f);
        gl.glVertex3f(2000.0f, -1.0f, -6000.0f);
        gl.glEnd();
        gl.glFlush();
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(60.0, 1.0 * (float) w / (float) h, 1.0, 30000.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }

    /*
     * 3D arrays are never used by gl command TexImage2D. it instead use byte
     * buffer.
     */
    private void makeImages() {
        int i, j;

        for (i = 0; i < 32; i++) {
            for (j = 0; j < 32; j++) {
                // mipmapImage32[i][j][0] = (byte) 255;
                // mipmapImage32[i][j][1] = (byte) 255;
                // mipmapImage32[i][j][2] = (byte) 0;
                //
                mipmapImage32Buf.put((byte) 255);
                mipmapImage32Buf.put((byte) 255);
                mipmapImage32Buf.put((byte) 0);
            }
        }
        for (i = 0; i < 16; i++) {
            for (j = 0; j < 16; j++) {
                // mipmapImage16[i][j][0] = (byte) 255;
                // mipmapImage16[i][j][1] = (byte) 0;
                // mipmapImage16[i][j][2] = (byte) 255;
                //
                mipmapImage16Buf.put((byte) 255);
                mipmapImage16Buf.put((byte) 0);
                mipmapImage16Buf.put((byte) 255);
            }
        }
        for (i = 0; i < 8; i++) {
            for (j = 0; j < 8; j++) {
                // mipmapImage8[i][j][0] = (byte) 255;
                // mipmapImage8[i][j][1] = (byte) 0;
                // mipmapImage8[i][j][2] = (byte) 0;
                //
                mipmapImage8Buf.put((byte) 255);
                mipmapImage8Buf.put((byte) 0);
                mipmapImage8Buf.put((byte) 0);
            }
        }
        for (i = 0; i < 4; i++) {
            for (j = 0; j < 4; j++) {
                // mipmapImage4[i][j][0] = (byte) 0;
                // mipmapImage4[i][j][1] = (byte) 255;
                // mipmapImage4[i][j][2] = (byte) 0;
                //
                mipmapImage4Buf.put((byte) 0);
                mipmapImage4Buf.put((byte) 255);
                mipmapImage4Buf.put((byte) 0);
            }
        }
        for (i = 0; i < 2; i++) {
            for (j = 0; j < 2; j++) {
                // mipmapImage2[i][j][0] = (byte) 0;
                // mipmapImage2[i][j][1] = (byte) 0;
                // mipmapImage2[i][j][2] = (byte) 255;
                //
                mipmapImage2Buf.put((byte) 0);
                mipmapImage2Buf.put((byte) 0);
                mipmapImage2Buf.put((byte) 255);
            }
        }
        // mipmapImage1[0][0][0] = (byte) 255;
        // mipmapImage1[0][0][1] = (byte) 255;
        // mipmapImage1[0][0][2] = (byte) 255;
        //
        mipmapImage1Buf.put((byte) 255);
        mipmapImage1Buf.put((byte) 255);
        mipmapImage1Buf.put((byte) 255);
        // rewind all
        mipmapImage32Buf.rewind();
        mipmapImage16Buf.rewind();
        mipmapImage8Buf.rewind();
        mipmapImage4Buf.rewind();
        mipmapImage2Buf.rewind();
        mipmapImage1Buf.rewind();
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
