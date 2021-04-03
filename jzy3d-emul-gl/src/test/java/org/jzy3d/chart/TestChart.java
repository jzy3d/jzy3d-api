package org.jzy3d.chart;

import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.chart.controllers.mouse.camera.ICameraMouseController;
import org.jzy3d.chart.factories.ChartFactory;
import org.jzy3d.chart.factories.EmulGLChartFactory;
import org.jzy3d.plot3d.rendering.canvas.Quality;

public class TestChart {
  /**
   * Ensure mouse is automatically configured to be suitable with repaint on demand or continuous repaint mode.
   */
  @Test
  public void whenChart_IS_Animated_ThenMouse_ISNOT_UpdatingViewUponRotation() {
    Quality q = Quality.Advanced.clone();
    
    Assert.assertTrue(q.isAnimated());
    
    ChartFactory factory = new EmulGLChartFactory();
    Chart chart = factory.newChart(q);
    
    Assert.assertTrue(chart.getQuality().isAnimated());
    
    chart.render();

    ICameraMouseController mouse = chart.addMouseCameraController();
    
    Assert.assertFalse(mouse.isUpdateViewDefault());
  }
  
  /**
   * Ensure mouse is automatically configured to be suitable with repaint on demand or continuous repaint mode.
   */
  @Test
  public void whenChart_ISNOT_Animated_ThenMouse_IS_UpdatingViewUponRotation() {
    Quality q = Quality.Advanced.clone();
    q.setAnimated(false);

    Assert.assertFalse(q.isAnimated());
    
    ChartFactory factory = new EmulGLChartFactory();
    Chart chart = factory.newChart(q);
    
    Assert.assertFalse(chart.getQuality().isAnimated());
    
    ICameraMouseController mouse = chart.addMouseCameraController();
    
    Assert.assertTrue(mouse.isUpdateViewDefault());
  }

}
