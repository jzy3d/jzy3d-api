package examples.applets.glut;
/*
 * bezcurve.java This program uses evaluators to draw a Bezier curve.
 */

import jgl.GL;
import jgl.wt.awt.GLApplet;

public class bezcurve extends GLApplet {

  private static final float ctrlpoints[][] =
      {{-4.0f, -4.0f, 0.0f}, {-2.0f, 4.0f, 0.0f}, {2.0f, -4.0f, 0.0f}, {4.0f, 4.0f, 0.0f}};

  private void myinit() {
    myGL.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    myGL.glShadeModel(GL.GL_FLAT);
    myGL.glMap1f(GL.GL_MAP1_VERTEX_3, 0.0f, 1.0f, 3, 4, ctrlpoints);
    myGL.glEnable(GL.GL_MAP1_VERTEX_3);
  }

  public void display() {
    int i;

    myGL.glClear(GL.GL_COLOR_BUFFER_BIT);
    myGL.glColor3f(1.0f, 1.0f, 1.0f);
    myGL.glBegin(GL.GL_LINE_STRIP);
    for (i = 0; i <= 30; i++)
      myGL.glEvalCoord1f((float) i / 30.0f);
    myGL.glEnd();
    /* The following code displays the control points as dots. */
    myGL.glPointSize(5.0f);
    myGL.glColor3f(1.0f, 1.0f, 0.0f);
    myGL.glBegin(GL.GL_POINTS);
    for (i = 0; i < 4; i++)
      myGL.glVertex3fv(ctrlpoints[i]);
    myGL.glEnd();
    myGL.glFlush();
  }

  public void myReshape(int w, int h) {
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

  public void init() {
    myUT.glutInitWindowSize(500, 500);
    myUT.glutInitWindowPosition(0, 0);
    myUT.glutCreateWindow(this);
    myinit();
    myUT.glutDisplayFunc("display");
    myUT.glutReshapeFunc("myReshape");
    myUT.glutMainLoop();
  }

}
