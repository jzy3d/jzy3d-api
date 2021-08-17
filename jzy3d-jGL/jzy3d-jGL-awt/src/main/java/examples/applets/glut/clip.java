package examples.applets.glut;
/*
 * clip.java This program demonstrates arbitrary clipping planes.
 */

import jgl.GL;
import jgl.wt.awt.GLApplet;

public class clip extends GLApplet {

  private void myinit() {
    myGL.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    myGL.glShadeModel(GL.GL_FLAT);
  }

  public void display() {
    double eqn[] = {0.0, 1.0, 0.0, 0.0};
    double eqn2[] = {1.0, 0.0, 0.0, 0.0};

    myGL.glClear(GL.GL_COLOR_BUFFER_BIT);

    myGL.glColor3f(1.0f, 1.0f, 1.0f);
    myGL.glPushMatrix();
    myGL.glTranslatef(0.0f, 0.0f, -5.0f);

    /* clip lower half -- y < 0 */
    myGL.glClipPlane(GL.GL_CLIP_PLANE0, eqn);
    myGL.glEnable(GL.GL_CLIP_PLANE0);
    /* clip left half -- x < 0 */
    myGL.glClipPlane(GL.GL_CLIP_PLANE1, eqn2);
    myGL.glEnable(GL.GL_CLIP_PLANE1);

    myGL.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
    myUT.glutWireSphere(1.0, 20, 16);
    myGL.glPopMatrix();

    myGL.glFlush();
  }

  public void myReshape(int w, int h) {
    myGL.glViewport(0, 0, w, h);
    myGL.glMatrixMode(GL.GL_PROJECTION);
    myGL.glLoadIdentity();
    myGLU.gluPerspective(60.0, (float) w / (float) h, 1.0, 20.0);
    myGL.glMatrixMode(GL.GL_MODELVIEW);
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
