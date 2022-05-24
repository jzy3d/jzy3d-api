package examples.apps;
/*
 * texture3d.java This program demonstrates using a three-dimensional texture. It creates a 3D
 * texture and then renders two rectangles with different texture coordinates to obtain different
 * "slices" of the 3D texture.
 */

import java.awt.Frame;
import java.io.IOException;
import jgl.GL;
import jgl.wt.awt.GLCanvas;

public class texture3d extends GLCanvas {

  private static final int iWidth = 16;
  private static final int iHeight = 16;
  private static final int iDepth = 16;

  private byte image[][][][] = new byte[iDepth][iHeight][iWidth][3];
  private int texName[] = new int[1];

  /*
   * Create a 16x16x16x3 array with different color values in each array element [r, g, b]. Values
   * range from 0 to 255.
   */
  private void makeImage() {
    int s, t, r;

    for (s = 0; s < 16; s++) {
      for (t = 0; t < 16; t++) {
        for (r = 0; r < 16; r++) {
          image[r][s][t][0] = (byte) (s * 17);
          image[r][s][t][1] = (byte) (t * 17);
          image[r][s][t][2] = (byte) (r * 17);
        }
      }
    }
  }

  /*
   * Initialize state: the 3D texture object and its image.
   */
  private void myinit() {
    myGL.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    myGL.glShadeModel(GL.GL_FLAT);
    myGL.glEnable(GL.GL_DEPTH_TEST);

    makeImage();
    myGL.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);

    myGL.glGenTextures(1, texName);
    myGL.glBindTexture(GL.GL_TEXTURE_3D, texName[0]);

    myGL.glTexParameterf(GL.GL_TEXTURE_3D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP);
    myGL.glTexParameterf(GL.GL_TEXTURE_3D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP);
    myGL.glTexParameterf(GL.GL_TEXTURE_3D, GL.GL_TEXTURE_WRAP_R, GL.GL_CLAMP);

    myGL.glTexParameterf(GL.GL_TEXTURE_3D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
    myGL.glTexParameterf(GL.GL_TEXTURE_3D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
    myGL.glTexImage3D(GL.GL_TEXTURE_3D, 0, GL.GL_RGB, iWidth, iHeight, iDepth, 0, GL.GL_RGB,
        GL.GL_UNSIGNED_BYTE, image);
    myGL.glEnable(GL.GL_TEXTURE_3D);
  }

  public void display() {
    myGL.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    myGL.glBegin(GL.GL_QUADS);
    myGL.glTexCoord3f(0.0f, 0.0f, 0.0f);
    myGL.glVertex3f(-2.25f, -1.0f, 0.0f);
    myGL.glTexCoord3f(0.0f, 1.0f, 0.0f);
    myGL.glVertex3f(-2.25f, 1.0f, 0.0f);
    myGL.glTexCoord3f(1.0f, 1.0f, 1.0f);
    myGL.glVertex3f(-0.25f, 1.0f, 0.0f);
    myGL.glTexCoord3f(1.0f, 0.0f, 1.0f);
    myGL.glVertex3f(-0.25f, -1.0f, 0.0f);

    myGL.glTexCoord3f(0.0f, 0.0f, 1.0f);
    myGL.glVertex3f(0.25f, -1.0f, 0.0f);
    myGL.glTexCoord3f(0.0f, 1.0f, 1.0f);
    myGL.glVertex3f(0.25f, 1.0f, 0.0f);
    myGL.glTexCoord3f(1.0f, 1.0f, 0.0f);
    myGL.glVertex3f(2.25f, 1.0f, 0.0f);
    myGL.glTexCoord3f(1.0f, 0.0f, 0.0f);
    myGL.glVertex3f(2.25f, -1.0f, 0.0f);
    myGL.glEnd();
    myGL.glFlush();
  }

  public void myReshape(int w, int h) {
    myGL.glViewport(0, 0, w, h);
    myGL.glMatrixMode(GL.GL_PROJECTION);
    myGL.glLoadIdentity();
    myGLU.gluPerspective(60.0, 1.0 * (double) w / (double) h, 1.0, 30.0);
    myGL.glMatrixMode(GL.GL_MODELVIEW);
    myGL.glLoadIdentity();
    myGL.glTranslatef(0.0f, 0.0f, -4.0f);
  }

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
    myUT.glutReshapeFunc("myReshape");
    myUT.glutDisplayFunc("display");
    myUT.glutKeyboardFunc("keyboard");
    myUT.glutMainLoop();
  }

  static public void main(String args[]) throws IOException {
    Frame mainFrame = new Frame();
    mainFrame.setSize(508, 527);
    texture3d mainCanvas = new texture3d();
    mainCanvas.init();
    mainFrame.add(mainCanvas);
    mainFrame.setVisible(true);
  }

}
