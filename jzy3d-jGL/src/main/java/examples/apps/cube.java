package examples.apps;
/*
 * cube.java This program demonstrates a single modeling transformation, glScalef() and a single
 * viewing transformation, gluLookAt(). A wireframe cube is rendered.
 */

import java.awt.Frame;
import java.io.IOException;
import jgl.GL;
import jgl.wt.awt.GLCanvas;

public class cube extends GLCanvas {

  private void myinit() {
    myGL.glClearColor(0.0f, 1.0f, 0.0f, 0.0f);
    myGL.glShadeModel(GL.GL_FLAT);
  }

  public void display() {
    myGL.glClear(GL.GL_COLOR_BUFFER_BIT);
    myGL.glColor3f(1.0f, 1.0f, 1.0f);
    myGL.glLoadIdentity(); /* clear the matrix */
    /* viewing transformation */
    myGL.glTranslatef(0.0f, 0.0f, -5.0f);
    myGL.glScalef(1.0f, 2.0f, 1.0f); /* modeling transformation */
    myUT.glutWireCube(1.0);
    myGL.glFlush();
  }

  public void myReshape(int w, int h) {
    myGL.glViewport(0, 0, w, h); /* define the viewport */
    myGL.glMatrixMode(GL.GL_PROJECTION); /* prepare for and then */
    myGL.glLoadIdentity(); /* define the projection */
    myGL.glFrustum(-1.0f, 1.0f, /* transformation */
        -1.0f, 1.0f, 1.5f, 20.0f);
    myGL.glMatrixMode(GL.GL_MODELVIEW); /* back to modelview matrix */
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
    cube mainCanvas = new cube();
    mainCanvas.init();
    mainFrame.add(mainCanvas);
    mainFrame.setVisible(true);
  }

}
