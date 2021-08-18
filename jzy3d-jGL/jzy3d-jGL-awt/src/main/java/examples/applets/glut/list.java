package examples.applets.glut;
/*
 * list.java This program demonstrates how to make and execute a display list. Note that attributes,
 * such as current color and matrix, are changed.
 */

import jgl.GL;
import jgl.wt.awt.GLApplet;

public class list extends GLApplet {

  private int listName;

  private void myinit() {
    listName = myGL.glGenLists(1);
    myGL.glNewList(listName, GL.GL_COMPILE);
    myGL.glColor3f(1.0f, 0.0f, 0.0f); /* current color red */
    myGL.glBegin(GL.GL_TRIANGLES);
    myGL.glVertex2f(0.0f, 0.0f);
    myGL.glVertex2f(1.0f, 0.0f);
    myGL.glVertex2f(0.0f, 1.0f);
    myGL.glEnd();
    myGL.glTranslatef(1.5f, 0.0f, 0.0f);/* move position */
    myGL.glEndList();
    myGL.glShadeModel(GL.GL_FLAT);
  }

  private void drawLine() {
    myGL.glBegin(GL.GL_LINES);
    myGL.glVertex2f(0.0f, 0.5f);
    myGL.glVertex2f(15.0f, 0.5f);
    myGL.glEnd();
  }

  public void display() {
    int i;

    myGL.glClear(GL.GL_COLOR_BUFFER_BIT);
    myGL.glColor3f(1.0f, 0.0f, 0.0f); /* current color green */
    for (i = 0; i < 10; i++) /* draw 10 triangles */
      myGL.glCallList(listName);
    drawLine(); /* is this line green? NO! */
    /* where is the line drawn? */
    myGL.glFlush();
  }

  public void myReshape(int w, int h) {
    myGL.glViewport(0, 0, w, h);
    myGL.glMatrixMode(GL.GL_PROJECTION);
    myGL.glLoadIdentity();
    if (w <= h) {
      myGLU.gluOrtho2D(0.0, 2.0, -0.5 * (float) h / (float) w, 1.5 * (float) h / (float) w);
    } else {
      myGLU.gluOrtho2D(0.0, 2.0 * (float) w / (float) h, -0.5, 1.5);
    }
    myGL.glMatrixMode(GL.GL_MODELVIEW);
    myGL.glLoadIdentity();
  }

  /*
   * Main Loop Open window with initial window size, title bar, RGBA display mode, and handle input
   * events.
   */
  public void init() {
    myUT.glutInitWindowSize(400, 50);
    myUT.glutInitWindowPosition(0, 0);
    myUT.glutCreateWindow(this);
    myinit();
    myUT.glutReshapeFunc("myReshape");
    myUT.glutDisplayFunc("display");
    myUT.glutMainLoop();
  }

}
