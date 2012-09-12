package glredbook10;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
 * This is an illustration of the selection mode and name stack, which detects
 * whether objects which collide with a viewing volume. First, four triangles
 * and a rectangular box representing a viewing volume are drawn (drawScene
 * routine). The green triangle and yellow triangles appear to lie within the
 * viewing volume, but the red triangle appears to lie outside it. Then the
 * selection mode is entered (selectObjects routine). Drawing to the screen
 * ceases. To see if any collisions occur, the four triangles are called. In
 * this example, the green triangle causes one hit with the name 1, and the
 * yellow triangles cause one hit with the name 3.
 * 
 * @author Kiet Le (Java port) Ported to JOGL 2.x by Claudio Eduardo Goes
 */

public class select//
        extends GLSkeleton<GLJPanel>
        implements GLEventListener, KeyListener {
  private GLU glu; 
  private static final int BUFSIZE = 512;

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
      select demo = new select();
      //
      JFrame.setDefaultLookAndFeelDecorated(true);
      JFrame frame = new JFrame("select");
      frame.setSize(200, 200);
      frame.setLocationRelativeTo(null);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      frame.getContentPane().add(demo.drawable);
      frame.setVisible(true);
      demo.drawable.requestFocusInWindow();
 
  }

  public void init(GLAutoDrawable drawable)
  {
    GL2 gl = drawable.getGL().getGL2();
    glu = new GLU(); 
    //
    gl.glDepthFunc(GL.GL_LESS);
    gl.glEnable(GL.GL_DEPTH_TEST);
    gl.glShadeModel(GL2.GL_FLAT);
  }

  public void display(GLAutoDrawable drawable)
  {
    GL2 gl = drawable.getGL().getGL2();
    //
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    drawScene(gl);
    selectObjects(gl);
    gl.glFlush();
  }

  public void reshape(GLAutoDrawable drawable, int x, int y, int width,
      int height)
  {
    GL2 gl = drawable.getGL().getGL2();
    //
  }

  public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
      boolean deviceChanged)
  {
  }

  /*
   * draw a triangle with vertices at (x1, y1), (x2, y2) and (x3, y3) at z units
   * away from the origin.
   */
  private void drawTriangle(GL2 gl, float x1, float y1, float x2, float y2,
      float x3, float y3, float z)
  {
    gl.glBegin(GL2.GL_TRIANGLES);
    gl.glVertex3f(x1, y1, z);
    gl.glVertex3f(x2, y2, z);
    gl.glVertex3f(x3, y3, z);
    gl.glEnd();
  }

  /* draw a rectangular box with these outer x, y, and z values */
  private void drawViewVolume(GL2 gl, float x1, float x2, float y1, float y2,
      float z1, float z2)
  {
    gl.glColor3f(1.0f, 1.0f, 1.0f);
    gl.glBegin(GL.GL_LINE_LOOP);
    gl.glVertex3f(x1, y1, -z1);
    gl.glVertex3f(x2, y1, -z1);
    gl.glVertex3f(x2, y2, -z1);
    gl.glVertex3f(x1, y2, -z1);
    gl.glEnd();

    gl.glBegin(GL.GL_LINE_LOOP);
    gl.glVertex3f(x1, y1, -z2);
    gl.glVertex3f(x2, y1, -z2);
    gl.glVertex3f(x2, y2, -z2);
    gl.glVertex3f(x1, y2, -z2);
    gl.glEnd();

    gl.glBegin(GL.GL_LINES); /* 4 lines */
    gl.glVertex3f(x1, y1, -z1);
    gl.glVertex3f(x1, y1, -z2);
    gl.glVertex3f(x1, y2, -z1);
    gl.glVertex3f(x1, y2, -z2);
    gl.glVertex3f(x2, y1, -z1);
    gl.glVertex3f(x2, y1, -z2);
    gl.glVertex3f(x2, y2, -z1);
    gl.glVertex3f(x2, y2, -z2);
    gl.glEnd();
  }

  /*
   * drawScene draws 4 triangles and a wire frame which represents the viewing
   * volume.
   */
  void drawScene(GL2 gl)
  {
    gl.glMatrixMode(GL2.GL_PROJECTION);
    gl.glLoadIdentity();
    glu.gluPerspective(40.0, 4.0 / 3.0, 1.0, 100.0);

    gl.glMatrixMode(GL2.GL_MODELVIEW);
    gl.glLoadIdentity();
    glu.gluLookAt(7.5, 7.5, 12.5, 2.5, 2.5, -5.0, 0.0, 1.0, 0.0);
    gl.glColor3f(0.0f, 1.0f, 0.0f); /* green triangle */
    drawTriangle(gl, 2.0f, 2.0f, 3.0f, 2.0f, 2.5f, 3.0f, -5.0f);
    gl.glColor3f(1.0f, 0.0f, 0.0f); /* red triangle */
    drawTriangle(gl, 2.0f, 7.0f, 3.0f, 7.0f, 2.5f, 8.0f, -5.0f);
    gl.glColor3f(1.0f, 1.0f, 0.0f); /* yellow triangles */
    drawTriangle(gl, 2.0f, 2.0f, 3.0f, 2.0f, 2.5f, 3.0f, 0.0f);
    drawTriangle(gl, 2.0f, 2.0f, 3.0f, 2.0f, 2.5f, 3.0f, -10.0f);
    drawViewVolume(gl, 0.0f, 5.0f, 0.0f, 5.0f, 0.0f, 10.0f);
  }

  /*
   * processHits prints out the contents of the selection array
   */
  private void processHits(int hits, int buffer[])
  {
    int names;
    int ptr;

    System.out.println("hits = " + hits);
    // ptr = buffer;
    ptr = 0;
    for (int i = 0; i < hits; i++)
    { /* for each hit */
      names = buffer[i];
      System.out.println(" number of names for hit =  " + names);
      ptr++;
      System.out.print("  z1 is " + (float) buffer[ptr] / 0x7fffffff);
      ptr++;
      System.out.println(" z2 is " + (float) buffer[ptr] / 0x7fffffff);
      ptr++;
      System.out.print("\tthe name is ");
      for (int j = 0; j < buffer.length /* names */; j++)
      { /* for each name */
        System.out.print(" " + buffer[--ptr]);
        ptr++;
      }
      System.out.println();
    }
  }

  /*
   * selectObjects "draws" the triangles in selection mode, assigning names for
   * the triangles. Note that the third and fourth triangles share one name, so
   * that if either or both triangles intersects the viewing/clipping volume,
   * only one hit will be registered.
   */
  private void selectObjects(GL2 gl)
  {
    int selectBuf[] = new int[BUFSIZE];
    IntBuffer selectBuffer = GLBuffers.newDirectIntBuffer(BUFSIZE);
    int hits;

    gl.glSelectBuffer(BUFSIZE, selectBuffer);
    gl.glRenderMode(GL2.GL_SELECT);

    gl.glInitNames();
    gl.glPushName(0);

    gl.glPushMatrix();
    gl.glMatrixMode(GL2.GL_PROJECTION);
    gl.glLoadIdentity();
    gl.glOrtho(0.0, 5.0, 0.0, 5.0, 0.0, 10.0);
    gl.glMatrixMode(GL2.GL_MODELVIEW);
    gl.glLoadIdentity();
    gl.glLoadName(1);
    drawTriangle(gl, 2.0f, 2.0f, 3.0f, 2.0f, 2.5f, 3.0f, -5.0f);
    gl.glLoadName(2);
    drawTriangle(gl, 2.0f, 7.0f, 3.0f, 7.0f, 2.5f, 8.0f, -5.0f);
    gl.glLoadName(3);
    drawTriangle(gl, 2.0f, 2.0f, 3.0f, 2.0f, 2.5f, 3.0f, 0.0f);
    drawTriangle(gl, 2.0f, 2.0f, 3.0f, 2.0f, 2.5f, 3.0f, -10.0f);
    gl.glPopMatrix();
    gl.glFlush();

    hits = gl.glRenderMode(GL2.GL_RENDER);
    selectBuffer.get(selectBuf);
    processHits(hits, selectBuf);
  }

  public void keyTyped(KeyEvent key)
  {
  }

  public void keyPressed(KeyEvent key)
  {
    switch (key.getKeyCode()) {
      case KeyEvent.VK_ESCAPE:
        System.exit(0);
        break;
      default:
        break;
    }
  }

  public void keyReleased(KeyEvent key)
  {
  }

    public void dispose(GLAutoDrawable arg0) {
         
    }

}
