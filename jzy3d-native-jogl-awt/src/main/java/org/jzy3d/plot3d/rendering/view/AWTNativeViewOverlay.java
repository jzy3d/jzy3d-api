package org.jzy3d.plot3d.rendering.view;

import java.awt.Graphics2D;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jzy3d.chart.Chart;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.PolygonFill;
import org.jzy3d.plot3d.primitives.PolygonMode;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.INativeCanvas;
import org.jzy3d.plot3d.rendering.canvas.IScreenCanvas;
import org.jzy3d.plot3d.rendering.tooltips.ITooltipRenderer;
import org.jzy3d.plot3d.rendering.tooltips.Tooltip;
import com.jogamp.opengl.util.awt.Overlay;

public class AWTNativeViewOverlay implements IViewOverlay {
  protected static Logger LOGGER = LogManager.getLogger(AWTNativeViewOverlay.class);

  protected Overlay overlay;
  protected java.awt.Color overlayBackground = new java.awt.Color(0, 0, 0, 0);

  /**
   * Renders all provided {@link Tooltip}s and {@link AWTRenderer2d}s on top of the scene.
   * 
   * Due to the behaviour of the {@link Overlay} implementation, Java2d geometries must be drawn
   * relative to the {@link Chart}'s {@link IScreenCanvas}, BUT will then be stretched to fit in the
   * {@link Camera}'s viewport. This bug is very important to consider, since the Camera's viewport
   * may not occupy the full {@link IScreenCanvas}. Indeed, when View is not maximized (like the
   * default behaviour), the viewport remains square and centered in the canvas, meaning the Overlay
   * won't cover the full canvas area.
   * 
   * In other words, the following piece of code draws a border around the {@link View}, and not
   * around the complete chart canvas, although queried to occupy chart canvas dimensions:
   * 
   * g2d.drawRect(1, 1, chart.getCanvas().getRendererWidth()-2,
   * chart.getCanvas().getRendererHeight()-2);
   * 
   * {@link renderOverlay()} must be called while the OpenGL2 context for the drawable is current,
   * and after the OpenGL2 scene has been rendered.
   */
  @Override
  public void render(View view, ViewportConfiguration viewport, IPainter painter) {
    AWTView awtView = ((AWTView) view);
    ICanvas canvas = view.getCanvas();
    INativeCanvas nCanvas = (INativeCanvas) canvas;

    if (!awtView.hasOverlayStuffs())
      return;

    if (overlay == null)
      this.overlay = new Overlay(nCanvas.getDrawable());

    // TODO: don't know why needed to allow working with Overlay!!!????

    painter.glPolygonMode(PolygonMode.FRONT_AND_BACK, PolygonFill.FILL);

    painter.glViewport(viewport.x, viewport.y, viewport.width, viewport.height);

    if (overlay != null && viewport.width > 0 && viewport.height > 0) {

      try {
        if (nCanvas.getDrawable().getSurfaceWidth() > 0
            && nCanvas.getDrawable().getSurfaceHeight() > 0) {
          Graphics2D g2d = overlay.createGraphics();
          
          // make overlay HiDPI aware
          g2d.scale(view.getPixelScale().x, view.getPixelScale().y);

          g2d.setBackground(overlayBackground);
          g2d.clearRect(0, 0, canvas.getRendererWidth(), canvas.getRendererHeight());

          // Tooltips
          for (ITooltipRenderer t : awtView.getTooltips())
            t.render(g2d);

          // Renderers
          for (AWTRenderer2d renderer : awtView.getRenderers2d()) {
            
            renderer.paint(g2d, canvas.getRendererWidth(), canvas.getRendererHeight());
          }
          overlay.markDirty(0, 0, canvas.getRendererWidth(), canvas.getRendererHeight());
          overlay.drawAll();
          g2d.dispose();
        }

      } catch (Exception e) {
        LOGGER.error(e, e);
      }
    }

  }

}
