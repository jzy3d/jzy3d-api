package jgl;

import org.junit.Test;

import jgl.context.gl_context;
import jgl.context.gl_util;
import jgl.wt.awt.GLCanvas;
import jgl.wt.awt.GLU;
import jgl.wt.awt.GLUT;
import junit.framework.Assert;

public class TestGL_polygonOffsetFill {
  int WIDTH = 500;
  int HEIGHT = 500;

  /**
   * <img src="../target/TestGL_polygonOffsetFill.png"/>
   */
  @Test
  public void whenInvokePolygon_ThenTwoTrianglesAreDrawn_BUT_Visible_WhiteDiagonal_IfNonOpaque() {

    float ALPHA = 0.95f;

    // ----------------------------------------------------
    // Given openGL
    GLCanvas canvas = new GLCanvas();
    GL gl = canvas.getGL();
    GLU glu = canvas.getGLU();
    GLUT glut = canvas.getGLUT();

    glut.glutInitWindowSize(WIDTH, HEIGHT);
    glut.glutCreateWindow(canvas); // register canvas for rendering image into it.

    // ----------------------------------------------------
    // Set parameters for alpha blending

    gl.glEnable(GL.GL_ALPHA_TEST);
    gl.glEnable(GL.GL_BLEND);
    gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
    gl.glShadeModel(GL.GL_FLAT);

    // ----------------------------------------------------
    // When defining the clear color

    gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f); // White background
    gl.glClear(GL.GL_COLOR_BUFFER_BIT);

    // ----------------------------------------------------
    // When defining viewport and calling glClear

    gl.glMatrixMode(GL.GL_PROJECTION);
    gl.glLoadIdentity();
    glu.gluOrtho2D(0.0, 1.0, 0.0, 1.0); // look from top to the Z=0 plane
    gl.glViewport(0, 0, WIDTH, HEIGHT); // a 100x100 color buffer

    // --------------------------------------------------
    // When drawing a polygon

    gl.glMatrixMode(GL.GL_MODELVIEW);
    gl.glLoadIdentity();

    gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
    gl.glBegin(GL.GL_POLYGON);

    // gl.glPolygonMode(frontOrBack, fill);

    gl.glColor4f(1, 1, 0, ALPHA); // create translucent polygon with non opaque alpha
    gl.glVertex3f(0.1f, 0.1f, 0); // draw a polygon on the Z=0 plane
    gl.glVertex3f(0.9f, 0.1f, 0);
    gl.glVertex3f(0.9f, 0.9f, 0);
    gl.glVertex3f(0.1f, 0.9f, 0);
    gl.glEnd();


    Assert.assertEquals(0, countBlackPixels(gl.getContext()));

    gl.glFlush();
	Util.debugWriteImageTo(gl, "target/" + getClass().getSimpleName() + ".png");


    // Then there should be no BLACK pixel


  }

  private int countBlackPixels(gl_context Context) {
    int a0 = 0;
    int a255 = 0;
    int black = 0;
    int notBlack = 0;

    for (int i = 0; i < Context.ColorBuffer.Buffer.length; i++) {
      int color = Context.ColorBuffer.Buffer[i];
      int r = gl_util.ItoR(color);
      int g = gl_util.ItoG(color);
      int b = gl_util.ItoB(color);
      int a = gl_util.ItoA(color);


      // System.out.println(color + " r:" + r + " g:" + g + " b:" + b);

      // CHECK NUMBER OF BLACK PIXEL == ALPHA PIXELS

      if (r == 0 && g == 0 && b == 0 && a != 0)
        black++;
      else
        notBlack++;

      if (a == 0)
        a0++;
      if (a == 255)
        a255++;
    }

    return black;
    // System.out.println("glFlush() #translucent = " + a0 + " #opaque = " + a255 + " #black=" +
    // black + " #notBlack=" + notBlack);
  }
}
