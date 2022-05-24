package examples.applets.glut;
/*
 * movelight.java This program demonstrates when to issue lighting and transformation commands to
 * render a model with a light which is moved by a modeling transformation (rotate or translate).
 * The light position is reset after the modeling transformation is called. The eye position does
 * not change.
 *
 * A sphere is drawn using a grey material characteristic. A single light source illuminates the
 * object.
 *
 * Interaction: pressing the left mouse button alters the modeling transformation (x rotation) by 30
 * degrees. The scene is then redrawn with the light in a new position.
 */

import jgl.GL;
import jgl.wt.awt.GLApplet;
import jgl.wt.awt.GLUT;

public class movelight extends GLApplet {

  private int spin = 0;

  /*
   * Initialize material property, light source, lighting model, and depth buffer.
   */
  private void myinit() {
    myGL.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    myGL.glShadeModel(GL.GL_SMOOTH);
    myGL.glEnable(GL.GL_LIGHTING);
    myGL.glEnable(GL.GL_LIGHT0);
    myGL.glEnable(GL.GL_DEPTH_TEST);
  }

  /*
   * Here is where the light position is reset after the modeling transformation (glRotated) is
   * called. This places the light at a new position in world coordinates. The cube represents the
   * position of the light.
   */
  public void display() {
    float position[] = {0.0f, 0.0f, 1.5f, 1.0f};

    myGL.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    myGL.glPushMatrix();
    myGLU.gluLookAt(0.0, 0.0, 5.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);

    myGL.glPushMatrix();
    myGL.glRotated((double) spin, 1.0, 0.0, 0.0);
    myGL.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, position);

    myGL.glTranslated(0.0, 0.0, 1.5);
    myGL.glDisable(GL.GL_LIGHTING);
    myGL.glColor3f(0.0f, 1.0f, 1.0f);
    myUT.glutWireCube(0.1);
    myGL.glEnable(GL.GL_LIGHTING);
    myGL.glPopMatrix();

    myUT.glutSolidTorus(0.275, 0.85, 8, 15);
    myGL.glPopMatrix();
    myGL.glFlush();
  }

  public void myReshape(int w, int h) {
    myGL.glViewport(0, 0, w, h);
    myGL.glMatrixMode(GL.GL_PROJECTION);
    myGL.glLoadIdentity();
    myGLU.gluPerspective(40.0, (float) w / (float) h, 1.0, 20.0);
    myGL.glMatrixMode(GL.GL_MODELVIEW);
    myGL.glLoadIdentity();
  }

  /* ARGSUSED2 */
  public void mouse(int button, int state, int x, int y) {
    switch (button) {
      case GLUT.GLUT_LEFT_BUTTON:
        if (state == GLUT.GLUT_DOWN) {
          spin = (spin + 30) % 360;
          myUT.glutPostRedisplay();
        }
        break;
      default:
        break;
    }
  }

  public void init() {
    myUT.glutInitWindowSize(500, 500);
    myUT.glutInitWindowPosition(0, 0);
    myUT.glutCreateWindow(this);
    myinit();
    myUT.glutDisplayFunc("display");
    myUT.glutReshapeFunc("myReshape");
    myUT.glutMouseFunc("mouse");
    myUT.glutMainLoop();
  }

}
