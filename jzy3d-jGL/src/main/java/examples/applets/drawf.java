package examples.applets;

import java.applet.Applet;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import jgl.wt.awt.GL;
import jgl.wt.awt.GLAUX;

public class drawf extends Applet implements ComponentListener {
  // must use GL to use jGL.....
  // and use GLAUX to use the aux functions.....
  // remember to give GL to initialize GLAUX
  GL myGL = new GL();
  GLAUX myAUX = new GLAUX(myGL);

  // public static final byte rasters [] = {
  // 0xc0, 0x00, 0xc0, 0x00, 0xc0, 0x00, 0xc0, 0x00, 0xc0, 0x00,
  // 0xff, 0x00, 0xff, 0x00, 0xc0, 0x00, 0xc0, 0x00, 0xc0, 0x00,
  // 0xff, 0xc0, 0xff, 0xc0 };

  private void myinit() {
    myGL.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);
    myGL.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
  }

  private void display() {
    myGL.glClear(GL.GL_COLOR_BUFFER_BIT);
    myGL.glColor3f(1.0f, 1.0f, 1.0f);
    myGL.glRasterPos2f(20.5f, 20.5f);
    // myGL.glBitmap (10, 12, 0.0, 0.0, 12.0, 0.0, rasters);
    // myGL.glBitmap (10, 12, 0.0, 0.0, 12.0, 0.0, rasters);
    // myGL.glBitmap (10, 12, 0.0, 0.0, 12.0, 0.0, rasters);
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
    myGL.glOrtho(0, w, 0, h, -1.0, 1.0);
    myGL.glMatrixMode(GL.GL_MODELVIEW);
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
