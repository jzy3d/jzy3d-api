package org.jzy3d.chart;

import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.rendering.view.layout.IViewportLayout;



/**
 * A {@link ChartView} allows displaying a 3d scene on the left, and a set of {@link Drawable}'s
 * {@link AWTLegend} on the right.
 *
 * @author Martin Pernollet
 */
public class ChartView extends View {

  public ChartView(IChartFactory factory, Scene scene, ICanvas canvas, Quality quality) {
    super(factory, scene, canvas, quality);
  }

  public void initInstance(IChartFactory factory, Scene scene, ICanvas canvas, Quality quality) {
    super.initInstance(factory, scene, canvas, quality);
    layout = factory.getPainterFactory().newViewportLayout();
  }

  /* */


  /**
   * Set the camera held by this view, and draw the scene graph. Performs all transformations of
   * eye, target coordinates to adapt the camera settings to the scaled scene.
   */
  @Override
  public void render() {
    fireViewLifecycleWillRender(null);

    if (layout != null && getChart() != null) {
      layout.update(getChart());
      layout.render(painter, getChart());

      // renderOverlay(gl);
      if (dimensionDirty)
        dimensionDirty = false;
    }
  }


  /* */

  public IViewportLayout getLayout() {
    return layout;
  }

  public void setLayout(IViewportLayout layout) {
    this.layout = layout;
  }


  protected IViewportLayout layout;
}

