package examples.applets;

import java.applet.Applet;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import jgl.wt.awt.GL;
import jgl.wt.awt.GLAUX;
import jgl.wt.awt.GLU;

public class robot extends Applet implements ComponentListener, KeyListener {
  // must use GL to use jGL.....
  // and use GLU to use the glu functions....
  // remember to give GL to initialize GLU
  // and use GLAUX to use the aux functions.....
  // remember to give GL to initialize GLAUX
  GL myGL = new GL();
  GLU myGLU = new GLU(myGL);
  GLAUX myAUX = new GLAUX(myGL);

  private static int shoulder = 0, elbow = 0;

  private void elbowAdd() {
    elbow = (elbow + 5) % 360;
  }

  private void elbowSubtract() {
    elbow = (elbow - 5) % 360;
  }

  private void shoulderAdd() {
    shoulder = (shoulder + 5) % 360;
  }

  private void shoulderSubtract() {
    shoulder = (shoulder - 5) % 360;
  }

  public void keyTyped(KeyEvent e) {}

  public void keyReleased(KeyEvent e) {}

  public void keyPressed(KeyEvent e) {
    int keyCode = e.getKeyCode();

    if (keyCode == KeyEvent.VK_LEFT) {
      shoulderSubtract();
      display();
      repaint();
    } else if (keyCode == KeyEvent.VK_RIGHT) {
      shoulderAdd();
      display();
      repaint();
    } else if (keyCode == KeyEvent.VK_UP) {
      elbowAdd();
      display();
      repaint();
    } else if (keyCode == KeyEvent.VK_DOWN) {
      elbowSubtract();
      display();
      repaint();
    }
    e.consume();
  }

  private void display() {
    myGL.glClear(GL.GL_COLOR_BUFFER_BIT);
    myGL.glColor3f(1.0f, 1.0f, 1.0f);
    myGL.glPushMatrix();

    myGL.glTranslatef(-1.0f, 0.0f, 0.0f);
    myGL.glRotatef((float) shoulder, 0.0f, 0.0f, 1.0f);
    myGL.glTranslatef(1.0f, 0.0f, 0.0f);
    myAUX.auxWireBox(2.0, 0.4, 1.0); /* shoulder */

    myGL.glTranslatef(1.0f, 0.0f, 0.0f);
    myGL.glRotatef((float) elbow, 0.0f, 0.0f, 1.0f);
    myGL.glTranslatef(1.0f, 0.0f, 0.0f);
    myAUX.auxWireBox(2.0, 0.4, 1.0); /* elbow */

    myGL.glPopMatrix();
    myGL.glFlush();
  }

  private void myinit() {
    myGL.glShadeModel(GL.GL_FLAT);
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
    myGLU.gluPerspective(60.0, (double) w / (double) h, 1.0, 20.0);
    myGL.glMatrixMode(GL.GL_MODELVIEW);
    myGL.glLoadIdentity();
    myGL.glTranslatef(0.0f, 0.0f, -5.0f);
  }

  public void update(Graphics g) {
    // skip the clear screen command....
    paint(g);
  }

  public void paint(Graphics g) {
    myGL.glXSwapBuffers(g, this);
  }

  public void init() {
    myAUX.auxInitPosition(0, 0, 400, 400);
    myAUX.auxInitWindow(this);
    myinit();

    // as call auxKeyFunc()
    addKeyListener(this);

    // as call auxReshapeFunc()
    addComponentListener(this);
    myReshape(getSize().width, getSize().height);

    // call display as call auxIdleFunc(display)
    display();
  }

}
