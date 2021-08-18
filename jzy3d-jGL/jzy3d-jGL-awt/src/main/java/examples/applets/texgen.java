package examples.applets;

import java.applet.Applet;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import jgl.wt.awt.GL;
import jgl.wt.awt.GLAUX;

public class texgen extends Applet implements ComponentListener {
  // must use GL to use jGL.....
  // and use GLAUX to use the aux functions.....
  // remember to give GL to initialize GLAUX
  GL myGL = new GL();
  GLAUX myAUX = new GLAUX(myGL);

  private static final int stripeImageWidth = 32;
  private byte stripeImage[][] = new byte[stripeImageWidth][3];

  private void makeStripeImage() {
    int j;

    for (j = 0; j < stripeImageWidth; j++) {
      if (j <= 4)
        stripeImage[j][0] = (byte) 255;
      else
        stripeImage[j][0] = (byte) 0;
      if (j > 4)
        stripeImage[j][1] = (byte) 255;
      else
        stripeImage[j][1] = (byte) 0;
      stripeImage[j][2] = 0;
    }
  }

  /* glTexGen stuff: */
  float sgenparams[] = {1.0f, 1.0f, 1.0f, 0.0f};

  private void myinit() {
    myGL.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

    makeStripeImage();
    myGL.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);
    myGL.glTexEnvf(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_MODULATE);
    myGL.glTexParameterf(GL.GL_TEXTURE_1D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
    myGL.glTexParameterf(GL.GL_TEXTURE_1D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
    myGL.glTexParameterf(GL.GL_TEXTURE_1D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
    myGL.glTexImage1D(GL.GL_TEXTURE_1D, 0, 3, stripeImageWidth, 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE,
        stripeImage);

    myGL.glTexGeni(GL.GL_S, GL.GL_TEXTURE_GEN_MODE, GL.GL_OBJECT_LINEAR);
    myGL.glTexGenfv(GL.GL_S, GL.GL_OBJECT_PLANE, sgenparams);

    myGL.glEnable(GL.GL_DEPTH_TEST);
    myGL.glDepthFunc(GL.GL_LESS);
    myGL.glEnable(GL.GL_TEXTURE_GEN_S);
    myGL.glEnable(GL.GL_TEXTURE_1D);
    myGL.glEnable(GL.GL_CULL_FACE);
    myGL.glEnable(GL.GL_LIGHTING);
    myGL.glEnable(GL.GL_LIGHT0);
    myGL.glEnable(GL.GL_AUTO_NORMAL);
    myGL.glEnable(GL.GL_NORMALIZE);
    myGL.glFrontFace(GL.GL_CW);
    myGL.glCullFace(GL.GL_BACK);
    myGL.glMaterialf(GL.GL_FRONT, GL.GL_SHININESS, 64.0f);
  }

  private void display() {
    myGL.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    myGL.glPushMatrix();
    myGL.glRotatef(45.0f, 0.0f, 0.0f, 1.0f);
    myAUX.auxSolidTeapot(2.0);
    myGL.glPopMatrix();
    myGL.glFlush();
  }

  private void myReshape(int w, int h) {
    myGL.glViewport(0, 0, w, h);
    myGL.glMatrixMode(GL.GL_PROJECTION);
    myGL.glLoadIdentity();
    if (w <= h) {
      myGL.glOrtho(-3.5f, 3.5f, -3.5f * (float) h / (float) w, 3.5f * (float) h / (float) w, -3.5f,
          3.5f);
    } else {
      myGL.glOrtho(-3.5f * (float) w / (float) h, 3.5f * (float) w / (float) h, -3.5f, 3.5f, -3.5f,
          3.5f);
    }
    myGL.glMatrixMode(GL.GL_MODELVIEW);
    myGL.glLoadIdentity();
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

  public void update(Graphics g) {
    // skip the clear screen command....
    paint(g);
  }

  public void paint(Graphics g) {
    myGL.glXSwapBuffers(g, this);
  }

  public void init() {
    myAUX.auxInitPosition(0, 0, 200, 200);
    myAUX.auxInitWindow(this);
    myinit();

    // as call auxReshapeFunc()
    addComponentListener(this);
    myReshape(getSize().width, getSize().height);

    // call display as call auxIdleFunc(display)
    display();
  }

}
