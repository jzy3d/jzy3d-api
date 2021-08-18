package examples.apps;
/*
 * texgen.java This program draws a texture mapped teapot with automatically generated texture
 * coordinates. The texture is rendered as stripes on the teapot. Initially, the object is drawn
 * with texture coordinates based upon the object coordinates of the vertex and distance from the
 * plane x = 0. Pressing the 'e' key changes the coordinate generation to eye coordinates of the
 * vertex. Pressing the 'o' key switches it back to the object coordinates. Pressing the 's' key
 * changes the plane to a slanted one (x + y + z = 0). Pressing the 'x' key switches it back to x =
 * 0.
 */

import java.awt.Frame;
import java.io.IOException;
import jgl.GL;
import jgl.wt.awt.GLCanvas;

public class texgen extends GLCanvas {

  private static final int stripeImageWidth = 32;
  private byte stripeImage[][] = new byte[stripeImageWidth][4];

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
      stripeImage[j][2] = (byte) 0;
      stripeImage[j][3] = (byte) 255;
    }
  }

  /* planes for texture coordinate generation */
  private float xequalzero[] = {1.0f, 0.0f, 0.0f, 0.0f};
  private float slanted[] = {1.0f, 1.0f, 1.0f, 0.0f};
  private float currentCoeff[];
  private int currentPlane;
  private int currentGenMode;

  private void myinit() {
    myGL.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    myGL.glEnable(GL.GL_DEPTH_TEST);
    myGL.glShadeModel(GL.GL_SMOOTH);

    makeStripeImage();
    myGL.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);

    myGL.glTexParameterf(GL.GL_TEXTURE_1D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
    myGL.glTexParameterf(GL.GL_TEXTURE_1D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
    myGL.glTexParameterf(GL.GL_TEXTURE_1D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
    myGL.glTexImage1D(GL.GL_TEXTURE_1D, 0, 4, stripeImageWidth, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE,
        stripeImage);

    myGL.glTexEnvf(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_MODULATE);
    currentCoeff = xequalzero;
    currentGenMode = GL.GL_OBJECT_LINEAR;
    currentPlane = GL.GL_OBJECT_PLANE;
    myGL.glTexGeni(GL.GL_S, GL.GL_TEXTURE_GEN_MODE, currentGenMode);
    myGL.glTexGenfv(GL.GL_S, currentPlane, currentCoeff);

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

  public void display() {
    myGL.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

    myGL.glPushMatrix();
    myGL.glRotatef(45.0f, 0.0f, 0.0f, 1.0f);
    myUT.glutSolidTeapot(2.0);
    myGL.glPopMatrix();
    myGL.glFlush();
  }

  public void myReshape(int w, int h) {
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

  /* ARGSUSED1 */
  public void keyboard(char key, int x, int y) {
    switch (key) {
      case 'e':
      case 'E':
        currentGenMode = GL.GL_EYE_LINEAR;
        currentPlane = GL.GL_EYE_PLANE;
        myGL.glTexGeni(GL.GL_S, GL.GL_TEXTURE_GEN_MODE, currentGenMode);
        myGL.glTexGenfv(GL.GL_S, currentPlane, currentCoeff);
        myUT.glutPostRedisplay();
        break;
      case 'o':
      case 'O':
        currentGenMode = GL.GL_OBJECT_LINEAR;
        currentPlane = GL.GL_OBJECT_PLANE;
        myGL.glTexGeni(GL.GL_S, GL.GL_TEXTURE_GEN_MODE, currentGenMode);
        myGL.glTexGenfv(GL.GL_S, currentPlane, currentCoeff);
        myUT.glutPostRedisplay();
        break;
      case 's':
      case 'S':
        currentCoeff = slanted;
        myGL.glTexGenfv(GL.GL_S, currentPlane, currentCoeff);
        myUT.glutPostRedisplay();
        break;
      case 'x':
      case 'X':
        currentCoeff = xequalzero;
        myGL.glTexGenfv(GL.GL_S, currentPlane, currentCoeff);
        myUT.glutPostRedisplay();
        break;
      case 27:
        System.exit(0);
      default:
        break;
    }
  }

  public void init() {
    myUT.glutInitWindowSize(200, 200);
    myUT.glutInitWindowPosition(0, 0);
    myUT.glutCreateWindow(this);
    myinit();
    myUT.glutDisplayFunc("display");
    myUT.glutReshapeFunc("myReshape");
    myUT.glutKeyboardFunc("keyboard");
    myUT.glutMainLoop();
  }

  static public void main(String args[]) throws IOException {
    Frame mainFrame = new Frame();
    mainFrame.setSize(208, 227);
    texgen mainCanvas = new texgen();
    mainCanvas.init();
    mainFrame.add(mainCanvas);
    mainFrame.setVisible(true);
  }

}
