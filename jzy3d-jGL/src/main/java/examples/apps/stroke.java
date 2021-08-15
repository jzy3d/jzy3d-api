package examples.apps;
/*
 * stroke.java This program demonstrates some characters of a stroke (vector) font. The characters
 * are represented by display lists, which are given numbers which correspond to the ASCII values of
 * the characters. Use of glCallLists() is demonstrated.
 */

import java.awt.Frame;
import java.io.IOException;
import jgl.GL;
import jgl.wt.awt.GLCanvas;

class CP {
  public float x[] = new float[2];
  public int type;

  public CP(float a, float b, int c) {
    x[0] = a;
    x[1] = b;
    type = c;
  }
}


public class stroke extends GLCanvas {

  public static final int PT = 1;
  public static final int STROKE = 2;
  public static final int END = 3;

  private CP Adata[] = {new CP(0, 0, PT), new CP(0, 9, PT), new CP(1, 10, PT), new CP(4, 10, PT),
      new CP(5, 9, PT), new CP(5, 0, STROKE), new CP(0, 5, PT), new CP(5, 5, END)};

  private CP Edata[] = {new CP(5, 0, PT), new CP(0, 0, PT), new CP(0, 10, PT),
      new CP(5, 10, STROKE), new CP(0, 5, PT), new CP(4, 5, END)};

  private CP Pdata[] = {new CP(0, 0, PT), new CP(0, 10, PT), new CP(4, 10, PT), new CP(5, 9, PT),
      new CP(5, 6, PT), new CP(4, 5, PT), new CP(0, 5, END)};

  private CP Rdata[] =
      {new CP(0, 0, PT), new CP(0, 10, PT), new CP(4, 10, PT), new CP(5, 9, PT), new CP(5, 6, PT),
          new CP(4, 5, PT), new CP(0, 5, STROKE), new CP(3, 5, PT), new CP(5, 0, END)};

  private CP Sdata[] = {new CP(0, 1, PT), new CP(1, 0, PT), new CP(4, 0, PT), new CP(5, 1, PT),
      new CP(5, 4, PT), new CP(4, 5, PT), new CP(1, 5, PT), new CP(0, 6, PT), new CP(0, 9, PT),
      new CP(1, 10, PT), new CP(4, 10, PT), new CP(5, 9, END)};

  /*
   * drawLetter() interprets the instructions from the array for that letter and renders the letter
   * with line segments.
   */
  private void drawLetter(CP l[]) {
    int i = 0;
    myGL.glBegin(GL.GL_LINE_STRIP);
    while (i < l.length) {
      switch (l[i].type) {
        case PT:
          myGL.glVertex2fv(l[i].x);
          break;
        case STROKE:
          myGL.glVertex2fv(l[i].x);
          myGL.glEnd();
          myGL.glBegin(GL.GL_LINE_STRIP);
          break;
        case END:
          myGL.glVertex2fv(l[i].x);
          myGL.glEnd();
          myGL.glTranslatef(8.0f, 0.0f, 0.0f);
          return;
      }
      i++;
    }
  }

  /* Create a display list for each of 6 characters */
  private void myinit() {
    int base;

    myGL.glShadeModel(GL.GL_FLAT);

    base = myGL.glGenLists(128);
    myGL.glListBase(base);
    myGL.glNewList(base + 'A', GL.GL_COMPILE);
    drawLetter(Adata);
    myGL.glEndList();
    myGL.glNewList(base + 'E', GL.GL_COMPILE);
    drawLetter(Edata);
    myGL.glEndList();
    myGL.glNewList(base + 'P', GL.GL_COMPILE);
    drawLetter(Pdata);
    myGL.glEndList();
    myGL.glNewList(base + 'R', GL.GL_COMPILE);
    drawLetter(Rdata);
    myGL.glEndList();
    myGL.glNewList(base + 'S', GL.GL_COMPILE);
    drawLetter(Sdata);
    myGL.glEndList();
    myGL.glNewList(base + ' ', GL.GL_COMPILE);
    myGL.glTranslatef(8.0f, 0.0f, 0.0f);
    myGL.glEndList();
  }

  private String test1 = "A SPARE SERAPE APPEARS AS";
  private String test2 = "APES PREPARE RARE PEPPERS";

  private void printStrokedString(String s) {
    int len = s.length();
    myGL.glCallLists(len, GL.GL_BYTE, s.getBytes());
  }

  public void display() {
    myGL.glClear(GL.GL_COLOR_BUFFER_BIT);
    myGL.glColor3f(1.0f, 1.0f, 1.0f);
    myGL.glPushMatrix();
    myGL.glScalef(2.0f, 2.0f, 2.0f);
    myGL.glTranslatef(10.0f, 30.0f, 0.0f);
    printStrokedString(test1);
    myGL.glPopMatrix();
    myGL.glPushMatrix();
    myGL.glScalef(2.0f, 2.0f, 2.0f);
    myGL.glTranslatef(10.0f, 13.0f, 0.0f);
    printStrokedString(test2);
    myGL.glPopMatrix();
    myGL.glFlush();
  }

  public void myReshape(int w, int h) {
    myGL.glViewport(0, 0, w, h);
    myGL.glMatrixMode(GL.GL_PROJECTION);
    myGL.glLoadIdentity();
    myGL.glOrtho(0.0, (double) w, 0.0, (double) h, -1.0, 1.0);
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
   * Main Loop Open window with initial window size, title bar, RGBA display mode, and handle input
   * events.
   */
  public void init() {
    myUT.glutInitWindowSize(440, 120);
    myUT.glutInitWindowPosition(0, 0);
    myUT.glutCreateWindow(this);
    myinit();
    myUT.glutDisplayFunc("display");
    myUT.glutReshapeFunc("myReshape");
    myUT.glutKeyboardFunc("keyboard");
    myUT.glutMainLoop();
  }

  static public void main(String args[]) throws IOException {
    Frame mainFrame = new Frame();
    mainFrame.setSize(448, 147);
    stroke mainCanvas = new stroke();
    mainCanvas.init();
    mainFrame.add(mainCanvas);
    mainFrame.setVisible(true);
  }

}
