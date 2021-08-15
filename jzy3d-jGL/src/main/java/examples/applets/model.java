package examples.applets;

import java.applet.Applet;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import jgl.wt.awt.GL;
import jgl.wt.awt.GLAUX;

public class model extends Applet implements ComponentListener {
  // must use GL to use jGL.....
  // and use GLAUX to use the aux functions.....
  // remember to give GL to initialize GLAUX
  GL myGL = new GL();
  GLAUX myAUX = new GLAUX(myGL);

  private void draw_triangle() {
    myGL.glBegin(GL.GL_LINE_LOOP);
    myGL.glVertex2f(0.0f, 25.0f);
    myGL.glVertex2f(25.0f, -25.0f);
    myGL.glVertex2f(-25.0f, -25.0f);
    myGL.glEnd();
  }

  private void display() {
    myGL.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    myGL.glClear(GL.GL_COLOR_BUFFER_BIT);

    myGL.glLoadIdentity();
    myGL.glColor3f(1.0f, 1.0f, 1.0f);
    draw_triangle(); /* solid lines */

    myGL.glEnable(GL.GL_LINE_STIPPLE); /* dashed lines */
    myGL.glLineStipple(1, (short) 0xF0F0);
    myGL.glLoadIdentity();
    myGL.glTranslatef(-20.0f, 0.0f, 0.0f);
    draw_triangle();

    myGL.glLineStipple(1, (short) 0xF00F); /* long dashed lines */
    myGL.glLoadIdentity();
    myGL.glScalef(1.5f, 0.5f, 1.0f);
    draw_triangle();

    myGL.glLineStipple(1, (short) 0x8888); /* dotted lines */
    myGL.glLoadIdentity();
    myGL.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);
    draw_triangle();
    myGL.glDisable(GL.GL_LINE_STIPPLE);

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
    if (w <= h) {
      myGL.glOrtho(-50.0f, 50.0f, -50.0f * (float) h / (float) w, 50.0f * (float) h / (float) w,
          -1.0f, 1.0f);
    } else {
      myGL.glOrtho(-50.0f * (float) w / (float) h, 50.0f * (float) w / (float) h, -50.0f, 50.0f,
          -1.0f, 1.0f);
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
