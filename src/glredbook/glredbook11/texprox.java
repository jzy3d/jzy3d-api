package glredbook11;

import glredbook10.GLSkeleton;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLJPanel;
import javax.swing.JFrame;

/**
 * The brief program illustrates use of texture proxies. This program only
 * prints out some messages about whether certain size textures are supported
 * and then exits.
 * 
 * @author Kiet Le (Java port) Ported to JOGL 2.x by Claudio Eduardo Goes
 */
public class texprox//
        extends GLSkeleton<GLJPanel>
        implements GLEventListener, KeyListener {

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
        texprox demo = new texprox();

        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("texprox");
        frame.setSize(400, 400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().add(demo.drawable);
        frame.setVisible(true);
        demo.drawable.requestFocusInWindow();

        System.out.println("This program demonstrates a feature "
                + "which is not in OpenGL Version 1.0.");
        System.out.println("If your implementation of OpenGL "
                + "Version 1.0 has the right extensions,");
        System.out.println("you may be able to modify this "
                + "program to make it run.");
    }

    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        //
        int proxyComponents[] = new int[1];

        gl.glTexImage2D(GL2.GL_PROXY_TEXTURE_2D, 0, GL2.GL_RGBA8, 64, 64, 0,
                GL2.GL_RGBA, GL.GL_UNSIGNED_BYTE, null);
        gl.glGetTexLevelParameteriv(GL2.GL_PROXY_TEXTURE_2D, 0,
                GL2.GL_TEXTURE_COMPONENTS, proxyComponents, 0);
        System.out.println("proxyComponents are " + proxyComponents[0]);
        if (proxyComponents[0] == GL2.GL_RGBA8) //
            System.out.println("proxy allocation succeeded");
        else
            System.out.println("proxy allocation failed");
        //
        gl.glTexImage2D(GL2.GL_PROXY_TEXTURE_2D, 0, GL2.GL_RGBA16, 2048, 2048, 0,
                GL2.GL_RGBA, GL.GL_UNSIGNED_SHORT, null);
        gl.glGetTexLevelParameteriv(GL2.GL_PROXY_TEXTURE_2D, 0,
                GL2.GL_TEXTURE_COMPONENTS, proxyComponents, 0);
        System.out.println("proxyComponents are " + proxyComponents[0]);
        if (proxyComponents[0] == GL2.GL_RGBA16) //
            System.out.println("proxy allocation succeeded");
        else
            System.out.println("proxy allocation failed");
    }

    public void display(GLAutoDrawable drawable) {
        drawable.getGL().glClear(GL.GL_COLOR_BUFFER_BIT);
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged) {
    }

    public void keyTyped(KeyEvent key) {
    }

    public void keyPressed(KeyEvent key) {
        switch (key.getKeyCode()) {
        case KeyEvent.VK_ESCAPE:
            System.exit(0);
        default:
            break;
        }
    }

    public void keyReleased(KeyEvent key) {
    }

    public void dispose(GLAutoDrawable arg0) {
         
    }
}
