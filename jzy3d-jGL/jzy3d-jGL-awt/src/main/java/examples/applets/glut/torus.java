package examples.applets.glut;
/*
 * torus.java This program demonstrates the creation of a display list.
 */

import jgl.GL;
import jgl.wt.awt.GLApplet;

public class torus extends GLApplet {

  private int theTorus;

  /* Draw a torus */
  private void torus(int numc, int numt) {
    int i, j, k;
    double s, t, x, y, z, twopi;

    twopi = 2 * Math.PI;
    for (i = 0; i < numc; i++) {
      myGL.glBegin(GL.GL_QUAD_STRIP);
      for (j = 0; j <= numt; j++) {
        for (k = 1; k >= 0; k--) {
          s = (i + k) % numc + 0.5;
          t = j % numt;

          x = (1 + .1 * Math.cos(s * twopi / numc)) * Math.cos(t * twopi / numt);
          y = (1 + .1 * Math.cos(s * twopi / numc)) * Math.sin(t * twopi / numt);
          z = .1 * Math.sin(s * twopi / numc);
          myGL.glVertex3f((float) x, (float) y, (float) z);
        }
      }
      myGL.glEnd();
    }
  }

  /* Create display list with Torus and initialize state */
  private void myinit() {
    theTorus = myGL.glGenLists(1);
    myGL.glNewList(theTorus, GL.GL_COMPILE);
    torus(8, 25);
    myGL.glEndList();

    myGL.glShadeModel(GL.GL_FLAT);
    myGL.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
  }

  /* Clear window and draw torus */
  public void display() {
    myGL.glClear(GL.GL_COLOR_BUFFER_BIT);
    myGL.glColor3f(1.0f, 1.0f, 1.0f);
    myGL.glCallList(theTorus);
    myGL.glFlush();
  }

  /* Handle window resize */
  public void myReshape(int w, int h) {
    myGL.glViewport(0, 0, w, h);
    myGL.glMatrixMode(GL.GL_PROJECTION);
    myGL.glLoadIdentity();
    myGLU.gluPerspective(30.0, (double) w / (double) h, 1.0, 100.0);
    myGL.glMatrixMode(GL.GL_MODELVIEW);
    myGL.glLoadIdentity();
    myGLU.gluLookAt(0.0, 0.0, 10.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);
  }

  /*
   * Rotate about x-axis when "x" typed; rotate about y-axis when "y" typed; "i" returns torus to
   * original view
   */
  /* ARGSUSED1 */
  public void keyboard(char key, int x, int y) {
    switch (key) {
      case 'x':
      case 'X':
        myGL.glRotatef(30.0f, 1.0f, 0.0f, 0.0f);
        myUT.glutPostRedisplay();
        break;
      case 'y':
      case 'Y':
        myGL.glRotatef(30.0f, 0.0f, 1.0f, 0.0f);
        myUT.glutPostRedisplay();
        break;
      case 'i':
      case 'I':
        myGL.glLoadIdentity();
        myGLU.gluLookAt(0.0, 0.0, 10.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);
        myUT.glutPostRedisplay();
        break;
      default:
        break;
    }
  }

  public void init() {
    myUT.glutInitWindowSize(200, 200);
    myUT.glutInitWindowPosition(0, 0);
    myUT.glutCreateWindow(this);
    myinit();
    myUT.glutDisplayFunc("display");
    myUT.glutReshapeFunc("myReshape");
    myUT.glutKeyboardFunc("keyboard");
    myUT.glutMainLoop();
  }

}
