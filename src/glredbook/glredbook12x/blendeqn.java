package glredbook12x;

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
 * Demonstrate the different blending functions available with the OpenGL
 * imaging subset. This program demonstrates use of the glBlendEquation() call.
 * The following keys change the selected blend equation function: <br>
 * <ul>
 * <li>'a' -> GL_FUNC_ADD
 * <li>'s' -> GL_FUNC_SUBTRACT
 * <li>'r' -> GL_FUNC_REVERSE_SUBTRACT
 * <li>'m' -> GL_MIN 'x' -> GL_MAX
 * </ul>
 * 
 * @author Kiet Le (Java port) Ported to JOGL 2.x by Claudio Eduardo Goes
 */


public class blendeqn//
        extends GLSkeleton<GLJPanel>
        implements GLEventListener, KeyListener {
  private KeyEvent key;

    @Override
    protected GLJPanel createDrawable() {
        GLCapabilities caps = new GLCapabilities(null);
        //
        GLJPanel panel = new GLJPanel(caps);
        panel.addGLEventListener(this);
        panel.addKeyListener(this);
        return panel;
    }
 
  public static void main(String[] args)
  {
      blendeqn demo = new blendeqn();

      JFrame.setDefaultLookAndFeelDecorated(true);
      JFrame frame = new JFrame("blendeqn");
      frame.setSize(640, 480);
      frame.setLocationRelativeTo(null);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
      frame.getContentPane().add(demo.drawable);
      frame.setVisible(true);
      demo.drawable.requestFocusInWindow();
  }

  public void init(GLAutoDrawable drawable)
  {
    GL2 gl = drawable.getGL().getGL2();
    //
    gl.glClearColor(1.0f, 1.0f, 0.0f, 0.0f);

    gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE);
    gl.glEnable(GL.GL_BLEND);
    
    System.out.println("blend available:"+gl.isFunctionAvailable("glBlendFunc"));
  }

  public void display(GLAutoDrawable drawable)
  {
    GL2 gl = drawable.getGL().getGL2();
    //
    gl.glClear(GL.GL_COLOR_BUFFER_BIT);

    if (key != null)
    {
      switch (key.getKeyCode()) {
        case KeyEvent.VK_A:
          /*
           * Colors are added as: (1, 1, 0) + (0, 0, 1) = (1, 1, 1) which will
           * produce a white square on a yellow background.
           */
          gl.glBlendEquation(GL.GL_FUNC_ADD);
          break;

        case KeyEvent.VK_S:
          /*
           * Colors are subtracted as: (0, 0, 1) - (1, 1, 0) = (-1, -1, 1) which
           * is clamped to (0, 0, 1), producing a blue square on a yellow
           * background
           */
          gl.glBlendEquation(GL.GL_FUNC_SUBTRACT);
          break;

        case KeyEvent.VK_R:
          /*
           * Colors are subtracted as: (1, 1, 0) - (0, 0, 1) = (1, 1, -1) which
           * is clamed to (1, 1, 0). This produces yellow for both the square
           * and the background.
           */
          gl.glBlendEquation(GL.GL_FUNC_REVERSE_SUBTRACT);
          break;

        case KeyEvent.VK_M:

          /*
           * The minimum of each component is computed, as [min(1, 0), min(1,
           * 0), min(0, 1)] which equates to (0, 0, 0). This will produce a
           * black square on the yellow background.
           */
          gl.glBlendEquation(GL2.GL_MIN);
          break;

        case KeyEvent.VK_X:
          /*
           * The minimum of each component is computed, as [max(1, 0), max(1,
           * 0), max(0, 1)] which equates to (1, 1, 1) This will produce a white
           * square on the yellow background.
           */
          gl.glBlendEquation(GL2.GL_MAX);
          break;  
      }
      key = null;
    }
    gl.glColor3f(0.0f, 0.0f, 1.0f);
    gl.glRectf(-0.5f, -0.5f, 0.5f, 0.5f);

    gl.glFlush();

  }

  public void reshape(GLAutoDrawable drawable, //
      int x, int y, int w, int h)
  {
    GL2 gl = drawable.getGL().getGL2();
    //
    double aspect = (double) w / (double) h;

    gl.glViewport(0, 0, w, h);
    gl.glMatrixMode(GL2.GL_PROJECTION);
    gl.glLoadIdentity();
    if (aspect < 1.0)
    {
      aspect = 1.0 / aspect;
      gl.glOrtho(-aspect, aspect, -1.0, 1.0, -1.0, 1.0);
    }
    else gl.glOrtho(-1.0, 1.0, -aspect, aspect, -1.0, 1.0);
    gl.glMatrixMode(GL2.GL_MODELVIEW);

  }

  public void displayChanged(GLAutoDrawable drawable, //
      boolean deviceChanged, boolean modeChanged)
  {
  }

  public void keyTyped(KeyEvent e)
  {
  }

  public void keyPressed(KeyEvent e)
  {
    this.key = e;
    switch (e.getKeyCode()) {
      case KeyEvent.VK_ESCAPE:
        System.exit(0);
        break;
    }
        super.refresh();
  }

  public void keyReleased(KeyEvent e)
  {
  }

    public void dispose(GLAutoDrawable arg0) {
         
    }
}
