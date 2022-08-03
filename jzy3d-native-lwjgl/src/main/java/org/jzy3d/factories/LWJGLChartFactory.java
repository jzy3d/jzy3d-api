package org.jzy3d.factories;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.ChartFactory;
import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.chart.factories.IPainterFactory;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.View;

public class LWJGLChartFactory extends ChartFactory{
  static Logger logger = LogManager.getLogger(ChartFactory.class);

  public LWJGLChartFactory() {
    super(new LWJGLPainterFactory());
  }

  public LWJGLChartFactory(IPainterFactory painterFactory) {
    super(painterFactory);
  }

  /**
   * The {@link AWTNativeView} returned by this factory support Java2d defined components
   * (background images, tooltips, post-renderers and overlay)
   */
  @Override
  public View newView(IChartFactory factory, Scene scene, ICanvas canvas, Quality quality) {
    return new View(factory, scene, canvas, quality);
  }

  @Override
  public IChartFactory getFactory() {
    return this;
  }
}
