package org.jzy3d.plot3d.rendering.view;

import org.jzy3d.painters.IPainter;
import org.jzy3d.painters.NativeDesktopPainter;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.INativeCanvas;
import org.jzy3d.plot3d.rendering.scene.Scene;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLPipelineFactory;
import com.jogamp.opengl.util.GLReadBufferUtil;
import com.jogamp.opengl.util.texture.TextureData;

/**
 * The {@link NativeRenderer} object is a {@link GLEventListener}, that makes openGL calls necessary
 * to initialize and render a {@link Scene} for an {@link ICanvas}.
 * 
 * One can activate OpenGl errors in console by setting debugGL to true in the constructor One can
 * activate OpenGl feedback in console by setting traceGL to true in the constructor
 * 
 * @author Martin Pernollet
 */
public class NativeRenderer implements Renderer {

  /** Initialize a Renderer attached to the given View. */
  public NativeRenderer(View view) {
    this(view, false, false);
  }

  public NativeRenderer() {
    this(null, false, false);
  }

  /**
   * Initialize a Renderer attached to the given View, and activate GL trace and errors to console.
   */
  public NativeRenderer(View view, boolean traceGL, boolean debugGL) {
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
  public void init(ICanvas canvas) {
    if (!(canvas instanceof INativeCanvas))
      return;

    GLAutoDrawable glDrawable = ((INativeCanvas) canvas).getDrawable();

    if (glDrawable != null && glDrawable.getGL() != null && glDrawable.getGL().getGL2() != null
        && view != null) {
      if (debugGL)
        glDrawable.getGL().getContext().setGL(
            GLPipelineFactory.create("com.jogamp.opengl.Debug", null, glDrawable.getGL(), null));
      if (traceGL)
        glDrawable.getGL().getContext().setGL(GLPipelineFactory.create("com.jogamp.opengl.Trace",
            null, glDrawable.getGL(), new Object[] {System.err}));

      updatePainterWithGL(glDrawable);
      view.init();
    }
  }

  /**
   * Called when the {@link GLAutoDrawable} requires a rendering. All call to rendering methods
   * should appear here.
   */
  @Override
  public void display(ICanvas canvas) {
    if (!(canvas instanceof INativeCanvas))
      return;

    GLAutoDrawable glDrawable = ((INativeCanvas) canvas).getDrawable();
    GL gl = glDrawable.getGL();

    updatePainterWithGL(glDrawable);

    if (view != null) {
      view.clear();
      view.render();

      if (doScreenshotAtNextDisplay) {
        GLReadBufferUtil screenshot = new GLReadBufferUtil(false, false);
        screenshot.readPixels(gl, true);
        image = screenshot.getTextureData();
        doScreenshotAtNextDisplay = false;
      }
    }
  }

  /** Called when the {@link GLAutoDrawable} is resized. */
  @Override
  public void reshape(ICanvas canvas, int x, int y, int width, int height) {
    if (!(canvas instanceof INativeCanvas))
      return;

    GLAutoDrawable glDrawable = ((INativeCanvas) canvas).getDrawable();

    this.width = width;
    this.height = height;

    if (view != null) {
      view.dimensionDirty = true;

      if (canvas != null) {

        updatePainterWithGL(glDrawable);

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
    ((NativeDesktopPainter) view.getPainter()).setGL(canvas.getGL());
  }

  // protected boolean first = true;

  @Override
  public void dispose(ICanvas canvas) {
    view = null;
  }

  public void nextDisplayUpdateScreenshot() {
    doScreenshotAtNextDisplay = true;
  }

  public TextureData getLastScreenshot() {
    return image;
  }

  /** Return the width that was given after the last resize event. */
  public int getWidth() {
    return width;
  }

  /** Return the height that was given after the last resize event. */
  public int getHeight() {
    return height;
  }


  protected View view;
  protected int width = 0;
  protected int height = 0;
  protected boolean doScreenshotAtNextDisplay = false;
  protected TextureData image = null;
  protected boolean traceGL = false;
  protected boolean debugGL = false;

}
