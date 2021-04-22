package org.jzy3d.demos;

import org.jzy3d.bridge.swt.FrameSWTBridge;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.bridged.SWTBridgeChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.Quality;

public class SurfaceDemoSWTAWTBridge {

  public static void main(String[] args) {
    final Shape surface = surface();

    // Create a chart
    Chart chart = new SWTBridgeChartFactory().newChart(Quality.Advanced());
    chart.getScene().getGraph().add(surface);

    // TODO : let SWT Frame open in non blocking mode.
    FrameSWTBridge f = (FrameSWTBridge) chart.open(SurfaceDemoSWTAWTBridge.class.getSimpleName());
    
    chart.addMouseCameraController();
    //chart.startAnimation();
    // f.print("target/" + SurfaceDemoSWTAWTBridge.class.getSimpleName() + ".png");
  }

  private static Shape surface() {
    Mapper mapper = new Mapper() {
      @Override
      public double f(double x, double y) {
        return x * Math.sin(x * y);
      }
    };

    // Define range and precision for the function to plot
    Range range = new Range(-3, 3);
    int steps = 80;

    // Create the object to represent the function over the given range.
    final Shape surface =
        new SurfaceBuilder().orthonormal(new OrthonormalGrid(range, steps, range, steps), mapper);
    surface.setColorMapper(new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(),
        surface.getBounds().getZmax(), new Color(1, 1, 1, .5f)));
    surface.setFaceDisplayed(true);
    surface.setWireframeDisplayed(false);
    return surface;
  }
}
