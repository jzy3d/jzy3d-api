package glredbook12x;

import glredbook10.GLSkeleton;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
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
 *
 * @author Kiet Le (Java port) Ported to JOGL 2.x by Claudio Eduardo Goes
 */

public class multitex //
        extends GLSkeleton<GLJPanel>
        implements GLEventListener, KeyListener {
    private GLU glu;
    // private byte[][][] texels0 = new byte[32][32][4];
    // private byte[][][] texels1 = new byte[16][16][4];
    private ByteBuffer texelsBuf0 = GLBuffers.newDirectByteBuffer(32 * 32 * 4);
    private ByteBuffer texelsBuf1 = GLBuffers.newDirectByteBuffer(16 * 16 * 4);

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
        GLCapabilities caps = new GLCapabilities(null);
        GLJPanel canvas = new GLJPanel(caps);

        multitex demo = new multitex();
        canvas.addGLEventListener(demo);
        if (demo instanceof KeyListener)
            canvas.addKeyListener(demo);
        // explicit cast for class not impl'ing listeners
        // to make it compile,
        if (demo instanceof MouseListener)
            canvas.addMouseListener((MouseListener) demo);
        if (demo instanceof MouseMotionListener)
            canvas.addMouseMotionListener((MouseMotionListener) demo);
        if (demo instanceof MouseWheelListener)
            canvas.addMouseWheelListener((MouseWheelListener) demo);

        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("multitex");
        frame.setSize(512, 512);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().add(canvas);
        frame.setVisible(true);
        canvas.requestFocusInWindow();
    }

    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        glu = new GLU();
        //
        int texNames[] = new int[2];

        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glShadeModel(GL2.GL_FLAT);
        gl.glEnable(GL.GL_DEPTH_TEST);

        makeCheckImages();
        gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);

        gl.glGenTextures(2, texNames, 0);
        gl.glBindTexture(GL2.GL_TEXTURE_2D, texNames[0]);
        gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_RGBA, //
                32, 32, 0, GL2.GL_RGBA, GL.GL_UNSIGNED_BYTE, texelsBuf0);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, //
                GL2.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, //
                GL2.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
        gl
                .glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S,
                        GL2.GL_REPEAT);
        gl
                .glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T,
                        GL2.GL_REPEAT);

        gl.glBindTexture(GL2.GL_TEXTURE_2D, texNames[1]);
        gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_RGBA, //
                16, 16, 0, GL2.GL_RGBA, GL.GL_UNSIGNED_BYTE, texelsBuf1);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, //
                GL2.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, //
                GL2.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, //
                GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, //
                GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP_TO_EDGE);
        /*
         * Use the two texture objects to define two texture units for use in
         * multitexturing
         */

        // gl.glActiveTextureARB(GL2.GL_TEXTURE0_ARB);// deprecated
        gl.glActiveTexture(GL2.GL_TEXTURE0);
        gl.glEnable(GL2.GL_TEXTURE_2D);
        gl.glBindTexture(GL2.GL_TEXTURE_2D, texNames[0]);
        gl.glTexEnvi(GL2.GL_TEXTURE_ENV, //
                GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);
        gl.glMatrixMode(GL2.GL_TEXTURE);
        gl.glLoadIdentity();
        gl.glTranslatef(0.5f, 0.5f, 0.0f);
        gl.glRotatef(45.0f, 0.0f, 0.0f, 1.0f);
        gl.glTranslatef(-0.5f, -0.5f, 0.0f);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        // gl.glActiveTextureARB (GL2.GL_TEXTURE1_ARB); deprecated
        gl.glActiveTexture(GL2.GL_TEXTURE1);
        gl.glEnable(GL2.GL_TEXTURE_2D);
        gl.glBindTexture(GL2.GL_TEXTURE_2D, texNames[1]);
        gl.glTexEnvi(GL2.GL_TEXTURE_ENV, //
                GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);
    }

    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        gl.glBegin(GL2.GL_TRIANGLES);
        gl.glMultiTexCoord2f(GL2.GL_TEXTURE0, 0.0f, 0.0f);
        gl.glMultiTexCoord2f(GL2.GL_TEXTURE1, 1.0f, 0.0f);
        gl.glVertex2f(0.0f, 0.0f);
        gl.glMultiTexCoord2f(GL2.GL_TEXTURE0, 0.5f, 1.0f);
        gl.glMultiTexCoord2f(GL2.GL_TEXTURE1, 0.5f, 0.0f);
        gl.glVertex2f(50.0f, 100.0f);
        gl.glMultiTexCoord2f(GL2.GL_TEXTURE0, 1.0f, 0.0f);
        gl.glMultiTexCoord2f(GL2.GL_TEXTURE1, 1.0f, 1.0f);
        gl.glVertex2f(100.0f, 0.0f);
        gl.glEnd();

        gl.glFlush();
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        if (w <= h)
            glu.gluOrtho2D(0.0, 100.0, //
                    0.0, 100.0 * (double) h / (double) w);
        else
            glu.gluOrtho2D(0.0, 100.0 * (double) w / (double) h, 0.0, 100.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }

    void makeCheckImages() {
        for (int i = 0; i < 32; i++) {
            for (int j = 0; j < 32; j++) {
                // texels0[i][j][0] = (byte) i;
                // texels0[i][j][1] = (byte) j;
                // texels0[i][j][2] = (byte) ((i*j)/255);
                // texels0[i][j][3] = (byte) 255;
                // changed from above for more visible result
                texelsBuf0.put((byte) (i * i * i));
                texelsBuf0.put((byte) (j * j * j));
                texelsBuf0.put((byte) ((i * j) / 255));
                texelsBuf0.put((byte) 0xFF);
            }
        }

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                // texels1[i][j][0] = (byte) 255;
                // texels1[i][j][1] = (byte) i;
                // texels1[i][j][2] = (byte) j;
                // texels1[i][j][3] = (byte) 255;
                // changed from above for more visible result
                texelsBuf1.put((byte) 0xFF);
                texelsBuf1.put((byte) (i * i));
                texelsBuf1.put((byte) (j * j));
                texelsBuf1.put((byte) 0xFF);
            }
        }
        texelsBuf0.rewind();
        texelsBuf1.rewind();
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
