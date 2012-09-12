package glredbook10;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLJPanel;
import javax.swing.JFrame;

import com.jogamp.common.nio.Buffers;

/**
 * This program uses evaluators to generate a curved surface and automatically
 * generated texture coordinates.
 * 
 * @author Kiet Le (Java port) Ported to JOGL 2.x by Claudio Eduardo Goes
 */

public class texturesurf//
        extends GLSkeleton<GLJPanel>
        implements GLEventListener, KeyListener {

    // as from C version of file
    private static final float ctrlpoints[][][] = new float[][][] {
            { { -1.5f, -1.5f, 4.0f }, { -0.5f, -1.5f, 2.0f },
                    { 0.5f, -1.5f, -1.0f }, { 1.5f, -1.5f, 2.0f } },
            { { -1.5f, -0.5f, 1.0f }, { -0.5f, -0.5f, 3.0f },
                    { 0.5f, -0.5f, 0.0f }, { 1.5f, -0.5f, -1.0f } },
            { { -1.5f, 0.5f, 4.0f }, { -0.5f, 0.5f, 0.0f },
                    { 0.5f, 0.5f, 3.0f }, { 1.5f, 0.5f, 4.0f } },
            { { -1.5f, 1.5f, -2.0f }, { -0.5f, 1.5f, -2.0f },
                    { 0.5f, 1.5f, 0.0f }, { 1.5f, 1.5f, -1.0f } } };

    // need float buffer instead of n-dimensional array above
    private FloatBuffer ctrlpointsBuf = Buffers.newDirectFloatBuffer(ctrlpoints.length * ctrlpoints[0].length * ctrlpoints[0][0].length);

    {// SO copy 4x4x3 array above to float buffer
        for (int i = 0; i < ctrlpoints.length; i++)
            // System.out.print(ctrlpoints.length+ " ");
            for (int j = 0; j < ctrlpoints[0].length; j++) {
                // System.out.println(ctrlpoints[0][0].length+" ");
                for (int k = 0; k < ctrlpoints[0][0].length; k++) {
                    ctrlpointsBuf.put(ctrlpoints[i][j][k]);
                    // System.out.print(ctrlpoints[i][j][k] + " ");
                }
                // System.out.println(ctrlpointsBuf.toString());
            }
        // THEN rewind it before use
        ctrlpointsBuf.rewind();
    }
    private float[][][] texpts = new float[][][] {
            { { 0.0f, 0.0f }, { 0.0f, 1.0f } },
            { { 1.0f, 0.0f }, { 1.0f, 1.0f } } };
    private FloatBuffer texptsBuf = Buffers.newDirectFloatBuffer(texpts.length * texpts[0].length * texpts[1].length);
    {
        for (int i = 0; i < texpts.length; i++)
            // System.out.print(ctrlpoints.length+ " ");
            for (int j = 0; j < texpts[0].length; j++) {
                // System.out.println(ctrlpoints[0][0].length+" ");
                for (int k = 0; k < texpts[0][0].length; k++) {
                    texptsBuf.put(texpts[i][j][k]);
                    // System.out.print(texpts[i][j][k] + " ");
                }
                // System.out.println(texptsBuf.toString());
            }
        // THEN rewind it before use
        texptsBuf.rewind();

    }
    private static final int imageWidth = 64;
    private static final int imageHeight = 64;
    private static byte image[] = new byte[3 * imageWidth * imageHeight];
    private static ByteBuffer imageBuf = Buffers.newDirectByteBuffer(image.length);

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

        texturesurf demo = new texturesurf();
        //
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("texturesurf");
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
        gl.glMap2f(GL2.GL_MAP2_VERTEX_3, 0, 1, 3, 4, 0, 1, 12, 4, ctrlpointsBuf);
        gl.glMap2f(GL2.GL_MAP2_TEXTURE_COORD_2, 0, 1, 2, 2, 0, 1, 4, 2, texptsBuf);
        gl.glEnable(GL2.GL_MAP2_TEXTURE_COORD_2);
        gl.glEnable(GL2.GL_MAP2_VERTEX_3);
        gl.glMapGrid2f(20, 0.0f, 1.0f, 20, 0.0f, 1.0f);
        makeImage();
        gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_DECAL);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
        gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_RGB, imageWidth, imageHeight, 0, GL2.GL_RGB, GL.GL_UNSIGNED_BYTE, imageBuf);
        gl.glEnable(GL2.GL_TEXTURE_2D);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glShadeModel(GL2.GL_FLAT);
    }

    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glEvalMesh2(GL2.GL_FILL, 0, 20, 0, 20);
        gl.glFlush();

    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        if (w <= h)
            gl.glOrtho(-4.0, 4.0, -4.0 * (float) h / (float) w, 4.0 * (float) h
                    / (float) w, -4.0, 4.0);
        else
            gl.glOrtho(-4.0 * (float) w / (float) h, //
                    4.0 * (float) w / (float) h, -4.0, 4.0, -4.0, 4.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glRotatef(85.0f, 1.0f, 1.0f, 1.0f);
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    }

    private void makeImage() {
        float ti, tj;

        for (int i = 0; i < imageWidth; i++) {
            ti = 2.0f * (float) Math.PI * i / imageWidth;
            for (int j = 0; j < imageHeight; j++) {
                tj = 2.0f * (float) Math.PI * j / imageHeight;

                // image[3 * (imageHeight * i + j)] = (byte) (255 * (1.0 +
                // sin(ti)));
                // image[3 * (imageHeight * i + j) + 1] = (byte) (255 * (1.0 +
                // cos(2 *
                // tj)));
                // image[3 * (imageHeight * i + j) + 2] = (byte) (255 * (1.0 +
                // cos(ti +
                // tj)));
                // image[3 * (imageHeight * i + j) + 2] = (byte)0xff;
                imageBuf.put((byte) (127 * (1.0 + Math.sin(ti))));
                imageBuf.put((byte) (127 * (1.0 + Math.cos(2 * tj))));
                imageBuf.put((byte) (127 * (1.0 + Math.cos(ti + tj))));
            }
        }
        imageBuf.rewind();
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
