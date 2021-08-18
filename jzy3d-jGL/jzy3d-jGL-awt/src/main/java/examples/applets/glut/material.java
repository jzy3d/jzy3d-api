package examples.applets.glut;
/*
 * material.java This program demonstrates the use of the GL lighting model. Several objects are
 * drawn using different material characteristics. A single light source illuminates the objects.
 */

import java.util.Date;

import jgl.GL;
import jgl.wt.awt.GLApplet;

public class material extends GLApplet {

  /*
   * Initialize z-buffer, projection matrix, light source, and lighting model. Do not specify a
   * material property here.
   */
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

  /*
   * Draw twelve spheres in 3 rows with 4 columns. The spheres in the first row have materials with
   * no ambient reflection. The second row has materials with significant ambient reflection. The
   * third row has materials with colored ambient reflection.
   *
   * The first column has materials with blue, diffuse reflection only. The second column has blue
   * diffuse reflection, as well as specular reflection with a low shininess exponent. The third
   * column has blue diffuse reflection, as well as specular reflection with a high shininess
   * exponent (a more concentrated highlight). The fourth column has materials which also include an
   * emissive component.
   *
   * glTranslatef() is used to move spheres to their appropriate locations.
   */
  public void display() {
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
    myUT.glutSolidSphere(1.0, 16, 16);
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
    myUT.glutSolidSphere(1.0, 16, 16);
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
    myUT.glutSolidSphere(1.0, 16, 16);
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
    myUT.glutSolidSphere(1.0, 16, 16);
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
    myUT.glutSolidSphere(1.0, 16, 16);
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
    myUT.glutSolidSphere(1.0, 16, 16);
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
    myUT.glutSolidSphere(1.0, 16, 16);
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
    myUT.glutSolidSphere(1.0, 16, 16);
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
    myUT.glutSolidSphere(1.0, 16, 16);
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
    myUT.glutSolidSphere(1.0, 16, 16);
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
    myUT.glutSolidSphere(1.0, 16, 16);
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
    myUT.glutSolidSphere(1.0, 16, 16);
    myGL.glPopMatrix();

    myGL.glFlush();

    Date endTime = new Date();
    // System.out.println ("Run Time : " + (endTime.getTime () - startTime.getTime ()) + " ms");
    // System.out.println ((endTime.getTime () - startTime.getTime ()));
  }

  public void rotateDisplay() {
    myUT.glutPostRedisplay();
  }

  public void myReshape(int w, int h) {
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

  /*
   * Main Loop Open window with initial window size, title bar, RGBA display mode, and handle input
   * events.
   */
  public void init() {
    myUT.glutInitWindowSize(600, 450);
    myUT.glutInitWindowPosition(0, 0);
    myUT.glutCreateWindow(this);
    myinit();
    myUT.glutReshapeFunc("myReshape");
    myUT.glutDisplayFunc("display");
    myUT.glutMainLoop();
    myUT.glutIdleFunc("rotateDisplay");
  }

}
