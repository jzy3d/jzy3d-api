package org.jzy3d.plot3d.rendering.ddp.algorithms;

import java.io.File;
import java.net.URL;
import org.jzy3d.painters.IPainter;
import org.jzy3d.painters.NativeDesktopPainter;
import org.jzy3d.plot3d.primitives.IGLRenderer;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

public abstract class AbstractDepthPeelingAlgorithm implements IDepthPeelingAlgorithm {
  public final static float MAX_DEPTH = 1.0f;

  protected int g_drawBuffers[] = {GL2.GL_COLOR_ATTACHMENT0, GL2.GL_COLOR_ATTACHMENT1,
      GL2.GL_COLOR_ATTACHMENT2, GL2.GL_COLOR_ATTACHMENT3, GL2.GL_COLOR_ATTACHMENT4,
      GL2.GL_COLOR_ATTACHMENT5, GL2.GL_COLOR_ATTACHMENT6};
  
  protected int g_quadDisplayList;
  protected int g_numPasses = 1;
  protected int g_numGeoPasses = 0;
  
  protected boolean g_useOQ = true;
 
  protected float[] g_white = new float[] {1.0f, 1.0f, 1.0f};
  protected float[] g_black = new float[] {0.0f};
  protected float[] g_backgroundColor = g_white;
  protected float[] g_opacity = new float[] {0.6f};
  
  protected int[] g_queryId = new int[1];


  protected GLU glu = new GLU();


  public AbstractDepthPeelingAlgorithm() {}
  
  
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


  protected abstract void buildShaders(GL2 gl);

  protected abstract void destroyShaders(GL2 gl);

  protected void reloadShaders(GL2 gl) {
    destroyShaders(gl);
    buildShaders(gl);
  }

  protected void buildFullScreenQuad(GL2 gl) {
    GLU glu = GLU.createGLU(gl);

    g_quadDisplayList = gl.glGenLists(1);
    gl.glNewList(g_quadDisplayList, GL2.GL_COMPILE);

    gl.glMatrixMode(GL2.GL_MODELVIEW);
    gl.glPushMatrix();
    gl.glLoadIdentity();
    glu.gluOrtho2D(0.0f, 1.0f, 0.0f, 1.0f);
    gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
    gl.glBegin(GL2.GL_QUADS);
    {
      gl.glVertex2f(0.0f, 0.0f);
      gl.glVertex2f(1.0f, 0.0f);
      gl.glVertex2f(1.0f, 1.0f);
      gl.glVertex2f(0.0f, 1.0f);
    }
    gl.glEnd();
    gl.glPopMatrix();

    gl.glEndList();
  }

  public void buildFinish(GL2 gl) {
    gl.glDisable(GL2.GL_CULL_FACE);
    gl.glDisable(GL2.GL_LIGHTING);
    gl.glDisable(GL2.GL_NORMALIZE);
    gl.glGenQueries(1, g_queryId, 0);
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
    destroyShaders(getGL(painter));
  }

  protected URL shader(String glsl) {
    return getClass().getClassLoader()
        .getResource(File.separator + "org" + File.separator + "jzy3d" + File.separator + "plot3d"
            + File.separator + "rendering" + File.separator + "ddp" + File.separator + "algorithms"
            + File.separator + glsl);
  }
  
  
  protected GL2 getGL(IPainter painter) {
    return ((NativeDesktopPainter)painter).getGL().getGL2();
  }

  protected GLU getGLU(IPainter painter) {
    return ((NativeDesktopPainter)painter).getGLU();
  }

}
