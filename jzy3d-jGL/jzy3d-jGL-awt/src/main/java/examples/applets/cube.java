package examples.applets;

import java.applet.Applet;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import jgl.wt.awt.GL;
import jgl.wt.awt.GLAUX;

public class cube extends Applet implements ComponentListener {
  // must use GL to use jGL.....
  // and use GLAUX to use the aux functions.....
  // remember to give GL to initialize GLAUX
  GL myGL = new GL();
  GLAUX myAUX = new GLAUX(myGL);

  private void display() {
    myGL.glClear(GL.GL_COLOR_BUFFER_BIT);
    myGL.glColor3f(1.0f, 1.0f, 1.0f);
    myGL.glLoadIdentity(); /* clear the matrix */
    myGL.glTranslatef(0.0f, 0.0f, -5.0f);
    /* viewing transformation */
    myGL.glScalef(1.0f, 2.0f, 1.0f);
    /* modeling transformation */
    myAUX.auxWireCube(1.0); /* draw the cube */
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
    myGL.glMatrixMode(GL.GL_PROJECTION); /* prepare for and then */
    myGL.glLoadIdentity(); /* define the projection */
    myGL.glFrustum(-1.0f, 1.0f, /* transformation */
        -1.0f, 1.0f, 1.5f, 20.0f);
    myGL.glMatrixMode(GL.GL_MODELVIEW); /* back to modelview matrix */
    myGL.glViewport(0, 0, w, h); /* define the viewport */
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

    // call display as call auxMainLoop(display);
    display();
  }

}
