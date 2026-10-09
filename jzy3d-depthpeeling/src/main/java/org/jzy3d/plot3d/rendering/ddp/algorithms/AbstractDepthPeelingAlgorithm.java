package org.jzy3d.plot3d.rendering.ddp.algorithms;

import java.io.File;
import java.net.URL;
import org.jzy3d.painters.GLConstants;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.IGLRenderer;

public abstract class AbstractDepthPeelingAlgorithm implements IDepthPeelingAlgorithm {
  public final static float MAX_DEPTH = 1.0f;

  protected int g_drawBuffers[] = {GLConstants.GL_COLOR_ATTACHMENT0, GLConstants.GL_COLOR_ATTACHMENT1,
      GLConstants.GL_COLOR_ATTACHMENT2, GLConstants.GL_COLOR_ATTACHMENT3, GLConstants.GL_COLOR_ATTACHMENT4,
      GLConstants.GL_COLOR_ATTACHMENT5, GLConstants.GL_COLOR_ATTACHMENT6};
  
  protected int g_quadDisplayList;
  protected int g_numPasses = 1;
  protected int g_numGeoPasses = 0;
  
  protected boolean g_useOQ = true;
 
  protected float[] g_white = new float[] {1.0f, 1.0f, 1.0f};
  protected float[] g_black = new float[] {0.0f};
  protected float[] g_backgroundColor = g_white;
  protected float[] g_opacity = new float[] {0.6f};
  
  protected int[] g_queryId = new int[1];



  /**
   * The framebuffer and draw buffer the algorithm renders the final image to, which were bound
   * before the algorithm executes. This is the default framebuffer for an onscreen canvas, or the
   * canvas framebuffer for an offscreen canvas.
   */
  protected int[] targetFramebuffer = new int[] {0};
  protected int[] targetDrawBuffer = new int[] {GLConstants.GL_BACK};

  public AbstractDepthPeelingAlgorithm() {}

  /** Remember the framebuffer and draw buffer the algorithm must render the final image to. */
  protected void saveTargetFramebuffer(IPainter painter) {
    painter.glGetIntegerv(GLConstants.GL_FRAMEBUFFER_BINDING, targetFramebuffer, 0);
    painter.glGetIntegerv(GLConstants.GL_DRAW_BUFFER, targetDrawBuffer, 0);
  }

  /** Bind the framebuffer the algorithm must render the final image to. */
  protected void bindTargetFramebuffer(IPainter painter) {
    painter.glBindFramebuffer(GLConstants.GL_FRAMEBUFFER, targetFramebuffer[0]);
  }

  /**
   * Verify the framebuffer currently bound can be rendered to. A GL implementation not supporting
   * the format of one of its attachments would otherwise silently render nothing.
   * 
   * @throws IllegalStateException if the framebuffer is incomplete.
   */
  protected void checkFramebuffer(IPainter painter, String name, int width, int height) {
    // Buffers are empty before the canvas has a size (e.g. JOGL initializes with a 0x0 size) and
    // rebuilt when reshaped : they can not be complete yet
    if (width <= 0 || height <= 0) {
      return;
    }
    int status = painter.glCheckFramebufferStatus(GLConstants.GL_FRAMEBUFFER);
    if (status != GLConstants.GL_FRAMEBUFFER_COMPLETE) {
      throw new IllegalStateException(getClass().getSimpleName() + " : framebuffer " + name
          + " is incomplete, status 0x" + Integer.toHexString(status));
    }
  }

  /** Bind the framebuffer and draw buffer the algorithm must render the final image to. */
  protected void bindTargetFramebufferAndDrawBuffer(IPainter painter) {
    bindTargetFramebuffer(painter);
    painter.glDrawBuffer(targetDrawBuffer[0]);
  }
  
  
  public void setBackground(float[] color) {
    if(color.length!=3) {
      throw new IllegalArgumentException("Expect an array with three components");
    }
    g_backgroundColor = color;
  }
  
  public float[] getBackground(){
    return g_backgroundColor;
  }
  
  public void setOpacity(float opacity){
    g_opacity[0] = opacity;
  }
  
  public float getOpacity(){
    return g_opacity[0];
  }


  protected abstract void buildShaders(IPainter painter);

  protected abstract void destroyShaders(IPainter painter);

  protected void reloadShaders(IPainter painter) {
    destroyShaders(painter);
    buildShaders(painter);
  }

  protected void buildFullScreenQuad(IPainter painter) {
    g_quadDisplayList = painter.glGenLists(1);
    painter.glNewList(g_quadDisplayList, GLConstants.GL_COMPILE);

    painter.glMatrixMode(GLConstants.GL_MODELVIEW);
    painter.glPushMatrix();
    painter.glLoadIdentity();
    painter.gluOrtho2D(0.0f, 1.0f, 0.0f, 1.0f);
    painter.glPolygonMode(GLConstants.GL_FRONT_AND_BACK, GLConstants.GL_FILL);
    painter.glBegin(GLConstants.GL_QUADS);
    {
      painter.glVertex3f(0.0f, 0.0f, 0.0f);
      painter.glVertex3f(1.0f, 0.0f, 0.0f);
      painter.glVertex3f(1.0f, 1.0f, 0.0f);
      painter.glVertex3f(0.0f, 1.0f, 0.0f);
    }
    painter.glEnd();
    painter.glPopMatrix();

    painter.glEndList();
  }

  public void buildFinish(IPainter painter) {
    painter.glDisable(GLConstants.GL_CULL_FACE);
    painter.glDisable(GLConstants.GL_LIGHTING);
    painter.glDisable(GLConstants.GL_NORMALIZE);
    painter.glGenQueries(1, g_queryId, 0);
  }

  /* ACTUAL RENDERING */

  IGLRenderer tasksToRender = new IGLRenderer() {
    @Override
    public void draw(IPainter painter) {
      throw new RuntimeException("nothing to render?!");
    }
  };

  @Override
  public IGLRenderer getTasksToRender() {
    return tasksToRender;
  }

  @Override
  public void setTasksToRender(IGLRenderer tasksToRender) {
    this.tasksToRender = tasksToRender;
  }

  protected void tasksToRender(IPainter painter) {
    tasksToRender.draw(painter);
    incrementGeoPasses();
  }

  protected void resetNumPass() {
    g_numGeoPasses = 0;
  }

  protected void incrementGeoPasses() {
    g_numGeoPasses++;
  }

  @Override
  public void dispose(IPainter painter) {
    destroyShaders(painter);
  }

  protected URL shader(String glsl) {
    return getClass().getClassLoader()
        .getResource(File.separator + "org" + File.separator + "jzy3d" + File.separator + "plot3d"
            + File.separator + "rendering" + File.separator + "ddp" + File.separator + "algorithms"
            + File.separator + glsl);
  }
  
  

}
