package org.jzy3d.tests.integration;

import org.junit.Test;
import org.jzy3d.chart.Chart;
import org.jzy3d.painters.Font;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.primitives.axis.layout.ZAxisSide;
import org.jzy3d.plot3d.rendering.legends.colorbars.AWTColorbarLegend;
import org.jzy3d.plot3d.rendering.view.HiDPI;
import org.jzy3d.tests.integration.ITTest.WT;


/**
 * Test non reg of colorbar and text layout with HiDPI EmulGL charts.
 */
public class ITTest_Colorbar {

  @Test
  public void whenColorbarIsModifiedByCustomFont() {
    whenColorbarIsModifiedByCustomFont(WT.EmulGL_AWT, HiDPI.ON);
    whenColorbarIsModifiedByCustomFont(WT.EmulGL_AWT, HiDPI.OFF);
    whenColorbarIsModifiedByCustomFont(WT.Native_AWT, HiDPI.OFF);
    //whenColorbarIsModifiedByCustomFont(WT.Native_AWT, HiDPI.ON);
  }

  protected void whenColorbarIsModifiedByCustomFont(WT wt, HiDPI hidpi) {
    // Given
    Chart chart = ITTest.chart(wt, hidpi);
    Shape surface = ITTest.surface();
    chart.add(surface);
    
    // When
    chart.getAxisLayout().setZAxisSide(ZAxisSide.LEFT);
    chart.getAxisLayout().setFont(Font.TimesRoman_24); 
    
    surface.setLegend(new AWTColorbarLegend(surface, chart.getView().getAxis().getLayout()));

    // Then
    ITTest.assertChart(chart, ITTest.name(this, wt, chart.getQuality().getHiDPI(), "Font=TimesRoman24"));
  }
  
  @Test
  public void whenColorbarHasMininumWidth() {
    whenColorbarHasMininumWidth(WT.EmulGL_AWT, HiDPI.ON);
    whenColorbarHasMininumWidth(WT.EmulGL_AWT, HiDPI.OFF);
    whenColorbarHasMininumWidth(WT.Native_AWT, HiDPI.OFF);
  }

  private void whenColorbarHasMininumWidth(WT wt, HiDPI hidpi) {
    // Given
    Chart chart = ITTest.chart(wt, hidpi);
    Shape surface = ITTest.surface();
    chart.add(surface);

    // When
    chart.getAxisLayout().setZAxisSide(ZAxisSide.LEFT);
    chart.getAxisLayout().setFont(Font.TimesRoman_24); 
    
    AWTColorbarLegend legend = new AWTColorbarLegend(surface, chart.getView().getAxis().getLayout());
    surface.setLegend(legend);
    legend.setMinimumWidth(300);    
    

    // Then
    ITTest.assertChart(chart, ITTest.name(this, wt, chart.getQuality().getHiDPI(), "Width=300"));
  }


}
