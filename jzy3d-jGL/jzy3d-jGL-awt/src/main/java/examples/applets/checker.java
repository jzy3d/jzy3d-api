package examples.applets;

import java.applet.Applet;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import jgl.wt.awt.GL;
import jgl.wt.awt.GLAUX;
import jgl.wt.awt.GLU;

public class checker extends Applet implements ComponentListener {
  // must use GL to use jGL.....
  // and use GLU to use the glu functions....
  // remember to give GL to initialize GLU
  // and use GLAUX to use the aux functions.....
  // remember to give GL to initialize GLAUX
  GL myGL = new GL();
  GLU myGLU = new GLU(myGL);
  GLAUX myAUX = new GLAUX(myGL);

  private static final int checkImageWidth = 64;
  private static final int checkImageHeight = 64;
  private byte checkImage[][][] = new byte[checkImageWidth][checkImageHeight][3];

  private void makeCheckImage() {
    int i, j, r;
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
    myGL.glDepthFunc(GL.GL_LEQUAL);
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

  private void display() {
    myGL.glClear(GL.GL_COLOR_BUFFER_BIT);
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
    myGLU.gluPerspective(60.0, 1.0 * (double) w / (double) h, 1.0, 30.0);
    myGL.glMatrixMode(GL.GL_MODELVIEW);
    myGL.glLoadIdentity();
    myGL.glTranslatef(0.0f, 0.0f, -3.6f);
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
