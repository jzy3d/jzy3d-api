package examples.applets.glut;
/*
 * alpha.java This program draws several overlapping filled polygons to demonstrate the effect order
 * has on alpha blending results. Use the 't' key to toggle the order of drawing polygons.
 */

import jgl.GL;
import jgl.wt.awt.GLApplet;

public class alpha extends GLApplet {

  private boolean leftFirst = GL.GL_TRUE;

  /*
   * Initialize alpha blending function.
   */
  private void myinit() {
    myGL.glEnable(GL.GL_BLEND);
    myGL.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
    myGL.glShadeModel(GL.GL_FLAT);
    myGL.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
  }

  private void drawLeftTriangle() {
    /* draw yellow triangle on LHS of screen */

    myGL.glBegin(GL.GL_TRIANGLES);
    myGL.glColor4f(1.0f, 1.0f, 0.0f, 0.75f);
    myGL.glVertex3f(0.1f, 0.9f, 0.0f);
    myGL.glVertex3f(0.1f, 0.1f, 0.0f);
    myGL.glVertex3f(0.7f, 0.5f, 0.0f);
    myGL.glEnd();
  }

  private void drawRightTriangle() {
    /* draw cyan triangle on RHS of screen */

    myGL.glBegin(GL.GL_TRIANGLES);
    myGL.glColor4f(0.0f, 1.0f, 1.0f, 0.75f);
    myGL.glVertex3f(0.9f, 0.9f, 0.0f);
    myGL.glVertex3f(0.3f, 0.5f, 0.0f);
    myGL.glVertex3f(0.9f, 0.1f, 0.0f);
    myGL.glEnd();
  }

  public void display() {
    myGL.glClear(GL.GL_COLOR_BUFFER_BIT);

    if (leftFirst) {
      drawLeftTriangle();
      drawRightTriangle();
    } else {
      drawRightTriangle();
      drawLeftTriangle();
    }

    myGL.glFlush();
  }

  public void myReshape(int w, int h) {
    myGL.glViewport(0, 0, w, h);
    myGL.glMatrixMode(GL.GL_PROJECTION);
    myGL.glLoadIdentity();
    if (w <= h) {
      myGLU.gluOrtho2D(0.0, 1.0, 0.0, 1.0 * (float) h / (float) w);
    } else {
      myGLU.gluOrtho2D(0.0, 1.0 * (float) w / (float) h, 0.0, 1.0);
    }
  }

  public void keyboard(char key, int x, int y) {
    switch (key) {
      case 't':
      case 'T':
        leftFirst = !leftFirst;
        myUT.glutPostRedisplay();
        break;
      default:
        break;
    }
  }

  /*
   * Main Loop Open window with initial window size, title bar, RGBA display mode, and handle input
   * events.
   */
  public void init() {
    myUT.glutInitWindowSize(200, 200);
    myUT.glutInitWindowPosition(0, 0);
    myUT.glutCreateWindow(this);
    myinit();
    myUT.glutReshapeFunc("myReshape");
    myUT.glutKeyboardFunc("keyboard");
    myUT.glutDisplayFunc("display");
    myUT.glutMainLoop();
  }

}
