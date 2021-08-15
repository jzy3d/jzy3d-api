package examples.apps;
/*
 * surface.java This program draws a NURBS surface in the shape of a symmetrical hill.
 */

import java.awt.Frame;
import java.io.IOException;
import jgl.GL;
import jgl.glu.GLUnurbsObj;
import jgl.wt.awt.GLCanvas;
import jgl.wt.awt.GLU;
import jgl.wt.awt.GLUT;

public class surface extends GLCanvas {

  private float ctlpoints[][][] = new float[4][4][3];
  private boolean showPoints = false;

  private GLUnurbsObj theNurb;

  /*
   * Initializes the control points of the surface to a small hill. The control points range from -3
   * to +3 in x, y, and z
   */
  private void init_surface() {
    int u, v;
    for (u = 0; u < 4; u++) {
      for (v = 0; v < 4; v++) {
        ctlpoints[u][v][0] = 2.0f * ((float) u - 1.5f);
        ctlpoints[u][v][1] = 2.0f * ((float) v - 1.5f);

        if (((u == 1) || (u == 2)) && ((v == 1) || (v == 2))) {
          ctlpoints[u][v][2] = 7.0f;
        } else {
          ctlpoints[u][v][2] = -3.0f;
        }
      }
    }
  }

  /*
   * Initialize material property and depth buffer.
   */
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
    myGL.glDepthFunc(GL.GL_LESS);
    myGL.glEnable(GL.GL_DEPTH_TEST);
    myGL.glEnable(GL.GL_AUTO_NORMAL);
    myGL.glEnable(GL.GL_NORMALIZE);

    init_surface();

    theNurb = myGLU.gluNewNurbsRenderer();
    myGLU.gluNurbsProperty(theNurb, GLU.GLU_SAMPLING_TOLERANCE, 25.0f);
    myGLU.gluNurbsProperty(theNurb, GLU.GLU_DISPLAY_MODE, GLU.GLU_FILL);

    myGL.glMatrixMode(GL.GL_MODELVIEW);
    myGL.glLoadIdentity();
    myGL.glTranslatef(0.0f, 0.0f, -5.0f);
  }

  public void display() {
    float knots[] = {0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f};
    int i, j;

    myGL.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

    myGL.glPushMatrix();
    myGL.glRotatef(330.0f, 1.0f, 0.0f, 0.0f);
    myGL.glScalef(0.25f, 0.25f, 0.25f);

    myGLU.gluBeginSurface(theNurb);
    myGLU.gluNurbsSurface(theNurb, 8, knots, 8, knots, 4 * 3, 3, ctlpoints, 4, 4,
        GL.GL_MAP2_VERTEX_3);
    myGLU.gluEndSurface(theNurb);

    if (showPoints) {
      myGL.glPointSize(5.0f);
      myGL.glDisable(GL.GL_LIGHTING);
      myGL.glColor3f(1.0f, 1.0f, 0.0f);
      myGL.glBegin(GL.GL_POINTS);
      for (i = 0; i < 4; i++) {
        for (j = 0; j < 4; j++) {
          myGL.glVertex3f(ctlpoints[i][j][0], ctlpoints[i][j][1], ctlpoints[i][j][2]);
        }
      }
      myGL.glEnd();
      myGL.glEnable(GL.GL_LIGHTING);
    }

    myGL.glPopMatrix();
    myGL.glFlush();
  }

  public void myReshape(int w, int h) {
    myGL.glViewport(0, 0, w, h);
    myGL.glMatrixMode(GL.GL_PROJECTION);
    myGL.glLoadIdentity();
    myGLU.gluPerspective(45.0, (double) w / (double) h, 3.0, 8.0);

    myGL.glMatrixMode(GL.GL_MODELVIEW);
  }

  public void menu(int value) {
    switch (value) {
      case 0:
        showPoints = false;
        break;
      case 1:
        showPoints = true;
        break;
      case 2:
        myGLU.gluNurbsProperty(theNurb, GLU.GLU_DISPLAY_MODE, GLU.GLU_FILL);
        break;
      case 3:
        myGLU.gluNurbsProperty(theNurb, GLU.GLU_DISPLAY_MODE, GLU.GLU_OUTLINE_POLYGON);
        break;
    }
    myUT.glutPostRedisplay();
  }

  private boolean down = false;
  private int lastx;

  /* ARGSUSED1 */
  public void motion(int x, int y) {
    if (down) {
      myGL.glRotatef(lastx - x, 0, 1, 0);
      lastx = x;
      myUT.glutPostRedisplay();
    }
  }

  /* ARGSUSED3 */
  public void mouse(int button, int state, int x, int y) {
    if (button == GLUT.GLUT_LEFT_BUTTON) {
      if (state == GLUT.GLUT_DOWN) {
        lastx = x;
        down = true;
      } else {
        down = false;
      }
    }
  }

  public void keyboard(char key, int x, int y) {
    switch (key) {
      case 27:
        System.exit(0);
      default:
        break;
    }
  }

  /* Main Loop */
  public void init() {
    myUT.glutInitWindowSize(500, 500);
    myUT.glutInitWindowPosition(0, 0);
    myUT.glutCreateWindow(this);
    myinit();
    myUT.glutReshapeFunc("myReshape");
    myUT.glutDisplayFunc("display");
    myUT.glutCreateMenu("menu");
    myUT.glutAddMenuEntry("Show control points", 1);
    myUT.glutAddMenuEntry("Hide control points", 0);
    myUT.glutAddMenuEntry("Solid", 2);
    myUT.glutAddMenuEntry("Wireframe", 3);
    myUT.glutAttachMenu(GLUT.GLUT_RIGHT_BUTTON);
    myUT.glutMouseFunc("mouse");
    myUT.glutMotionFunc("motion");
    myUT.glutKeyboardFunc("keyboard");
    myUT.glutMainLoop();
  }

  static public void main(String args[]) throws IOException {
    Frame mainFrame = new Frame();
    mainFrame.setSize(508, 527);
    surface mainCanvas = new surface();
    mainCanvas.init();
    mainFrame.add(mainCanvas);
    mainFrame.setVisible(true);
  }

}
