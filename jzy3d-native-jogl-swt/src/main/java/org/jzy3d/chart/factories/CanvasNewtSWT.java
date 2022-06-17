package org.jzy3d.chart.factories;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.jzy3d.awt.AWTHelper;
import org.jzy3d.chart.IAnimator;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Dimension;
import org.jzy3d.painters.IPainter;
import org.jzy3d.painters.NativeDesktopPainter;
import org.jzy3d.plot3d.GPUInfo;
import org.jzy3d.plot3d.rendering.canvas.ICanvasListener;
import org.jzy3d.plot3d.rendering.canvas.INativeCanvas;
import org.jzy3d.plot3d.rendering.canvas.IScreenCanvas;
import org.jzy3d.plot3d.rendering.canvas.PixelScaleWatch;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.Renderer3d;
import org.jzy3d.plot3d.rendering.view.View;
import com.jogamp.nativewindow.ScalableSurface;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.newt.swt.NewtCanvasSWT;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilitiesImmutable;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

/**
 * A Newt canvas wrapped in an AWT Newt is supposed to be faster than any other canvas, either for
 * AWT or Swing. If a non AWT panel where required, follow the guidelines given in
 * {@link IScreenCanvas} documentation.
 */
public class CanvasNewtSWT extends Composite implements IScreenCanvas, INativeCanvas {
  protected View view;
  protected Renderer3d renderer;
  protected IAnimator animator;
  protected GLWindow window;
  protected NewtCanvasSWT canvas;
  protected List<ICanvasListener> canvasListeners = new ArrayList<>();

  protected ScheduledExecutorService exec = new ScheduledThreadPoolExecutor(1);

  public CanvasNewtSWT(IChartFactory factory, Scene scene, Quality quality,
      GLCapabilitiesImmutable glci) {
    this(factory, scene, quality, glci, false, false);
  }

  public CanvasNewtSWT(IChartFactory factory, Scene scene, Quality quality,
      GLCapabilitiesImmutable glci, boolean traceGL, boolean debugGL) {
    super(((SWTChartFactory) factory).getComposite(), SWT.NONE);
    this.setLayout(new FillLayout());
    window = GLWindow.create(glci);
    canvas = new NewtCanvasSWT(this, SWT.NONE, window);
    view = scene.newView(this, quality);
    view.getPainter().setCanvas(this);

    renderer = newRenderer(factory, traceGL, debugGL);
    window.addGLEventListener(renderer);

    if (quality.isPreserveViewportSize()) {
      setPixelScale(newPixelScaleIdentity());
    }

    window.setAutoSwapBufferMode(quality.isAutoSwapBuffer());

    animator = ((SWTChartFactory) factory).newAnimator(window);

    if (quality.isAnimated()) {
      animator.start();
    }
    
    if(ALLOW_WATCH_PIXEL_SCALE)
      watchPixelScale();

    addDisposeListener(e -> new Thread(() -> {
      if (animator != null) {
        animator.stop();
      }
      if (renderer != null) {
        renderer.dispose(window);
      }
      window = null;
      renderer = null;
      view = null;
      animator = null;
    }).start());
  }
  
  protected void watchPixelScale() {
    exec.schedule(new PixelScaleWatch() {
      @Override
      public double getPixelScaleY() {
        return CanvasNewtSWT.this.getPixelScaleY();
      }
      @Override
      public double getPixelScaleX() {
        return CanvasNewtSWT.this.getPixelScaleX();
      }
      @Override
      protected void firePixelScaleChanged(double pixelScaleX, double pixelScaleY) {
        CanvasNewtSWT.this.firePixelScaleChanged(pixelScaleX, pixelScaleY);
      }
    }, 0, TimeUnit.SECONDS);
  }


  private Renderer3d newRenderer(IChartFactory factory, boolean traceGL, boolean debugGL) {
    return ((NativePainterFactory) factory.getPainterFactory()).newRenderer3D(view);
  }

  private float[] newPixelScaleIdentity() {
    return new float[] {ScalableSurface.IDENTITY_PIXELSCALE, ScalableSurface.IDENTITY_PIXELSCALE};
  }

  @Override
  public double getLastRenderingTimeMs() {
    return renderer.getLastRenderingTimeMs();
  }


  @Override
  public IAnimator getAnimation() {
    return animator;
  }


