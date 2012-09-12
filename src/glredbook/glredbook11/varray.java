package glredbook11;

import glredbook10.GLSkeleton;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

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
 * This program demonstrates vertex arrays.
 * 
 * @author Chris Brown (bug fix)
 * @author Kiet Le (Java port) Ported to JOGL 2.x by Claudio Eduardo Goes
 */

public class varray//
        extends GLSkeleton<GLJPanel>
        implements GLEventListener, KeyListener, MouseListener {
    private GLU glu;
    private final int POINTER = 1;
    private final int INTERLEAVED = 2;

    private final int DRAWARRAY = 1;
    private final int ARRAYELEMENT = 2;
    private final int DRAWELEMENTS = 3;

    private int setupMethod = POINTER;
    private int derefMethod = DRAWARRAY;

    private IntBuffer indicesBuf;
    private IntBuffer verticesBuf;
    private FloatBuffer colorsBuf;
    private FloatBuffer intertwinedBuf;

    @Override
    protected GLJPanel createDrawable() {
        GLCapabilities caps = new GLCapabilities(null);
        //
        GLJPanel panel = new GLJPanel(caps);
        panel.addGLEventListener(this);
        panel.addKeyListener(this);
        panel.addMouseListener(this);
        return panel;
    }

    public static void main(String[] args) {
        GLCapabilities caps = new GLCapabilities(null);
        caps.setSampleBuffers(true);// enable sample buffers for aliasing
        caps.setNumSamples(2);

        varray demo = new varray();

        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("varray");
        frame.setSize(512, 256);
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
        gl.glShadeModel(GL2.GL_SMOOTH);
        setupPointers(gl);
    }

    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);

        if (derefMethod == DRAWARRAY) {
            gl.glDrawArrays(GL2.GL_TRIANGLES, 0, 6);
        } else if (derefMethod == ARRAYELEMENT) {
            gl.glBegin(GL2.GL_TRIANGLES);
            gl.glArrayElement(2);
            gl.glArrayElement(3);
            gl.glArrayElement(5);
            gl.glEnd();
        } else if (derefMethod == DRAWELEMENTS) {
            int indices[] = new int[] { 0, 1, 3, 4 };
            if (indicesBuf == null) {
                indicesBuf = GLBuffers.newDirectIntBuffer(indices.length);
                indicesBuf.put(indices);
            }
            indicesBuf.rewind();
            gl.glDrawElements(GL2.GL_POLYGON, 4, GL2.GL_UNSIGNED_INT, indicesBuf);
        }
        gl.glFlush();

        // gl calls from C example's mouse routine are moved here
        if (setupMethod == INTERLEAVED)
            setupInterleave(gl);
        else if (setupMethod == POINTER)
            setupPointers(gl);

    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluOrtho2D(0.0, (double) w, 0.0, (double) h);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }

    private void setupPointers(GL2 gl) {
        int vertices[] = new int[] { 25, 25, 100, 325, 175, 25, 175, 325, 250,
                25, 325, 325 };
        float colors[] = new float[] { 1.0f, 0.2f, 0.2f, 0.2f, 0.2f, 1.0f,
                0.8f, 1.0f, 0.2f, 0.75f, 0.75f, 0.75f, 0.35f, 0.35f, 0.35f,
                0.5f, 0.5f, 0.5f };

        if (verticesBuf == null) {// IntBuffer tmpVerticesBuf
            verticesBuf = GLBuffers.newDirectIntBuffer(vertices.length);
            verticesBuf.put(vertices);
        }
        if (colorsBuf == null) {
            colorsBuf = GLBuffers.newDirectFloatBuffer(colors.length);
            colorsBuf.put(colors);
        }
        verticesBuf.rewind();
        colorsBuf.rewind();
        //
        gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL2.GL_COLOR_ARRAY);
        //
        gl.glVertexPointer(2, GL2.GL_INT, 0, verticesBuf);
        gl.glColorPointer(3, GL.GL_FLOAT, 0, colorsBuf);

    }

    private void setupInterleave(GL2 gl) {
        float intertwined[] = new float[] { 1.0f, 0.2f, 1.0f, 100.0f, 100.0f,
                0.0f, 1.0f, 0.2f, 0.2f, 0.0f, 200.0f, 0.0f, 1.0f, 1.0f, 0.2f,
                100.0f, 300.0f, 0.0f, 0.2f, 1.0f, 0.2f, 200.0f, 300.0f, 0.0f,
                0.2f, 1.0f, 1.0f, 300.0f, 200.0f, 0.0f, 0.2f, 0.2f, 1.0f,
                200.0f, 100.0f, 0.0f };
        if (intertwinedBuf == null) {
            intertwinedBuf = GLBuffers.newDirectFloatBuffer(intertwined.length);
            intertwinedBuf.put(intertwined);
        }

        intertwinedBuf.rewind();
        gl.glInterleavedArrays(GL2.GL_C3F_V3F, 0, intertwinedBuf);
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

    public void mouseClicked(MouseEvent mouse) {
    }

    public void mousePressed(MouseEvent mouse) {
        if (mouse.getButton() == MouseEvent.BUTTON1) {
            if (setupMethod == POINTER) {
                setupMethod = INTERLEAVED;
                // setupInterleave(gl);don't call
            } else if (setupMethod == INTERLEAVED) {
                setupMethod = POINTER;
                // setupPointers(gl);
            }
            // validate();
        }
        if (mouse.getButton() == MouseEvent.BUTTON2
                || mouse.getButton() == MouseEvent.BUTTON3) {
            if (derefMethod == DRAWARRAY)
                derefMethod = ARRAYELEMENT;
            else if (derefMethod == ARRAYELEMENT)
                derefMethod = DRAWELEMENTS;
            else if (derefMethod == DRAWELEMENTS)
                derefMethod = DRAWARRAY;
            // validate();
        }
        super.refresh();
    }

    public void mouseReleased(MouseEvent mouse) {
    }

    public void mouseEntered(MouseEvent mouse) {
    }

    public void mouseExited(MouseEvent mouse) {
    }

    public void dispose(GLAutoDrawable arg0) {
         
    }

}
