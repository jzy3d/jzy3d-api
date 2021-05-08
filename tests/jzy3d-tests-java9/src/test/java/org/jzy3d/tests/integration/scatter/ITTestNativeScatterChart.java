package org.jzy3d.tests.integration.scatter;

import org.junit.Test;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.junit.ChartTester;
import org.jzy3d.junit.NativeChartTester;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.HiDPI;
import org.jzy3d.tests.integration.ITTest;
import org.jzy3d.utils.LoggerUtils;

public class ITTestNativeScatterChart {
  @Test
  public void surfaceTest() {
    LoggerUtils.minimal();

    // When
    Chart chart = ITTest.chartNative(HiDPI.OFF);

    chart.add(ITTest.scatter(50000));

    // Then
    ITTest.assertChart(chart, this.getClass());
  }
}
