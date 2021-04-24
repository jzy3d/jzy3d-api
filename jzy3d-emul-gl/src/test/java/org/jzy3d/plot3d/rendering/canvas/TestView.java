package org.jzy3d.plot3d.rendering.canvas;

import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.chart.AWTChart;
import org.jzy3d.chart.EmulGLSkin;
import org.jzy3d.chart.factories.ChartFactory;
import org.jzy3d.chart.factories.EmulGLChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Range;
import org.jzy3d.painters.Font;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.legends.colorbars.AWTColorbarLegend;
import org.jzy3d.plot3d.rendering.view.HiDPI;

public class TestView {
  @Test
  public void whenPixelScaleChange_ThenTextFont_ofAxisAndColorbar_Changes() {
    // Given
    ChartFactory f = new EmulGLChartFactory();
    AWTChart chart = (AWTChart)f.newChart();
    
    Shape surface = surface();
    chart.add(surface);
    AWTColorbarLegend legend = chart.colorbar(surface);

    EmulGLSkin skin = EmulGLSkin.on(chart);
    EmulGLCanvas canvas = skin.getCanvas();
    
    Font font_Default = new Font("font_Default", 15);
    Font font_NoHiDPI = new Font("font_NoHiDPI", 10);
    Font font_HiDPI = new Font("font_HiDPI", 20);
    
    chart.getAxisLayout().setFont(font_Default);
    chart.getAxisLayout().setFont(font_NoHiDPI, HiDPI.OFF);
    chart.getAxisLayout().setFont(font_HiDPI, HiDPI.ON);
    
    
    // When
    canvas.firePixelScaleChanged(2, 2); // trigger update of axis font size and default font size
    chart.render(); // trigger update of legend font based on default font size
    
    
    // Then
    Assert.assertEquals(font_HiDPI, chart.getAxisLayout().getFont());
    Assert.assertEquals(font_HiDPI, legend.getFont());

    // When
    canvas.firePixelScaleChanged(1, 1); // trigger update of axis font size and default font size
    chart.render(); // trigger update of legend font based on default font size
    
    
    // Then
    Assert.assertEquals(font_NoHiDPI, chart.getAxisLayout().getFont());
    Assert.assertEquals(font_NoHiDPI, legend.getFont());

  
  }

  
  protected Shape surface() {
    Mapper mapper = new Mapper() {
      @Override
      public double f(double x, double y) {
        return x * Math.sin(x * y);
      }
    };
    Range range = new Range(-3, 3);
    int steps = 50;

    Shape surface =
        new SurfaceBuilder().orthonormal(new OrthonormalGrid(range, steps, range, steps), mapper);
    surface.setPolygonOffsetFillEnable(false);

    ColorMapper colorMapper = new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(),
        surface.getBounds().getZmax(), new Color(1, 1, 1, .5f));

    surface.setColorMapper(colorMapper);
    surface.setFaceDisplayed(true);
    surface.setWireframeDisplayed(true);
    surface.setWireframeColor(Color.BLACK);
    return surface;
  }

}
