package org.jzy3d.tests.integration.colorbar;

import org.junit.Test;
import org.jzy3d.chart.Chart;
import org.jzy3d.painters.Font;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.primitives.axis.layout.ZAxisSide;
import org.jzy3d.plot3d.rendering.legends.colorbars.AWTColorbarLegend;
import org.jzy3d.plot3d.rendering.view.HiDPI;
import org.jzy3d.tests.integration.ITTest;


/**
 * Test non reg of colorbar and text layout with HiDPI EmulGL charts.
 */
public class ITTestEmulGLHiDPI_Colorbar {

  @Test
  public void hidpi() {
    // When
    Chart chart = ITTest.chartEmulGL(HiDPI.ON);

    Shape surface = ITTest.surface();
    
    chart.add(surface);
    chart.getAxisLayout().setZAxisSide(ZAxisSide.LEFT);
    chart.getAxisLayout().setFont(Font.TimesRoman_24); 
    
    surface.setLegend(new AWTColorbarLegend(surface, chart.getView().getAxis().getLayout()));

    // Then
    ITTest.assertChart(chart, this.getClass());
  }
}
