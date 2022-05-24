package org.jzy3d.demos.chart2d;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.plot3d.primitives.SampleGeom;
import org.jzy3d.plot3d.rendering.canvas.Quality;

/**
 * Demonstrate a 2D scatter chart
 * 
 * @author Martin Pernollet
 */
public class Scatter2D_DemoAWT {
  public static void main(String[] args)  {
    Chart chart = new AWTChartFactory().newChart(Quality.Advanced());
    chart.add(SampleGeom.scatter(20000, 2));
    chart.view2d();
    chart.open();
  }
}
