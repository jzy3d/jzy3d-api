package examples.applets.glut;
/*
 * doublebuffer.java This is a simple double buffered program. Pressing the left mouse button
 * rotates the rectangle. Pressing the middle mouse button stops the rotation.
 */

import jgl.GL;
import jgl.wt.awt.GLApplet;
import jgl.wt.awt.GLUT;

// "double" is a reserved word in Java
public class doublebuffer extends GLApplet {

  private float spin = 0;

  public void display() {
    myGL.glClear(GL.GL_COLOR_BUFFER_BIT);
    myGL.glPushMatrix();
    myGL.glRotatef(spin, 0.0f, 0.0f, 1.0f);
    myGL.glColor3f(1.0f, 1.0f, 1.0f);
    myGL.glRectf(-25.0f, -25.0f, 25.0f, 25.0f);
    myGL.glPopMatrix();

    myGL.glFlush();
  }

  public void spinDisplay() {
    spin += 2.0f;
    if (spin > 360.0f)
      spin = spin - 360.0f;
    myUT.glutPostRedisplay();
  }

  private void myinit() {
    myGL.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    myGL.glShadeModel(GL.GL_FLAT);
  }

  public void myReshape(int w, int h) {
    myGL.glViewport(0, 0, w, h);
    myGL.glMatrixMode(GL.GL_PROJECTION);
    myGL.glLoadIdentity();
    myGL.glOrtho(-50.0f, 50.0f, -50.0f, 50.0f, -1.0f, 1.0f);
    myGL.glMatrixMode(GL.GL_MODELVIEW);
    myGL.glLoadIdentity();
  }

  /* ARGSUSED2 */
  public void mouse(int button, int state, int x, int y) {
    if (button == GLUT.GLUT_LEFT_BUTTON) {
      if (state == GLUT.GLUT_DOWN) {
        myUT.glutIdleFunc("spinDisplay");
      }
    } else if (button == GLUT.GLUT_MIDDLE_BUTTON) {
      if (state == GLUT.GLUT_DOWN) {
        myUT.glutIdleFunc(null);
      }
    }
  }

  /*
   * Request double buffer display mode. Register mouse input callback functions
   */
  public void init() {
    myUT.glutInitWindowSize(500, 500);
    myUT.glutInitWindowPosition(0, 0);
    myUT.glutCreateWindow(this);
    myinit();
    myUT.glutDisplayFunc("display");
    myUT.glutReshapeFunc("myReshape");
    myUT.glutMouseFunc("mouse");
    myUT.glutMainLoop();
  }

}
