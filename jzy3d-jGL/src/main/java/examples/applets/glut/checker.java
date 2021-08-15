package examples.applets.glut;
/*
 * checker.java This program texture maps a checkerboard image onto two rectangles. This program
 * clamps the texture, if the texture coordinates fall outside 0.0 and 1.0.
 */

import jgl.GL;
import jgl.wt.awt.GLApplet;

public class checker extends GLApplet {

  /* Create checkerboard texture */
  private static final int checkImageWidth = 64;
  private static final int checkImageHeight = 64;
  private byte checkImage[][][] = new byte[checkImageWidth][checkImageHeight][3];

  private void makeCheckImage() {
    int i, j;
    byte c;

    for (i = 0; i < checkImageWidth; i++) {
      for (j = 0; j < checkImageHeight; j++) {
        if ((((i & 0x8) == 0) ^ ((j & 0x8)) == 0)) {
          c = (byte) 255;
        } else {
          c = (byte) 0;
        }
        checkImage[i][j][0] = c;
        checkImage[i][j][1] = c;
        checkImage[i][j][2] = c;
      }
    }
  }

  private void myinit() {
    myGL.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    myGL.glEnable(GL.GL_DEPTH_TEST);
    myGL.glDepthFunc(GL.GL_LESS);

    makeCheckImage();
    myGL.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);
    myGL.glTexImage2D(GL.GL_TEXTURE_2D, 0, 3, checkImageWidth, checkImageHeight, 0, GL.GL_RGB,
        GL.GL_UNSIGNED_BYTE, checkImage);
    myGL.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP);
    myGL.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP);
    myGL.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
    myGL.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
    myGL.glTexEnvf(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_DECAL);
    myGL.glEnable(GL.GL_TEXTURE_2D);
    myGL.glShadeModel(GL.GL_FLAT);
  }

  public void display() {
    myGL.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    myGL.glBegin(GL.GL_QUADS);
    myGL.glTexCoord2f(0.0f, 0.0f);
    myGL.glVertex3f(-2.0f, -1.0f, 0.0f);
    myGL.glTexCoord2f(0.0f, 1.0f);
    myGL.glVertex3f(-2.0f, 1.0f, 0.0f);
    myGL.glTexCoord2f(1.0f, 1.0f);
    myGL.glVertex3f(0.0f, 1.0f, 0.0f);
    myGL.glTexCoord2f(1.0f, 0.0f);
    myGL.glVertex3f(0.0f, -1.0f, 0.0f);

    myGL.glTexCoord2f(0.0f, 0.0f);
    myGL.glVertex3f(1.0f, -1.0f, 0.0f);
    myGL.glTexCoord2f(0.0f, 1.0f);
    myGL.glVertex3f(1.0f, 1.0f, 0.0f);
    myGL.glTexCoord2f(1.0f, 1.0f);
    myGL.glVertex3f(2.41421f, 1.0f, -1.41421f);
    myGL.glTexCoord2f(1.0f, 0.0f);
    myGL.glVertex3f(2.41421f, -1.0f, -1.41421f);
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
    myGL.glTranslatef(0.0f, 0.0f, -3.6f);
  }

  public void init() {
    myUT.glutInitWindowSize(500, 500);
    myUT.glutInitWindowPosition(0, 0);
    myUT.glutCreateWindow(this);
    myinit();
    myUT.glutReshapeFunc("myReshape");
    myUT.glutDisplayFunc("display");
    myUT.glutMainLoop();
  }

}
