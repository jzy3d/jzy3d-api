package examples.applets;

import java.applet.Applet;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import jgl.wt.awt.GL;
import jgl.wt.awt.GLAUX;
import jgl.wt.awt.GLU;

public class mipmap extends Applet implements ComponentListener {
  // must use GL to use jGL.....
  // and use GLU to use the glu functions....
  // remember to give GL to initialize GLU
  // and use GLAUX to use the aux functions.....
  // remember to give GL to initialize GLAUX
  GL myGL = new GL();
  GLU myGLU = new GLU(myGL);
  GLAUX myAUX = new GLAUX(myGL);

  private byte mipmapImage32[][][] = new byte[32][32][3];
  private byte mipmapImage16[][][] = new byte[16][16][3];
  private byte mipmapImage8[][][] = new byte[8][8][3];
  private byte mipmapImage4[][][] = new byte[4][4][3];
  private byte mipmapImage2[][][] = new byte[2][2][3];
  private byte mipmapImage1[][][] = new byte[1][1][3];

  private void loadImage() {
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
    myGL.glDepthFunc(GL.GL_LEQUAL);
    myGL.glShadeModel(GL.GL_FLAT);
    myGL.glTranslatef(0.0f, 0.0f, -3.6f);
    loadImage();
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

  private void display() {
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

  public void componentMoved(ComponentEvent e) {}

  public void componentShown(ComponentEvent e) {}

  public void componentHidden(ComponentEvent e) {}

  public void componentResized(ComponentEvent e) {
    // get window width and height by myself
    myReshape(getSize().width, getSize().height);
    display();
    repaint();
  }

  private void myReshape(int w, int h) {
    myGL.glViewport(0, 0, w, h);
    myGL.glMatrixMode(GL.GL_PROJECTION);
    myGL.glLoadIdentity();
    myGLU.gluPerspective(60.0, 1.0 * (double) w / (double) h, 1.0, 30000.0);
    myGL.glMatrixMode(GL.GL_MODELVIEW);
    myGL.glLoadIdentity();
  }

  public void update(Graphics g) {
    // skip the clear screen command....
    paint(g);
  }

  public void paint(Graphics g) {
    // STRANGE if call display here, it will be never stoped
    myGL.glXSwapBuffers(g, this);
  }

  public void init() {
    myAUX.auxInitPosition(0, 0, 500, 500);
    myAUX.auxInitWindow(this);
    myinit();

    // as call auxReshapeFunc()
    addComponentListener(this);
    myReshape(getSize().width, getSize().height);

    // call display as call auxMainLoop(display);
    display();
  }

}
