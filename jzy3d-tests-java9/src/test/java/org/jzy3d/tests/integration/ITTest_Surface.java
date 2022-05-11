package org.jzy3d.tests.integration;

import org.junit.Test;
import org.jzy3d.chart.Chart;
import org.jzy3d.plot3d.rendering.view.HiDPI;

public class ITTest_Surface extends ITTest{
  @Test
  public void whenSurfaceChart_ThenMatchBaselineImagePixelwise() {
    System.out.println("ITTest : whenSurfaceChart_ThenMatchBaselineImagePixelwise");

    forEach((toolkit, resolution) -> whenSurfaceChart_ThenMatchBaselineImagePixelwise(toolkit, resolution));
  }

  private void whenSurfaceChart_ThenMatchBaselineImagePixelwise(WT wt, HiDPI hidpi) {
    // Given
    Chart chart = chart(wt, hidpi);

    // When
    chart.add(surface());

    // Then
    assertChart(chart, name(this, wt, chart.getQuality().getHiDPI()));
  }



}
