package org.jzy3d.tests.integration;

import org.junit.Test;
import org.jzy3d.chart.Chart;
import org.jzy3d.plot3d.rendering.view.HiDPI;
import org.jzy3d.tests.integration.ITTest.WT;
import org.jzy3d.utils.LoggerUtils;

public class ITTest_Surface {
  @Test
  public void whenSurfaceChart_ThenMatchBaselineImagePixelwise() {
    LoggerUtils.minimal();

    whenSurfaceChart_ThenMatchBaselineImagePixelwise(WT.EmulGL_AWT, HiDPI.ON);
    whenSurfaceChart_ThenMatchBaselineImagePixelwise(WT.EmulGL_AWT, HiDPI.OFF);
    whenSurfaceChart_ThenMatchBaselineImagePixelwise(WT.Native_AWT, HiDPI.OFF);
  }

  private void whenSurfaceChart_ThenMatchBaselineImagePixelwise(WT wt, HiDPI hidpi) {
    // Given
    Chart chart = ITTest.chart(wt, hidpi);

    // When
    chart.add(ITTest.surface());

    // Then
    ITTest.assertChart(chart, ITTest.name(this, wt, chart.getQuality().getHiDPI()));
  }



}
