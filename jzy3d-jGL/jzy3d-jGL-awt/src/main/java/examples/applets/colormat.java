package examples.applets;

import java.applet.Applet;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import jgl.wt.awt.GL;
import jgl.wt.awt.GLAUX;

public class colormat extends Applet implements ComponentListener, MouseListener {
  // must use GL to use jGL.....
  // and use GLAUX to use the aux functions.....
  // remember to give GL to initialize GLAUX
  GL myGL = new GL();
  GLAUX myAUX = new GLAUX(myGL);

  private float diffuseMaterial[] = {0.5f, 0.5f, 0.5f, 1.0f};

  private void myinit() {
    float mat_specular[] = {1.0f, 1.0f, 1.0f, 1.0f};
    float light_position[] = {1.0f, 1.0f, 1.0f, 0.0f};

    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, diffuseMaterial);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, mat_specular);
    myGL.glMaterialf(GL.GL_FRONT, GL.GL_SHININESS, 25.0f);
    myGL.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, light_position);

    myGL.glEnable(GL.GL_LIGHTING);
    myGL.glEnable(GL.GL_LIGHT0);
    myGL.glDepthFunc(GL.GL_LEQUAL);
    myGL.glEnable(GL.GL_DEPTH_TEST);

    myGL.glColorMaterial(GL.GL_FRONT, GL.GL_DIFFUSE);
    myGL.glEnable(GL.GL_COLOR_MATERIAL);
  }

  private void changeRedDiffuse() {
    diffuseMaterial[0] += 0.1f;
    if (diffuseMaterial[0] > 1.0f) {
      diffuseMaterial[0] = 0.0f;
    }
    myGL.glColor4fv(diffuseMaterial);
  }

  private void changeGreenDiffuse() {
    diffuseMaterial[1] += 0.1f;
    if (diffuseMaterial[1] > 1.0f) {
      diffuseMaterial[1] = 0.0f;
    }
    myGL.glColor4fv(diffuseMaterial);
  }

  private void changeBlueDiffuse() {
    diffuseMaterial[2] += 0.1f;
    if (diffuseMaterial[2] > 1.0f) {
      diffuseMaterial[2] = 0.0f;
    }
    myGL.glColor4fv(diffuseMaterial);
  }

  public void mouseClicked(MouseEvent e) {}

  public void mouseEntered(MouseEvent e) {}

  public void mouseExited(MouseEvent e) {}

  public void mouseReleased(MouseEvent e) {}

  public void mousePressed(MouseEvent e) {
    // process which button by myself
    if ((e.getModifiers() & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK) { // left
      changeRedDiffuse();
      display();
      repaint();
    } else if ((e.getModifiers() & InputEvent.BUTTON2_MASK) == InputEvent.BUTTON2_MASK) { // middle
      changeGreenDiffuse();
      display();
      repaint();
    } else if ((e.getModifiers() & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK) { // right
      changeBlueDiffuse();
      display();
      repaint();
    }
    e.consume();
  }

  private void display() {
    myGL.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    myAUX.auxSolidSphere(1.0);
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

  public void update(Graphics g) {
    // skip the clear screen command....
    paint(g);
  }

  public void paint(Graphics g) {
    myGL.glXSwapBuffers(g, this);
  }

  public void init() {
    myAUX.auxInitPosition(0, 0, 500, 500);
    myAUX.auxInitWindow(this);
    myinit();

    // as call auxMouseFunc()
    addMouseListener(this);

    // as call auxReshapeFunc()
    addComponentListener(this);
    myReshape(getSize().width, getSize().height);

    // call display as call auxMainLoop(display);
    display();
  }

}
