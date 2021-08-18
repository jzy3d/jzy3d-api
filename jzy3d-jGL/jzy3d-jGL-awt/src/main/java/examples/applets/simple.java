package examples.applets;

import java.applet.Applet;
import java.awt.Graphics;

import jgl.wt.awt.GL;
import jgl.wt.awt.GLAUX;

public class simple extends Applet {
  // must use GL to use jGL.....
  // and use GLAUX to use the aux functions.....
  // remember to give GL to initialize GLAUX
  GL myGL = new GL();
  GLAUX myAUX = new GLAUX(myGL);

  public void update(Graphics g) {
    // since using DOUBLEBUFFER mode, clear screen is not needed
    // skip the clear screen command....
    paint(g);
  }

  public void paint(Graphics g) {
    // since JavaGL only offers DOUBLEBUFFER mode, call
    // glXSwapBuffers at every "paint" time
    myGL.glXSwapBuffers(g, this);
  }

  // "init" in Java is like "main" in C program
  public void init() {
    // auxInitDisplayMode has not supported
    myAUX.auxInitPosition(0, 0, 500, 500);
    myAUX.auxInitWindow(this);

    myGL.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    myGL.glClear(GL.GL_COLOR_BUFFER_BIT);
    myGL.glColor3f(1.0f, 1.0f, 1.0f);
    myGL.glMatrixMode(GL.GL_PROJECTION);
    myGL.glLoadIdentity();
    myGL.glOrtho(-1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f);
    myGL.glBegin(GL.GL_POLYGON);
    myGL.glVertex2f(-1.5f, -0.5f);
    myGL.glVertex2f(-0.5f, 0.5f);
    myGL.glVertex2f(0.5f, 0.5f);
    myGL.glVertex2f(0.5f, -0.5f);
    myGL.glEnd();
    myGL.glFlush();
  }

}
