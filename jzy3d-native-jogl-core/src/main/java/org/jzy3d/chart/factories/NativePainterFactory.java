package org.jzy3d.chart.factories;

import org.jzy3d.chart.NativeAnimator;
import org.jzy3d.maths.Dimension;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.painters.IPainter;
import org.jzy3d.painters.NativeDesktopPainter;
import org.jzy3d.plot3d.pipelines.NotImplementedException;
import org.jzy3d.plot3d.primitives.symbols.SymbolHandler;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.INativeCanvas;
import org.jzy3d.plot3d.rendering.canvas.OffscreenCanvas;
import org.jzy3d.plot3d.rendering.image.IImageWrapper;
import org.jzy3d.plot3d.rendering.view.Renderer3d;
import org.jzy3d.plot3d.rendering.view.View;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;

public abstract class NativePainterFactory implements IPainterFactory {
  protected IChartFactory chartFactory;

  protected GLCapabilities capabilities;

  protected boolean offscreen = false;
  protected int width;
  protected int height;

  protected boolean traceGL = false;
  protected boolean debugGL = false;


  /**
   * Initialize a factory with a default desired {@link GLCapabilities} defined by
   * {@link NativePainterFactory#getDefaultCapabilities(GLProfile)} based on the detected
   * {@link GLProfile}, either {@link GLProfile#GL2} or {@link GLProfile#GL2ES2} if
   * {@link GLProfile#GL2} is not available. If none of these profile is available, an
   * {@link UnsupportedOperationException} is thrown.
   */
  public NativePainterFactory() {
    this(getDefaultCapabilities(detectGLProfile()));
  }

  public NativePainterFactory(GLCapabilities capabilities) {
    this.capabilities = capabilities;
  }

  /** Return desired Open GL Capabilities */
  public GLCapabilities getCapabilities() {
    return capabilities;
  }

  // @Override
  /** Only needed by {@link INativeCanvas} */
  /*
   * public Renderer3d newRenderer3D(View view, boolean traceGL, boolean debugGL) { return new
   * Renderer3d(view, traceGL, debugGL); }
   */

  // @Override
  /** Only needed by {@link INativeCanvas} */
  public Renderer3d newRenderer3D(View view) {
    return new Renderer3d(view, traceGL, debugGL);
    // newRenderer3D(view, this.traceGL, this.traceGL);
  }

  @Override
  public IPainter newPainter() {
    return new NativeDesktopPainter();
  }

  @Override
  public NativeAnimator newAnimator(ICanvas canvas) {
    return new NativeAnimator((GLAutoDrawable) canvas);
  }

  @Override
  public SymbolHandler newSymbolHandler(IImageWrapper image) {
    throw new NotImplementedException();
  }

  @Override
  public IChartFactory getChartFactory() {
    return chartFactory;
  }

  @Override
  public void setChartFactory(IChartFactory chartFactory) {
    this.chartFactory = chartFactory;
  }

  @Override
  public boolean isOffscreen() {
    return offscreen;
  }

  @Override
  public void setOffscreenDisabled() {
    this.offscreen = false;
    this.capabilities.setOnscreen(true);
  }

  /**
   * Set the painter factory with the offscreen image dimension will configure
   * {@link GLCapabilities#setOnscreen(boolean)} properly and let inheriting painters they should
   * use {@link OffscreenCanvas} instead of the {@link ICanvas} they use by default for display.
   */
  @Override
  public void setOffscreen(int width, int height) {
    this.offscreen = true;
    this.width = width;
    this.height = height;
    this.capabilities.setOnscreen(false);
  }

  @Override
  public void setOffscreen(Rectangle rectangle) {
    setOffscreen(rectangle.width, rectangle.height);
  }

  @Override
  public Dimension getOffscreenDimension() {
    return new Dimension(width, height);
  }

  /************ OPENGL PROFILE AND CAPABILITIES HELPERS ************/

  public static GLProfile detectGLProfile() {
    if (!(GLProfile.isAvailable(GLProfile.GL2) || GLProfile.isAvailable(GLProfile.GL2ES2))) {
      throw new UnsupportedOperationException(
          "Jzy3d requires an OpenGL 2 or OpenGL 2 ES 2 hardware");
    }

    // GLProfile profile = GLProfile.get(GLProfile.GL4); // GL4bcImpl fail to downcast to GL2 on Mac
    // so won't use GLProfile.getMaximum(true) until https://github.com/jzy3d/jogl/issues/7 is fixed

    if (GLProfile.isAvailable(GLProfile.GL2)) {
      // Preferred profile = GL2
      return GLProfile.get(GLProfile.GL2);
    } else {
      // second option for Android = GL2ES2
      return GLProfile.get(GLProfile.GL2ES2);
    }
  }



  /**
   * This profile has prove to fix the fact that using a raw GLCapabilities without settings let
   * screenshot as gray only
   */
  public static GLCapabilities getOffscreenCapabilities(GLProfile glp) {
    GLCapabilities caps = getDefaultCapabilities(glp);
    caps.setOnscreen(false);
    return caps;
  }

  public static GLCapabilities getDefaultCapabilities(GLProfile glp) {
    GLCapabilities caps = new GLCapabilities(glp);
    caps.setHardwareAccelerated(true);

    // false lead to not erased background on MacOS X 10.15.3 (Catalina) but not 10.12
    caps.setDoubleBuffered(true);

    boolean fixedResolution = true;
    if (fixedResolution) {
      caps.setAlphaBits(8);
      caps.setRedBits(8);
      caps.setBlueBits(8);
      caps.setGreenBits(8);
    }
    return caps;
  }

  public boolean isTraceGL() {
    return traceGL;
  }

  /** If true, will enable GL code tracing in console. Default is false. */
  public void setTraceGL(boolean traceGL) {
    this.traceGL = traceGL;
  }

  @Override
  public boolean isDebugGL() {
    return debugGL;
  }

  /**
   * If true, will let GL trigger {@link GLException} if an error occur in OpenGL which ease
   * debugging. Default is false.
   */
  @Override
  public void setDebugGL(boolean debugGL) {
    this.debugGL = debugGL;
  }



}
