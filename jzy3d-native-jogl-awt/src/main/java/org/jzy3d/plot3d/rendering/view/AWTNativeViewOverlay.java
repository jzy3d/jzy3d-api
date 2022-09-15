package org.jzy3d.plot3d.rendering.view;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.PolygonFill;
import org.jzy3d.plot3d.primitives.PolygonMode;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.INativeCanvas;
import org.jzy3d.plot3d.rendering.tooltips.ITooltipRenderer;
import org.jzy3d.plot3d.rendering.tooltips.Tooltip;
import com.jogamp.opengl.util.awt.Overlay;

/**
 * Renders all {@link Tooltip}s and {@link AWTRenderer2d}s on top of the scene.
 * 
 * The current pixel scale is taken into account so that all {@link AWTRenderer2d} do not have to
 * worry about it.
 * 
 * @see {@link #setUseFullCanvas(boolean)}
 */
public class AWTNativeViewOverlay implements IViewOverlay {
  protected static Logger LOGGER = LogManager.getLogger(AWTNativeViewOverlay.class);

  protected Overlay overlay;
  protected java.awt.Color overlayBackground = new java.awt.Color(0, 0, 0, 0);

  protected boolean useFullCanvas = true;

  @Override
  public void render(View view, ViewportConfiguration viewport, IPainter painter) {
    AWTView awtView = ((AWTView) view);
    ICanvas canvas = view.getCanvas();
    INativeCanvas nCanvas = (INativeCanvas) canvas;

    if (!awtView.hasOverlayStuffs() || !(viewport.width > 0 && viewport.height > 0))
      return;

    if (overlay == null)
      this.overlay = new Overlay(nCanvas.getDrawable());

    // Reset the polygon mode to let the Overlay util work properly
    painter.glPolygonMode(PolygonMode.FRONT_AND_BACK, PolygonFill.FILL);

    // Define the canvas area to use
    if (useFullCanvas) {
      int width = canvas.getRendererWidth();
      int height = canvas.getRendererHeight();

      /*
       * if(painter.getOS().isWindows() && painter.getWindowingToolkit().isAWT()) { Coord2d
       * pixelScale = canvas.getPixelScaleJVM(); width = (int)(width*pixelScale.x); height =
       * (int)(height*pixelScale.y); }
       */

      painter.glViewport(0, 0, width, height);
    } else {
      // Only work on canvas section that relates to camera (excludes the colorbar part)
      painter.glViewport(viewport.x, viewport.y, viewport.width, viewport.height);
    }

    // Perform rendering
    try {
      if (nCanvas.getDrawable().getSurfaceWidth() > 0
          && nCanvas.getDrawable().getSurfaceHeight() > 0) {

        Graphics2D g2d = overlay.createGraphics();

        // Consider HiDPI and viewports not occupying full canvas
        configureG2DScale(view, viewport, canvas, g2d);

        // Draw
        g2d.setBackground(overlayBackground);
        g2d.clearRect(0, 0, canvas.getRendererWidth(), canvas.getRendererHeight());

        // Tooltips
        for (ITooltipRenderer t : awtView.getTooltips()) {
          t.render(g2d);
        }

        // Renderers
        for (AWTRenderer2d renderer : awtView.getRenderers2d()) {
          renderer.setView(awtView);
          renderer.paint(g2d, canvas.getRendererWidth(), canvas.getRendererHeight());
          
          //System.out.println("AWTNativeViewOverlay - width : " + canvas.getRendererWidth());
        }

        // Render all
        overlay.markDirty(0, 0, canvas.getRendererWidth(), canvas.getRendererHeight());
        overlay.drawAll();

        g2d.dispose();
      }

    } catch (Exception e) {
      LOGGER.error(e, e);
    }
  }

  /**
   * Enable multiple stuff for HiDPI
   * <ul>
   * <li>make overlay HiDPI aware so that legend and its text content remain the same size than axis
   * text and constant over different screen.
   * <li>avoid a stretch effect when viewport does not occupy full canvas (occurs when adding a
   * colorbar for native charts)
   * <li>enable antialiasing and clean interpolation
   * </ul>
   */
  protected void configureG2DScale(View view, ViewportConfiguration viewport, ICanvas canvas,
      Graphics2D g2d) {

    boolean scaled = false;

    Coord2d pixelScale = view.getPixelScale();

    // Apply pixel scale
    if (pixelScale.x != 1 || pixelScale.y != 1) {
      g2d.scale(pixelScale.x, pixelScale.y);
      scaled = true;
    }

    // avoid a stretch effect when viewport does not occupy full canvas
    // (occurs when adding a colorbar for native charts)

    boolean unstretched = false;
    if (!useFullCanvas) {
      float xUnstretch = 1f * canvas.getRendererWidth() / viewport.width;
      float yUnstretch = 1f * canvas.getRendererHeight() / viewport.height;

      if (xUnstretch != 1 || yUnstretch != 1) {
        g2d.scale(xUnstretch, yUnstretch);
        unstretched = true;
      }
    }

    // enable antialiasing and clean interpolation
    if (unstretched || scaled) {
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
      g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
          RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    }

    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

  }

  public boolean isUseFullCanvas() {
    return useFullCanvas;
  }

  /**
   * If true, the overlay will occupy the full canvas. If false, it will only occupy the viewport
   * used by the {@Camera}, hence won't overlay on the colorbar.
   * 
   * If not occupying full canvas, a scale is applied to the Graphics2D instance of the overlay with
   * additional rendering hints to avoid aliasing of the anti-stretch effect.
   */
  public void setUseFullCanvas(boolean useFullCanvas) {
    this.useFullCanvas = useFullCanvas;
  }
}
