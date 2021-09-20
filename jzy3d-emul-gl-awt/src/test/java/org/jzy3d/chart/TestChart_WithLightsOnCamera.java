package org.jzy3d.chart;

import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.chart.factories.ChartFactory;
import org.jzy3d.chart.factories.EmulGLChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.lights.Light;

public class TestChart_WithLightsOnCamera {
  @Test
  public void whenAddWithLight_ThenLightPositionIsUpdated() {

    // Given
    ChartFactory factory = new EmulGLChartFactory();
    factory.getPainterFactory().setOffscreen(500, 500);
    Chart chart = factory.newChart();
    
    // When
    Light light = chart.addLightOnCamera();
    Coord3d initialPosition = light.getPosition();
    
    chart.add(surface());

    // Then light position was updated
    Assert.assertNotEquals(initialPosition, light.getPosition());
    
    // When another creation, return the same light
    Assert.assertEquals(light, chart.addLightOnCamera());
  }

  @Test
  public void whenAddWithLightPair_ThenLightPositionIsUpdated() {

    // Given
    ChartFactory factory = new EmulGLChartFactory();
    factory.getPainterFactory().setOffscreen(500, 500);
    Chart chart = factory.newChart();

    // When
    Light[] light = chart.addLightPairOnCamera();
    Coord3d initialPositionUp = light[0].getPosition();
    Coord3d initialPositionDo = light[1].getPosition();
    
    chart.add(surface());

    // Then light position was updated
    Assert.assertNotEquals(initialPositionUp, light[0].getPosition());
    Assert.assertNotEquals(initialPositionDo, light[1].getPosition());
    
    // When another creation, return the same light
    Assert.assertEquals(light[0], chart.addLightPairOnCamera()[0]);
    Assert.assertEquals(light[1], chart.addLightPairOnCamera()[1]);
    
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
