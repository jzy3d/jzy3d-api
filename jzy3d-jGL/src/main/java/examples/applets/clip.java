package examples.applets;

import java.applet.Applet;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import jgl.wt.awt.GL;
import jgl.wt.awt.GLAUX;
import jgl.wt.awt.GLU;

public class clip extends Applet implements ComponentListener {
  // must use GL to use jGL.....
  // and use GLU to use the glu functions....
  // remember to give GL to initialize GLU
  // and use GLAUX to use the aux functions.....
  // remember to give GL to initialize GLAUX
  GL myGL = new GL();
  GLU myGLU = new GLU(myGL);
  GLAUX myAUX = new GLAUX(myGL);

  private void display() {
    double eqn[] = {0.0, 1.0, 0.0, 0.0}; /* y < 0 */
    double eqn2[] = {1.0, 0.0, 0.0, 0.0}; /* x < 0 */

    myGL.glClear(GL.GL_COLOR_BUFFER_BIT);

    myGL.glColor3f(1.0f, 1.0f, 1.0f);
    myGL.glPushMatrix();
    myGL.glTranslatef(0.0f, 0.0f, -5.0f);

    myGL.glClipPlane(GL.GL_CLIP_PLANE0, eqn);
    myGL.glEnable(GL.GL_CLIP_PLANE0);
    myGL.glClipPlane(GL.GL_CLIP_PLANE1, eqn2);
    myGL.glEnable(GL.GL_CLIP_PLANE1);

    myGL.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
    myAUX.auxWireSphere(1.0);
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
    myGLU.gluPerspective(60.0, (float) w / (float) h, 1.0, 20.0);
    myGL.glMatrixMode(GL.GL_MODELVIEW);
  }

  public void update(Graphics g) {
    // skip the clear screen command....
    paint(g);
  }

  public void paint(Graphics g) {
    // STRANGE if call display here, it will be never stoped
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
