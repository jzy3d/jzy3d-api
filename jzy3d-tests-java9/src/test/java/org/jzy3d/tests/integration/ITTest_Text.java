package org.jzy3d.tests.integration;

import org.junit.Test;
import org.jzy3d.chart.Chart;
import org.jzy3d.painters.Font;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.primitives.axis.layout.AxisLayout;
import org.jzy3d.plot3d.primitives.axis.layout.ZAxisSide;
import org.jzy3d.plot3d.primitives.axis.layout.fonts.HiDPIProportionalFontSizePolicy;
import org.jzy3d.plot3d.rendering.legends.colorbars.AWTColorbarLegend;
import org.jzy3d.plot3d.rendering.view.HiDPI;


public class ITTest_Text extends ITTest{
  /** This main method is here to test manually a chart and keep it open until one close it explicitely. */
  public static void main(String[] args) {
    open(new ITTest_Text().whenCustomFont(WT.EmulGL_AWT, HiDPI.ON));
  }
  
  /* ************************************************************************************************** */

  /**
   * <img src="src/test/resources/"/>
   */
  @Test
  public void whenColorbar_IsModifiedByCustomFont() {
    whenCustomFont(WT.EmulGL_AWT, HiDPI.ON);
    whenCustomFont(WT.EmulGL_AWT, HiDPI.OFF);
    whenCustomFont(WT.Native_AWT, HiDPI.OFF);
    //whenColorbarIsModifiedByCustomFont(WT.Native_AWT, HiDPI.ON);
  }

  public Chart whenCustomFont(WT wt, HiDPI hidpi) {
    // Given
    Chart chart = chart(wt, hidpi);
    Shape surface = surface();
    chart.add(surface);
    
    // When
    AxisLayout layout = (AxisLayout)chart.getAxisLayout();
    layout.setZAxisSide(ZAxisSide.LEFT);
    layout.setFont(new Font("Apple Chancery", 24)); 
    layout.setFontSizePolicy(new HiDPIProportionalFontSizePolicy(chart.getView()));

    surface.setLegend(new AWTColorbarLegend(surface, chart.getView().getAxis().getLayout()));

    // Then
    assertChart(chart, name(this, wt, chart.getQuality().getHiDPI(), "Font=AppleChancery24"));
    
    // For manual tests
    return chart;
  }
}
