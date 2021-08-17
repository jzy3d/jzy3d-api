package examples.applets;

import java.applet.Applet;
import java.awt.Graphics;

import jgl.wt.awt.GL;
import jgl.wt.awt.GLAUX;
import jgl.wt.awt.GLU;

public class select extends Applet {
  // must use GL to use jGL.....
  // and use GLU to use the glu functions....
  // remember to give GL to initialize GLU
  // and use GLAUX to use the aux functions.....
  // remember to give GL to initialize GLAUX
  GL myGL = new GL();
  GLU myGLU = new GLU(myGL);
  GLAUX myAUX = new GLAUX(myGL);

  private void drawTriangle(float x1, float y1, float x2, float y2, float x3, float y3, float z) {
    myGL.glBegin(GL.GL_TRIANGLES);
    myGL.glVertex3f(x1, y1, z);
    myGL.glVertex3f(x2, y2, z);
    myGL.glVertex3f(x3, y3, z);
    myGL.glEnd();
  }

  private void drawViewVolume(float x1, float x2, float y1, float y2, float z1, float z2) {
    myGL.glColor3f(1.0f, 1.0f, 1.0f);
    myGL.glBegin(GL.GL_LINE_LOOP);
    myGL.glVertex3f(x1, y1, -z1);
    myGL.glVertex3f(x2, y1, -z1);
    myGL.glVertex3f(x2, y2, -z1);
    myGL.glVertex3f(x1, y2, -z1);
    myGL.glEnd();

    myGL.glBegin(GL.GL_LINE_LOOP);
    myGL.glVertex3f(x1, y1, -z2);
    myGL.glVertex3f(x2, y1, -z2);
    myGL.glVertex3f(x2, y2, -z2);
    myGL.glVertex3f(x1, y2, -z2);
    myGL.glEnd();

    myGL.glBegin(GL.GL_LINES); /* 4 lines */
    myGL.glVertex3f(x1, y1, -z1);
    myGL.glVertex3f(x1, y1, -z2);
    myGL.glVertex3f(x1, y2, -z1);
    myGL.glVertex3f(x1, y2, -z2);
    myGL.glVertex3f(x2, y1, -z1);
    myGL.glVertex3f(x2, y1, -z2);
    myGL.glVertex3f(x2, y2, -z1);
    myGL.glVertex3f(x2, y2, -z2);
    myGL.glEnd();
  }

  private void drawScene() {
    myGL.glMatrixMode(GL.GL_PROJECTION);
    myGL.glLoadIdentity();
    myGLU.gluPerspective(40.0, 4.0 / 3.0, 0.01, 100.0);

    myGL.glMatrixMode(GL.GL_MODELVIEW);
    myGL.glLoadIdentity();
    myGLU.gluLookAt(7.5f, 7.5f, 12.5f, 2.5f, 2.5f, -5.0f, 0.0f, 1.0f, 0.0f);
    myGL.glColor3f(0.0f, 1.0f, 0.0f); /* green triangle */
    drawTriangle(2.0f, 2.0f, 3.0f, 2.0f, 2.5f, 3.0f, -5.0f);
    myGL.glColor3f(1.0f, 0.0f, 0.0f); /* red triangle */
    drawTriangle(2.0f, 7.0f, 3.0f, 7.0f, 2.5f, 8.0f, -5.0f);
    myGL.glColor3f(1.0f, 1.0f, 0.0f); /* yellow triangles */
    drawTriangle(2.0f, 2.0f, 3.0f, 2.0f, 2.5f, 3.0f, 0.0f);
    drawTriangle(2.0f, 2.0f, 3.0f, 2.0f, 2.5f, 3.0f, -10.0f);
    drawViewVolume(0.0f, 5.0f, 0.0f, 5.0f, 0.0f, 10.0f);
  }

  private void processHits(int hits, int buffer[]) {
    int i, j;
    int names, ptr;

    System.out.println("hits = " + hits);
    ptr = 0;
    for (i = 0; i < hits; i++) { /* for each hit */
      names = buffer[ptr];
      System.out.println(" number of names for hit = " + names);
      ptr++;
      System.out.print("  z1 is " + buffer[ptr] + ";");
      ptr++;
      System.out.println(" z2 is " + buffer[ptr]);
      ptr++;
      System.out.print("   the name is ");
      for (j = 0; j < names; j++) { /* for each name */
        System.out.print(buffer[ptr] + " ");
        ptr++;
      }
      System.out.println();
    }
  }

  private static final int BUFSIZE = 512;

  private void selectObjects() {
    int selectBuf[] = new int[BUFSIZE];
    int hits;
    int viewport[] = new int[4];

    myGL.glSelectBuffer(BUFSIZE, selectBuf);
    myGL.glRenderMode(GL.GL_SELECT);

    myGL.glInitNames();
    myGL.glPushName(-1);

    myGL.glPushMatrix();
    myGL.glMatrixMode(GL.GL_PROJECTION);
    myGL.glLoadIdentity();
    myGL.glOrtho(0.0f, 5.0f, 0.0f, 5.0f, 0.0f, 10.0f);
    myGL.glMatrixMode(GL.GL_MODELVIEW);
    myGL.glLoadIdentity();
    myGL.glLoadName(1);
    drawTriangle(2.0f, 2.0f, 3.0f, 2.0f, 2.5f, 3.0f, -5.0f);
    myGL.glLoadName(2);
    drawTriangle(2.0f, 7.0f, 3.0f, 7.0f, 2.5f, 8.0f, -5.0f);
    myGL.glLoadName(3);
    drawTriangle(2.0f, 2.0f, 3.0f, 2.0f, 2.5f, 3.0f, 0.0f);
    drawTriangle(2.0f, 2.0f, 3.0f, 2.0f, 2.5f, 3.0f, -10.0f);
    myGL.glPopMatrix();
    myGL.glFlush();

    hits = myGL.glRenderMode(GL.GL_RENDER);
    processHits(hits, selectBuf);
  }

  private void myinit() {
    myGL.glDepthFunc(GL.GL_LEQUAL);
    myGL.glEnable(GL.GL_DEPTH_TEST);
    myGL.glShadeModel(GL.GL_FLAT);
  }

  private void display() {
    myGL.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    myGL.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    drawScene();
    selectObjects();
    myGL.glFlush();
  }

  public void update(Graphics g) {
    // skip the clear screen command....
    paint(g);
  }

  public void paint(Graphics g) {
    myGL.glXSwapBuffers(g, this);
  }

  public void init() {
    myAUX.auxInitPosition(0, 0, 200, 200);
    myAUX.auxInitWindow(this);
    myinit();

    // call display as call auxIdleFunc(display)
    display();
  }

}
