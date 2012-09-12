package glredbook10;

/**
 * Draws some text in using GlyphVector.<br>
 * This example is my replacement for xfont.c.
 * 
 * @author Kiet Le (Java port) Ported to JOGL 2.x by Claudio Eduardo Goes
 */

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.font.GlyphVector;
import java.awt.geom.PathIterator;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLJPanel;
import javax.swing.JFrame;

public class jfont //
        extends GLSkeleton<GLJPanel>
        implements GLEventListener, KeyListener {

    private JFrame frame;

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
        jfont demo = new jfont();

        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("jfont");
        frame.setSize(500, 500);
        demo.setJFrame(frame);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().add(demo.drawable);
        frame.setVisible(true);
        demo.drawable.requestFocusInWindow();
    }

    public void setJFrame(JFrame frame) {
        this.frame = frame;
    }

    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2(); 
        //

        gl.glShadeModel(GL2.GL_FLAT);
        gl.glEnable(GL2.GL_POLYGON_SMOOTH);
    }

    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glPushMatrix();
        gl.glTranslatef(frame.getWidth() / 2 - frame.getWidth() / 4, //
                frame.getHeight() / 2, 0);
        // GL has lower left origin compare java's upper left
        gl.glScalef(5, -5, 0);

        gl.glColor3f(0, 0, 1);
        drawString(gl, "OpenGL", true);

        gl.glTranslatef(-20, -20, 0);
        gl.glScalef(0.75f, 1, 0);
        gl.glColor3f(1, 0, 0);
        drawString(gl, "javax.media.opengl", false);

        gl.glTranslatef(20, 40, 0);
        drawString(gl, "OpenGL Everywhere", false);
        gl.glPopMatrix();
        gl.glFlush();
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(0, (float) w, 0, (float) h, -1.0, 1.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }

    private void drawString(GL2 gl, String s, boolean drawBounds) {
        Font font = new Font("Times", Font.PLAIN, 14);// getFont();
        System.out.println(font.toString());
        Graphics2D g2 = (Graphics2D) frame.getGraphics();

        //FontMetrics fontInfo = g2.getFontMetrics(font);
        GlyphVector gv = font.createGlyphVector(g2.getFontRenderContext(), s);
        Shape shape = gv.getOutline();
        // System.out.println(gv.toString());
        PathIterator itor = shape.getPathIterator(null, 0.01f);// very fine
        // grain
        int it = 0;
        float seg[] = new float[6];
        if (drawBounds)
            drawGlyphBounds(gl, shape.getBounds());
        gl.glBegin(GL.GL_LINE_LOOP);
        while (!itor.isDone()) {
            System.out.println(++it + " " + seg[0] + " " + seg[1]);
            itor.currentSegment(seg);
            gl.glVertex2f(seg[0], seg[1]);
            itor.next();
            gl.glColor3d(Math.random(), Math.random(), Math.random());
        }
        gl.glEnd();
    }

    private void drawGlyphBounds(GL2 gl, Rectangle r) {
        gl.glBegin(GL.GL_LINE_LOOP);
        gl.glVertex2f(r.x, r.y);
        gl.glVertex2f(r.x + r.width, r.y);
        gl.glVertex2f(r.x + r.width, r.height + r.y);
        gl.glVertex2f(r.x, r.y + r.height);
        gl.glEnd();
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
