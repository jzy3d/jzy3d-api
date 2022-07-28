package org.jzy3d.lwjgl;

import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2f;
import static org.lwjgl.opengl.GL11.glViewport;
import org.lwjgl.opengl.awt.AWTGLCanvas;

public class CanvasAWT_LWJGL extends AWTGLCanvas {
  int width = 800;
  int height = 600;
  
  /**
   * 
   */
  private static final long serialVersionUID = -1185158326104909667L;

  @Override
  public void initGL() {
    System.out.println("OpenGL version: " + effective.majorVersion + "." + effective.minorVersion
        + " (Profile: " + effective.profile + ")");
    createCapabilities();
    glClearColor(0.3f, 0.4f, 0.5f, 1);
  }

  @Override
  public void paintGL() {
    // int w = getFramebufferWidth();
    // int h = getFramebufferHeight();

    int w = width;
    int h = height;

    float aspect = (float) w / h;
    double now = System.currentTimeMillis() * 0.001;
    float width = (float) Math.abs(Math.sin(now * 0.3));
    glClear(GL_COLOR_BUFFER_BIT);
    glViewport(0, 0, w, h);
    glBegin(GL_QUADS);
    glColor3f(0.4f, 0.6f, 0.8f);
    glVertex2f(-0.75f * width / aspect, 0.0f);
    glVertex2f(0, -0.75f);
    glVertex2f(+0.75f * width / aspect, 0);
    glVertex2f(0, +0.75f);
    glEnd();
    swapBuffers();
  }

}
