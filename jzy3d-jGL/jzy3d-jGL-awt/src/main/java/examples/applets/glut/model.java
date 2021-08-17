package examples.applets.glut;
/*
 * model.java This program demonstrates modeling transformations
 */

import jgl.GL;
import jgl.wt.awt.GLApplet;

public class model extends GLApplet {

  private void myinit() {
    myGL.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    myGL.glShadeModel(GL.GL_FLAT);
  }

  private void draw_triangle() {
    myGL.glBegin(GL.GL_LINE_LOOP);
    myGL.glVertex2f(0.0f, 25.0f);
    myGL.glVertex2f(25.0f, -25.0f);
    myGL.glVertex2f(-25.0f, -25.0f);
    myGL.glEnd();
  }

  public void display() {
    myGL.glClear(GL.GL_COLOR_BUFFER_BIT);
    myGL.glColor3f(1.0f, 1.0f, 1.0f);

    myGL.glLoadIdentity();
    myGL.glColor3f(1.0f, 1.0f, 1.0f);
    draw_triangle();

    myGL.glEnable(GL.GL_LINE_STIPPLE);
    myGL.glLineStipple(1, (short) 0xF0F0);
    myGL.glLoadIdentity();
    myGL.glTranslatef(-20.0f, 0.0f, 0.0f);
    draw_triangle();

    myGL.glLineStipple(1, (short) 0xF00F);
    myGL.glLoadIdentity();
    myGL.glScalef(1.5f, 0.5f, 1.0f);
    draw_triangle();

    myGL.glLineStipple(1, (short) 0x8888);
    myGL.glLoadIdentity();
    myGL.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);
    draw_triangle();
    myGL.glDisable(GL.GL_LINE_STIPPLE);

    myGL.glFlush();
  }

  public void myReshape(int w, int h) {
    myGL.glViewport(0, 0, w, h);
    myGL.glMatrixMode(GL.GL_PROJECTION);
    myGL.glLoadIdentity();
    if (w <= h) {
      myGL.glOrtho(-50.0f, 50.0f, -50.0f * (float) h / (float) w, 50.0f * (float) h / (float) w,
          -1.0f, 1.0f);
    } else {
      myGL.glOrtho(-50.0f * (float) w / (float) h, 50.0f * (float) w / (float) h, -50.0f, 50.0f,
          -1.0f, 1.0f);
    }
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
