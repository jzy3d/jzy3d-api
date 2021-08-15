package examples.applets.glut;
/*
 * robot.java This program shows how to composite modeling transformations to draw translated and
 * rotated hierarchical models. Interaction: pressing the s and e keys (shoulder and elbow) alters
 * the rotation of the robot arm.
 */

import jgl.GL;
import jgl.wt.awt.GLApplet;

public class robot extends GLApplet {

  private static int shoulder = 0, elbow = 0;

  private void myinit() {
    myGL.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    myGL.glShadeModel(GL.GL_FLAT);
  }

  public void display() {
    myGL.glClear(GL.GL_COLOR_BUFFER_BIT);
    myGL.glPushMatrix();
    myGL.glTranslatef(-1.0f, 0.0f, 0.0f);
    myGL.glRotatef((float) shoulder, 0.0f, 0.0f, 1.0f);
    myGL.glTranslatef(1.0f, 0.0f, 0.0f);
    myGL.glPushMatrix();
    myGL.glScalef(2.0f, 0.4f, 1.0f);
    myUT.glutWireCube(1.0);
    myGL.glPopMatrix();

    myGL.glTranslatef(1.0f, 0.0f, 0.0f);
    myGL.glRotatef((float) elbow, 0.0f, 0.0f, 1.0f);
    myGL.glTranslatef(1.0f, 0.0f, 0.0f);
    myGL.glPushMatrix();
    myGL.glScalef(2.0f, 0.4f, 1.0f);
    myUT.glutWireCube(1.0);
    myGL.glPopMatrix();

    myGL.glPopMatrix();
    myGL.glFlush();
  }

  public void myReshape(int w, int h) {
    myGL.glViewport(0, 0, w, h);
    myGL.glMatrixMode(GL.GL_PROJECTION);
    myGL.glLoadIdentity();
    myGLU.gluPerspective(60.0, (double) w / (double) h, 1.0, 20.0);
    myGL.glMatrixMode(GL.GL_MODELVIEW);
    myGL.glLoadIdentity();
    myGL.glTranslatef(0.0f, 0.0f, -5.0f);
  }

  /* ARGSUSED1 */
  public void keyboard(char key, int x, int y) {
    switch (key) {
      case 's':
        shoulder = (shoulder + 5) % 360;
        myUT.glutPostRedisplay();
        break;
      case 'S':
        shoulder = (shoulder - 5) % 360;
        myUT.glutPostRedisplay();
        break;
      case 'e':
        elbow = (elbow + 5) % 360;
        myUT.glutPostRedisplay();
        break;
      case 'E':
        elbow = (elbow - 5) % 360;
        myUT.glutPostRedisplay();
        break;
      default:
        break;
    }
  }

  public void init() {
    myUT.glutInitWindowSize(400, 400);
    myUT.glutInitWindowPosition(0, 0);
    myUT.glutCreateWindow(this);
    myinit();
    myUT.glutDisplayFunc("display");
    myUT.glutReshapeFunc("myReshape");
    myUT.glutKeyboardFunc("keyboard");
    myUT.glutMainLoop();
  }

}
