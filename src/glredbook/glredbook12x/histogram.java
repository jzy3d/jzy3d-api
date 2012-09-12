package glredbook12x;

import glredbook10.GLSkeleton;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataInputStream;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLJPanel;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.jogamp.opengl.util.GLBuffers;

/**
 * Compute the histogram of the image. This program illustrates the use of the
 * glHistogram() function.
 * 
 * @author Kiet Le (Java port) Ported to JOGL 2.x by Claudio Eduardo Goes
 */
public class histogram //
        extends GLSkeleton<GLJPanel>
        implements GLEventListener, KeyListener {
    private JFrame frame;
    private KeyEvent key;
    //
    private ByteBuffer pixels;
    // private int width; not reference as params...
    // private int height;...as are all Java primitives
    private Dimension dim = new Dimension(0, 0);
    private static final int HISTOGRAM_SIZE = 256;

    @Override
    protected GLJPanel createDrawable() {
        GLCapabilities caps = new GLCapabilities(null);
        //
        GLJPanel panel = new GLJPanel(caps);
        panel.addGLEventListener(this);
        panel.addKeyListener(this);
        return panel;
    }

    protected void setFrame(JFrame frame) {
        this.frame = frame;
    }

    public static void main(String[] args) {

        histogram demo = new histogram();

        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("histogram");
        frame.setSize(640, 480);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        demo.setFrame(frame);
        frame.getContentPane().add(demo.drawable);
        frame.setVisible(true);
        demo.drawable.requestFocusInWindow();
    }

    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        if (gl.isExtensionAvailable("GL_ARB_imaging") //
                && gl.isFunctionAvailable("glHistogram")) {
            gl.glHistogram(GL2.GL_HISTOGRAM, HISTOGRAM_SIZE, GL2.GL_RGB, false);
            gl.glEnable(GL2.GL_HISTOGRAM);
        } else {
            frame.setTitle("histogram: NO (optional) ARB Imaging Subset");

            SwingUtilities.updateComponentTreeUI(frame);
        }

        if (pixels == null) {
            pixels = readImage("Data/leeds.bin", dim);
            System.out.println(pixels.toString());
        }

    }

    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);

        boolean sink = false;
        if (key != null && gl.isFunctionAvailable("glHistogram"))
            switch (key.getKeyChar()) {
            case 's':
                if (gl.isExtensionAvailable("GL_ARB_imaging") && //
                        gl.isFunctionAvailable("glHistogram")) {
                    sink = !sink;
                    gl.glHistogram(GL2.GL_HISTOGRAM, HISTOGRAM_SIZE, GL2.GL_RGB,
                            sink);
                }
                break;

            }// key sw

        // short values[][] = new short[HISTOGRAM_SIZE][3];
        ShortBuffer values = GLBuffers.newDirectShortBuffer(HISTOGRAM_SIZE * 3);

        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glRasterPos2i(1, 1);
        gl.glDrawPixels(dim.width, dim.height, //
                GL2.GL_RGB, GL.GL_UNSIGNED_BYTE, pixels);

        if (gl.isExtensionAvailable("GL_ARB_imaging")//
                && gl.isFunctionAvailable("glGetHistogram")) {
            gl.glGetHistogram(GL2.GL_HISTOGRAM, true, GL2.GL_RGB,//
                    GL.GL_UNSIGNED_SHORT, values);
        }

        /* Plot histogram */
        gl.glBegin(GL.GL_LINE_STRIP);
        gl.glColor3f(1.0f, 0.0f, 0.0f);
        for (int i = 0; i < HISTOGRAM_SIZE; i++)
            gl.glVertex2s((short) i, values.get(i * 2));
        gl.glEnd();

        gl.glBegin(GL.GL_LINE_STRIP);
        gl.glColor3f(0.0f, 1.0f, 0.0f);
        for (int i = 0; i < HISTOGRAM_SIZE; i++)
            gl.glVertex2s((short) i, values.get(i * 2 + 1));// [i][1]);
        gl.glEnd();

        gl.glBegin(GL.GL_LINE_STRIP);
        gl.glColor3f(0.0f, 0.0f, 1.0f);
        for (int i = 0; i < HISTOGRAM_SIZE; i++)
            gl.glVertex2s((short) i, values.get(i * 2 + 2));// [i][2]);
        gl.glEnd();

        gl.glFlush();

    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(0, 256, 0, 10000, -1.0, 1.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }

    /**
     * @author Mike "Top Coder" Butler : fucking signed byte is gay!
     * @author Kiet "Abysmal Coder" Le : major. gay. primitive.
     */
    private ByteBuffer readImage(String filename, Dimension dim) {
        if (dim == null)
            dim = new Dimension(0, 0);
        ByteBuffer bytes = null;
        try {
            // InputStream is = getClass().getClassLoader()
            // .getResourceAsStream(filename);

            // FileInputStream fis = new FileInputStream(filename);
            DataInputStream dis = new DataInputStream(getClass()
                    .getClassLoader().getResourceAsStream(filename));
            // DataInputStream dis = new DataInputStream(fis);

            // int width = 0, height = 0;
            dim.width = dis.readInt();
            dim.height = dis.readInt();
            System.out.println("Creating buffer, width: " + dim.width
                    + " height: " + dim.height);
            // byte[] buf = new byte[3 * dim.height * dim.width];
            bytes = GLBuffers.newDirectByteBuffer(3 * dim.width * dim.height);
            for (int i = 0; i < bytes.capacity(); i++) {
                bytes.put(dis.readByte());
                // int b = dis.readByte();// dis.read();
                // System.out.print(b + " ");
                // if (i % 3 == 0) System.out.println();
                // bytes.put((byte) b);
                // System.out.print(bytes.get(i) + " . ");
                // if (i %3 ==0) System.out.println();
            }
            // fis.close();
            dis.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        bytes.rewind();
        return bytes;
    }

    public void keyTyped(KeyEvent key) {
    }

    public void keyPressed(KeyEvent key) {
        this.key = key;
        switch (key.getKeyCode()) {
        case KeyEvent.VK_ESCAPE:
            System.exit(0);
            break;
        }
        super.refresh();
    }

    public void keyReleased(KeyEvent key) {
    }

    public void dispose(GLAutoDrawable arg0) {
         
    }

}
