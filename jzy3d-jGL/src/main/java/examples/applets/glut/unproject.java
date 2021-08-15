package examples.applets.glut;

import jgl.GL;
import jgl.wt.awt.GLApplet;
import jgl.wt.awt.GLUT;

public class unproject extends GLApplet {

  public void display() {
    myGL.glClear(GL.GL_COLOR_BUFFER_BIT);
    myGL.glFlush();
  }

  /*
   * Change these values for a different transformation
   */
  public void myReshape(int w, int h) {
    myGL.glViewport(0, 0, w, h);
    myGL.glMatrixMode(GL.GL_PROJECTION);
    myGL.glLoadIdentity();
    myGLU.gluPerspective(45.0, (float) w / (float) h, 1.0, 100.0);
    myGL.glMatrixMode(GL.GL_MODELVIEW);
    myGL.glLoadIdentity();
  }

  public void mouse(int button, int state, int x, int y) {
    int viewport[] = new int[4];
    double mvmatrix[] = new double[16], projmatrix[] = new double[16];
    int realy; /* OpenGL y coordinate position */
    double wx[] = new double[1], wy[] = new double[1],
        wz[] = new double[1]; /* returned world x, y, z coords */

    switch (button) {
      case GLUT.GLUT_LEFT_BUTTON:
        if (state == GLUT.GLUT_DOWN) {
          myGL.glGetIntegerv(GL.GL_VIEWPORT, viewport);
          myGL.glGetDoublev(GL.GL_MODELVIEW_MATRIX, mvmatrix);
          myGL.glGetDoublev(GL.GL_PROJECTION_MATRIX, projmatrix);
          /* note viewport[3] is height of window in pixels */
          realy = viewport[3] - y - 1;
          System.out.println("Coordinates at cursor are (" + x + ", " + realy + ")");
          myGLU.gluUnProject(x, realy, 0.0, mvmatrix, projmatrix, viewport, wx, wy, wz);
          System.out
              .println("World coords at z=0.0 are (" + wx[0] + ", " + wy[0] + ", " + wz[0] + ")");
          myGLU.gluUnProject(x, realy, 1.0, mvmatrix, projmatrix, viewport, wx, wy, wz);
          System.out
              .println("World coords at z=1.0 are (" + wx[0] + ", " + wy[0] + ", " + wz[0] + ")");
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
    myUT.glutDisplayFunc("display");
    myUT.glutReshapeFunc("myReshape");
    myUT.glutMouseFunc("mouse");
    myUT.glutMainLoop();
  }

}
