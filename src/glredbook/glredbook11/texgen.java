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
import javax.swing.JFrame;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.gl2.GLUT;

/**
 * This program draws a texture mapped teapot with automatically generated
 * texture coordinates. The texture is rendered as stripes on the teapot.
 * Initially, the object is drawn with texture coordinates based upon the object
 * coordinates of the vertex and distance from the plane x = 0. Pressing the 'e'
 * key changes the coordinate generation to eye coordinates of the vertex.
 * Pressing the 'o' key switches it back to the object coordinates. Pressing the
 * 's' key changes the plane to a slanted one (x + y + z = 0). Pressing the 'x'
 * key switches it back to x = 0.
 * 
 * @author Kiet Le (Java port) Ported to JOGL 2.x by Claudio Eduardo Goes
 */
public class texgen//
        extends GLSkeleton<GLJPanel>
        implements GLEventListener, KeyListener {
    private GLUT glut;
    private static final int stripeImageWidth = 32;
    private byte stripeImage[] = new byte[3 * stripeImageWidth];
    private ByteBuffer stripeImageBuf = Buffers.newDirectByteBuffer(stripeImage.length);
    /* glTexGen stuff: */
    // private float sgenparams[] = { 1.0f, 1.0f, 1.0f, 0.0f };
    private int texName[] = new int[1];
    private static double xequalzero[] = { 1.0, 0.0, 0.0, 0.0 };
    private static double slanted[] = { 1.0, 1.0, 1.0, 0.0 };
    private static double currentCoeff[];
    private static int currentPlane;
    private static int currentGenMode;

    @Override
    protected GLJPanel createDrawable() {
        GLCapabilities caps = new GLCapabilities(null);
        caps.setSampleBuffers(true);// enable sample buffers for aliasing
        caps.setNumSamples(2);
        //
        GLJPanel panel = new GLJPanel(caps);
        panel.addGLEventListener(this);
        panel.addKeyListener(this);
        return panel;
    }

    public static void main(String[] args) {
        texgen demo = new texgen();
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("texgen");
        frame.setSize(400, 400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().add(demo.drawable);
        frame.setVisible(true);
        demo.drawable.requestFocusInWindow();
    }

    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        glut = new GLUT();
        //
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        makeStripeImage();

        gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);

        gl.glGenTextures(1, texName, 0);
        gl.glBindTexture(GL2.GL_TEXTURE_1D, texName[0]);

        gl
                .glTexParameterf(GL2.GL_TEXTURE_1D, GL2.GL_TEXTURE_WRAP_S,
                        GL2.GL_REPEAT);
        gl.glTexParameterf(GL2.GL_TEXTURE_1D, //
                GL2.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        gl.glTexParameterf(GL2.GL_TEXTURE_1D, //
                GL2.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl.glTexImage1D(GL2.GL_TEXTURE_1D, 0, GL2.GL_RGBA, stripeImageWidth, //
                0, GL2.GL_RGB, GL.GL_UNSIGNED_BYTE, stripeImageBuf);

        gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);
        currentCoeff = xequalzero;
        currentGenMode = GL2.GL_OBJECT_LINEAR;
        currentPlane = GL2.GL_OBJECT_PLANE;
        gl.glTexGeni(GL2.GL_S, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_OBJECT_LINEAR);
        gl.glTexGendv(GL2.GL_S, GL2.GL_OBJECT_PLANE, currentCoeff, 0);

        // gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDepthFunc(GL.GL_LESS);
        gl.glEnable(GL2.GL_TEXTURE_GEN_S);
        gl.glEnable(GL2.GL_TEXTURE_1D);
        gl.glEnable(GL.GL_CULL_FACE);
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);
        gl.glEnable(GL2.GL_AUTO_NORMAL);
        gl.glEnable(GL2.GL_NORMALIZE);
        gl.glFrontFace(GL.GL_CW);
        gl.glCullFace(GL.GL_BACK);
        gl.glMaterialf(GL.GL_FRONT, GL2.GL_SHININESS, 64.0f);
    }

    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glPushMatrix();
        gl.glRotatef(45.0f, 0.0f, 0.0f, 1.0f);

        gl.glTexGeni(GL2.GL_S, GL2.GL_TEXTURE_GEN_MODE, currentGenMode);
        gl.glTexGendv(GL2.GL_S, currentPlane, currentCoeff, 0);

        gl.glBindTexture(GL2.GL_TEXTURE_1D, texName[0]);
        glut.glutSolidTeapot(2.0f);
        gl.glPopMatrix();
        gl.glFlush();
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        if (w <= h)
            gl.glOrtho(-3.5, 3.5, -3.5 * (float) h / (float) w, 3.5 * (float) h
                    / (float) w, -3.5, 3.5);
        else
            gl.glOrtho(-3.5 * (float) w / (float) h, //
                    3.5 * (float) w / (float) h, -3.5, 3.5, -3.5, 3.5);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }

    private void makeStripeImage() {
        for (int j = 0; j < stripeImageWidth; j++) {
            // stripeImage[3 * j] = (j <= 4) ? 255 : 0;
            // stripeImage[3 * j + 1] = (j > 4) ? 255 : 0;
            // stripeImage[3 * j + 2] = 0;
            stripeImageBuf.put(((j <= 4) ? (byte) 255 : (byte) 0));
            stripeImageBuf.put(((j > 4) ? (byte) 255 : (byte) 0));
            stripeImageBuf.put((byte) 0);
        }
        stripeImageBuf.rewind();
    }

    public void keyTyped(KeyEvent key) {
    }

    public void keyPressed(KeyEvent key) {
        switch (key.getKeyChar()) {
        case KeyEvent.VK_ESCAPE:
            System.exit(0);
            break;
        case 'e':
        case 'E':
            currentGenMode = GL2.GL_EYE_LINEAR;
            currentPlane = GL2.GL_EYE_PLANE;
            break;
        case 'o':
        case 'O':
            currentGenMode = GL2.GL_OBJECT_LINEAR;
            currentPlane = GL2.GL_OBJECT_PLANE;
            break;
        case 's':
        case 'S':
            currentCoeff = slanted;
            break;
        case 'x':
        case 'X':
            currentCoeff = xequalzero;
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
