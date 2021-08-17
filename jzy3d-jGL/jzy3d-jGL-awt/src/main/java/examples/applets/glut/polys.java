package examples.applets.glut;
/*
 * polys.java This program demonstrates polygon stippling.
 */

import jgl.GL;
import jgl.wt.awt.GLApplet;

public class polys extends GLApplet {

  public void display() {
    byte fly[] = {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x80, (byte) 0x01, (byte) 0xC0, (byte) 0x06,
        (byte) 0xC0, (byte) 0x03, (byte) 0x60, (byte) 0x04, (byte) 0x60, (byte) 0x06, (byte) 0x20,
        (byte) 0x04, (byte) 0x30, (byte) 0x0C, (byte) 0x20, (byte) 0x04, (byte) 0x18, (byte) 0x18,
        (byte) 0x20, (byte) 0x04, (byte) 0x0C, (byte) 0x30, (byte) 0x20, (byte) 0x04, (byte) 0x06,
        (byte) 0x60, (byte) 0x20, (byte) 0x44, (byte) 0x03, (byte) 0xC0, (byte) 0x22, (byte) 0x44,
        (byte) 0x01, (byte) 0x80, (byte) 0x22, (byte) 0x44, (byte) 0x01, (byte) 0x80, (byte) 0x22,
        (byte) 0x44, (byte) 0x01, (byte) 0x80, (byte) 0x22, (byte) 0x44, (byte) 0x01, (byte) 0x80,
        (byte) 0x22, (byte) 0x44, (byte) 0x01, (byte) 0x80, (byte) 0x22, (byte) 0x44, (byte) 0x01,
        (byte) 0x80, (byte) 0x22, (byte) 0x66, (byte) 0x01, (byte) 0x80, (byte) 0x66, (byte) 0x33,
        (byte) 0x01, (byte) 0x80, (byte) 0xCC, (byte) 0x19, (byte) 0x81, (byte) 0x81, (byte) 0x98,
        (byte) 0x0C, (byte) 0xC1, (byte) 0x83, (byte) 0x30, (byte) 0x07, (byte) 0xe1, (byte) 0x87,
        (byte) 0xe0, (byte) 0x03, (byte) 0x3f, (byte) 0xfc, (byte) 0xc0, (byte) 0x03, (byte) 0x31,
        (byte) 0x8c, (byte) 0xc0, (byte) 0x03, (byte) 0x33, (byte) 0xcc, (byte) 0xc0, (byte) 0x06,
        (byte) 0x64, (byte) 0x26, (byte) 0x60, (byte) 0x0c, (byte) 0xcc, (byte) 0x33, (byte) 0x30,
        (byte) 0x18, (byte) 0xcc, (byte) 0x33, (byte) 0x18, (byte) 0x10, (byte) 0xc4, (byte) 0x23,
        (byte) 0x08, (byte) 0x10, (byte) 0x63, (byte) 0xC6, (byte) 0x08, (byte) 0x10, (byte) 0x30,
        (byte) 0x0c, (byte) 0x08, (byte) 0x10, (byte) 0x18, (byte) 0x18, (byte) 0x08, (byte) 0x10,
        (byte) 0x00, (byte) 0x00, (byte) 0x08};

    byte halftone[] = {(byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x55, (byte) 0x55,
        (byte) 0x55, (byte) 0x55, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x55,
        (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA,
        (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA,
        (byte) 0xAA, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0xAA, (byte) 0xAA,
        (byte) 0xAA, (byte) 0xAA, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0xAA,
        (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0x55,
        (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x55, (byte) 0x55, (byte) 0x55,
        (byte) 0x55, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x55, (byte) 0x55,
        (byte) 0x55, (byte) 0x55, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x55,
        (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA,
        (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA,
        (byte) 0xAA, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0xAA, (byte) 0xAA,
        (byte) 0xAA, (byte) 0xAA, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0xAA,
        (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x55, (byte) 0x55, (byte) 0x55, (byte) 0x55,
        (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x55, (byte) 0x55, (byte) 0x55,
        (byte) 0x55, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x55, (byte) 0x55,
        (byte) 0x55, (byte) 0x55, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x55,
        (byte) 0x55, (byte) 0x55, (byte) 0x55};

    myGL.glClear(GL.GL_COLOR_BUFFER_BIT);

    /* draw all polygons in white */
    myGL.glColor3f(1.0f, 1.0f, 1.0f);

    /* draw one solid, unstippled rectangle, */
    /* then two stippled rectangles */
    myGL.glRectf(25.0f, 25.0f, 125.0f, 125.0f);
    myGL.glEnable(GL.GL_POLYGON_STIPPLE);
    myGL.glPolygonStipple(fly);
    myGL.glRectf(125.0f, 25.0f, 225.0f, 125.0f);
    myGL.glPolygonStipple(halftone);
    myGL.glRectf(225.0f, 25.0f, 325.0f, 125.0f);
    myGL.glDisable(GL.GL_POLYGON_STIPPLE);

    myGL.glFlush();
  }

  private void myinit() {
    /* background to be cleared to black */
    myGL.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    myGL.glShadeModel(GL.GL_FLAT);
  }

  public void myReshape(int w, int h) {
    myGL.glViewport(0, 0, w, h);
    myGL.glMatrixMode(GL.GL_PROJECTION);
    myGL.glLoadIdentity();
    myGL.glOrtho(0.0, (double) w, 0.0, (double) h, -1.0, 1.0);
    myGL.glMatrixMode(GL.GL_MODELVIEW);
    myGL.glLoadIdentity();
  }

  /*
   * Main Loop Open window with initial window size, title bar, RGBA display mode, and handle input
   * events.
   */
  public void init() {
    myUT.glutInitWindowSize(350, 150);
    myUT.glutInitWindowPosition(0, 0);
    myUT.glutCreateWindow(this);
    myinit();
    myUT.glutDisplayFunc("display");
    myUT.glutReshapeFunc("myReshape");
    myUT.glutMainLoop();
  }

}
