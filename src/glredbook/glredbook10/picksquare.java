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
 * Use of multiple names and picking are demonstrated. A 3x3 grid of squares is
 * drawn. When the left mouse button is pressed, all squares under the cursor
 * position have their color changed.
 * 
 * @author Kiet Le (Java port) Ported to JOGL 2.x by Claudio Eduardo Goes
 */
public class picksquare//
        extends GLSkeleton<GLJPanel>
        implements GLEventListener, KeyListener, MouseListener {
    private GLU glu;
    //
    private int board[][] = new int[3][3]; /* amount of color for each square */
    private static final int BUFSIZE = 512;
    private Point pickPoint;

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

        picksquare demo = new picksquare();
        //
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("picksquare");
        frame.setSize(512, 256);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().add(demo.drawable);
        frame.setVisible(true);
        demo.drawable.requestFocusInWindow();
    }

    /* Clear color value for every square on the board */
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        glu = new GLU();
        //
        int i, j;
        for (i = 0; i < 3; i++)
            for (j = 0; j < 3; j++)
                board[i][j] = 0;
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }

    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        if (pickPoint != null)
            pickSquares(gl);

        drawSquares(gl, GL2.GL_RENDER);

        gl.glFlush();
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluOrtho2D(0.0, 3.0, 0.0, 3.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();

    }

    public void displayChanged(GLAutoDrawable drawable,//
            boolean modeChanged, boolean deviceChanged) {
    }

    /*
     * The nine squares are drawn. In selection mode, each square is given two
     * names: one for the row and the other for the column on the grid. The
     * color of each square is determined by its position on the grid, and the
     * value in the board[][] array.
     */
    private void drawSquares(GL2 gl, int mode) {
        int i, j;
        for (i = 0; i < 3; i++) {
            if (mode == GL2.GL_SELECT)
                gl.glLoadName(i);
            for (j = 0; j < 3; j++) {
                if (mode == GL2.GL_SELECT)
                    gl.glPushName(j);
                gl.glColor3f((float) i / 3.0f, (float) j / 3.0f,
                        (float) board[i][j] / 3.0f);
                gl.glRecti(i, j, i + 1, j + 1);
                if (mode == GL2.GL_SELECT)
                    gl.glPopName();
            }
        }
    }

    /*
     * processHits prints out the contents of the selection array.
     */
    private void processHits(int hits, int buffer[]) {
        int i, j;
        int ii = 0, jj = 0, names, ptr = 0;

        System.out.println("hits =  " + hits);
        // ptr = (GLuint *) buffer;
        for (i = 0; i < hits; i++) { /* for each hit */
            names = buffer[ptr];
            System.out.println(" number of names for this hit = " + names);
            ptr++;
            System.out.println("  z1 is  " + (float) buffer[ptr] / 0x7fffffff);
            ptr++;
            System.out.println(" z2 is " + (float) buffer[ptr] / 0x7fffffff);
            ptr++;
            System.out.println("   names are ");
            for (j = 0; j < names; j++) { /* for each name */
                System.out.println("" + buffer[ptr]);
                if (j == 0) /* set row and column */
                    ii = buffer[ptr];
                else if (j == 1)
                    jj = buffer[ptr];
                ptr++;
            }
            System.out.println("\n");
            board[ii][jj] = (board[ii][jj] + 1) % 3;
        }
    }

    /*
     * pickSquares() sets up selection mode, name stack, and projection matrix
     * for picking. Then the objects are drawn.
     */
    // private void pickSquares(GL2 gl, int button, int state, int x, int y)
    private void pickSquares(GL2 gl) {
        int selectBuf[] = new int[BUFSIZE];
        IntBuffer selectBuffer = GLBuffers.newDirectIntBuffer(BUFSIZE);
        int hits;
        int viewport[] = new int[4];

        // if (button != GLUT_LEFT_BUTTON || state != GLUT_DOWN) return;

        gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);

        gl.glSelectBuffer(BUFSIZE, selectBuffer);
        gl.glRenderMode(GL2.GL_SELECT);

        gl.glInitNames();
        gl.glPushName(0);

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        /* create 5x5 pixel picking region near cursor location */
        glu.gluPickMatrix((double) pickPoint.x,
                (double) (viewport[3] - pickPoint.y),// 
                5.0, 5.0, viewport, 0);
        glu.gluOrtho2D(0.0, 3.0, 0.0, 3.0);
        drawSquares(gl, GL2.GL_SELECT);

        gl.glMatrixMode(GL2.GL_PROJECTION);
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

    public void mouseClicked(MouseEvent arg0) {
    }

    public void mousePressed(MouseEvent mouse) {
        if (mouse.getButton() == MouseEvent.BUTTON1) //
        {
            pickPoint = mouse.getPoint();
            super.refresh();
        }
    }

    public void mouseReleased(MouseEvent arg0) {
    }

    public void mouseEntered(MouseEvent arg0) {
    }

    public void mouseExited(MouseEvent arg0) {
    }

    public void dispose(GLAutoDrawable arg0) {
         
    }

}
