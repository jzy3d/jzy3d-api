package examples.apps;
/*
 * lines.java This program demonstrates geometric primitives and their attributes.
 */

import java.awt.Frame;
import java.io.IOException;
import jgl.GL;
import jgl.wt.awt.GLCanvas;

public class lines extends GLCanvas {

  private void drawOneLine(float x1, float y1, float x2, float y2) {
    myGL.glBegin(GL.GL_LINES);
    myGL.glVertex2f(x1, y1);
    myGL.glVertex2f(x2, y2);
    myGL.glEnd();
  }

  private void myinit() {
    myGL.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    myGL.glShadeModel(GL.GL_FLAT);
  }

  public void display() {
    int i;

    myGL.glClear(GL.GL_COLOR_BUFFER_BIT);

    /* select white for all lines */
    myGL.glColor3f(1.0f, 1.0f, 1.0f);

    /* in 1st row, 3 lines, each with a different stipple */
    myGL.glEnable(GL.GL_LINE_STIPPLE);

    myGL.glLineStipple(1, (short) 0x0101); /* dotted */
    drawOneLine(50.0f, 125.0f, 150.0f, 125.0f);
    myGL.glLineStipple(1, (short) 0x00FF); /* dashed */
    drawOneLine(150.0f, 125.0f, 250.0f, 125.0f);
    myGL.glLineStipple(1, (short) 0x1C47); /* dash/dot/dash */
    drawOneLine(250.0f, 125.0f, 350.0f, 125.0f);

    /* in 2nd row, 3 width lines, each with different stipple */
    myGL.glLineWidth(5.0f);
    myGL.glLineStipple(1, (short) 0x0101);
    drawOneLine(50.0f, 100.0f, 150.0f, 100.0f);
    myGL.glLineStipple(1, (short) 0x00FF);
    drawOneLine(150.0f, 100.0f, 250.0f, 100.0f);
    myGL.glLineStipple(1, (short) 0x1C47);
    drawOneLine(250.0f, 100.0f, 350.0f, 100.0f);
    myGL.glLineWidth(1.0f);

    /* in 3rd row, 6 lines, with dash/dot/dash stipple, */
    /* as part of a single connected line strip */
    myGL.glLineStipple(1, (short) 0x1C47); /* dash/dot/dash */
    myGL.glBegin(GL.GL_LINE_STRIP);
    for (i = 0; i < 7; i++)
      myGL.glVertex2f(50.0f + ((float) i * 50.0f), 75.0f);
    myGL.glEnd();

    /* in 4th row, 6 independent lines, */
    /* with dash/dot/dash stipple */
    for (i = 0; i < 6; i++) {
      drawOneLine(50.0f + ((float) i * 50.0f), 50.0f, 50.0f + ((float) (i + 1) * 50.0f), 50.0f);
    }

    /* in 5th row, 1 line, with dash/dot/dash stipple */
    /* and repeat factor of 5 */
    myGL.glLineStipple(1, (short) 0x1C47); /* dash/dot/dash */
    drawOneLine(50.0f, 25.0f, 350.0f, 25.0f);

    myGL.glDisable(GL.GL_LINE_STIPPLE);
    myGL.glFlush();
  }

  public void myReshape(int w, int h) {
    myGL.glViewport(0, 0, w, h);
    myGL.glMatrixMode(GL.GL_PROJECTION);
    myGL.glLoadIdentity();
    myGLU.gluOrtho2D(0.0, (double) w, 0.0, (double) h);
  }

  /* ARGSUSED1 */
  public void keyboard(char key, int x, int y) {
    switch (key) {
      case 27:
        System.exit(0);
      default:
        break;
    }
  }

  public void init() {
    myUT.glutInitWindowSize(450, 150);
    myUT.glutInitWindowPosition(0, 0);
    myUT.glutCreateWindow(this);
    myinit();
    myUT.glutDisplayFunc("display");
    myUT.glutReshapeFunc("myReshape");
    myUT.glutKeyboardFunc("keyboard");
    myUT.glutMainLoop();
  }

  static public void main(String args[]) throws IOException {
    Frame mainFrame = new Frame();
    mainFrame.setSize(458, 177);
    lines mainCanvas = new lines();
    mainCanvas.init();
    mainFrame.add(mainCanvas);
    mainFrame.setVisible(true);
  }

}