  @Override
  public void setPixelScale(float[] scale) {
    if (scale != null) {
      window.setSurfaceScale(scale);
    } else {
      window.setSurfaceScale(new float[] {1f, 1f});
    }
  }

  /**
   * Pixel scale is used to model the pixel ratio thay may be introduced by HiDPI or Retina
   * displays.
   */
  @Override
  public Coord2d getPixelScale() {
    return new Coord2d(getPixelScaleX(), getPixelScaleY());
  }

  @Override
  public Coord2d getPixelScaleJVM() {
    return getPixelScale();
  }
  
  public double getPixelScaleX() {
    float[] scale = new float[2];
    window.getCurrentSurfaceScale(scale);
    return scale[0];
  }

  public double getPixelScaleY() {
    float[] scale = new float[2];
    window.getCurrentSurfaceScale(scale);
    return scale[1];
  }
  
  public GLWindow getWindow() {
    return window;
  }

  public NewtCanvasSWT getCanvas() {
    return canvas;
  }

  @Override
  public GLAutoDrawable getDrawable() {
    return window;
  }

  @Override
  public void display() {
    window.display();
  }

  @Override
  public void forceRepaint() {
    display();
  }

  @Override
  public TextureData screenshot() {
    renderer.nextDisplayUpdateScreenshot();
    display();
    return renderer.getLastScreenshot();
  }

  @Override
  public void screenshot(File file) throws IOException {
    if (!file.getParentFile().exists())
      file.getParentFile().mkdirs();
    TextureData screen = screenshot();
    TextureIO.write(screen, file);
  }
  
  @Override
  public String getDebugInfo() {
    IPainter painter = getView().getPainter();
    
    GLCapabilitiesImmutable caps = window.getChosenGLCapabilities();
    
    GL gl = (GL) painter.acquireGL();
    GPUInfo info = GPUInfo.load(gl);
    painter.releaseGL();
    
    return "Capabilities  : " + caps + "\n" + info.toString();
  }

  /**
   * Provide the actual renderer width for the open gl camera settings, which is obtained after a
   * resize event.
   */
  @Override
  public int getRendererWidth() {
    return (renderer != null ? renderer.getWidth() : 0);
  }

  /**
   * Provide the actual renderer height for the open gl camera settings, which is obtained after a
   * resize event.
   */
  @Override
  public int getRendererHeight() {
    return (renderer != null ? renderer.getHeight() : 0);
  }
  
  @Override
  public Dimension getDimension() {
    if(renderer!=null) {
      return new Dimension(renderer.getWidth(), renderer.getHeight());
    }
    else {
      return new Dimension(0, 0);
    }
  }


  @Override
  public Renderer3d getRenderer() {
    return renderer;
  }

  /** Provide a reference to the View that renders into this canvas. */
  @Override
  public View getView() {
    return view;
  }

  /* */

  public synchronized void addKeyListener(KeyListener l) {
    getWindow().addKeyListener(l);
  }

  public void addMouseListener(MouseListener l) {
    getWindow().addMouseListener(l);
  }

  public void removeMouseListener(com.jogamp.newt.event.MouseListener l) {
    getWindow().removeMouseListener(l);
  }

  public void removeKeyListener(com.jogamp.newt.event.KeyListener l) {
    getWindow().removeKeyListener(l);
  }

  @Override
  public void addMouseController(Object o) {
    addMouseListener((MouseListener) o);
  }

  @Override
  public void addKeyController(Object o) {
    addKeyListener((KeyListener) o);
  }

  @Override
  public void removeMouseController(Object o) {
    removeMouseListener((MouseListener) o);
  }

  @Override
  public void removeKeyController(Object o) {
    removeKeyListener((KeyListener) o);
  }


  @Override
  public void addCanvasListener(ICanvasListener listener) {
    canvasListeners.add(listener);
  }

  @Override
  public void removeCanvasListener(ICanvasListener listener) {
    canvasListeners.remove(listener);
  }

  @Override
  public List<ICanvasListener> getCanvasListeners() {
    return canvasListeners;
  }

  protected void firePixelScaleChanged(double pixelScaleX, double pixelScaleY) {
    for (ICanvasListener listener : canvasListeners) {
      listener.pixelScaleChanged(pixelScaleX, pixelScaleY);
    }
  }
  
  @Override
  public boolean isNative() {
    return true;
  }

}
