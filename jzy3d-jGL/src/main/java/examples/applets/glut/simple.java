package examples.applets.glut;
/*
 * simple.java This is a simple, introductory OpenGL program.
 */

import jgl.GL;
import jgl.wt.awt.GLApplet;

public class simple extends GLApplet {

  public void display() {
    /* clear all pixels */
    myGL.glClear(GL.GL_COLOR_BUFFER_BIT);

    /*
     * draw white polygon (rectangle) with corners at (0.25, 0.25, 0.0) and (0.75, 0.75, 0.0)
     */
    myGL.glColor3f(1.0f, 1.0f, 1.0f);
    myGL.glBegin(GL.GL_POLYGON);
    myGL.glVertex3f(0.25f, 0.25f, 0.0f);
    myGL.glVertex3f(0.75f, 0.25f, 0.0f);
    myGL.glVertex3f(0.75f, 0.75f, 0.0f);
    myGL.glVertex3f(0.25f, 0.75f, 0.0f);
    myGL.glEnd();

    /*
     * don't wait! start processing buffered OpenGL routines
     */
    myGL.glFlush();
  }

  private void myinit() {
    /* select clearing color */
    myGL.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

    /* initialize viewing values */
    myGL.glMatrixMode(GL.GL_PROJECTION);
    myGL.glLoadIdentity();
    myGL.glOrtho(0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f);
  }

  /*
   * Declare initial window size, position, and display mode (single buffer and RGBA). Open window
   * with "hello" in its title bar. Call initialization routines. Register callback function to
   * display graphics. Enter main loop and process events.
   */
  public void init() {
    myUT.glutInitWindowSize(500, 500);
    myUT.glutInitWindowPosition(0, 0);
    myUT.glutCreateWindow(this);
    myinit();
    myUT.glutDisplayFunc("display");
    myUT.glutMainLoop();
  }

}
