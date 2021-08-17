package examples.apps;
/*
 * pickdepth.java Picking is demonstrated in this program. In rendering mode, three overlapping
 * rectangles are drawn. When the left mouse button is pressed, selection mode is entered with the
 * picking matrix. Rectangles which are drawn under the cursor position are "picked." Pay special
 * attention to the depth value range, which is returned.
 */

import java.awt.Frame;
import java.io.IOException;
import jgl.GL;
import jgl.wt.awt.GLCanvas;
import jgl.wt.awt.GLUT;

public class pickdepth extends GLCanvas {

  private void myinit() {
    myGL.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    myGL.glDepthFunc(GL.GL_LESS);
    myGL.glEnable(GL.GL_DEPTH_TEST);
    myGL.glShadeModel(GL.GL_FLAT);
    myGL.glDepthRange(0.0f, 1.0f); /* The default z mapping */
  }

  /*
   * The three rectangles are drawn. In selection mode, each rectangle is given the same name. Note
   * that each rectangle is drawn with a different z value.
   */
  private void drawRects(int mode) {
    if (mode == GL.GL_SELECT)
      myGL.glLoadName(1);
    myGL.glBegin(GL.GL_QUADS);
    myGL.glColor3f(1.0f, 1.0f, 0.0f);
    myGL.glVertex3i(2, 0, 0);
    myGL.glVertex3i(2, 6, 0);
    myGL.glVertex3i(6, 6, 0);
    myGL.glVertex3i(6, 0, 0);
    myGL.glEnd();
    if (mode == GL.GL_SELECT)
      myGL.glLoadName(2);
    myGL.glBegin(GL.GL_QUADS);
    myGL.glColor3f(0.0f, 1.0f, 1.0f);
    myGL.glVertex3i(3, 2, -1);
    myGL.glVertex3i(3, 8, -1);
    myGL.glVertex3i(8, 8, -1);
    myGL.glVertex3i(8, 2, -1);
    myGL.glEnd();
    if (mode == GL.GL_SELECT)
      myGL.glLoadName(3);
    myGL.glBegin(GL.GL_QUADS);
    myGL.glColor3f(1.0f, 0.0f, 1.0f);
    myGL.glVertex3i(0, 2, -2);
    myGL.glVertex3i(0, 7, -2);
    myGL.glVertex3i(5, 7, -2);
    myGL.glVertex3i(5, 2, -2);
    myGL.glEnd();
  }

  /*
   * processHits() prints out the contents of the selection array.
   */
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

  /*
   * pickRects() sets up selection mode, name stack, and projection matrix for picking. Then the
   * objects are drawn.
   */
  private static final int BUFSIZE = 512;

  public void pickRects(int button, int state, int x, int y) {
    int selectBuf[] = new int[BUFSIZE];
    int hits;
    int viewport[] = new int[4];

    if (button != GLUT.GLUT_LEFT_BUTTON || state != GLUT.GLUT_DOWN)
      return;

    myGL.glGetIntegerv(GL.GL_VIEWPORT, viewport);

    myGL.glSelectBuffer(BUFSIZE, selectBuf);
    myGL.glRenderMode(GL.GL_SELECT);

    myGL.glInitNames();
    myGL.glPushName(-1);

    myGL.glMatrixMode(GL.GL_PROJECTION);
    myGL.glPushMatrix();
    myGL.glLoadIdentity();
    /* create 5x5 pixel picking region near cursor location */
    myGLU.gluPickMatrix((double) x, (double) (viewport[3] - y), 5.0, 5.0, viewport);
    myGL.glOrtho(0.0f, 8.0f, 0.0f, 8.0f, -0.5f, 2.5f);
    drawRects(GL.GL_SELECT);
    myGL.glPopMatrix();
    myGL.glFlush();

    hits = myGL.glRenderMode(GL.GL_RENDER);
    processHits(hits, selectBuf);
  }

  public void display() {
    myGL.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    drawRects(GL.GL_RENDER);
    myGL.glFlush();
  }

  public void myReshape(int w, int h) {
    myGL.glViewport(0, 0, w, h);
    myGL.glMatrixMode(GL.GL_PROJECTION);
    myGL.glLoadIdentity();
    myGL.glOrtho(0.0f, 8.0f, 0.0f, 8.0f, -0.5f, 2.5f);
    myGL.glMatrixMode(GL.GL_MODELVIEW);
    myGL.glLoadIdentity();
  }

  public void keyboard(char key, int x, int y) {
    switch (key) {
      case 27:
        System.exit(0);
      default:
        break;
    }
  }

  /*
   * Main Loop Open window with initial window size, title bar, RGBA display mode, depth buffer, and
   * handle input events.
   */
  public void init() {
    myUT.glutInitWindowSize(100, 100);
    myUT.glutInitWindowPosition(0, 0);
    myUT.glutCreateWindow(this);
    myinit();
    myUT.glutMouseFunc("pickRects");
    myUT.glutReshapeFunc("myReshape");
    myUT.glutDisplayFunc("display");
    myUT.glutKeyboardFunc("keyboard");
    myUT.glutMainLoop();
  }

  static public void main(String args[]) throws IOException {
    Frame mainFrame = new Frame();
    mainFrame.setSize(108, 127);
    pickdepth mainCanvas = new pickdepth();
    mainCanvas.init();
    mainFrame.add(mainCanvas);
    mainFrame.setVisible(true);
  }

}
