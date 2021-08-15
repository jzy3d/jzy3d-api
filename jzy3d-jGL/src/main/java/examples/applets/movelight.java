package examples.applets;

import java.applet.Applet;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import jgl.wt.awt.GL;
import jgl.wt.awt.GLAUX;
import jgl.wt.awt.GLU;

public class movelight extends Applet implements ComponentListener, MouseListener {
  // must use GL to use jGL.....
  // and use GLU to use the glu functions....
  // remember to give GL to initialize GLU
  // and use GLAUX to use the aux functions.....
  // remember to give GL to initialize GLAUX
  GL myGL = new GL();
  GLU myGLU = new GLU(myGL);
  GLAUX myAUX = new GLAUX(myGL);

  private int spin = 0;

  private void movelight() {
    spin = (spin + 30) % 360;
  }

  public void mouseClicked(MouseEvent e) {}

  public void mouseEntered(MouseEvent e) {}

  public void mouseExited(MouseEvent e) {}

  public void mouseReleased(MouseEvent e) {}

  public void mousePressed(MouseEvent e) {
    // process which button by myself
    if ((e.getModifiers() & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK) { // left
      movelight();
      display();
      repaint();
      e.consume();
    }
  }

  private void myinit() {
    myGL.glEnable(GL.GL_LIGHTING);
    myGL.glEnable(GL.GL_LIGHT0);

    myGL.glDepthFunc(GL.GL_LESS);
    myGL.glEnable(GL.GL_DEPTH_TEST);
  }

  private void display() {
    float position[] = {0.0f, 0.0f, 1.5f, 1.0f};

    myGL.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    myGL.glPushMatrix();
    myGL.glTranslatef(0.0f, 0.0f, -5.0f);

    myGL.glPushMatrix();
    myGL.glRotated((double) spin, 1.0, 0.0, 0.0);
    myGL.glRotated(0.0, 1.0, 0.0, 0.0);
    myGL.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, position);

    myGL.glTranslated(0.0, 0.0, 1.5);
    myGL.glDisable(GL.GL_LIGHTING);
    myGL.glColor3f(0.0f, 1.0f, 1.0f);
    myAUX.auxWireCube(0.1);
    myGL.glEnable(GL.GL_LIGHTING);
    myGL.glPopMatrix();

    myAUX.auxSolidTorus(0.275, 0.85);
    myGL.glPopMatrix();
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
    myGLU.gluPerspective(40.0, (float) w / (float) h, 1.0, 20.0);
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

    // as call auxMouseFunc()
    addMouseListener(this);

    // as call auxReshapeFunc()
    addComponentListener(this);
    myReshape(getSize().width, getSize().height);

    // call display as call auxIdleFunc(display)
    display();
  }

}
