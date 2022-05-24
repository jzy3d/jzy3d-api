package examples.applets;

import java.applet.Applet;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import jgl.glu.GLUnurbsObj;
import jgl.wt.awt.GL;
import jgl.wt.awt.GLAUX;
import jgl.wt.awt.GLU;

public class surface extends Applet implements ComponentListener {
  // must use GL to use jGL.....
  // and use GLU to use the glu functions.....
  // remember to give GL to initialize GLU
  // and use GLAUX to use the aux functions.....
  // remember to give GL to initialize GLAUX
  GL myGL = new GL();
  GLU myGLU = new GLU(myGL);
  GLAUX myAUX = new GLAUX(myGL);

  private float ctlpoints[][][] = new float[4][4][3];
  private GLUnurbsObj theNurb;

  private void init_surface() {
    int u, v;
    for (u = 0; u < 4; u++) {
      for (v = 0; v < 4; v++) {
        ctlpoints[u][v][0] = 2.0f * ((float) u - 1.5f);
        ctlpoints[u][v][1] = 2.0f * ((float) v - 1.5f);

        if (((u == 1) || (u == 2)) && ((v == 1) || (v == 2))) {
          ctlpoints[u][v][2] = 3.0f;
        } else {
          ctlpoints[u][v][2] = -3.0f;
        }
      }
    }
  }

  private void myinit() {
    float mat_diffuse[] = {0.7f, 0.7f, 0.7f, 1.0f};
    float mat_specular[] = {1.0f, 1.0f, 1.0f, 1.0f};
    float mat_shininess[] = {100.0f};

    myGL.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, mat_diffuse);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, mat_specular);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, mat_shininess);

    myGL.glEnable(GL.GL_LIGHTING);
    myGL.glEnable(GL.GL_LIGHT0);
    myGL.glDepthFunc(GL.GL_LEQUAL);
    myGL.glEnable(GL.GL_DEPTH_TEST);
    myGL.glEnable(GL.GL_AUTO_NORMAL);
    myGL.glEnable(GL.GL_NORMALIZE);

    init_surface();

    theNurb = myGLU.gluNewNurbsRenderer();
    myGLU.gluNurbsProperty(theNurb, GLU.GLU_SAMPLING_TOLERANCE, 25.0f);
    myGLU.gluNurbsProperty(theNurb, GLU.GLU_DISPLAY_MODE, GLU.GLU_FILL);
  }

  private void display() {
    float knots[] = {0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f};

    myGL.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

    myGL.glPushMatrix();
    myGL.glRotatef(330.0f, 1.0f, 0.0f, 0.0f);
    myGL.glScalef(0.5f, 0.5f, 0.5f);

    myGLU.gluBeginSurface(theNurb);
    myGLU.gluNurbsSurface(theNurb, 8, knots, 8, knots, 4 * 3, 3, ctlpoints, 4, 4,
        GL.GL_MAP2_VERTEX_3);
    myGLU.gluEndSurface(theNurb);

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
    myGLU.gluPerspective(45.0, (double) w / (double) h, 3.0, 8.0);
    myGL.glMatrixMode(GL.GL_MODELVIEW);
    myGL.glLoadIdentity();
    myGL.glTranslatef(0.0f, 0.0f, -5.0f);
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
