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
import com.jogamp.opengl.util.gl2.GLUT;

/**
 * This program demonstrates cube map textures. Six different colored checker
 * board textures are created and applied to a lit sphere. Pressing the 'f' and
 * 'b' keys translate the viewer forward and backward.
 * 
 * @author Kiet Le (Java port) Ported to JOGL 2.x by Claudio Eduardo Goes
 */
public class cubemap// 
        extends GLSkeleton<GLJPanel>
        implements GLEventListener, KeyListener {
    // reference to the canvas in main() in order to refresh in input
  
    private GLU glu;
    private GLUT glut;
    private static int imageSize = 4;
    // private static byte image1[][][] = new byte[imageSize][imageSize][4];
    // private static byte image2[][][] = new byte[imageSize][imageSize][4];
    // private static byte image3[][][] = new byte[imageSize][imageSize][4];
    // private static byte image4[][][] = new byte[imageSize][imageSize][4];
    // private static byte image5[][][] = new byte[imageSize][imageSize][4];
    // private static byte image6[][][] = new byte[imageSize][imageSize][4];
    private static ByteBuffer imageBuf1 //
    = GLBuffers.newDirectByteBuffer(imageSize * imageSize * 4);
    private static ByteBuffer imageBuf2 //
    = GLBuffers.newDirectByteBuffer(imageSize * imageSize * 4);
    private static ByteBuffer imageBuf3 //
    = GLBuffers.newDirectByteBuffer(imageSize * imageSize * 4);
    private static ByteBuffer imageBuf4 //
    = GLBuffers.newDirectByteBuffer(imageSize * imageSize * 4);
    private static ByteBuffer imageBuf5 //
    = GLBuffers.newDirectByteBuffer(imageSize * imageSize * 4);
    private static ByteBuffer imageBuf6 //
    = GLBuffers.newDirectByteBuffer(imageSize * imageSize * 4);

    private static double ztrans = 0.0;

    @Override
    protected GLJPanel createDrawable() {
        GLCapabilities caps = new GLCapabilities(null);
        caps.setSampleBuffers(true);
        //
        GLJPanel panel = new GLJPanel(caps);
        panel.addGLEventListener(this);
        panel.addKeyListener(this);
        return panel;
    }
    /*
     * Demonstrate this example.
     */
    public static void main(String[] args) { 

        cubemap demo = new cubemap();

        /* metal/ocean LAF can't handle heavy-weight canvas */
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("cubemap");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLocationRelativeTo(null);

        frame.getContentPane().add(demo.drawable);
        frame.setVisible(true);
        demo.drawable.requestFocusInWindow();
    }

    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        glu = new GLU();
        glut = new GLUT();
        //
        float diffuse[] = { 1.0f, 1.0f, 1.0f, 1.0f };

        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glShadeModel(GL2.GL_SMOOTH);

        makeImages();
        gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);
        gl.glTexParameteri(GL2.GL_TEXTURE_CUBE_MAP, GL2.GL_TEXTURE_WRAP_S,
                GL2.GL_REPEAT);
        gl.glTexParameteri(GL2.GL_TEXTURE_CUBE_MAP, GL2.GL_TEXTURE_WRAP_T,
                GL2.GL_REPEAT);
        gl.glTexParameteri(GL2.GL_TEXTURE_CUBE_MAP, GL2.GL_TEXTURE_WRAP_R,
                GL2.GL_REPEAT);
        gl.glTexParameteri(GL2.GL_TEXTURE_CUBE_MAP, GL2.GL_TEXTURE_MAG_FILTER,
                GL.GL_NEAREST);
        gl.glTexParameteri(GL2.GL_TEXTURE_CUBE_MAP, GL2.GL_TEXTURE_MIN_FILTER,
                GL.GL_NEAREST);
        gl.glTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, GL2.GL_RGBA,
                imageSize, imageSize, 0, GL2.GL_RGBA, GL.GL_UNSIGNED_BYTE,
                imageBuf1);
        gl.glTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, GL2.GL_RGBA,
                imageSize, imageSize, 0, GL2.GL_RGBA, GL.GL_UNSIGNED_BYTE,
                imageBuf4);
        gl.glTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, GL2.GL_RGBA,
                imageSize, imageSize, 0, GL2.GL_RGBA, GL.GL_UNSIGNED_BYTE,
                imageBuf2);
        gl.glTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, GL2.GL_RGBA,
                imageSize, imageSize, 0, GL2.GL_RGBA, GL.GL_UNSIGNED_BYTE,
                imageBuf5);
        gl.glTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, GL2.GL_RGBA,
                imageSize, imageSize, 0, GL2.GL_RGBA, GL.GL_UNSIGNED_BYTE,
                imageBuf3);
        gl.glTexImage2D(GL2.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, GL2.GL_RGBA,
                imageSize, imageSize, 0, GL2.GL_RGBA, GL.GL_UNSIGNED_BYTE,
                imageBuf6);
        gl.glTexGeni(GL2.GL_S, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_NORMAL_MAP);
        gl.glTexGeni(GL2.GL_T, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_NORMAL_MAP);
        gl.glTexGeni(GL2.GL_R, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_NORMAL_MAP);
        gl.glEnable(GL2.GL_TEXTURE_GEN_S);
        gl.glEnable(GL2.GL_TEXTURE_GEN_T);
        gl.glEnable(GL2.GL_TEXTURE_GEN_R);

        gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);

        gl.glEnable(GL2.GL_TEXTURE_CUBE_MAP);
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);
        gl.glEnable(GL2.GL_AUTO_NORMAL);
        gl.glEnable(GL2.GL_NORMALIZE);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_DIFFUSE, diffuse, 0);

    }

    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        gl.glPushMatrix();
        gl.glTranslatef(0.0f, 0.0f, (float) ztrans);
        glut.glutSolidSphere(5.0, 20, 10);
        gl.glPopMatrix();

        gl.glFlush();
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glViewport(0, 0, w, h);

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        glu.gluPerspective(40.0, (float) w / (float) h, 1.0, 300.0);

        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();

        gl.glTranslatef(0.0f, 0.0f, -20.0f);
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }

    private void makeImages() {
        int c = 0;
        for (int i = 0; i < imageSize; i++) {
            for (int j = 0; j < imageSize; j++) {
                // c = ((((i&0x1)==0)^((j&0x1))==0))*255;
                c = (byte) ((((byte) ((i & 0x1) == 0 ? 0x00 : 0xff)//
                ^ (byte) ((j & 0x1) == 0 ? 0x00 : 0xff))));
                imageBuf1.put((byte) c);
                imageBuf1.put((byte) c);
                imageBuf1.put((byte) c);
                imageBuf1.put((byte) 255);

                imageBuf2.put((byte) c);
                imageBuf2.put((byte) c);
                imageBuf2.put((byte) 0);
                imageBuf2.put((byte) 255);

                imageBuf3.put((byte) c);
                imageBuf3.put((byte) 0);
                imageBuf3.put((byte) c);
                imageBuf3.put((byte) 255);

                imageBuf4.put((byte) 0);
                imageBuf4.put((byte) c);
                imageBuf4.put((byte) c);
                imageBuf4.put((byte) 255);

                imageBuf5.put((byte) 255);
                imageBuf5.put((byte) c);
                imageBuf5.put((byte) c);
                imageBuf5.put((byte) 255);

                imageBuf6.put((byte) c);
                imageBuf6.put((byte) c);
                imageBuf6.put((byte) 255);
                imageBuf6.put((byte) 255);
            }
        }
        imageBuf1.rewind();
        imageBuf2.rewind();
        imageBuf3.rewind();
        imageBuf4.rewind();
        imageBuf5.rewind();
        imageBuf6.rewind();
    }

    public void keyTyped(KeyEvent key) {
    }

    public void keyPressed(KeyEvent key) {
        switch (key.getKeyCode()) {
        case KeyEvent.VK_ESCAPE:
            System.exit(0);
            break;
            
        case KeyEvent.VK_F:
        case KeyEvent.VK_UP:
            ztrans = ztrans - 0.2;
            break;
            
        case KeyEvent.VK_B:
        case KeyEvent.VK_DOWN:
            ztrans = ztrans + 0.2;
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

}//