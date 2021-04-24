package org.jzy3d.plot3d.rendering.canvas;

import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.EmulGLSkin;
import org.jzy3d.chart.factories.ChartFactory;
import org.jzy3d.chart.factories.EmulGLChartFactory;
import org.jzy3d.painters.Font;
import org.jzy3d.plot3d.rendering.view.HiDPI;

public class TestView {
  @Test
  public void whenPixelScaleChange_ThenTextFontChanges() {
    // Given
    ChartFactory f = new EmulGLChartFactory();
    Chart c = f.newChart();
    EmulGLSkin skin = EmulGLSkin.on(c);
    EmulGLCanvas canvas = skin.getCanvas();
    
    Font font_Default = new Font("font_Default", 15);
    Font font_NoHiDPI = new Font("font_NoHiDPI", 10);
    Font font_HiDPI = new Font("font_HiDPI", 20);
    
    c.getAxisLayout().setFont(font_Default);
    c.getAxisLayout().setFont(font_NoHiDPI, HiDPI.OFF);
    c.getAxisLayout().setFont(font_HiDPI, HiDPI.ON);
    
    
    // When
    canvas.firePixelScaleChanged(2, 2);
    
    // Then
    Assert.assertEquals(font_HiDPI, c.getAxisLayout().getFont());

    // When
    canvas.firePixelScaleChanged(1, 1);
    
    // Then
    Assert.assertEquals(font_NoHiDPI, c.getAxisLayout().getFont());

  
  }

}
