package examples.applets;

import java.applet.Applet;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import jgl.wt.awt.GL;
import jgl.wt.awt.GLAUX;
import jgl.wt.awt.GLU;

public class list extends Applet implements ComponentListener {
  // must use GL to use jGL.....
  // and use GLU to use the glu functions....
  // remember to give GL to initialize GLU
  // and use GLAUX to use the aux functions.....
  // remember to give GL to initialize GLAUX
  GL myGL = new GL();
  GLU myGLU = new GLU(myGL);
  GLAUX myAUX = new GLAUX(myGL);

  private int listName = 1;

  private void myinit() {
    myGL.glNewList(listName, GL.GL_COMPILE);
    myGL.glColor3f(1.0f, 0.0f, 0.0f);
    myGL.glBegin(GL.GL_TRIANGLES);
    myGL.glVertex2f(0.0f, 0.0f);
    myGL.glVertex2f(1.0f, 0.0f);
    myGL.glVertex2f(0.0f, 1.0f);
    myGL.glEnd();
    myGL.glTranslatef(1.5f, 0.0f, 0.0f);
    myGL.glEndList();
    myGL.glShadeModel(GL.GL_FLAT);
  }

  private void drawLine() {
    myGL.glBegin(GL.GL_LINES);
    myGL.glVertex2f(0.0f, 0.5f);
    myGL.glVertex2f(15.0f, 0.5f);
    myGL.glEnd();
  }

  private void display() {
    int i;

    myGL.glClear(GL.GL_COLOR_BUFFER_BIT);
    myGL.glColor3f(1.0f, 0.0f, 0.0f);
    for (i = 0; i < 10; i++) {
      myGL.glCallList(listName);
    }
    drawLine();
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
      myGLU.gluOrtho2D(0.0, 2.0, -0.5 * (float) h / (float) w, 1.5 * (float) h / (float) w);
    } else {
      myGLU.gluOrtho2D(0.0, 2.0 * (float) w / (float) h, -0.5, 1.5);
    }
    myGL.glMatrixMode(GL.GL_MODELVIEW);
    myGL.glLoadIdentity();
  }

  public void update(Graphics g) {
    // skip the clear screen command....
    paint(g);
  }

  public void paint(Graphics g) {
    myGL.glXSwapBuffers(g, this);
  }

  public void init() {
    myAUX.auxInitPosition(0, 0, 400, 50);
    myAUX.auxInitWindow(this);
    myinit();

    // as call auxReshapeFunc()
    addComponentListener(this);
    myReshape(getSize().width, getSize().height);

    // call display as call auxMainLoop(display);
    display();
  }

}
