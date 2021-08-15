package examples.apps;
/*
 * planet.java This program shows how to composite modeling transformations to draw translated and
 * rotated models. Interaction: pressing the d and y keys (day and year) alters the rotation of the
 * planet around the sun.
 */

import java.awt.Frame;
import java.io.IOException;
import jgl.GL;
import jgl.wt.awt.GLCanvas;

public class planet extends GLCanvas {

  private static int year = 0, day = 0;

  private void myinit() {
    myGL.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    myGL.glShadeModel(GL.GL_FLAT);
  }

  public void display() {
    myGL.glClear(GL.GL_COLOR_BUFFER_BIT);
    myGL.glColor3f(1.0f, 1.0f, 1.0f);

    myGL.glPushMatrix();
    myUT.glutWireSphere(1.0, 20, 16); /* draw sun */
    myGL.glRotatef((float) year, 0.0f, 1.0f, 0.0f);
    myGL.glTranslatef(2.0f, 0.0f, 0.0f);
    myGL.glRotatef((float) day, 0.0f, 1.0f, 0.0f);
    myUT.glutWireSphere(0.2, 10, 8); /* draw smaller planet */
    myGL.glPopMatrix();
    myGL.glFlush();
  }

  public void myReshape(int w, int h) {
    myGL.glViewport(0, 0, w, h);
    myGL.glMatrixMode(GL.GL_PROJECTION);
    myGL.glLoadIdentity();
    myGLU.gluPerspective(60.0, (double) w / (double) h, 1.0, 20.0);
    myGL.glMatrixMode(GL.GL_MODELVIEW);
    myGL.glLoadIdentity();
    myGLU.gluLookAt(0.0, 0.0, 5.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);
  }

  /* ARGSUSED1 */
  public void keyboard(char key, int x, int y) {
    switch (key) {
      case 'd':
        day = (day + 10) % 360;
        myUT.glutPostRedisplay();
        break;
      case 'D':
        day = (day - 10) % 360;
        myUT.glutPostRedisplay();
        break;
      case 'y':
        year = (year + 5) % 360;
        myUT.glutPostRedisplay();
        break;
      case 'Y':
        year = (year - 5) % 360;
        myUT.glutPostRedisplay();
        break;
      case 27:
        System.exit(0);
      default:
        break;
    }
  }

  public void init() {
    myUT.glutInitWindowSize(500, 500);
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
    mainFrame.setSize(508, 527);
    planet mainCanvas = new planet();
    mainCanvas.init();
    mainFrame.add(mainCanvas);
    mainFrame.setVisible(true);
  }

}
