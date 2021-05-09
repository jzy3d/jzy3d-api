package org.jzy3d.tests.integration;

import org.junit.Test;
import org.jzy3d.chart.Chart;
import org.jzy3d.plot3d.rendering.view.HiDPI;
import org.jzy3d.tests.integration.ITTest.WT;
import org.jzy3d.utils.LoggerUtils;

public class ITTest_Scatter {
  @Test
  public void whenScatterChart_ThenMatchBaselineImagePixelwise() {
    LoggerUtils.minimal();

    whenScatterChart_ThenMatchBaselineImagePixelwise(WT.EmulGL_AWT, HiDPI.ON);
    whenScatterChart_ThenMatchBaselineImagePixelwise(WT.EmulGL_AWT, HiDPI.OFF);
    whenScatterChart_ThenMatchBaselineImagePixelwise(WT.Native_AWT, HiDPI.OFF);
  }

  private void whenScatterChart_ThenMatchBaselineImagePixelwise(WT wt, HiDPI hidpi) {
    // When
    Chart chart = ITTest.chart(wt, hidpi);
    chart.add(ITTest.scatter(50000));

    // Then
    ITTest.assertChart(chart, ITTest.name(this, wt, chart.getQuality().getHiDPI()));
  }
}
