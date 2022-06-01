package org.jzy3d.plot3d.rendering.canvas;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.chart.factories.NativePainterFactory;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Dimension;
import org.jzy3d.painters.IPainter;
import org.jzy3d.painters.NativeDesktopPainter;
import org.jzy3d.plot3d.GPUInfo;
import org.jzy3d.plot3d.pipelines.NotImplementedException;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.Renderer3d;
import org.jzy3d.plot3d.rendering.view.View;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLCapabilitiesImmutable;
import com.jogamp.opengl.GLDrawableFactory;
import com.jogamp.opengl.GLFBODrawable;
import com.jogamp.opengl.GLOffscreenAutoDrawable;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.GLPixelBuffer;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

/**
 * An {@link ICanvas} implementation able to render the chart in an offscreen canvas, meaning no
 * frame or GUI is needed to get a chart.
 * 
 * Subsequently, one will wish to generate chart images by calling:
 * 
 * <pre>
 * <code>
 * chart.screenshot();
 * </code>
 * </pre>
 * 
 * Note that the {@link GLCapabilities} are modified while an instance of {@link OffscreenCanvas} is
 * modified.
 * 
 * @author Nils Hoffman
 * @author Martin Pernollet
 */
public class OffscreenCanvas implements ICanvas, INativeCanvas {
  protected View view;
  protected Renderer3d renderer;
  protected GLOffscreenAutoDrawable offscreenDrawable;
  protected GLCapabilities capabilities;
  protected List<ICanvasListener> canvasListeners = new ArrayList<>();


  public OffscreenCanvas(IChartFactory factory, Scene scene, Quality quality,
      GLCapabilities capabilities, int width, int height) {
    this.view = scene.newView(this, quality);
    this.view.getPainter().setCanvas(this);
    this.renderer = newRenderer(factory);
    this.capabilities = capabilities;

    initBuffer(capabilities, width, height);
  }

  private Renderer3d newRenderer(IChartFactory factory) {
    return ((NativePainterFactory) factory.getPainterFactory()).newRenderer3D(view);
  }

  /**
   * Initialize a {@link GLOffscreenAutoDrawable} to the desired dimensions
   * 
   * Might be a {@link GLPixelBuffer} or {@link GLFBODrawable} depending on the provided
   * {@link GLCapabilities} ({@link GLCapabilities#setPBuffer(true)} to enable PBuffer).
   * 
   * Can be called several time to reset buffer dimensions.
   * 
   * @param capabilities with a setOnscreen flag set to true, otherwise forced to true.
   * @param width image width
   * @param height image width
   */
  public void initBuffer(GLCapabilities capabilities, int width, int height) {
    if (capabilities.isOnscreen()) {
      System.err.println(this.getClass().getSimpleName()
          + " : The provided capabilities should be set to setOnscreen(false). Forcing this configuration");
      capabilities.setOnscreen(false);
      // capabilities.setDoubleBuffered(false);
      // capabilities.setPBuffer(true);

    }

    GLProfile profile = capabilities.getGLProfile();
    GLDrawableFactory factory = GLDrawableFactory.getFactory(profile);

    // if (!factory.canCreateGLPbuffer(null, profile))
    // throw new RuntimeException("No pbuffer support");

    if (offscreenDrawable != null) {
      offscreenDrawable.removeGLEventListener(renderer);
      offscreenDrawable.destroy();
      // glpBuffer.setSurfaceSize(width, height);
    }
    offscreenDrawable = factory.createOffscreenAutoDrawable(factory.getDefaultDevice(),
        capabilities, null, width, height);
    offscreenDrawable.addGLEventListener(renderer);

  }

  public GLCapabilities getCapabilities() {
    return capabilities;
  }

  @Override
  public double getLastRenderingTimeMs() {
    return renderer.getLastRenderingTimeMs();
  }


  /* NOT IMPLEMENTED */
  @Override
  public void setPixelScale(float[] scale) {
    throw new NotImplementedException();
  }

  @Override
  public Coord2d getPixelScale() {
    LogManager.getLogger(OffscreenCanvas.class)
        .info("getPixelScale() not implemented. Will return {1,1}");
    return new Coord2d(1, 1);
  }
  
  @Override
  public Coord2d getPixelScaleJVM() {
    return getPixelScale();
  }

  @Override
  public GLOffscreenAutoDrawable getDrawable() {
    return offscreenDrawable;
  }

  @Override
  public void dispose() {
    offscreenDrawable.destroy();
    renderer = null;
    view = null;
  }

  @Override
  public void forceRepaint() {
    offscreenDrawable.display();
  }

  @Override
  public TextureData screenshot() {
    renderer.nextDisplayUpdateScreenshot();
    offscreenDrawable.display();
    return renderer.getLastScreenshot();
  }

  @Override
  public void screenshot(File file) throws IOException {
    TextureData screen = screenshot();
    file.getParentFile().mkdirs();
    TextureIO.write(screen, file);
  }

  /** Provide a reference to the View that renders into this canvas. */
  @Override
  public View getView() {
    return view;
  }

  @Override
  public int getRendererWidth() {
    return (renderer != null ? renderer.getWidth() : 0);
  }

  @Override
  public int getRendererHeight() {
    return (renderer != null ? renderer.getHeight() : 0);
  }

  @Override
  public Renderer3d getRenderer() {
    return renderer;
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
  public String getDebugInfo() {
    IPainter painter = getView().getPainter();
    
    GLCapabilitiesImmutable caps = offscreenDrawable.getChosenGLCapabilities();
    
    GL gl = (GL) painter.acquireGL();
    GPUInfo info = GPUInfo.load(gl);
    painter.releaseGL();
    
    return "Capabilities  : " + caps + "\n" + info.toString();
  }


  @Override
  public void addMouseController(Object o) {}

  @Override
  public void addKeyController(Object o) {}

  @Override
  public void removeMouseController(Object o) {}

  @Override
  public void removeKeyController(Object o) {}

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
