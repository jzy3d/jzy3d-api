package org.jzy3d.plot3d.rendering.view;

import org.jzy3d.maths.TicToc;
import org.jzy3d.painters.IPainter;
import org.jzy3d.painters.NativeDesktopPainter;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.scene.Scene;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLPipelineFactory;
import com.jogamp.opengl.util.GLReadBufferUtil;
import com.jogamp.opengl.util.texture.TextureData;

/**
 * The {@link Renderer3d} is a {@link GLEventListener} that handles init, display, reshape and
 * screenshots of a {@link Scene} in a {@link ICanvas}.
 * 
 * One can activate OpenGl errors in console by setting debugGL to true in the constructor. One can
 * activate OpenGl feedback in console by setting traceGL to true in the constructor.
 * 
 * @author Martin Pernollet
 */
public class Renderer3d implements GLEventListener {
  protected View view;
  protected int width = 0;
  protected int height = 0;

  protected boolean doScreenshotAtNextDisplay = false;
  protected TextureData image = null;

  protected boolean traceGL = false;
  protected boolean debugGL = false;

  protected TicToc profileDisplayTimer = new TicToc();
  protected double lastRenderingTimeMs;

  /** Initialize a Renderer attached to the given View. */
  public Renderer3d(View view) {
    this(view, false, false);
  }

  public Renderer3d() {
    this(null, false, false);
  }

  /**
   * Initialize a Renderer attached to the given View, and activate GL trace and errors to console.
   */
  public Renderer3d(View view, boolean traceGL, boolean debugGL) {
    this.view = view;
    this.traceGL = traceGL;
    this.debugGL = debugGL;
  }

  /**
   * Called when the {@link GLAutoDrawable} is rendered for the first time. When one calls
   * Scene.init() function, this function is called and makes the OpenGL buffers initialization.
   * 
   * Note: in this implementation, GL Exceptions are not triggered. To do so, make te following call
   * at the beginning of the init() body: <code>
   * canvas.setGL( new DebugGL(canvas.getGL()) );
   * </code>
   */
  @Override
  public void init(GLAutoDrawable canvas) {
    if (canvas != null && canvas.getGL() != null && view != null) {

      GLContext context = canvas.getGL().getContext();
      
      if (debugGL)
        context.setGL(GLPipelineFactory.create("com.jogamp.opengl.Debug", null, canvas.getGL(), null));
      if (traceGL)
        context.setGL(GLPipelineFactory.create("com.jogamp.opengl.Trace", null, canvas.getGL(), new Object[] {System.err}));

      updatePainterWithGL(canvas);

      view.init();
    }
  }

  /**
   * Called when the {@link GLAutoDrawable} requires a rendering. All call to rendering methods
   * should appear here.
   */
  @Override
  public void display(GLAutoDrawable canvas) {
    profileDisplayTimer.tic();

    if (view != null) {
      if(canvas!=null && canvas.getGL()!=null) {
  
        updatePainterWithGL(canvas);
  
        if (view != null) {
          view.clear();
          view.render();
  
          exportImageIfRequired(canvas.getGL());

          renderScreenshotIfRequired(canvas.getGL());
        }
      }
    }

    profileDisplayTimer.toc();
    lastRenderingTimeMs = profileDisplayTimer.elapsedMilisecond();
  }
  

  /** Called when the {@link GLAutoDrawable} is resized. */
  @Override
  public void reshape(GLAutoDrawable canvas, int x, int y, int width, int height) {
    this.width = width;
    this.height = height;

    if (view != null) {
      view.dimensionDirty = true;

      if (canvas != null) {

        updatePainterWithGL(canvas);

        view.clear();
        view.render();
      }
    }
  }

  /**
   * This method allows configuring the {@link IPainter} with the current {@link GL} context
   * provided by the {@link GLAutoDrawable}. This may be usefull to override in case of a mocking GL
   * (to avoid having the mock replaced by a real GL Context).
   * 
   * @param canvas
   */
  protected void updatePainterWithGL(GLAutoDrawable canvas) {
    NativeDesktopPainter painter = ((NativeDesktopPainter) view.getPainter());
    painter.setGL(canvas.getGL());
  }

  @Override
  public void dispose(GLAutoDrawable arg0) {
    // do not loose reference to view since the init/display/dispose may be called
    // several time during the lifetime of this renderer and canvas, especially if the
    // chart is embedded in dockable windows that involve parent component change.
    
    
    // free possible resource, especially usefull to clear text renderer cache
    // and deal with 
    // https://forum.jogamp.org/TextRenderer-crash-the-JVM-after-removing-then-adding-a-canvas-from-a-AWT-or-Swing-layout-td4041660.html
    
    view.getPainter().clearCache();
  }

  /********************* SCREENSHOTS ***********************/

  public void nextDisplayUpdateScreenshot() {
    doScreenshotAtNextDisplay = true;
  }

  public TextureData getLastScreenshot() {
    return image;
  }

  protected void renderScreenshotIfRequired(GL gl) {
    if (doScreenshotAtNextDisplay) {
      GLReadBufferUtil screenshot = new GLReadBufferUtil(false, false);
      screenshot.readPixels(gl, true);
      image = screenshot.getTextureData();
      doScreenshotAtNextDisplay = false;
    }
  }
  
  protected void exportImageIfRequired(GL gl) {
    
  }




  /** Return the width that was given after the last resize event. */
  public int getWidth() {
    return width;
  }

  /** Return the height that was given after the last resize event. */
  public int getHeight() {
    return height;
  }

  public double getLastRenderingTimeMs() {
    return lastRenderingTimeMs;
  }


}
