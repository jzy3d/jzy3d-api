package org.jzy3d.chart.factories;

import org.jzy3d.chart.AWTChart;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.thread.camera.CameraThreadControllerWithTime;
import org.jzy3d.painters.EmulGLPainter;
import org.jzy3d.plot3d.rendering.canvas.EmulGLCanvas;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.AWTView;
import org.jzy3d.plot3d.rendering.view.modes.ViewBoundMode;
import jgl.wt.awt.GL;

public class EmulGLChartFactory extends ChartFactory {
  public EmulGLChartFactory() {
    super(new EmulGLPainterFactory());
  }

  public EmulGLChartFactory(IPainterFactory windowFactory) {
    super(windowFactory);
  }

  @Override
  public AWTChart newChart() {
    return newChart(Quality.Advanced());
  }

  @Override
  public AWTChart newChart(Quality quality) {
    return newChart(getFactory(), quality);
  }
  
  @Override
  public AWTChart newChart(IChartFactory factory, Quality quality) {
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
  
  /**
   * This return a factory with a custom GL instance which can be used for manual mocking
   * @return
   */
  public static EmulGLChartFactory forGL(GL glMock) {

    // Given a surface chart with a mock GL injected for spying calls to glDepthRange
    EmulGLPainterFactory painterF = new EmulGLPainterFactory() {
      protected EmulGLCanvas newEmulGLCanvas(IChartFactory factory, Scene scene, Quality quality) {
        EmulGLCanvas c = new EmulGLCanvas(factory, scene, quality);
        c.setGL(glMock);
        return c;
      }
    };
    
    EmulGLChartFactory factory = new EmulGLChartFactory(painterF);
    Chart chart = factory.newChart(Quality.Advanced());
    EmulGLPainter painter = (EmulGLPainter)chart.getPainter();
    painter.setGL(glMock); // << spy
    
    return factory;
  }

}
