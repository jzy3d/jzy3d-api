package examples.applets.glut;
/*
 * texsub.java This program texture maps a checkerboard image onto two rectangles. This program
 * clamps the texture, if the texture coordinates fall outside 0.0 and 1.0. If the s key is pressed,
 * a texture subimage is used to alter the original texture. If the r key is pressed, the original
 * texture is restored.
 */

import jgl.GL;
import jgl.wt.awt.GLApplet;

public class texsub extends GLApplet {

  /* Create checkerboard texture */
  private static final int checkImageWidth = 64;
  private static final int checkImageHeight = 64;
  private static final int subImageWidth = 16;
  private static final int subImageHeight = 16;
  private byte checkImage[][][] = new byte[checkImageWidth][checkImageHeight][4];
  private byte subImage[][][] = new byte[subImageWidth][subImageHeight][4];

  private int texName[] = new int[1];

  private void makeCheckImage() {
    int i, j, c;

    for (i = 0; i < checkImageWidth; i++) {
      for (j = 0; j < checkImageHeight; j++) {
        if ((((i & 0x8) == 0) ^ ((j & 0x8)) == 0))
          c = 255;
        else
          c = 0;
        checkImage[i][j][0] = (byte) c;
        checkImage[i][j][1] = (byte) c;
        checkImage[i][j][2] = (byte) c;
        checkImage[i][j][3] = (byte) 255;
      }
    }

    for (i = 0; i < subImageWidth; i++) {
      for (j = 0; j < subImageHeight; j++) {
        if ((((i & 0x4) == 0) ^ ((j & 0x4)) == 0))
          c = 255;
        else
          c = 0;
        subImage[i][j][0] = (byte) c;
        subImage[i][j][1] = (byte) 0;
        subImage[i][j][2] = (byte) 0;
        subImage[i][j][3] = (byte) 255;
      }
    }
  }

  private void myinit() {
    myGL.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    myGL.glShadeModel(GL.GL_FLAT);
    myGL.glEnable(GL.GL_DEPTH_TEST);

    makeCheckImage();
    myGL.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);

    myGL.glGenTextures(1, texName);
    myGL.glBindTexture(GL.GL_TEXTURE_2D, texName[0]);

    myGL.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
    myGL.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
    myGL.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
    myGL.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
    myGL.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, checkImageWidth, checkImageHeight, 0,
        GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, checkImage);
  }

  public void display() {
    myGL.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    myGL.glEnable(GL.GL_TEXTURE_2D);
    myGL.glTexEnvf(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_DECAL);
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
    myGL.glDisable(GL.GL_TEXTURE_2D);
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

  public void keyboard(char key, int x, int y) {
    switch (key) {
      case 's':
      case 'S':
        myGL.glBindTexture(GL.GL_TEXTURE_2D, texName[0]);
        myGL.glTexSubImage2D(GL.GL_TEXTURE_2D, 0, 12, 44, subImageWidth, subImageHeight, GL.GL_RGBA,
            GL.GL_UNSIGNED_BYTE, subImage);
        myUT.glutPostRedisplay();
        break;
      case 'r':
      case 'R':
        myGL.glBindTexture(GL.GL_TEXTURE_2D, texName[0]);
        myGL.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, checkImageWidth, checkImageHeight, 0,
            GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, checkImage);
        myUT.glutPostRedisplay();
        break;
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

}
