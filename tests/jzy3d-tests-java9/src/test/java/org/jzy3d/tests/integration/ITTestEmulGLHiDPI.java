package org.jzy3d.tests.integration;

import org.junit.Test;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.ChartFactory;
import org.jzy3d.chart.factories.EmulGLChartFactory;
import org.jzy3d.chart.factories.EmulGLPainterFactory;
import org.jzy3d.chart.factories.IPainterFactory;
import org.jzy3d.junit.ChartTester;
import org.jzy3d.painters.Font;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.primitives.axis.layout.ZAxisSide;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.legends.colorbars.AWTColorbarLegend;
import org.jzy3d.plot3d.rendering.view.HiDPI;


/**
 * Test non reg of colorbar and text layout with HiDPI EmulGL charts.
 */
public class ITTestEmulGLHiDPI {

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
