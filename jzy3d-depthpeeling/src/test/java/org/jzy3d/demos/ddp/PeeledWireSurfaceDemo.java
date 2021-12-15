package org.jzy3d.demos.ddp;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.ChartLauncher;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.factories.DepthPeelingChartFactory;
import org.jzy3d.factories.DepthPeelingPainterFactory;
import org.jzy3d.maths.Dimension;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.legends.colorbars.AWTColorbarLegend;


public class PeeledWireSurfaceDemo {
  public static void main(String[] args) throws Exception {
    // Define a function to plot
    Mapper mapper = new Mapper() {
      @Override
      public double f(double x, double y) {
        return 10 * Math.sin(x / 10) * Math.cos(y / 20) * x;
      }
    };

    // Define range and precision for the function to plot
    Range range = new Range(-150, 150);
    int steps = 50;

    // Create the object to represent the function over the given range.
    final Shape surface =
        new SurfaceBuilder().orthonormal(new OrthonormalGrid(range, steps, range, steps), mapper);
    surface.setColorMapper(new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(),
        surface.getBounds().getZmax(), new Color(1, 1, 1, .5f)));
    surface.setFaceDisplayed(true);
    surface.setWireframeDisplayed(true);
    surface.setWireframeColor(Color.BLACK);

    // Create a chart and add surface
    
    AWTChartFactory f = new DepthPeelingChartFactory(new DepthPeelingPainterFactory());
    Chart chart = f.newChart(Quality.Advanced().setAlphaActivated(false));

    chart.getScene().getGraph().add(surface);
    
    chart.getScene().getGraph().setStrategy(null);

    // Setup a colorbar
    AWTColorbarLegend cbar = new AWTColorbarLegend(surface, chart.getView().getAxis().getLayout());
    cbar.setMinimumSize(new Dimension(100, 600));
    //surface.setLegend(cbar);
    
    chart.open();
    chart.getMouse();

    ChartLauncher.openChart(chart);
  }
}
