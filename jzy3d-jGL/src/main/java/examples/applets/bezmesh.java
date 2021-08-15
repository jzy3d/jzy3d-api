package examples.applets;

import java.applet.Applet;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import jgl.wt.awt.GL;
import jgl.wt.awt.GLAUX;

public class bezmesh extends Applet implements ComponentListener {
  // must use GL to use jGL.....
  // and use GLAUX to use the aux functions.....
  // remember to give GL to initialize GLAUX
  GL myGL = new GL();
  GLAUX myAUX = new GLAUX(myGL);

  private static final float ctrlpoints[][][] =
      {{{-1.5f, -1.5f, 4.0f}, {-0.5f, -1.5f, 2.0f}, {0.5f, -1.5f, -1.0f}, {1.5f, -1.5f, 2.0f}},
          {{-1.5f, -0.5f, 1.0f}, {-0.5f, -0.5f, 3.0f}, {0.5f, -0.5f, 0.0f}, {1.5f, -0.5f, -1.0f}},
          {{-1.5f, 0.5f, 4.0f}, {-0.5f, 0.5f, 0.0f}, {0.5f, 0.5f, 3.0f}, {1.5f, 0.5f, 4.0f}},
          {{-1.5f, 1.5f, -2.0f}, {-0.5f, 1.5f, -2.0f}, {0.5f, 1.5f, 0.0f}, {1.5f, 1.5f, -1.0f}}};

  private void initlights() {
    float ambient[] = {0.2f, 0.2f, 0.2f, 1.0f};
    float position[] = {0.0f, 0.0f, 2.0f, 1.0f};
    float mat_diffuse[] = {0.6f, 0.6f, 0.6f, 1.0f};
    float mat_specular[] = {1.0f, 1.0f, 1.0f, 1.0f};
    float mat_shininess[] = {50.0f};

    myGL.glEnable(GL.GL_LIGHTING);
    myGL.glEnable(GL.GL_LIGHT0);
    myGL.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT, ambient);
    myGL.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, position);

    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, mat_diffuse);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, mat_specular);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, mat_shininess);
  }

  private void myinit() {
    myGL.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    myGL.glMap2f(GL.GL_MAP2_VERTEX_3, 0.0f, 1.0f, 3, 4, 0.0f, 1.0f, 12, 4, ctrlpoints);
    myGL.glEnable(GL.GL_MAP2_VERTEX_3);
    myGL.glEnable(GL.GL_AUTO_NORMAL);
    myGL.glEnable(GL.GL_NORMALIZE);
    myGL.glMapGrid2f(20, 0.0f, 1.0f, 20, 0.0f, 1.0f);
    initlights(); /* for lighted version only */
  }

  private void display() {
    myGL.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    myGL.glPushMatrix();
    myGL.glRotatef(85.0f, 1.0f, 1.0f, 1.0f);
    myGL.glEvalMesh2(GL.GL_FILL, 0, 20, 0, 20);
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
    if (w <= h) {
      myGL.glOrtho(-4.0f, 4.0f, -4.0f * (float) h / (float) w, 4.0f * (float) h / (float) w, -4.0f,
          4.0f);
    } else {
      myGL.glOrtho(-4.0f * (float) w / (float) h, 4.0f * (float) w / (float) h, -4.0f, 4.0f, -4.0f,
          4.0f);
    }
    myGL.glMatrixMode(GL.GL_MODELVIEW);
    myGL.glLoadIdentity();
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
