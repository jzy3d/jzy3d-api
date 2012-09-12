package glredbook10;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
 * Picking is demonstrated in this program. In rendering mode, three overlapping
 * rectangles are drawn. When the left mouse button is pressed, selection mode
 * is entered with the picking matrix. Rectangles which are drawn under the
 * cursor position are "picked." Pay special attention to the depth value range,
 * which is returned.
 * 
 * @author Kiet Le (Java port) Ported to JOGL 2.x by Claudio Eduardo Goes
 */
public class pickdepth//
        extends GLSkeleton<GLJPanel>
        implements GLEventListener, KeyListener, MouseListener {
    private GLU glu;
    private static final int BUFSIZE = 512;
    private Point pickPoint = new Point();

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
        pickdepth demo = new pickdepth();
        //
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("pickdepth");
        frame.setSize(100, 100);
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
        gl.glDepthFunc(GL.GL_LESS);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glShadeModel(GL2.GL_FLAT);
        gl.glDepthRange(0.0, 1.0); /* The default z mapping */
    }

    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        pickRects(gl);
        drawRects(gl, GL2.GL_RENDER);
        gl.glFlush();
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(0.0, 8.0, 0.0, 8.0, -0.5, 2.5);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }

    /*
     * The three rectangles are drawn. In selection mode, each rectangle is
     * given the same name. Note that each rectangle is drawn with a different z
     * value.
     */
    private void drawRects(GL2 gl, int mode) {
        if (mode == GL2.GL_SELECT)
            gl.glLoadName(1);
        gl.glBegin(GL2.GL_QUADS);
        gl.glColor3f(1.0f, 1.0f, 0.0f);
        gl.glVertex3i(2, 0, 0);
        gl.glVertex3i(2, 6, 0);
        gl.glVertex3i(6, 6, 0);
        gl.glVertex3i(6, 0, 0);
        gl.glColor3f(0.0f, 1.0f, 1.0f);
        gl.glVertex3i(3, 2, -1);
        gl.glVertex3i(3, 8, -1);
        gl.glVertex3i(8, 8, -1);
        gl.glVertex3i(8, 2, -1);
        gl.glColor3f(1.0f, 0.0f, 1.0f);
        gl.glVertex3i(0, 2, -2);
        gl.glVertex3i(0, 7, -2);
        gl.glVertex3i(5, 7, -2);
        gl.glVertex3i(5, 2, -2);
        gl.glEnd();
    }

    /*
     * prints out the contents of the selection array.
     */
    private void processHits(int hits, int buffer[]) {
        int names, ptr = 0;

        System.out.println("hits = " + hits);
        // ptr = (GLuint *) buffer;
        for (int i = 0; i < hits; i++) { /* for each hit */
            names = buffer[ptr];
            System.out.println(" number of names for hit = " + names);
            ptr++;
            System.out.println("  z1 is " + buffer[ptr]);
            ptr++;
            System.out.println(" z2 is " + buffer[ptr]);
            ptr++;
            System.out.print("\n   the name is ");
            for (int j = 0; j < names; j++) { /* for each name */
                System.out.println("" + buffer[ptr]);
                ptr++;
            }
            System.out.println();
        }
    }

    /*
     * sets up selection mode, name stack, and projection matrix for picking.
     * Then the objects are drawn.
     */
    private void pickRects(GL2 gl) {
        int[] selectBuf = new int[BUFSIZE];
        IntBuffer selectBuffer = GLBuffers.newDirectIntBuffer(BUFSIZE);
        int hits;
        int viewport[] = new int[4];
        // int x, y;

        gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);

        gl.glSelectBuffer(BUFSIZE, selectBuffer);
        gl.glRenderMode(GL2.GL_SELECT);

        gl.glInitNames();
        gl.glPushName(-1);

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        /* create 5x5 pixel picking region near cursor location */
        glu.gluPickMatrix((double) pickPoint.x,
                (double) (viewport[3] - pickPoint.y), //
                5.0, 5.0, viewport, 0);
        gl.glOrtho(0.0, 8.0, 0.0, 8.0, -0.5, 2.5);
        drawRects(gl, GL2.GL_SELECT);
        gl.glPopMatrix();
        gl.glFlush();

        hits = gl.glRenderMode(GL2.GL_RENDER);
        selectBuffer.get(selectBuf);
        processHits(hits, selectBuf);
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
        pickPoint = mouse.getPoint();

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
