package org.jzy3d.tests.integration.surface;

import org.junit.Test;
import org.jzy3d.chart.Chart;
import org.jzy3d.junit.ChartTester;
import org.jzy3d.plot3d.rendering.view.HiDPI;
import org.jzy3d.tests.integration.ITTest;
import org.jzy3d.utils.LoggerUtils;

public class ITTestEmulGLSurfaceChart {
  @Test
  public void whenSurfaceChart_ThenMatchBaselineImagePixelwise() {
    LoggerUtils.minimal();

    // When
    Chart chart = ITTest.chartEmulGL(HiDPI.ON);

    chart.add(ITTest.surface());

    // Then
    ITTest.assertChart(chart, this.getClass());
  }



}
