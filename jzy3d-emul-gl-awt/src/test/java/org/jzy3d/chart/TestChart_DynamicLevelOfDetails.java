package org.jzy3d.chart;

import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.chart.controllers.RateLimiterAdaptsToRenderTime;
import org.jzy3d.chart.controllers.mouse.camera.AdaptiveRenderingPolicy;
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
import org.jzy3d.plot3d.rendering.view.lod.LODCandidates;
import org.jzy3d.plot3d.rendering.view.lod.LODPerf;
import org.jzy3d.plot3d.rendering.view.lod.LODSetting;

public class TestChart_DynamicLevelOfDetails {
  @Test
  public void whenAddWithLODSettings_thenLODScoreIsAvailable() {

    // Given
    ChartFactory factory = new EmulGLChartFactory();
    factory.getPainterFactory().setOffscreen(500, 500);
    Chart chart = factory.newChart();
    
    AdaptiveRenderingPolicy policy = new AdaptiveRenderingPolicy();
    policy.renderingRateLimiter = new RateLimiterAdaptsToRenderTime();
    policy.optimizeForRenderingTimeLargerThan = 80;// ms
    policy.optimizeByPerformanceKnowledge = true;

    EmulGLSkin skin = EmulGLSkin.on(chart);
    skin.getMouse().setPolicy(policy);

    // --------------------------
    // When
    
    Light light = chart.addLightOnCamera();
    Coord3d initialPosition = light.getPosition();
    
    LODCandidates candidates = new LODCandidates();
    chart.add(surface(), candidates);

    // Then light position was updated
    Assert.assertNotEquals(initialPosition, light.getPosition());
    
    // Then perf score are attached to mouse
    LODPerf perf = skin.getMouse().getLODPerf();
    
    Assert.assertEquals(4, candidates.getRank().size());

    for(LODSetting s: perf.getCandidates().getRank()) {
      double value = perf.getScore(s);
      Assert.assertFalse(Double.isNaN(value));
    }
    
    perf.print();
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
