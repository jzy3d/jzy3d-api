package org.jzy3d.tests.integration;

import org.junit.Test;
import org.jzy3d.bridge.swing.FrameSwing;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.chart.factories.SwingChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.junit.ChartTester;
import org.jzy3d.junit.NativeChartTester;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Func3D;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.CanvasSwing;
import org.jzy3d.plot3d.rendering.canvas.Quality;

public class ITTestNativeSurfaceChart_Swing {
  @Test
  public void surfaceTest() {

    IChartFactory factory = new SwingChartFactory();

    Chart chart = factory.newChart(Quality.Advanced());

    chart.add(surface());

    FrameSwing f = (FrameSwing) chart.open(800, 600);

    chart.render();

    // We want to ensure that we won't start baseline image
    // comparison before the canvas is (1) displayed
    // and (2) having the good image size.
    CanvasSwing canvas = (CanvasSwing) chart.getCanvas();

    while (!f.isVisible() || !canvas.isRealized()) {
      int waitTimeMs = 1500;
      System.out.println("Waiting " + waitTimeMs);

      chart.sleep(waitTimeMs);
      // canvas.forceRepaint();
    }


    // Then
    ChartTester tester = new NativeChartTester();
    tester.assertSimilar(chart, tester.path(this));
  }



  private static Shape surface() {
    Func3D func = new Func3D((x, y) -> x * Math.sin(x * y));
    Range range = new Range(-3, 3);
    int steps = 80;

    // Create the object to represent the function over the given range.
    final Shape surface = new SurfaceBuilder().orthonormal(new OrthonormalGrid(range, steps), func);
    surface
        .setColorMapper(new ColorMapper(new ColorMapRainbow(), surface, new Color(1, 1, 1, .5f)));
    surface.setFaceDisplayed(true);
    surface.setWireframeDisplayed(true);
    return surface;
  }
}
