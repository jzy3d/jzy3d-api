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
 * This program demonstrates using a three-dimensional texture. It creates a 3D
 * texture and then renders two rectangles with different texture coordinates to
 * obtain different "slices" of the 3D texture.
 * 
 * @author Kiet Le (Java port) Ported to JOGL 2.x by Claudio Eduardo Goes
 */
public class texture3d //
        extends GLSkeleton<GLJPanel>
        implements GLEventListener, KeyListener {
    private GLU glu;
    private static final int iWidth = 16;
    private static final int iHeight = 16;
    private static final int iDepth = 16;
    private static final int iRgb = 3;
    private ByteBuffer image 
        = GLBuffers.newDirectByteBuffer(iRgb * iWidth * iHeight * iDepth);
    private int texName[] = new int[1];
    
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
        
        texture3d demo = new texture3d();
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
        JFrame frame = new JFrame("texture3d");
        frame.setSize(250, 250);
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
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glShadeModel(GL2.GL_FLAT);
        gl.glEnable(GL.GL_DEPTH_TEST);

        makeImage();

        gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);

        gl.glGenTextures(1, texName, 0);
        gl.glBindTexture(GL2.GL_TEXTURE_3D, texName[0]);
        gl.glTexParameteri(GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
        gl.glTexParameteri(GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
        gl.glTexParameteri(GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_WRAP_R, GL2.GL_CLAMP);
        gl.glTexParameteri(GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_MAG_FILTER,
                GL.GL_NEAREST);
        gl.glTexParameteri(GL2.GL_TEXTURE_3D, GL2.GL_TEXTURE_MIN_FILTER,
                GL.GL_NEAREST);
        gl.glTexImage3D(GL2.GL_TEXTURE_3D, 0, GL2.GL_RGB,//
                iWidth, iHeight, iDepth, 0, GL2.GL_RGB, GL.GL_UNSIGNED_BYTE,
                image);
        gl.glEnable(GL2.GL_TEXTURE_3D);
    }//

    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord3f(0.0f, 0.0f, 0.0f);
        gl.glVertex3f(-2.25f, -1.0f, 0.0f);
        gl.glTexCoord3f(0.0f, 1.0f, 0.0f);
        gl.glVertex3f(-2.25f, 1.0f, 0.0f);
        gl.glTexCoord3f(1.0f, 1.0f, 1.0f);
        gl.glVertex3f(-0.25f, 1.0f, 0.0f);
        gl.glTexCoord3f(1.0f, 0.0f, 1.0f);
        gl.glVertex3f(-0.25f, -1.0f, 0.0f);

        gl.glTexCoord3f(0.0f, 0.0f, 1.0f);
        gl.glVertex3f(0.25f, -1.0f, 0.0f);
        gl.glTexCoord3f(0.0f, 1.0f, 1.0f);
        gl.glVertex3f(0.25f, 1.0f, 0.0f);
        gl.glTexCoord3f(1.0f, 1.0f, 0.0f);
        gl.glVertex3f(2.25f, 1.0f, 0.0f);
        gl.glTexCoord3f(1.0f, 0.0f, 0.0f);
        gl.glVertex3f(2.25f, -1.0f, 0.0f);
        gl.glEnd();

        gl.glFlush();
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

        gl.glTranslatef(0.0f, 0.0f, -4.0f);
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }

    /*
     * Create a 16x16x16x3 array with different color values in each array
     * element [r, g, b]. Values range from 0 to 255.
     */

    private void makeImage() {
        int ss = 0, tt = 0, rr = 0;
        for (int s = 0; s < 16; s++)
            for (int t = 0; t < 16; t++)
                for (int r = 0; r < 16; r++) {
                    // image[r][t][s][0] = (GLubyte) (s * 17);
                    // image[r][t][s][1] = (GLubyte) (t * 17);
                    // image[r][t][s][2] = (GLubyte) (r * 17);
                    ss = s * 17;
                    tt = t * 17;
                    rr = r * 17;
                    // System.out.println("s" + ss + "." + ss//
                    // + "t" + tt + "." + tt//
                    // + "r" + rr + "." + rr);
                    image.put((byte) (ss * 17));
                    image.put((byte) tt);
                    image.put((byte) rr);
                }
        image.rewind();
    }

    public void keyTyped(KeyEvent key) {
    }

    public void keyPressed(KeyEvent key) {
        switch (key.getKeyCode()) {
        case KeyEvent.VK_ESCAPE:
            System.exit(0);
            break;

        }
    }

    public void keyReleased(KeyEvent key) {
    }

    public void dispose(GLAutoDrawable arg0) {

    }
}
