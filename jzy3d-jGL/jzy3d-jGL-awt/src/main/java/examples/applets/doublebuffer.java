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

// "double" is a reserved word in Java
public class doublebuffer extends Applet implements ComponentListener, MouseListener, Runnable {
  // must use GL to use jGL.....
  // and use GLAUX to use the aux functions.....
  // remember to give GL to initialize GLAUX
  GL myGL = new GL();
  GLAUX myAUX = new GLAUX(myGL);

  private Thread kicker;

  private float spin = 0;

  private void display() {
    myGL.glClear(GL.GL_COLOR_BUFFER_BIT);

    myGL.glPushMatrix();
    myGL.glRotatef(spin, 0.0f, 0.0f, 1.0f);
    myGL.glRectf(-25.0f, -25.0f, 25.0f, 25.0f);
    myGL.glPopMatrix();

    myGL.glFlush();
  }

  private void spinDisplay() {
    spin += 2.0f;
    if (spin >= 360.0f) {
      spin -= 360.0f;
    }
    display();
  }

  public void mouseClicked(MouseEvent e) {}

  public void mouseEntered(MouseEvent e) {}

  public void mouseExited(MouseEvent e) {}

  public void mouseReleased(MouseEvent e) {}

  public void mousePressed(MouseEvent e) {
    // process which button by myself
    if ((e.getModifiers() & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK) { // left
      start();
    } else if ((e.getModifiers() & InputEvent.BUTTON2_MASK) == InputEvent.BUTTON2_MASK) { // middle
      stop();
    }
    e.consume();
  }

  private void myinit() {
    myGL.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    myGL.glColor3f(1.0f, 1.0f, 1.0f);
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
    myGL.glLoadIdentity();
  }

  public void update(Graphics g) {
    // skip the clear screen command....
    paint(g);
  }

  public void paint(Graphics g) {
    myGL.glXSwapBuffers(g, this);
    pause();
  }

  public void init() {
    myAUX.auxInitPosition(0, 0, 500, 500);
    myAUX.auxInitWindow(this);
    myinit();

    // as call auxReshapeFunc()
    addComponentListener(this);
    myReshape(getSize().width, getSize().height);

    // as call auxMouseFunc()
    addMouseListener(this);

    // call spinDisplay before paint
    spinDisplay();
  }

  public void run() {
    // call spinDisplay as call auxIdleFunc(spinDisplay)
    spinDisplay();
    repaint();
  }

  public void destroy() {
    removeComponentListener(this);
    removeMouseListener(this);
  }

  public synchronized void start() {
    if (kicker == null || !kicker.isAlive()) {
      kicker = new Thread(this);
      kicker.start();
    }
  }

  public synchronized void stop() {
    kicker = null;
  }

  public void pause() {
    if (kicker != null) {
      spinDisplay();
    }
    try {
      Thread.sleep(20);
    } catch (InterruptedException e) {
    }
  }

}
