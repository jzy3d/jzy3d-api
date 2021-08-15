package examples.applets.glut;

import jgl.GL;
import jgl.wt.awt.GLApplet;
import jgl.wt.awt.GLUT;

public class picksquare extends GLApplet {

  private int board[][] = new int[3][3]; /* amount of color for each square */

  /* Clear color value for every square on the board */
  private void myinit() {
    int i, j;
    for (i = 0; i < 3; i++)
      for (j = 0; j < 3; j++)
        board[i][j] = 0;
    myGL.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
  }

  /*
   * The nine squares are drawn. In selection mode, each square is given two names: one for the row
   * and the other for the column on the grid. The color of each square is determined by its
   * position on the grid, and the value in the board[][] array.
   */
  private void drawSquares(int mode) {
    int i, j;
    for (i = 0; i < 3; i++) {
      if (mode == GL.GL_SELECT)
        myGL.glLoadName(i);
      for (j = 0; j < 3; j++) {
        if (mode == GL.GL_SELECT)
          myGL.glPushName(j);
        myGL.glColor3f((float) (i / 3.0), (float) (j / 3.0), (float) (board[i][j] / 3.0));
        myGL.glRecti(i, j, i + 1, j + 1);
        if (mode == GL.GL_SELECT)
          myGL.glPopName();
      }
    }
  }

  /*
   * processHits prints out the contents of the selection array.
   */
  private void processHits(int hits, int buffer[]) {
    int i, j;
    int ii = 0, jj = 0, names, ptr;

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
        if (j == 0) { /* set row and column */
          ii = buffer[ptr];
        } else {
          jj = buffer[ptr];
        }
        ptr++;
      }
      System.out.println();
      board[ii][jj] = (board[ii][jj] + 1) % 3;
    }
  }

  /*
   * pickSquares() sets up selection mode, name stack, and projection matrix for picking. Then the
   * objects are drawn.
   */
  private static final int BUFSIZE = 512;

  public void pickSquares(int button, int state, int x, int y) {
    int selectBuf[] = new int[BUFSIZE];
    int hits;
    int viewport[] = new int[4];

    if (button != GLUT.GLUT_LEFT_BUTTON || state != GLUT.GLUT_DOWN)
      return;

    myGL.glGetIntegerv(GL.GL_VIEWPORT, viewport);

    myGL.glSelectBuffer(BUFSIZE, selectBuf);
    myGL.glRenderMode(GL.GL_SELECT);

    myGL.glInitNames();
    myGL.glPushName(0);

    myGL.glMatrixMode(GL.GL_PROJECTION);
    myGL.glPushMatrix();
    myGL.glLoadIdentity();
    /* create 5x5 pixel picking region near cursor location */
    myGLU.gluPickMatrix((double) x, (double) (viewport[3] - y), 5.0, 5.0, viewport);
    myGLU.gluOrtho2D(0.0, 3.0, 0.0, 3.0);
    drawSquares(GL.GL_SELECT);

    myGL.glMatrixMode(GL.GL_PROJECTION);
    myGL.glPopMatrix();
    myGL.glFlush();

    hits = myGL.glRenderMode(GL.GL_RENDER);
    processHits(hits, selectBuf);
    myUT.glutPostRedisplay();
  }

  public void display() {
    myGL.glClear(GL.GL_COLOR_BUFFER_BIT);
    drawSquares(GL.GL_RENDER);
    myGL.glFlush();
  }

  public void myReshape(int w, int h) {
    myGL.glViewport(0, 0, w, h);
    myGL.glMatrixMode(GL.GL_PROJECTION);
    myGL.glLoadIdentity();
    myGLU.gluOrtho2D(0.0, 3.0, 0.0, 3.0);
    myGL.glMatrixMode(GL.GL_MODELVIEW);
    myGL.glLoadIdentity();
  }

  /* Main Loop */
  public void init() {
    myUT.glutInitWindowSize(100, 100);
    myUT.glutInitWindowPosition(0, 0);
    myUT.glutCreateWindow(this);
    myinit();
    myUT.glutReshapeFunc("myReshape");
    myUT.glutDisplayFunc("display");
    myUT.glutMouseFunc("pickSquares");
    myUT.glutMainLoop();
  }

}
