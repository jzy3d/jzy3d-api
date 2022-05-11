package org.jzy3d.tests.integration;

import org.junit.Test;
import org.jzy3d.chart.Chart;
import org.jzy3d.plot3d.rendering.view.HiDPI;

public class ITTest_Scatter extends ITTest{
  public static void main(String[] args) {
    open(new ITTest_Scatter().whenScatterChart_ThenMatchBaselineImagePixelwise(WT.EmulGL_AWT, HiDPI.ON));
  }
  
  @Test
  public void whenScatterChart_ThenMatchBaselineImagePixelwise() {
    System.out.println("ITTest : whenScatterChart_ThenMatchBaselineImagePixelwise");

    forEach((toolkit, resolution) -> 
        whenScatterChart_ThenMatchBaselineImagePixelwise(toolkit, resolution));
  }

  private Chart whenScatterChart_ThenMatchBaselineImagePixelwise(WT wt, HiDPI hidpi) {
    // When
    Chart chart = chart(wt, hidpi);
    chart.add(scatter(50000));

    // Then
    assertChart(chart, name(this, wt, chart.getQuality().getHiDPI()));
    
    return chart;
  }
}
