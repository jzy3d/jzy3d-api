package org.jzy3d.chart.fallback;

import org.jzy3d.chart.AWTNativeChart;
import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.plot3d.rendering.canvas.Quality;

public class FallbackChart extends AWTNativeChart implements IFallbackChart {
  /** An image panel to display chart image generated offscreen. */
  protected FallbackChartImagePanel imagePanel = new FallbackChartImagePanel();

  @Override
  public FallbackChartImagePanel getImagePanel() {
    return imagePanel;
  }

  /* */

  public FallbackChart(IChartFactory components, Quality quality) {
    super(components, quality);
  }
}
