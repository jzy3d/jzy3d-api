package org.jzy3d.tests.integration;

import org.junit.Test;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.junit.NativeChartTester;
import org.jzy3d.plot3d.primitives.SampleGeom;
import org.jzy3d.plot3d.rendering.canvas.Quality;

public class ITTestNativeScatterChart {
  @Test
  public void scatterTest() {

    // When
    AWTChartFactory factory = new AWTChartFactory();
    factory.getPainterFactory().setOffscreen(700, 600);
    Chart chart = factory.newChart(Quality.Advanced());

    chart.add(SampleGeom.scatter(50000, 3));

    // Then
    NativeChartTester tester = new NativeChartTester();
    tester.assertSimilar(chart, tester.path(this));
  }
}
