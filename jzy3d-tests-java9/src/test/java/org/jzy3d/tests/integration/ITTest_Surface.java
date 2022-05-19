package org.jzy3d.tests.integration;

import org.junit.Test;
import org.jzy3d.chart.Chart;
import org.jzy3d.plot3d.rendering.view.HiDPI;

public class ITTest_Surface extends ITTest{
  public static void main(String[] args) {
    open(new ITTest_Surface().whenSurfaceChart_ThenMatchBaselineImagePixelwise(WT.EmulGL_AWT, HiDPI.ON));
  }

  @Test
  public void whenSurfaceChart_ThenMatchBaselineImagePixelwise() {
    System.out.println("ITTest : whenSurfaceChart_ThenMatchBaselineImagePixelwise");

    forEach((toolkit, resolution) -> whenSurfaceChart_ThenMatchBaselineImagePixelwise(toolkit, resolution));
  }

  private Chart whenSurfaceChart_ThenMatchBaselineImagePixelwise(WT wt, HiDPI hidpi) {
    // Given
    Chart chart = chart(wt, hidpi);

    // When
    chart.add(surface());

    // Then
    assertChart(chart, name(this, wt, chart.getQuality().getHiDPI()));
    
    return chart;
  }



}
