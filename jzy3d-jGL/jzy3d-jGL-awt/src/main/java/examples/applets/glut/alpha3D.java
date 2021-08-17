package examples.applets.glut;
/*
 * alpha3D.java This program demonstrates how to intermix opaque and alpha blended polygons in the
 * same scene, by using glDepthMask. Press the 'a' key to animate moving the transparent object
 * through the opaque object. Press the 'r' key to reset the scene.
 */

import jgl.GL;
import jgl.wt.awt.GLApplet;

public class alpha3D extends GLApplet {

  private static final float MAXZ = 8.0f;
  private static final float MINZ = -8.0f;
  private static final float ZINC = 0.4f;

  private float solidZ = MAXZ;
  private float transparentZ = MINZ;
  private int sphereList, cubeList;

  private void myinit() {
    float mat_specular[] = {1.0f, 1.0f, 1.0f, 0.15f};
    float mat_shininess[] = {100.0f};
    float position[] = {0.5f, 0.5f, 1.0f, 0.0f};

    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, mat_specular);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, mat_shininess);
    myGL.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, position);

    myGL.glEnable(GL.GL_LIGHTING);
    myGL.glEnable(GL.GL_LIGHT0);
    myGL.glEnable(GL.GL_DEPTH_TEST);

    sphereList = myGL.glGenLists(1);
    myGL.glNewList(sphereList, GL.GL_COMPILE);
    myUT.glutSolidSphere(0.4f, 16, 16);
    myGL.glEndList();

    cubeList = myGL.glGenLists(1);
    myGL.glNewList(cubeList, GL.GL_COMPILE);
    myUT.glutSolidCube(0.6f);
    myGL.glEndList();
  }

  public void display() {
    float mat_solid[] = {0.75f, 0.75f, 0.0f, 1.0f};
    float mat_zero[] = {0.0f, 0.0f, 0.0f, 1.0f};
    float mat_transparent[] = {0.0f, 0.8f, 0.8f, 0.6f};
    float mat_emission[] = {0.0f, 0.3f, 0.3f, 0.6f};

    myGL.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

    myGL.glPushMatrix();
    myGL.glTranslatef(-0.15f, -0.15f, solidZ);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_EMISSION, mat_zero);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, mat_solid);
    myGL.glCallList(sphereList);
    myGL.glPopMatrix();

    myGL.glPushMatrix();
    myGL.glTranslatef(0.15f, 0.15f, transparentZ);
    myGL.glRotatef(15.0f, 1.0f, 1.0f, 0.0f);
    myGL.glRotatef(30.0f, 0.0f, 1.0f, 0.0f);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_EMISSION, mat_emission);
    myGL.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, mat_transparent);
    myGL.glEnable(GL.GL_BLEND);
    myGL.glDepthMask(GL.GL_FALSE);
    myGL.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
    myGL.glCallList(cubeList);
    myGL.glDepthMask(GL.GL_TRUE);
    myGL.glDisable(GL.GL_BLEND);
    myGL.glPopMatrix();

    myGL.glFlush();
  }

  public void myReshape(int w, int h) {
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

  public void animate() {
    if (solidZ <= MINZ || transparentZ >= MAXZ)
      myUT.glutIdleFunc(null);
    else {
      solidZ -= ZINC;
      transparentZ += ZINC;
      myUT.glutPostRedisplay();
    }
  }

  public void keyboard(char key, int x, int y) {
    switch (key) {
      case 'a':
      case 'A':
        solidZ = MAXZ;
        transparentZ = MINZ;
        myUT.glutIdleFunc("animate");
        break;
      case 'r':
      case 'R':
        solidZ = MAXZ;
        transparentZ = MINZ;
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
    myUT.glutKeyboardFunc("keyboard");
    myUT.glutDisplayFunc("display");
    myUT.glutMainLoop();
  }

}
