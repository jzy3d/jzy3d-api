package examples.applets;

import java.applet.Applet;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import jgl.wt.awt.GL;
import jgl.wt.awt.GLAUX;
import jgl.wt.awt.GLU;

public class smooth extends Applet implements ComponentListener {
  // must use GL to use jGL.....
  // and use GLU to use the glu functions....
  // remember to give GL to initialize GLU
  // and use GLAUX to use the aux functions.....
  // remember to give GL to initialize GLAUX
  GL myGL = new GL();
  GLU myGLU = new GLU(myGL);
  GLAUX myAUX = new GLAUX(myGL);

  private void myinit() {
    myGL.glShadeModel(GL.GL_SMOOTH); /* GL_SMOOTH is the default */
  }

  private void triangle() {
    myGL.glBegin(GL.GL_TRIANGLES);
    myGL.glColor3f(1.0f, 0.0f, 0.0f);
    myGL.glVertex2f(5.0f, 5.0f);
    myGL.glColor3f(0.0f, 1.0f, 0.0f);
    myGL.glVertex2f(25.0f, 5.0f);
    myGL.glColor3f(0.0f, 0.0f, 1.0f);
    myGL.glVertex2f(5.0f, 25.0f);
    myGL.glEnd();
  }

  private void display() {
    myGL.glClear(GL.GL_COLOR_BUFFER_BIT);
    triangle();
    myGL.glFlush();
  }

  public void componentMoved(ComponentEvent e) {}

  public void componentShown(ComponentEvent e) {}

  public void componentHidden(ComponentEvent e) {}

  public void componentResized(ComponentEvent e) {
    // get window width and height by myself
    myReshape(getSize().width, getSize().height);
    display();
    repaint();
  }

  private void myReshape(int w, int h) {
    myGL.glViewport(0, 0, w, h);
    myGL.glMatrixMode(GL.GL_PROJECTION);
    myGL.glLoadIdentity();
    if (w <= h) {
      myGLU.gluOrtho2D(0.0, 30.0, 0.0, 30.0 * (float) h / (float) w);
    } else {
      myGLU.gluOrtho2D(0.0, 30.0 * (float) w / (float) h, 0.0, 30.0);
    }
    myGL.glMatrixMode(GL.GL_MODELVIEW);
  }

  public void update(Graphics g) {
    // skip the clear screen command....
    paint(g);
  }

  public void paint(Graphics g) {
    myGL.glXSwapBuffers(g, this);
  }

  public void init() {
    myAUX.auxInitPosition(0, 0, 500, 500);
    myAUX.auxInitWindow(this);
    myinit();

    // as call auxReshapeFunc()
    addComponentListener(this);
    myReshape(getSize().width, getSize().height);

    // call display as call auxIdleFunc(display)
    display();
  }

}
