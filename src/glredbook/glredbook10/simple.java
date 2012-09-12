package glredbook10;
/**
 * This program draws a white rectangle on a black background.
 * @author Kiet Le (Java port) Ported to JOGL 2.x by Claudio Eduardo Goes
 */
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLJPanel;
import javax.swing.JFrame;


public class simple
{
  public static void main(String[] args)
  {
    /*
     * remove/commet out to be able to resize window smaller
     */
    JFrame.setDefaultLookAndFeelDecorated(true);
    // name of class as title
    JFrame jframe = new JFrame("simple");
    jframe.setSize(500, 500);
    jframe.setLocationRelativeTo(null); // center of screen

    GLJPanel canvas = new GLJPanel();
    //GLJPanel jcanvas = new GLJPanel();
    // anonymous object of GLEventListener interface
//    jcanvas.addGLEventListener(new GLEventListener()
    canvas.addGLEventListener(new GLEventListener()
    {
      public void init(GLAutoDrawable drawable)
      {
        // TODO Auto-generated method stub
      }

      public void display(GLAutoDrawable drawable)
      {
        GL2 gl = drawable.getGL().getGL2();
        //
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(-1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f);
        gl.glBegin(GL2.GL_POLYGON);
        gl.glVertex2f(-0.5f, -0.5f);
        gl.glVertex2f(-0.5f, 0.5f);
        gl.glVertex2f(0.5f, 0.5f);
        gl.glVertex2f(0.5f, -0.5f);
        gl.glEnd();
        gl.glFlush();
      }

      public void reshape(GLAutoDrawable drawable, int x, int y, int width,
          int height)
      {
        // TODO Auto-generated method stub
      }

      public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
          boolean deviceChanged)
      {
        // TODO Auto-generated method stub
      }

            public void dispose(GLAutoDrawable arg0) {
                 
            }
    });

    jframe.getContentPane().add(canvas);// put the canvas into a JFrame window
    //jframe.add(jcanvas);
    jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    jframe.setVisible(true); // show window
  }
}
