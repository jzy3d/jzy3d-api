package glredbook1314;

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
 * This program renders a variety of quads showing different effects of texture
 * combiner functions. The first row renders an untextured polygon (so you can
 * compare the fragment colors) and then the 2 textures. The second row shows
 * several different combiner functions on a single texture: replace, modulate,
 * add, add-signed, and subtract. The third row shows the interpolate combiner
 * function on a single texture with a constant color/alpha value, varying the
 * amount of interpolation. The fourth row uses multitexturing with two textures
 * and different combiner functions. The fifth row are some combiner
 * experiments: using the scaling factor and reversing the order of subtraction
 * for a combination function.
 * 
 * @author Kiet Le (Java port) Ported to JOGL 2.x by Claudio Eduardo Goes
 */
public class combiner// 
        extends GLSkeleton<GLJPanel>
        implements GLEventListener, KeyListener {
    private GLU glu;
    private static final int imageWidth = 8;
    private static final int imageHeight = 8;
    private static final int imageColor = 4;
    /* arrays for two textures */
    // private static byte[][][] image0 = new
    // byte[imageHeight][imageWidth][imageColor];
    // private static byte[][][] image1 = new
    // byte[imageHeight][imageWidth][imageColor];
    private static ByteBuffer imageBuf0 //
    = GLBuffers.newDirectByteBuffer(imageWidth * imageHeight * imageColor);
    private static ByteBuffer imageBuf1 //
    = GLBuffers.newDirectByteBuffer(imageWidth * imageHeight * imageColor);
    private static int texName[] = new int[4];

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
        // set argument 'NotFirstUIActionOnProcess' in the JNLP's application-desc tag for example
        // <application-desc main-class="demos.j2d.TextCube"/>
        //   <argument>NotFirstUIActionOnProcess</argument>
        // </application-desc>
        // boolean firstUIActionOnProcess = 0==args.length || !args[0].equals("NotFirstUIActionOnProcess") ;
        // GLProfile.initSingleton(firstUIActionOnProcess);
        //GLProfile.initSingleton(); // just lazy to touch all html/jnlp's
        
        combiner demo = new combiner();

        GLCapabilities caps = new GLCapabilities(null);
        caps.setSampleBuffers(true);
        // GLCanvas canvas = new GLCanvas(caps);
        GLJPanel canvas = new GLJPanel(caps);
        canvas.addGLEventListener(demo);
        canvas.addKeyListener(demo);

        /* metal/ocean LAF can't handle heavy-weight canvas */
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("combiner");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLocationRelativeTo(null);

        frame.getContentPane().add(canvas);

        frame.setVisible(true);
        canvas.requestFocusInWindow();
    }

    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        glu = new GLU();
        //
        // int numunits[] = new int[1];

        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glShadeModel(GL2.GL_SMOOTH);

        makeImages();
        gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);

        gl.glGenTextures(4, texName, 0);

        gl.glBindTexture(GL2.GL_TEXTURE_2D, texName[0]);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, //
                GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, //
                GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, //
                GL2.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, //
                GL2.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
        gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_RGBA, imageWidth,
                imageHeight, 0, GL2.GL_RGBA, GL.GL_UNSIGNED_BYTE, imageBuf0);

        gl.glBindTexture(GL2.GL_TEXTURE_2D, texName[1]);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, //
                GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, //
                GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, //
                GL2.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, //
                GL2.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
        gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_RGBA, imageWidth,
                imageHeight, 0, GL2.GL_RGBA, GL.GL_UNSIGNED_BYTE, imageBuf1);

        /* smooth-shaded polygon with multiple texture coordinates */
        gl.glNewList(1, GL2.GL_COMPILE);
        gl.glBegin(GL2.GL_QUADS);
        gl.glMultiTexCoord2f(GL2.GL_TEXTURE0, 0.0f, 0.0f);
        gl.glMultiTexCoord2f(GL2.GL_TEXTURE1, 0.0f, 0.0f);
        gl.glColor3f(0.5f, 1.0f, 0.25f);
        gl.glVertex3f(0.0f, 0.0f, 0.0f);
        gl.glMultiTexCoord2f(GL2.GL_TEXTURE0, 0.0f, 2.0f);
        gl.glMultiTexCoord2f(GL2.GL_TEXTURE1, 0.0f, 2.0f);
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glVertex3f(0.0f, 1.0f, 0.0f);
        gl.glMultiTexCoord2f(GL2.GL_TEXTURE0, 2.0f, 2.0f);
        gl.glMultiTexCoord2f(GL2.GL_TEXTURE1, 2.0f, 2.0f);
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glVertex3f(1.0f, 1.0f, 0.0f);
        gl.glMultiTexCoord2f(GL2.GL_TEXTURE0, 2.0f, 0.0f);
        gl.glMultiTexCoord2f(GL2.GL_TEXTURE1, 2.0f, 0.0f);
        gl.glColor3f(1.0f, 0.5f, 0.25f);
        gl.glVertex3f(1.0f, 0.0f, 0.0f);
        gl.glEnd();
        gl.glEndList();

    }

    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);

        // for use as constant texture color
        float constColor[] = new float[] { 0.0f, 0.0f, 0.0f, 0.0f };

        // untextured polygon--see the "fragment" colors
        gl.glDisable(GL2.GL_TEXTURE_2D);
        gl.glPushMatrix();
        gl.glTranslatef(0.0f, 5.0f, 0.0f);
        gl.glCallList(1);
        gl.glPopMatrix();

        gl.glEnable(GL2.GL_TEXTURE_2D);
        /* draw ordinary textured polys; 1 texture unit; combine mode disabled */
        gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);
        gl.glPushMatrix();
        gl.glBindTexture(GL2.GL_TEXTURE_2D, texName[0]);
        gl.glTranslatef(1.0f, 5.0f, 0.0f);
        gl.glCallList(1);
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glBindTexture(GL2.GL_TEXTURE_2D, texName[1]);
        gl.glTranslatef(2.0f, 5.0f, 0.0f);
        gl.glCallList(1);
        gl.glPopMatrix();

        /*
         * different combine modes enabled; 1 texture unit defaults are:
         * glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_SOURCE0_RGB_ARB, GL2.GL_TEXTURE);
         * glTexEnvf(GL2.GL_TEXTURE_ENV, GL.GL_OPERAND0_RGB_ARB,
         * GL2.GL_SRC_COLOR); glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_SOURCE1_RGB_ARB,
         * GL.GL_PREVIOUS_ARB); glTexEnvf(GL2.GL_TEXTURE_ENV,
         * GL.GL_OPERAND1_RGB_ARB, GL2.GL_SRC_COLOR);
         */
        gl.glBindTexture(GL2.GL_TEXTURE_2D, texName[0]);
        gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_COMBINE);
        gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_COMBINE_RGB, GL2.GL_REPLACE);
        gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_SOURCE0_RGB, GL2.GL_TEXTURE);
        gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_OPERAND0_RGB, GL2.GL_SRC_COLOR);
        gl.glPushMatrix();
        gl.glTranslatef(1.0f, 4.0f, 0.0f);
        gl.glCallList(1);
        gl.glPopMatrix();

        gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_COMBINE_RGB, GL2.GL_MODULATE);
        gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_SOURCE1_RGB, GL2.GL_PREVIOUS);
        gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_OPERAND1_RGB, GL2.GL_SRC_COLOR);
        gl.glPushMatrix();
        gl.glTranslatef(2.0f, 4.0f, 0.0f);
        gl.glCallList(1);
        gl.glPopMatrix();

        gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_COMBINE_RGB, GL2.GL_ADD);
        gl.glPushMatrix();
        gl.glTranslatef(3.0f, 4.0f, 0.0f);
        gl.glCallList(1);
        gl.glPopMatrix();

        gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_COMBINE_RGB, GL2.GL_ADD_SIGNED);
        gl.glPushMatrix();
        gl.glTranslatef(4.0f, 4.0f, 0.0f);
        gl.glCallList(1);
        gl.glPopMatrix();

        gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_COMBINE_RGB, GL2.GL_SUBTRACT);
        gl.glPushMatrix();
        gl.glTranslatef(5.0f, 4.0f, 0.0f);
        gl.glCallList(1);
        gl.glPopMatrix();

        /*
         * interpolate combine with constant color; 1 texture unit use different
         * alpha values for constant color defaults are:
         * glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_SOURCE0_RGB_ARB, GL2.GL_TEXTURE);
         * glTexEnvf(GL2.GL_TEXTURE_ENV, GL.GL_OPERAND0_RGB_ARB,
         * GL2.GL_SRC_COLOR); glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_SOURCE1_RGB_ARB,
         * GL.GL_PREVIOUS_ARB); glTexEnvf(GL2.GL_TEXTURE_ENV,
         * GL.GL_OPERAND1_RGB_ARB, GL2.GL_SRC_COLOR);
         * glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_SOURCE2_RGB_ARB,
         * GL.GL_CONSTANT_ARB); glTexEnvf(GL2.GL_TEXTURE_ENV,
         * GL.GL_OPERAND2_RGB_ARB, GL2.GL_SRC_ALPHA);
         */
        constColor[3] = 0.2f;
        gl.glTexEnvfv(GL2.GL_TEXTURE_ENV, //
                GL2.GL_TEXTURE_ENV_COLOR, constColor, 0);
        gl.glBindTexture(GL2.GL_TEXTURE_2D, texName[0]);
        gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_COMBINE);
        gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_COMBINE_RGB, GL2.GL_INTERPOLATE);
        gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_SOURCE0_RGB, GL2.GL_TEXTURE);
        gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_OPERAND0_RGB, GL2.GL_SRC_COLOR);
        gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_SOURCE1_RGB, GL2.GL_PREVIOUS);
        gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_OPERAND1_RGB, GL2.GL_SRC_COLOR);
        gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_SOURCE2_RGB, GL2.GL_CONSTANT);
        gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_OPERAND2_RGB, GL2.GL_SRC_ALPHA);
        gl.glPushMatrix();
        gl.glTranslatef(1.0f, 3.0f, 0.0f);
        gl.glCallList(1);
        gl.glPopMatrix();

        constColor[3] = 0.4f;
        gl.glTexEnvfv(GL2.GL_TEXTURE_ENV, //
                GL2.GL_TEXTURE_ENV_COLOR, constColor, 0);
        gl.glPushMatrix();
        gl.glTranslatef(2.0f, 3.0f, 0.0f);
        gl.glCallList(1);
        gl.glPopMatrix();

        constColor[3] = 0.6f;
        gl.glTexEnvfv(GL2.GL_TEXTURE_ENV, //
                GL2.GL_TEXTURE_ENV_COLOR, constColor, 0);
        gl.glPushMatrix();
        gl.glTranslatef(3.0f, 3.0f, 0.0f);
        gl.glCallList(1);
        gl.glPopMatrix();

        constColor[3] = 0.8f;
        gl.glTexEnvfv(GL2.GL_TEXTURE_ENV, //
                GL2.GL_TEXTURE_ENV_COLOR, constColor, 0);
        gl.glPushMatrix();
        gl.glTranslatef(4.0f, 3.0f, 0.0f);
        gl.glCallList(1);
        gl.glPopMatrix();

        /*
         * combine textures 0 & 1 defaults are: glTexEnvf(GL2.GL_TEXTURE_ENV,
         * GL2.GL_SOURCE0_RGB_ARB, GL2.GL_TEXTURE); glTexEnvf(GL2.GL_TEXTURE_ENV,
         * GL.GL_OPERAND0_RGB_ARB, GL2.GL_SRC_COLOR);
         * glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_SOURCE1_RGB_ARB,
         * GL.GL_PREVIOUS_ARB); glTexEnvf(GL2.GL_TEXTURE_ENV,
         * GL.GL_OPERAND1_RGB_ARB, GL2.GL_SRC_COLOR);
         */

        gl.glActiveTexture(GL2.GL_TEXTURE0);
        gl.glEnable(GL2.GL_TEXTURE_2D);
        gl.glBindTexture(GL2.GL_TEXTURE_2D, texName[0]);
        gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);

        gl.glActiveTexture(GL2.GL_TEXTURE1);
        gl.glEnable(GL2.GL_TEXTURE_2D);
        gl.glBindTexture(GL2.GL_TEXTURE_2D, texName[1]);
        gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_COMBINE);
        gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_COMBINE_RGB, GL2.GL_REPLACE);
        gl.glPushMatrix();
        gl.glTranslatef(1.0f, 2.0f, 0.0f);
        gl.glCallList(1);
        gl.glPopMatrix();

        /* try different combiner modes of texture unit 1 */
        gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_COMBINE_RGB, GL2.GL_MODULATE);
        gl.glPushMatrix();
        gl.glTranslatef(2.0f, 2.0f, 0.0f);
        gl.glCallList(1);
        gl.glPopMatrix();

        gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_COMBINE_RGB, GL2.GL_ADD);
        gl.glPushMatrix();
        gl.glTranslatef(3.0f, 2.0f, 0.0f);
        gl.glCallList(1);
        gl.glPopMatrix();

        gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_COMBINE_RGB, GL2.GL_ADD_SIGNED);
        gl.glPushMatrix();
        gl.glTranslatef(4.0f, 2.0f, 0.0f);
        gl.glCallList(1);
        gl.glPopMatrix();

        gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_COMBINE_RGB, GL2.GL_SUBTRACT);
        gl.glPushMatrix();
        gl.glTranslatef(5.0f, 2.0f, 0.0f);
        gl.glCallList(1);
        gl.glPopMatrix();

        /* some experiments */

        /* see the effect of RGB_SCALE */
        gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_RGB_SCALE, 2.0f);
        gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_COMBINE_RGB, GL2.GL_REPLACE);
        gl.glPushMatrix();
        gl.glTranslatef(1.0f, 1.0f, 0.0f);
        gl.glCallList(1);
        gl.glPopMatrix();

        gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_COMBINE_RGB, GL2.GL_MODULATE);
        gl.glPushMatrix();
        gl.glTranslatef(2.0f, 1.0f, 0.0f);
        gl.glCallList(1);
        gl.glPopMatrix();
        gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_RGB_SCALE, 1.0f);

        /* using SOURCE0 and SOURCE1, reverse the order of subtraction Arg1-Arg0 */

        gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_COMBINE);
        gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_COMBINE_RGB, GL2.GL_SUBTRACT);
        gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_SOURCE0_RGB, GL2.GL_PREVIOUS);
        gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_OPERAND0_RGB, GL2.GL_SRC_COLOR);
        gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_SOURCE1_RGB, GL2.GL_TEXTURE);
        gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_OPERAND1_RGB, GL2.GL_SRC_COLOR);
        gl.glPushMatrix();
        gl.glTranslatef(5.0f, 1.0f, 0.0f);
        gl.glCallList(1);
        gl.glPopMatrix();

        gl.glActiveTexture(GL2.GL_TEXTURE1); /* deactivate multitexturing */
        gl.glDisable(GL2.GL_TEXTURE_2D);
        gl.glActiveTexture(GL2.GL_TEXTURE0); /* activate single texture unit */

        gl.glFlush();

    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glViewport(0, 0, w, h);

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        glu.gluOrtho2D(0.0f, 7.0f, 0.0f, 7.0f);

        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }

    private void makeImages() {
        int c = 0;
        for (int i = 0; i < imageHeight; i++) {
            for (int j = 0; j < imageWidth; j++) {
                c = (i & 2) == 0 ? c * 255 : 0;// ) * 255; /* horiz b & w
                // stripes */
                // image0[i][j][0] = (byte) c;
                // image0[i][j][1] = (byte) c;
                // image0[i][j][2] = (byte) c;
                // image0[i][j][3] = (byte) 255;
                imageBuf0.put((byte) c);
                imageBuf0.put((byte) c);
                imageBuf0.put((byte) c);
                imageBuf0.put((byte) 255);
                c = (j & 4) != 0 ? c * 128 : 0;// )*128; /* wider vertical 50%
                // cyan and
                // black stripes */
                // image1[i][j][0] = (byte) 0;
                // image1[i][j][1] = (byte) c;
                // image1[i][j][2] = (byte) c;
                // image1[i][j][3] = (byte) 255;
                imageBuf1.put((byte) 0);
                imageBuf1.put((byte) c);
                imageBuf1.put((byte) c);
                imageBuf1.put((byte) 255);
            }
        }
        imageBuf0.rewind();
        imageBuf1.rewind();
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
