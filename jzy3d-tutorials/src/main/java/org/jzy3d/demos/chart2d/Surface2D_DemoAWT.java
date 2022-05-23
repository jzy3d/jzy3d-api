package org.jzy3d.demos.chart2d;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.primitives.SampleGeom;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.Quality;

/**
 * Demonstrate a 2D surface chart
 * 
 * @author Martin Pernollet
 *
 */
public class Surface2D_DemoAWT {
  public static void main(String[] args) throws Exception {
    Shape surface = SampleGeom.surface(new Range (-3, 1), new Range(-1, 3), 1);
    surface.setWireframeDisplayed(false);
    
    Chart chart = new AWTChartFactory().newChart(Quality.Advanced());
    chart.add(surface);
    chart.view2d();
    chart.open();
    chart.addMouse();
  }
}
