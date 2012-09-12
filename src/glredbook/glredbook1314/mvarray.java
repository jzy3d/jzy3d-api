/**
 * 
 */
package glredbook1314;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLJPanel;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;

import com.jogamp.common.nio.PointerBuffer;
import com.jogamp.opengl.util.GLBuffers;

/**
 * This program demonstrates multiple vertex arrays, specifically the OpenGL
 * routine glMultiDrawElements(), but it's a bitch to setup--so I use
 * DrawElements in a loop instead.
 * 
 * @author Kiet Le (Java port) Ported to JOGL 2.x by Claudio Eduardo Goes (JOGL port)
 */
public class mvarray //
        implements GLEventListener //
        , KeyListener {
    private GLU glu;

    private int vertices[] = { 25, 25,//
            75, 75,//
            100, 125,//
            150, 75,//
            200, 175,//
            250, 150,//
            300, 125,//
            100, 200,//
            150, 250,//
            200, 225,//
            250, 300,//
            300, 250 };

    private IntBuffer vertexBuf = //
    GLBuffers.newDirectIntBuffer(vertices.length);

    private byte oneIndices[] = { 0, 1, 2, 3, 4, 5, 6 };

    private byte twoIndices[] = { 1, 7, 8, 9, 10, 11 };

    private int count[] = { 7, 6 };

    // static GLvoid * indices[2] = {oneIndices, twoIndices};
    private PointerBuffer indices = PointerBuffer.allocateDirect(2);

    {
        vertexBuf.put(vertices);
        vertexBuf.rewind();

        indices.referenceBuffer(GLBuffers.newDirectByteBuffer(oneIndices));
        indices.referenceBuffer(GLBuffers.newDirectByteBuffer(twoIndices));
        indices.rewind();
    }

    private boolean mde_bug;

    /**
     * 
     */
    public mvarray() {
    }

    private void setupPointer(GL2 gl) {
        gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
        gl.glVertexPointer(2, GL2.GL_INT, 0, vertexBuf);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.media.opengl.GLEventListener#display(javax.media.opengl.GLAutoDrawable)
     */
    public void display(GLAutoDrawable drawable) {
        final GL2 gl = drawable.getGL().getGL2();
        //
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glColor3f(1.0f, 1.0f, 1.0f);

        if (mde_bug)
            gl.glMultiDrawElements(GL.GL_LINE_STRIP, count, 0,//
                    GL.GL_UNSIGNED_BYTE, indices, 2);
        else {
            // workaround for glMultiDrawElem bug before July
            for (int i = 0; i < indices.capacity(); i++)
                gl.glDrawElements(GL.GL_LINE_STRIP, count[i], //
                        GL.GL_UNSIGNED_BYTE, (ByteBuffer)indices.getReferencedBuffer(i));
        }
        gl.glFlush();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.media.opengl.GLEventListener#displayChanged(javax.media.opengl.GLAutoDrawable,
     *      boolean, boolean)
     */
    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.media.opengl.GLEventListener#init(javax.media.opengl.GLAutoDrawable)
     */
    public void init(GLAutoDrawable drawable) {
        final GL2 gl = drawable.getGL().getGL2();
        glu = new GLU();
        //

        mde_bug = !gl.isFunctionAvailable("glMultiDrawElements");
        System.out.println("glMultiDrawElements bug: " + mde_bug);

        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glShadeModel(GL2.GL_SMOOTH);
        setupPointer(gl);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.media.opengl.GLEventListener#reshape(javax.media.opengl.GLAutoDrawable,
     *      int, int, int, int)
     */
    public void reshape(GLAutoDrawable drawable, int x, int y, //
            int width, int height) {
        final GL2 gl = drawable.getGL().getGL2();
        //
        gl.glViewport(0, 0, width, height);

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        glu.gluOrtho2D(0.0, (double) width, 0.0, (double) height);

        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        GLCapabilities caps = new GLCapabilities(null);
        caps.setSampleBuffers(true);
        GLJPanel canvas = new GLJPanel(caps);

        mvarray demo = new mvarray();
        canvas.addGLEventListener(demo);
        canvas.addKeyListener(demo);

        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("mvarray");
        frame.setSize(350, 350);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().add(canvas);
        frame.setVisible(true);
        frame.setFocusable(false);
        canvas.requestFocusInWindow();
    }

    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
        case KeyEvent.VK_ESCAPE:
            System.exit(0);
            break;

        default:
            break;
        }
    }

    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub
    }

    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub
    }

    public void dispose(GLAutoDrawable arg0) {
         
    }

}
