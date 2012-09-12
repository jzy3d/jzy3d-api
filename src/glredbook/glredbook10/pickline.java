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
public class pickline//
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
        pickline demo = new pickline();

        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("pickline");
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
        gl.glShadeModel(GL2.GL_FLAT);
    }

    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        pickLine(gl);
        drawLine(gl, GL2.GL_RENDER);

        gl.glFlush();
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL2 gl = drawable.getGL().getGL2();
        glu = new GLU();
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

    private void drawLine(GL2 gl, int mode) {
        if (mode == GL2.GL_SELECT)
            gl.glLoadName(1);
        gl.glBegin(GL.GL_LINES);
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glVertex3f(30.0f, 30.0f, 0.0f);
        gl.glVertex3f(50.0f, 60.0f, 0.0f);
        gl.glEnd();

        if (mode == GL2.GL_SELECT)
            gl.glLoadName(2);
        gl.glBegin(GL.GL_LINES);
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glVertex3f(50.0f, 60.0f, 0.0f);
        gl.glVertex3f(70.0f, 40.0f, 0.0f);
        gl.glEnd();
    }

    /*
     * pickline() is called when the mouse is pressed. The projection matrix is
     * reloaded to include the picking matrix. The line is "redrawn" during
     * selection mode, and names are sent to the buffer.
     */
    private void pickLine(GL2 gl) {
        int selectBuf[] = new int[BUFSIZE];
        IntBuffer selectBuffer = GLBuffers.newDirectIntBuffer(BUFSIZE);
        int hits;
        int viewport[] = new int[4];

        gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);

        gl.glSelectBuffer(BUFSIZE, selectBuffer);
        gl.glRenderMode(GL2.GL_SELECT);

        gl.glInitNames();
        gl.glPushName(-1);

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        glu.gluPickMatrix((double) pickPoint.x,
                (double) (viewport[3] - pickPoint.y),// 
                5.0, 5.0, viewport, 0);
        glu.gluOrtho2D(0.0, (double) viewport[2], //
                0.0, (double) viewport[3]);
        drawLine(gl, GL2.GL_SELECT);
        gl.glPopMatrix();
        gl.glFlush();

        hits = gl.glRenderMode(GL2.GL_RENDER);
        selectBuffer.get(selectBuf);
        System.out.println("hits is " + hits);
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
