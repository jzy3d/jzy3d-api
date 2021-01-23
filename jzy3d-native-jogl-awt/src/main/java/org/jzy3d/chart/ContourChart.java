package org.jzy3d.chart;

import org.jzy3d.chart.factories.ContourChartFactory;
import org.jzy3d.plot3d.rendering.canvas.Quality;

public class ContourChart extends AWTChart {
  public ContourChart() {
    this(DEFAULT_QUALITY);
  }

  public ContourChart(Quality quality) {
    super(new ContourChartFactory(), quality);
  }
}
