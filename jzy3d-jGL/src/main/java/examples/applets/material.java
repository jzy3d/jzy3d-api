package examples.applets;

import java.applet.Applet;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Date;

import jgl.wt.awt.GL;
import jgl.wt.awt.GLAUX;

public class material extends Applet implements ComponentListener {
  // must use GL to use jGL.....
  // and use GLAUX to use the aux functions.....
  // remember to give GL to initialize GLAUX
  GL myGL = new GL();
  GLAUX myAUX = new GLAUX(myGL);

  private void myinit() {
    float ambient[] = {0.0f, 0.0f, 0.0f, 1.0f};
    float diffuse[] = {1.0f, 1.0f, 1.0f, 1.0f};
    float specular[] = {1.0f, 1.0f, 1.0f, 1.0f};
    float position[] = {0.0f, 3.0f, 2.0f, 0.0f};
    float lmodel_ambient[] = {0.4f, 0.4f, 0.4f, 1.0f};
    float local_view[] = {0.0f};

    myGL.glEnable(GL.GL_DEPTH_TEST);
    myGL.glDepthFunc(GL.GL_LESS);

    myGL.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT, ambient);
    myGL.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, diffuse);
    myGL.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, position);
    myGL.glLightModelfv(GL.GL_LIGHT_MODEL_AMBIENT, lmodel_ambient);
    myGL.glLightModelfv(GL.GL_LIGHT_MODEL_LOCAL_VIEWER, local_view);

    myGL.glEnable(GL.GL_LIGHTING);
    myGL.glEnable(GL.GL_LIGHT0);

    myGL.glClearColor(0.0f, 0.1f, 0.1f, 0.0f);
  }

  private void display() {
    float no_mat[] = {0.0f, 0.0f, 0.0f, 1.0f};
    float mat_ambient[] = {0.7f, 0.7f, 0.7f, 1.0f};
    float mat_ambient_color[] = {0.8f, 0.8f, 0.2f, 1.0f};
    float mat_diffuse[] = {0.1f, 0.5f, 0.8f, 1.0f};
    float mat_specular[] = {1.0f, 1.0f, 1.0f, 1.0f};
    float no_shininess[] = {0.0f};
    float low_shininess[] = {5.0f};
    float high_shininess[] = {100.0f};
    float mat_emission[] = {0.3f, 0.2f, 0.2f, 0.0f};

    Date startTime = new Date();
    myGL.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

    /*
     * draw sphere in first row, first column diffuse reflection only; no ambient or specular
     */
    myGL.glPushMatrix();
    myGL.glTranslatef(-3.75f, 3.0f, 0.0f);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, no_mat);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, mat_diffuse);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, no_mat);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, no_shininess);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_EMISSION, no_mat);
    myAUX.auxSolidSphere(1.0);
    myGL.glPopMatrix();

    /*
     * draw sphere in first row, second column diffuse and specular reflection; low shininess; no
     * ambient
     */
    myGL.glPushMatrix();
    myGL.glTranslatef(-1.25f, 3.0f, 0.0f);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, no_mat);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, mat_diffuse);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, mat_specular);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, low_shininess);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_EMISSION, no_mat);
    myAUX.auxSolidSphere(1.0);
    myGL.glPopMatrix();

    /*
     * draw sphere in first row, third column diffuse and specular reflection; high shininess; no
     * ambient
     */
    myGL.glPushMatrix();
    myGL.glTranslatef(1.25f, 3.0f, 0.0f);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, no_mat);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, mat_diffuse);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, mat_specular);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, high_shininess);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_EMISSION, no_mat);
    myAUX.auxSolidSphere(1.0);
    myGL.glPopMatrix();

    /*
     * draw sphere in first row, fourth column diffuse reflection; emission; no ambient or specular
     * reflection
     */
    myGL.glPushMatrix();
    myGL.glTranslatef(3.75f, 3.0f, 0.0f);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, no_mat);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, mat_diffuse);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, no_mat);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, no_shininess);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_EMISSION, mat_emission);
    myAUX.auxSolidSphere(1.0);
    myGL.glPopMatrix();

    /*
     * draw sphere in second row, first column ambient and diffuse reflection; no specular
     */
    myGL.glPushMatrix();
    myGL.glTranslatef(-3.75f, 0.0f, 0.0f);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, mat_ambient);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, mat_diffuse);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, no_mat);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, no_shininess);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_EMISSION, no_mat);
    myAUX.auxSolidSphere(1.0);
    myGL.glPopMatrix();

    /*
     * draw sphere in second row, second column ambient, diffuse and specular reflection; low
     * shininess
     */
    myGL.glPushMatrix();
    myGL.glTranslatef(-1.25f, 0.0f, 0.0f);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, mat_ambient);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, mat_diffuse);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, mat_specular);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, low_shininess);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_EMISSION, no_mat);
    myAUX.auxSolidSphere(1.0);
    myGL.glPopMatrix();

    /*
     * draw sphere in second row, third column ambient, diffuse and specular reflection; high
     * shininess
     */
    myGL.glPushMatrix();
    myGL.glTranslatef(1.25f, 0.0f, 0.0f);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, mat_ambient);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, mat_diffuse);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, mat_specular);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, high_shininess);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_EMISSION, no_mat);
    myAUX.auxSolidSphere(1.0);
    myGL.glPopMatrix();

    /*
     * draw sphere in second row, fourth column ambient and diffuse reflection; emission; no
     * specular
     */
    myGL.glPushMatrix();
    myGL.glTranslatef(3.75f, 0.0f, 0.0f);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, mat_ambient);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, mat_diffuse);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, no_mat);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, no_shininess);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_EMISSION, mat_emission);
    myAUX.auxSolidSphere(1.0);
    myGL.glPopMatrix();

    /*
     * draw sphere in third row, first column colored ambient and diffuse reflection; no specular
     */
    myGL.glPushMatrix();
    myGL.glTranslatef(-3.75f, -3.0f, 0.0f);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, mat_ambient_color);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, mat_diffuse);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, no_mat);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, no_shininess);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_EMISSION, no_mat);
    myAUX.auxSolidSphere(1.0);
    myGL.glPopMatrix();

    /*
     * draw sphere in third row, second column colored ambient, diffuse and specular reflection; low
     * shininess
     */
    myGL.glPushMatrix();
    myGL.glTranslatef(-1.25f, -3.0f, 0.0f);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, mat_ambient_color);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, mat_diffuse);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, mat_specular);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, low_shininess);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_EMISSION, no_mat);
    myAUX.auxSolidSphere(1.0);
    myGL.glPopMatrix();

    /*
     * draw sphere in third row, third column colored ambient, diffuse and specular reflection; high
     * shininess
     */
    myGL.glPushMatrix();
    myGL.glTranslatef(1.25f, -3.0f, 0.0f);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, mat_ambient_color);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, mat_diffuse);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, mat_specular);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, high_shininess);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_EMISSION, no_mat);
    myAUX.auxSolidSphere(1.0);
    myGL.glPopMatrix();

    /*
     * draw sphere in third row, fourth column colored ambient and diffuse reflection; emission; no
     * specular
     */
    myGL.glPushMatrix();
    myGL.glTranslatef(3.75f, -3.0f, 0.0f);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, mat_ambient_color);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, mat_diffuse);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, no_mat);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, no_shininess);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_EMISSION, mat_emission);
    myAUX.auxSolidSphere(1.0);
    myGL.glPopMatrix();

    myGL.glFlush();

    Date endTime = new Date();
    System.out.println("Run Time : " + (endTime.getTime() - startTime.getTime()) + " ms");
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
    if (w <= (h * 2)) {
      myGL.glOrtho(-6.0f, 6.0f, -3.0f * ((float) h * 2) / (float) w,
          3.0f * ((float) h * 2) / (float) w, -10.0f, 10.0f);
    } else {
      myGL.glOrtho(-6.0f * (float) w / ((float) h * 2), 6.0f * (float) w / ((float) h * 2), -3.0f,
          3.0f, -10.0f, 10.0f);
    }
    myGL.glMatrixMode(GL.GL_MODELVIEW);
  }

  public void update(Graphics g) {
    // skip the clear screen command....
    paint(g);
  }

  public void paint(Graphics g) {
    myGL.glXSwapBuffers(g, this);
  }

  public void init() {
    myAUX.auxInitPosition(0, 0, 600, 450);
    myAUX.auxInitWindow(this);
    myinit();

    // as call auxReshapeFunc()
    addComponentListener(this);
    myReshape(getSize().width, getSize().height);

    // call display as call auxMainLoop(display);
    display();
  }

}
