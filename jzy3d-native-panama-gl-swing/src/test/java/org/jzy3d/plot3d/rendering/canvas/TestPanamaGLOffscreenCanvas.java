/*******************************************************************************
 * Copyright (c) 2022, 2023 Martin Pernollet & contributors.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA
 *******************************************************************************/
package org.jzy3d.plot3d.rendering.canvas;

import java.awt.image.BufferedImage;
import java.io.File;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.jzy3d.chart.AWTChart;
import org.jzy3d.chart.factories.PanamaGLSwingChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Shape;

public class TestPanamaGLOffscreenCanvas {

  @Test
  public void offscreenChartRendersWithoutWindow() throws Exception {
    PanamaGLSwingChartFactory factory = new PanamaGLSwingChartFactory();
    factory.getPainterFactory().setOffscreen(320, 240);

    AWTChart chart;
    try {
      chart = factory.newChart(Quality.Advanced());
    } catch (Throwable t) {
      Assume.assumeNoException("No OpenGL context available", t);
      return;
    }

    try {
      // Then the chart has an offscreen canvas
      Assert.assertTrue(chart.getCanvas() instanceof PanamaGLOffscreenCanvas);

      // When adding a surface and a colorbar
      Shape surface = surface();
      chart.add(surface);
      chart.colorbar(surface);

      // Then the screenshot has the offscreen size and shows something
      BufferedImage image = (BufferedImage) chart.screenshot();

      Assert.assertEquals(320, image.getWidth());
      Assert.assertEquals(240, image.getHeight());
      Assert.assertTrue("chart should not be blank", countNonWhitePixels(image) > 1000);

      // Then the image can be written
      File file = new File("target/" + getClass().getSimpleName() + ".png");
      file.delete();
      chart.screenshot(file);
      Assert.assertTrue(file.exists());
    } finally {
      chart.dispose();
    }
  }

  protected int countNonWhitePixels(BufferedImage image) {
    int n = 0;
    for (int x = 0; x < image.getWidth(); x++)
      for (int y = 0; y < image.getHeight(); y++)
        if ((image.getRGB(x, y) & 0xFFFFFF) != 0xFFFFFF)
          n++;
    return n;
  }

  protected Shape surface() {
    Mapper mapper = new Mapper() {
      @Override
      public double f(double x, double y) {
        return x * Math.sin(x * y);
      }
    };
    Range range = new Range(-3, 3);
    Shape surface = new SurfaceBuilder().orthonormal(new OrthonormalGrid(range, 40), mapper);
    surface.setColorMapper(new ColorMapper(new ColorMapRainbow(), surface, new Color(1, 1, 1, .5f)));
    return surface;
  }
}
