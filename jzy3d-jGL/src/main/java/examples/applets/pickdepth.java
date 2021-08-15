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

public class pickdepth extends Applet implements ComponentListener, MouseListener {
  // must use GL to use jGL.....
  // and use GLU to use the glu functions....
  // remember to give GL to initialize GLU
  // and use GLAUX to use the aux functions.....
  // remember to give GL to initialize GLAUX
  GL myGL = new GL();
  GLU myGLU = new GLU(myGL);
  GLAUX myAUX = new GLAUX(myGL);

  private void myinit() {
    myGL.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    myGL.glDepthFunc(GL.GL_LEQUAL);
    myGL.glEnable(GL.GL_DEPTH_TEST);
    myGL.glShadeModel(GL.GL_FLAT);
    myGL.glDepthRange(0.0f, 1.0f); /* The default z mapping */
  }

  private void drawRects(int mode) {
    if (mode == GL.GL_SELECT) {
      myGL.glLoadName(1);
    }
    myGL.glBegin(GL.GL_QUADS);
    myGL.glColor3f(1.0f, 1.0f, 0.0f);
    myGL.glVertex3i(2, 0, 0);
    myGL.glVertex3i(2, 6, 0);
    myGL.glVertex3i(6, 6, 0);
    myGL.glVertex3i(6, 0, 0);
    myGL.glColor3f(0.0f, 1.0f, 1.0f);
    myGL.glVertex3i(3, 2, -1);
    myGL.glVertex3i(3, 8, -1);
    myGL.glVertex3i(8, 8, -1);
    myGL.glVertex3i(8, 2, -1);
    myGL.glColor3f(1.0f, 0.0f, 1.0f);
    myGL.glVertex3i(0, 2, -2);
    myGL.glVertex3i(0, 7, -2);
    myGL.glVertex3i(5, 7, -2);
    myGL.glVertex3i(5, 2, -2);
    myGL.glEnd();
  }

  private void processHits(int hits, int buffer[]) {
    int i, j;
    int names, ptr;

    System.out.println("hits = " + hits);
    ptr = 0;
    for (i = 0; i < hits; i++) { /* for each hit */
      names = buffer[ptr];
      System.out.println(" number of names for hit = " + names);
      ptr++;
      System.out.print("  z1 is " + buffer[ptr] + ";");
      ptr++;
      System.out.println(" z2 is " + buffer[ptr]);
      ptr++;
      System.out.print("   the name is ");
      for (j = 0; j < names; j++) { /* for each name */
        System.out.print(buffer[ptr] + " ");
        ptr++;
      }
      System.out.println();
    }
  }

  private static final int BUFSIZE = 512;

  private void pickRects(MouseEvent e) {
    int selectBuf[] = new int[BUFSIZE];
    int hits;
    int viewport[] = new int[4];
    int x, y;

    x = e.getX();
    y = e.getY();
    myGL.glGetIntegerv(GL.GL_VIEWPORT, viewport);

    myGL.glSelectBuffer(BUFSIZE, selectBuf);
    myGL.glRenderMode(GL.GL_SELECT);

    myGL.glInitNames();
    myGL.glPushName(-1);

    myGL.glMatrixMode(GL.GL_PROJECTION);
    myGL.glPushMatrix();
    myGL.glLoadIdentity();
    /* create 5x5 pixel picking region near cursor location */
    myGLU.gluPickMatrix((double) x, (double) (viewport[3] - y), 5.0, 5.0, viewport);
    myGL.glOrtho(0.0f, 8.0f, 0.0f, 8.0f, -0.5f, 2.5f);
    drawRects(GL.GL_SELECT);

    myGL.glPopMatrix();
    myGL.glFlush();

    hits = myGL.glRenderMode(GL.GL_RENDER);
    processHits(hits, selectBuf);
  }

  public void mouseClicked(MouseEvent e) {}

  public void mouseEntered(MouseEvent e) {}

  public void mouseExited(MouseEvent e) {}

  public void mouseReleased(MouseEvent e) {}

  public void mousePressed(MouseEvent e) {
    // process which button by myself
    if ((e.getModifiers() & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK) { // left
      pickRects(e);
      display();
      repaint();
      e.consume();
    }
  }

  private void display() {
    myGL.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    drawRects(GL.GL_RENDER);
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
    myGL.glOrtho(0.0f, 8.0f, 0.0f, 8.0f, -0.5f, 2.5f);
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
    myAUX.auxInitPosition(0, 0, 100, 100);
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
