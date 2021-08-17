package examples.applets.glut;
/*
 * cube.java This program demonstrates a single modeling transformation, glScalef() and a single
 * viewing transformation, gluLookAt(). A wireframe cube is rendered.
 */

import jgl.GL;
import jgl.wt.awt.GLApplet;

public class cube extends GLApplet {

  private void myinit() {
    myGL.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
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
    myGL.glViewport(0, 0, w, h);
    myGL.glMatrixMode(GL.GL_PROJECTION);
    myGL.glLoadIdentity();
    myGL.glFrustum(-1.0f, 1.0f, -1.0f, 1.0f, 1.5f, 20.0f);
    myGL.glMatrixMode(GL.GL_MODELVIEW);
  }

  public void init() {
    myUT.glutInitWindowSize(500, 500);
    myUT.glutInitWindowPosition(0, 0);
    myUT.glutCreateWindow(this);
    myinit();
    myUT.glutDisplayFunc("display");
    myUT.glutReshapeFunc("myReshape");
    myUT.glutMainLoop();
  }

}
