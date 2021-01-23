package org.jzy3d.chart;

import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.chart.factories.SwingChartFactory;
import org.jzy3d.plot3d.rendering.canvas.Quality;

public class SwingChart extends AWTNativeChart {
  public SwingChart(Quality quality) {
    super(new SwingChartFactory(), quality);
  }

  public SwingChart(IChartFactory factory, Quality quality) {
    super(factory, quality);
  }
}
