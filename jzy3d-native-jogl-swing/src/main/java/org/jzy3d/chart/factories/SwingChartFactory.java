package org.jzy3d.chart.factories;

import org.jzy3d.chart.Chart;
import org.jzy3d.plot3d.rendering.canvas.Quality;

public class SwingChartFactory extends AWTChartFactory {
  public SwingChartFactory() {
    super(new SwingPainterFactory());
  }

  /* */

  public static Chart chart(Quality quality) {
    IChartFactory f = new SwingChartFactory();
    return f.newChart(quality);
  }
}
