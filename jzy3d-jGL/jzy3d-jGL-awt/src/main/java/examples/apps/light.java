package examples.apps;
/*
 * light.java This program demonstrates the use of the OpenGL lighting model. A sphere is drawn
 * using a grey material characteristic. A single light source illuminates the object.
 */

import java.awt.Frame;
import java.io.IOException;
import jgl.GL;
import jgl.wt.awt.GLCanvas;

public class light extends GLCanvas {

  /*
   * Initialize material property, light source, lighting model, and depth buffer.
   */
  private void myinit() {
    float mat_specular[] = {1.0f, 1.0f, 1.0f, 1.0f};
    float mat_shininess[] = {50.0f};
    float light_position[] = {1.0f, 1.0f, 1.0f, 0.0f};

    myGL.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    myGL.glShadeModel(GL.GL_SMOOTH);

    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, mat_specular);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, mat_shininess);
    myGL.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, light_position);

    myGL.glEnable(GL.GL_LIGHTING);
    myGL.glEnable(GL.GL_LIGHT0);
    myGL.glEnable(GL.GL_DEPTH_TEST);
  }

  public void display() {
    myGL.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    myUT.glutSolidSphere(1.0, 20, 16);
    myGL.glFlush();
  }

  public void myReshape(int w, int h) {
    myGL.glViewport(0, 0, w, h);
    myGL.glMatrixMode(GL.GL_PROJECTION);
    myGL.glLoadIdentity();
    if (w <= h) {
      myGL.glOrtho(-1.5f, 1.5f, -1.5f * (float) h / (float) w, 1.5f * (float) h / (float) w, -10.0f,
          10.0f);
    } else {
      myGL.glOrtho(-1.5f * (float) w / (float) h, 1.5f * (float) w / (float) h, -1.5f, 1.5f, -10.0f,
          10.0f);
    }
    myGL.glMatrixMode(GL.GL_MODELVIEW);
    myGL.glLoadIdentity();
  }

  /* ARGSUSED1 */
  public void keyboard(char key, int x, int y) {
    switch (key) {
      case 27:
        System.exit(0);
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
    myUT.glutKeyboardFunc("keyboard");
    myUT.glutMainLoop();
  }

  static public void main(String args[]) throws IOException {
    Frame mainFrame = new Frame();
    mainFrame.setSize(508, 527);
    light mainCanvas = new light();
    mainCanvas.init();
    mainFrame.add(mainCanvas);
    mainFrame.setVisible(true);
  }

}
