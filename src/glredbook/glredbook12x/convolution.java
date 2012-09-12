package glredbook12x;

import glredbook10.GLSkeleton;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataInputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

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
 * Use various 2D convolutions filters to find edges in an image.
 * 
 * @author Kiet Le (Java port) Ported to JOGL 2.x by Claudio Eduardo Goes
 */
public class convolution//
        extends GLSkeleton<GLJPanel>
        implements GLEventListener, KeyListener {

    private JFrame frame;
    private KeyEvent key;
    //
    private ByteBuffer pixels;
    // private int width; not reference as params...
    // private int height;...as are all Java primitives
    private Dimension dim = new Dimension(0, 0);
    private float horizontal[][] = { { 0, -1, 0 }, { 0, 1, 0 }, { 0, 0, 0 } };

    private float vertical[][] = { { 0, 0, 0 }, { -1, 1, 0 }, { 0, 0, 0 } };

    private float laplacian[][] = { { -0.125f, -0.125f, -0.125f },
            { -0.125f, 1.0f, -0.125f }, { -0.125f, -0.125f, -0.125f } };

    private FloatBuffer horizontalBuf = GLBuffers.newDirectFloatBuffer(horizontal.length * horizontal[0].length);
    private FloatBuffer verticalBuf = GLBuffers.newDirectFloatBuffer(vertical.length * vertical[0].length);
    private FloatBuffer laplacianBuf = GLBuffers.newDirectFloatBuffer(laplacian.length * laplacian[0].length);
    {
        for (int i = 0; i < 3; i++) {
            horizontalBuf.put(horizontal[i]);
            verticalBuf.put(vertical[i]);
            laplacianBuf.put(laplacian[i]);
        }
        horizontalBuf.rewind();
        verticalBuf.rewind();
        laplacianBuf.rewind();
    }

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
        GLCapabilities caps = new GLCapabilities(null);

        convolution demo = new convolution();

        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("convolution");
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

        if (gl.isExtensionAvailable("GL_ARB_imaging")) {
            if (gl.isFunctionAvailable("glConvolutionFilter2D")) {
                System.out.println("Using the horizontal filter");
                gl.glConvolutionFilter2D(GL2.GL_CONVOLUTION_2D, GL.GL_LUMINANCE, //
                        3, 3, GL.GL_LUMINANCE, GL.GL_FLOAT, horizontalBuf);
                gl.glEnable(GL2.GL_CONVOLUTION_2D);
            }
        } else {
            frame.setTitle("convolution: NO ARB Imaging Subset");
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

        if (gl.isFunctionAvailable("glConvolutionFilter2D"))
            if (key != null)
                switch (key.getKeyChar()) {
                case 'h':
                    System.out.println("Using a horizontal filter");
                    gl.glConvolutionFilter2D(GL2.GL_CONVOLUTION_2D,
                            GL.GL_LUMINANCE, //
                            3, 3, GL.GL_LUMINANCE, GL.GL_FLOAT, horizontalBuf);
                    break;

                case 'v':
                    System.out.println("Using the vertical filter\n");
                    gl.glConvolutionFilter2D(GL2.GL_CONVOLUTION_2D,
                            GL.GL_LUMINANCE, //
                            3, 3, GL.GL_LUMINANCE, GL.GL_FLOAT, verticalBuf);
                    break;

                case 'l':
                    System.out.println("Using the laplacian filter\n");
                    gl.glConvolutionFilter2D(GL2.GL_CONVOLUTION_2D,
                            GL.GL_LUMINANCE, //
                            3, 3, GL.GL_LUMINANCE, GL.GL_FLOAT, laplacianBuf);
                    break;

                }// key sw

        gl.glRasterPos2i(1, 1);
        gl.glDrawPixels(dim.width, dim.height, //
                GL2.GL_RGB, GL.GL_UNSIGNED_BYTE, pixels);

        gl.glFlush();

    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(0, w, 0, h, -1.0, 1.0);
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
