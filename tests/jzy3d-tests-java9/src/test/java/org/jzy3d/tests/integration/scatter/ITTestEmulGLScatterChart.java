package org.jzy3d.tests.integration.scatter;

import java.util.Random;
import org.junit.Test;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.EmulGLChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.junit.ChartTester;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.HiDPI;
import org.jzy3d.tests.integration.ITTest;
import org.jzy3d.utils.LoggerUtils;

public class ITTestEmulGLScatterChart {
  @Test
  public void whenScatterChart_ThenMatchBaselineImagePixelwise() {
    LoggerUtils.minimal();

    // When
    Chart chart = ITTest.chartEmulGL(HiDPI.ON);
    chart.add(ITTest.scatter(50000));

    // Then
    ITTest.assertChart(chart, this.getClass());
  }
}
