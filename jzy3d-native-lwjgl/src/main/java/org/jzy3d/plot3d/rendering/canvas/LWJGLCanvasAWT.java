package org.jzy3d.plot3d.rendering.canvas;

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
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import org.jzy3d.chart.IAnimator;
import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Dimension;
import org.jzy3d.plot3d.pipelines.NotImplementedException;
import org.jzy3d.plot3d.rendering.canvas.ICanvasListener;
import org.jzy3d.plot3d.rendering.canvas.IScreenCanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.View;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.awt.AWTGLCanvas;
import org.lwjgl.opengl.awt.GLData;

public class LWJGLCanvasAWT extends AWTGLCanvas implements IScreenCanvas{
  int width = 800;
  int height = 600;
  //GLData data = new GLData();
  
  /**
   * 
   */
  private static final long serialVersionUID = -1185158326104909667L;

  
  
  boolean sample = false;
  
  protected double pixelScaleX = 1;
  protected double pixelScaleY = 1;
  protected View view;
  //protected Renderer3d renderer;
  protected IAnimator animator;
  protected List<ICanvasListener> canvasListeners = new ArrayList<>();

  protected ScheduledExecutorService exec = new ScheduledThreadPoolExecutor(1);

  /**
   * Initialize a {@link CanvasAWT} attached to a {@link Scene}, with a given rendering
   * {@link Quality}.
   */
  public LWJGLCanvasAWT(IChartFactory factory, Scene scene, Quality quality) {
    super(new GLData());
    
    view = scene.newView(this, quality);
    view.getPainter().setCanvas(this);

    //renderer = newRenderer(factory);
    //addGLEventListener(renderer);

    //setAutoSwapBufferMode(quality.isAutoSwapBuffer());

    /*animator = factory.getPainterFactory().newAnimator(this);
    if (quality.isAnimated()) {
      animator.start();
    } else {
      animator.stop();
    }*/

    //if(ALLOW_WATCH_PIXEL_SCALE)
    //  watchPixelScale();
    
    //if (quality.isPreserveViewportSize())
    //  setPixelScale(newPixelScaleIdentity());
    
  }
  
  @Override
  public void initGL() {
    System.out.println("LWJGLCanvasAWT : OpenGL version: " + effective.majorVersion + "." + effective.minorVersion
        + " (Profile: " + effective.profile + ")");
    
    GLCapabilities glc = createCapabilities();
    
    System.out.println("LWJGLCanvasAWT : Caps : " + glc);
    
    if(sample) {
      glClearColor(0.3f, 0.4f, 0.5f, 1);
    }
    else {
      view.init();
           
    }
    
  }

  @Override
  public void paintGL() {
    // int w = getFramebufferWidth();
    // int h = getFramebufferHeight();

    if(sample) {
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
      

    }
    else {
      view.clear();
      view.render(); 
      

    }
    swapBuffers();
    
  }
  
  

  @Override
  public boolean isNative() {
    return true;
  }

  @Override
  public View getView() {
    return view;
  }

  @Override
  public int getRendererWidth() {
    return getWidth();
  }

  @Override
  public int getRendererHeight() {
    return getHeight();
  }

  @Override
  public Dimension getDimension() {
    return new Dimension(getWidth(), getHeight());
  }

  @Override
  public void screenshot(File file) throws IOException {
    throw new NotImplementedException();
    
  }

  @Override
  public Object screenshot() {
    throw new NotImplementedException();
  }

  @Override
  public void forceRepaint() {
    repaint();
  }

  @Override
  public void dispose() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void addMouseController(Object o) {
    addMouseListener((java.awt.event.MouseListener) o);
    if (o instanceof MouseWheelListener)
      addMouseWheelListener((MouseWheelListener) o);
    if (o instanceof MouseMotionListener)
      addMouseMotionListener((MouseMotionListener) o);
  }

  @Override
  public void addKeyController(Object o) {
    addKeyListener((java.awt.event.KeyListener) o);
  }

  @Override
  public void removeMouseController(Object o) {
    removeMouseListener((java.awt.event.MouseListener) o);
    if (o instanceof MouseWheelListener)
      removeMouseWheelListener((MouseWheelListener) o);
    if (o instanceof MouseMotionListener)
      removeMouseMotionListener((MouseMotionListener) o);
  }

  @Override
  public void removeKeyController(Object o) {
    removeKeyListener((java.awt.event.KeyListener) o);
  }

  @Override
  public String getDebugInfo() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setPixelScale(float[] scale) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public Coord2d getPixelScale() {
    return new Coord2d(pixelScaleX, pixelScaleY);
  }

  @Override
  public Coord2d getPixelScaleJVM() {
    return getPixelScale();
  }

  @Override
  public double getLastRenderingTimeMs() {
    return -1;
  }

  @Override
  public void addCanvasListener(ICanvasListener listener) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void removeCanvasListener(ICanvasListener listener) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public List<ICanvasListener> getCanvasListeners() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void display() {
    repaint();
  }

  @Override
  public IAnimator getAnimation() {
    return null;
  }

}
