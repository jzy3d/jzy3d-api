package org.jzy3d.chart.factories;

import org.jzy3d.chart.AWTChart;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.thread.camera.CameraThreadControllerWithTime;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.AWTView;
import org.jzy3d.plot3d.rendering.view.modes.ViewBoundMode;

public class EmulGLChartFactory extends ChartFactory {
  public EmulGLChartFactory() {
    super(new EmulGLPainterFactory());
  }

  public EmulGLChartFactory(IPainterFactory windowFactory) {
    super(windowFactory);
  }

  @Override
  public Chart newChart(IChartFactory factory, Quality quality) {
    AWTChart chart = new AWTChart(factory, quality);
    chart.getView().setBoundMode(ViewBoundMode.AUTO_FIT); // EMULGL NEEDS AUTO_FIT!!!
    return chart;
  }

  @Override
  public AWTView newView(IChartFactory factory, Scene scene, ICanvas canvas, Quality quality) {
    return new AWTView(factory, scene, canvas, quality);
  }
  
  /**
   * Returns a {@link CameraThreadControllerWithTime} which speed may be tuned.
   */
  @Override
  public CameraThreadControllerWithTime newCameraThreadController(Chart chart) {
    CameraThreadControllerWithTime controller = new CameraThreadControllerWithTime(chart, 60);
    return controller;
  }

}
