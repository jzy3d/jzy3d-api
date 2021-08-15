package examples.applets.glut;
/*
 * texbind.java This program demonstrates using glBindTexture() by creating and managing two
 * textures.
 */

import jgl.GL;
import jgl.wt.awt.GLApplet;

public class texbind extends GLApplet {

  /* Create checkerboard texture */
  private static final int checkImageWidth = 64;
  private static final int checkImageHeight = 64;
  private byte checkImage[][][] = new byte[checkImageWidth][checkImageHeight][4];
  private byte otherImage[][][] = new byte[checkImageWidth][checkImageHeight][4];

  private int texName[] = new int[2];

  private void makeCheckImage() {
    int i, j, c;

    for (i = 0; i < checkImageWidth; i++) {
      for (j = 0; j < checkImageHeight; j++) {
        if ((((i & 0x8) == 0) ^ ((j & 0x8)) == 0))
          c = (byte) 255;
        else
          c = (byte) 0;
        checkImage[i][j][0] = (byte) c;
        checkImage[i][j][1] = (byte) c;
        checkImage[i][j][2] = (byte) c;
        checkImage[i][j][3] = (byte) 255;
        if ((((i & 0x10) == 0) ^ ((j & 0x10)) == 0))
          c = (byte) 255;
        else
          c = (byte) 0;
        otherImage[i][j][0] = (byte) c;
        otherImage[i][j][1] = (byte) 0;
        otherImage[i][j][2] = (byte) 0;
        otherImage[i][j][3] = (byte) 255;
      }
    }
  }

  private void myinit() {
    myGL.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    myGL.glShadeModel(GL.GL_FLAT);
    myGL.glEnable(GL.GL_DEPTH_TEST);

    makeCheckImage();
    myGL.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);

    myGL.glGenTextures(2, texName);
    myGL.glBindTexture(GL.GL_TEXTURE_2D, texName[0]);
    myGL.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP);
    myGL.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP);
    myGL.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
    myGL.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
    myGL.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, checkImageWidth, checkImageHeight, 0,
        GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, checkImage);

    myGL.glBindTexture(GL.GL_TEXTURE_2D, texName[1]);
    myGL.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP);
    myGL.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP);
    myGL.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
    myGL.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
    myGL.glTexEnvf(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_DECAL);
    myGL.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, checkImageWidth, checkImageHeight, 0,
        GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, otherImage);
    myGL.glEnable(GL.GL_TEXTURE_2D);
  }

  public void display() {
    myGL.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    myGL.glBindTexture(GL.GL_TEXTURE_2D, texName[0]);
    myGL.glBegin(GL.GL_QUADS);
    myGL.glTexCoord2f(0.0f, 0.0f);
    myGL.glVertex3f(-2.0f, -1.0f, 0.0f);
    myGL.glTexCoord2f(0.0f, 1.0f);
    myGL.glVertex3f(-2.0f, 1.0f, 0.0f);
    myGL.glTexCoord2f(1.0f, 1.0f);
    myGL.glVertex3f(0.0f, 1.0f, 0.0f);
    myGL.glTexCoord2f(1.0f, 0.0f);
    myGL.glVertex3f(0.0f, -1.0f, 0.0f);
    myGL.glEnd();
    myGL.glBindTexture(GL.GL_TEXTURE_2D, texName[1]);
    myGL.glBegin(GL.GL_QUADS);
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
