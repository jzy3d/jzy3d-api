package examples.apps;

import java.awt.Frame;
import java.io.IOException;
import jgl.GL;
import jgl.wt.awt.GLCanvas;

public class tea extends GLCanvas {

  /* Initialize light source. */
  private void myinit() {
    float light_ambient[] = {0.0f, 0.0f, 0.0f, 1.0f};
    float light_diffuse[] = {1.0f, 1.0f, 1.0f, 1.0f};
    float light_specular[] = {1.0f, 1.0f, 1.0f, 1.0f};
    /* light_position is NOT default value */
    float light_position[] = {1.0f, 1.0f, 1.0f, 0.0f};

    myGL.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT, light_ambient);
    myGL.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, light_diffuse);
    myGL.glLightfv(GL.GL_LIGHT0, GL.GL_SPECULAR, light_specular);
    myGL.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, light_position);

    myGL.glFrontFace(GL.GL_CW);
    myGL.glEnable(GL.GL_LIGHTING);
    myGL.glEnable(GL.GL_LIGHT0);
    myGL.glEnable(GL.GL_AUTO_NORMAL);
    myGL.glEnable(GL.GL_NORMALIZE);
    myGL.glDepthFunc(GL.GL_LESS);
    myGL.glEnable(GL.GL_DEPTH_TEST);
  }

  public void display() {
    double eqn[] = {1.0, 0.0, -1.0, 1.0};
    boolean two_side_on[] = {GL.GL_TRUE};
    boolean two_side_off[] = {GL.GL_FALSE};
    float mat_diffuse[] = {0.8f, 0.8f, 0.8f, 1.0f};
    float back_diffuse[] = {0.8f, 0.2f, 0.8f, 1.0f};

    myGL.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

    myGL.glPushMatrix();
    myGL.glClipPlane(GL.GL_CLIP_PLANE0, eqn); /* slice objects */
    myGL.glEnable(GL.GL_CLIP_PLANE0);

    myGL.glPushMatrix();
    myGL.glTranslatef(0.0f, 2.0f, 0.0f);
    myUT.glutSolidTeapot(1.0); /* one-sided lighting */
    myGL.glPopMatrix();

    /* two-sided lighting, but same material */
    myGL.glLightModeli(GL.GL_LIGHT_MODEL_TWO_SIDE, GL.GL_TRUE);
    myGL.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_DIFFUSE, mat_diffuse);
    myGL.glPushMatrix();
    myGL.glTranslatef(0.0f, 0.0f, 0.0f);
    myUT.glutSolidTeapot(1.0);
    myGL.glPopMatrix();

    /* two-sided lighting, two different materials */
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, mat_diffuse);
    myGL.glMaterialfv(GL.GL_BACK, GL.GL_DIFFUSE, back_diffuse);
    myGL.glPushMatrix();
    myGL.glTranslatef(0.0f, -2.0f, 0.0f);
    myUT.glutSolidTeapot(1.0);
    myGL.glPopMatrix();

    myGL.glLightModeli(GL.GL_LIGHT_MODEL_TWO_SIDE, GL.GL_FALSE);
    myGL.glDisable(GL.GL_CLIP_PLANE0);
    myGL.glPopMatrix();
    myGL.glFlush();
  }

  public void myReshape(int w, int h) {
    myGL.glViewport(0, 0, w, h);
    myGL.glMatrixMode(GL.GL_PROJECTION);
    myGL.glLoadIdentity();
    if (w <= h) {
      myGL.glOrtho(-4.0f, 4.0f, -4.0f * (float) h / (float) w, 4.0f * (float) h / (float) w, -10.0f,
          10.0f);
    } else {
      myGL.glOrtho(-4.0f * (float) w / (float) h, 4.0f * (float) w / (float) h, -4.0f, 4.0f, -10.0f,
          10.0f);
    }
    myGL.glMatrixMode(GL.GL_MODELVIEW);
  }

  public void keyboard(char key, int x, int y) {
    switch (key) {
      case 27:
        System.exit(0);
      default:
        break;
    }
  }

  public void init() {
    myUT.glutInitWindowSize(500, 500);
    myUT.glutInitWindowPosition(0, 0);
    myUT.glutCreateWindow(this);
    myinit();
    myUT.glutReshapeFunc("myReshape");
    myUT.glutDisplayFunc("display");
    myUT.glutKeyboardFunc("keyboard");
    myUT.glutMainLoop();
  }

  static public void main(String args[]) throws IOException {
    Frame mainFrame = new Frame();
    mainFrame.setSize(508, 527);
    tea mainCanvas = new tea();
    mainCanvas.init();
    mainFrame.add(mainCanvas);
    mainFrame.setVisible(true);
  }

}
