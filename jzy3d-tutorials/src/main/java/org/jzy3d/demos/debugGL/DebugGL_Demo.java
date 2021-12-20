package org.jzy3d.demos.debugGL;

import org.jzy3d.analysis.AWTAbstractAnalysis;
import org.jzy3d.analysis.AnalysisLauncher;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.debugGL.tracers.DebugGLChart2d;
import org.jzy3d.debugGL.tracers.DebugGLChart3d;
import org.jzy3d.maths.Range;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.Quality;

public class DebugGL_Demo extends AWTAbstractAnalysis {
  public static void main(String[] args) throws Exception {
    DebugGL_Demo d = new DebugGL_Demo();
    AnalysisLauncher.open(d, new Rectangle(300, 0, 800, 800));

    DebugGLChart3d debugChart = new DebugGLChart3d(d.getChart(), new AWTChartFactory());
    debugChart.open(new Rectangle(0, 0, 300, 300));

    DebugGLChart2d debugChart2d = new DebugGLChart2d(d.getChart());
    /*
     * debugChart2d.watch("near", Color.RED, c->c.getView().getCamera().getNear());
     * debugChart2d.watch("far", Color.BLUE, c->c.getView().getCamera().getFar());
     * debugChart2d.watch("radius", Color.GREEN,
     * c->c.getView().getCamera().getRenderingSphereRadius());
     */

    debugChart2d.watch("viewpoint.x", Color.RED, c -> c.getView().getViewPoint().x);
    debugChart2d.watch("viewpoint.y", Color.BLUE, c -> c.getView().getViewPoint().y);
    debugChart2d.watch("viewpoint.z", Color.GREEN, c -> c.getView().getViewPoint().z);


    debugChart2d.open(new Rectangle(0, 300, 300, 300));
  }

  public DebugGL_Demo() {}

  @Override
  public void init() {
    // Define a function to plot
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
    final Shape surface = new SurfaceBuilder().orthonormal(mapper, range, steps);
    surface.setColorMapper(new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(),
        surface.getBounds().getZmax(), new Color(1, 1, 1, .5f)));
    surface.setFaceDisplayed(true);
    surface.setWireframeDisplayed(false);

    // Create a chart
    chart = initializeChart(Quality.Advanced());
    chart.getScene().getGraph().add(surface);
    chart.addKeyboardCameraController();
  }
}
