package examples.applets;

import java.applet.Applet;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import jgl.wt.awt.GL;
import jgl.wt.awt.GLAUX;

public class bezcurve extends Applet implements ComponentListener {
  // must use GL to use jGL.....
  // and use GLAUX to use the aux functions.....
  // remember to give GL to initialize GLAUX
	GL myGL = new GL();
  GLAUX myAUX = new GLAUX(myGL);

  private static final float ctrlpoints[][] =
      {{-4.0f, -4.0f, 0.0f}, {-2.0f, 4.0f, 0.0f}, {2.0f, -4.0f, 0.0f}, {4.0f, 4.0f, 0.0f}};

  private void myinit() {
    myGL.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    myGL.glMap1f(GL.GL_MAP1_VERTEX_3, 0.0f, 1.0f, 3, 4, ctrlpoints);
    myGL.glEnable(GL.GL_MAP1_VERTEX_3);
    myGL.glShadeModel(GL.GL_FLAT);
  }

  private void display() {
    int i;

    myGL.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    myGL.glColor3f(1.0f, 1.0f, 1.0f);
    myGL.glBegin(GL.GL_LINE_STRIP);
    for (i = 0; i <= 30; i++) {
      myGL.glEvalCoord1f((float) i / 30.0f);
    }
    myGL.glEnd();
    /* The following code displays the control points as dots. */
    myGL.glPointSize(5.0f);
    myGL.glColor3f(1.0f, 1.0f, 0.0f);
    myGL.glBegin(GL.GL_POINTS);
    for (i = 0; i < 4; i++) {
      myGL.glVertex3fv(ctrlpoints[i]);
    }
    myGL.glEnd();
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
      myGL.glOrtho(-5.0f, 5.0f, -5.0f * (float) h / (float) w, 5.0f * (float) h / (float) w, -5.0f,
          5.0f);
    } else {
      myGL.glOrtho(-5.0f * (float) w / (float) h, 5.0f * (float) w / (float) h, -5.0f, 5.0f, -5.0f,
          5.0f);
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
