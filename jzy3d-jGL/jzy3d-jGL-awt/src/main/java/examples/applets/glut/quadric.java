package examples.applets.glut;
/*
 * quadric.java This program demonstrates the use of some of the gluQuadric* routines. Quadric
 * objects are created with some quadric properties and the callback routine to handle errors. Note
 * that the cylinder has no top or bottom and the circle has a hole in it.
 */

import jgl.GL;
import jgl.glu.GLUquadricObj;
import jgl.wt.awt.GLApplet;
import jgl.wt.awt.GLU;

public class quadric extends GLApplet {

  private int startList;

  public void errorCallback(int errorCode) {
    String estring;

    estring = myGLU.gluErrorString(errorCode);
    System.err.println("Quadric Error: " + estring);
  }

  private void myinit() {
    GLUquadricObj qobj;
    float mat_ambient[] = {0.5f, 0.5f, 0.5f, 1.0f};
    float mat_specular[] = {1.0f, 1.0f, 1.0f, 1.0f};
    float mat_shininess[] = {50.0f};
    float light_position[] = {1.0f, 1.0f, 1.0f, 0.0f};
    float model_ambient[] = {0.5f, 0.5f, 0.5f, 1.0f};

    myGL.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, mat_ambient);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, mat_specular);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, mat_shininess);
    myGL.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, light_position);
    myGL.glLightModelfv(GL.GL_LIGHT_MODEL_AMBIENT, model_ambient);

    myGL.glEnable(GL.GL_LIGHTING);
    myGL.glEnable(GL.GL_LIGHT0);
    myGL.glEnable(GL.GL_DEPTH_TEST);

    /*
     * Create 4 display lists, each with a different quadric object. Different drawing styles and
     * surface normal specifications are demonstrated.
     */
    startList = myGL.glGenLists(4);
    qobj = myGLU.gluNewQuadric();
    myGLU.gluQuadricCallback(qobj, myGLU.GLU_ERROR, "errorCallback");

    myGLU.gluQuadricDrawStyle(qobj, GLU.GLU_FILL); /* smooth shaded */
    myGLU.gluQuadricNormals(qobj, GLU.GLU_SMOOTH);
    myGL.glNewList(startList, GL.GL_COMPILE);
    myGLU.gluSphere(qobj, 0.75, 15, 10);
    myGL.glEndList();

    myGLU.gluQuadricDrawStyle(qobj, GLU.GLU_FILL); /* flat shaded */
    myGLU.gluQuadricNormals(qobj, GLU.GLU_FLAT);
    myGL.glNewList(startList + 1, GL.GL_COMPILE);
    myGLU.gluCylinder(qobj, 0.5, 0.3, 1.0, 15, 5);
    myGL.glEndList();

    myGLU.gluQuadricDrawStyle(qobj, GLU.GLU_LINE); /* all polygons wireframe */
    myGLU.gluQuadricNormals(qobj, GLU.GLU_NONE);
    myGL.glNewList(startList + 2, GL.GL_COMPILE);
    myGLU.gluDisk(qobj, 0.25, 1.0, 20, 4);
    myGL.glEndList();

    myGLU.gluQuadricDrawStyle(qobj, GLU.GLU_SILHOUETTE); /* boundary only */
    myGLU.gluQuadricNormals(qobj, GLU.GLU_NONE);
    myGL.glNewList(startList + 3, GL.GL_COMPILE);
    myGLU.gluPartialDisk(qobj, 0.0, 1.0, 20, 4, 0.0, 225.0);
    myGL.glEndList();
  }

  public void display() {
    myGL.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    myGL.glPushMatrix();

    myGL.glEnable(GL.GL_LIGHTING);
    myGL.glShadeModel(GL.GL_SMOOTH);
    myGL.glTranslatef(-1.0f, -1.0f, 0.0f);
    myGL.glCallList(startList);

    myGL.glShadeModel(GL.GL_FLAT);
    myGL.glTranslatef(0.0f, 2.0f, 0.0f);
    myGL.glPushMatrix();
    myGL.glRotatef(300.0f, 1.0f, 0.0f, 0.0f);
    myGL.glCallList(startList + 1);
    myGL.glPopMatrix();

    myGL.glDisable(GL.GL_LIGHTING);
    myGL.glColor3f(0.0f, 1.0f, 1.0f);
    myGL.glTranslatef(2.0f, -2.0f, 0.0f);
    myGL.glCallList(startList + 2);

    myGL.glColor3f(1.0f, 1.0f, 0.0f);
    myGL.glTranslatef(0.0f, 2.0f, 0.0f);
    myGL.glCallList(startList + 3);

    myGL.glPopMatrix();
    myGL.glFlush();
  }

  public void myReshape(int w, int h) {
    myGL.glViewport(0, 0, w, h);
    myGL.glMatrixMode(GL.GL_PROJECTION);
    myGL.glLoadIdentity();
    if (w <= h) {
      myGL.glOrtho(-2.5f, 2.5f, -2.5f * (float) h / (float) w, 2.5f * (float) h / (float) w, -10.0f,
          10.0f);
    } else {
      myGL.glOrtho(-2.5f * (float) w / (float) h, 2.5f * (float) w / (float) h, -2.5f, 2.5f, -10.0f,
          10.0f);
    }
    myGL.glMatrixMode(GL.GL_MODELVIEW);
    myGL.glLoadIdentity();
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
