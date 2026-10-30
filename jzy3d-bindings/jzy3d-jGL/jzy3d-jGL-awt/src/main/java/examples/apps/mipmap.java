package examples.apps;
/*
 * mipmap.java This program demonstrates using mipmaps for texture maps. To overtly show the effect
 * of mipmaps, each mipmap reduction level has a solidly colored, contrasting texture image. Thus,
 * the quadrilateral which is drawn is drawn with several different colors.
 */

import java.awt.Frame;
import java.io.IOException;
import jgl.GL;
import jgl.wt.awt.GLCanvas;

public class mipmap extends GLCanvas {

  private byte mipmapImage32[][][] = new byte[32][32][3];
  private byte mipmapImage16[][][] = new byte[16][16][3];
  private byte mipmapImage8[][][] = new byte[8][8][3];
  private byte mipmapImage4[][][] = new byte[4][4][3];
  private byte mipmapImage2[][][] = new byte[2][2][3];
  private byte mipmapImage1[][][] = new byte[1][1][3];

  private void makeImages() {
    int i, j;

    for (i = 0; i < 32; i++) {
      for (j = 0; j < 32; j++) {
        mipmapImage32[i][j][0] = (byte) 255;
        mipmapImage32[i][j][1] = (byte) 255;
        mipmapImage32[i][j][2] = (byte) 0;
      }
    }
    for (i = 0; i < 16; i++) {
      for (j = 0; j < 16; j++) {
        mipmapImage16[i][j][0] = (byte) 255;
        mipmapImage16[i][j][1] = (byte) 0;
        mipmapImage16[i][j][2] = (byte) 255;
      }
    }
    for (i = 0; i < 8; i++) {
      for (j = 0; j < 8; j++) {
        mipmapImage8[i][j][0] = (byte) 255;
        mipmapImage8[i][j][1] = (byte) 0;
        mipmapImage8[i][j][2] = (byte) 0;
      }
    }
    for (i = 0; i < 4; i++) {
      for (j = 0; j < 4; j++) {
        mipmapImage4[i][j][0] = (byte) 0;
        mipmapImage4[i][j][1] = (byte) 255;
        mipmapImage4[i][j][2] = (byte) 0;
      }
    }
    for (i = 0; i < 2; i++) {
      for (j = 0; j < 2; j++) {
        mipmapImage2[i][j][0] = (byte) 0;
        mipmapImage2[i][j][1] = (byte) 0;
        mipmapImage2[i][j][2] = (byte) 255;
      }
    }
    mipmapImage1[0][0][0] = (byte) 255;
    mipmapImage1[0][0][1] = (byte) 255;
    mipmapImage1[0][0][2] = (byte) 255;
  }

  private void myinit() {
    myGL.glEnable(GL.GL_DEPTH_TEST);
    myGL.glDepthFunc(GL.GL_LESS);
    myGL.glShadeModel(GL.GL_FLAT);

    myGL.glTranslatef(0.0f, 0.0f, -3.6f);
    makeImages();
    myGL.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);
    myGL.glTexImage2D(GL.GL_TEXTURE_2D, 0, 3, 32, 32, 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE,
        mipmapImage32);
    myGL.glTexImage2D(GL.GL_TEXTURE_2D, 1, 3, 16, 16, 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE,
        mipmapImage16);
    myGL.glTexImage2D(GL.GL_TEXTURE_2D, 2, 3, 8, 8, 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE,
        mipmapImage8);
    myGL.glTexImage2D(GL.GL_TEXTURE_2D, 3, 3, 4, 4, 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE,
        mipmapImage4);
    myGL.glTexImage2D(GL.GL_TEXTURE_2D, 4, 3, 2, 2, 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE,
        mipmapImage2);
    myGL.glTexImage2D(GL.GL_TEXTURE_2D, 5, 3, 1, 1, 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE,
        mipmapImage1);
    myGL.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
    myGL.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
    myGL.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
    myGL.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST_MIPMAP_NEAREST);
    myGL.glTexEnvf(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_DECAL);
    myGL.glEnable(GL.GL_TEXTURE_2D);
  }

  public void display() {
    myGL.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    myGL.glBegin(GL.GL_QUADS);
    myGL.glTexCoord2f(0.0f, 0.0f);
    myGL.glVertex3f(-2.0f, -1.0f, 0.0f);
    myGL.glTexCoord2f(0.0f, 8.0f);
    myGL.glVertex3f(-2.0f, 1.0f, 0.0f);
    myGL.glTexCoord2f(8.0f, 8.0f);
    myGL.glVertex3f(2000.0f, 1.0f, -6000.0f);
    myGL.glTexCoord2f(8.0f, 0.0f);
    myGL.glVertex3f(2000.0f, -1.0f, -6000.0f);
    myGL.glEnd();
    myGL.glFlush();
  }

  public void myReshape(int w, int h) {
    myGL.glViewport(0, 0, w, h);
    myGL.glMatrixMode(GL.GL_PROJECTION);
    myGL.glLoadIdentity();
    myGLU.gluPerspective(60.0, 1.0 * (double) w / (double) h, 1.0, 30000.0);
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
    mipmap mainCanvas = new mipmap();
    mainCanvas.init();
    mainFrame.add(mainCanvas);
    mainFrame.setVisible(true);
  }

}
