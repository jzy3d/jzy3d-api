package org.jzy3d.tests.emulgl;

import java.io.File;
import java.io.IOException;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.mouse.camera.AWTCameraMouseController;
import org.jzy3d.chart.controllers.thread.camera.CameraThreadController;
import org.jzy3d.chart.factories.EmulGLChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.Quality;

/**
 * TODO : turn to integration test
 */
public class SurfaceDemoEmulGL_Coplanar {

  static final float ALPHA_FACTOR = .50f;

  public static void main(String[] args) {
    EmulGLChartFactory factory = new EmulGLChartFactory();
    Quality q = Quality.Advanced();

    Shape surface = surface();

    // IMPORTANT FOR JGL TO RENDER COPLANAR POLYGON & EDGE WHEN ALPHA
    // DISABLED
    surface.setPolygonWireframeDepthTrick(true);
    q.setAlphaActivated(false);
    //


    Chart chart = factory.newChart(q);
    chart.add(surface);
    chart.getView().setAxisDisplayed(false);
    chart.open();

    // --------------------------------
    CameraThreadController rotation = new CameraThreadController(chart);
    rotation.setStep(0.025f);
    rotation.setUpdateViewDefault(true);

    AWTCameraMouseController mouse = (AWTCameraMouseController) chart.addMouseCameraController();
    mouse.addSlaveThreadController(rotation);
    rotation.setUpdateViewDefault(true);
    mouse.setUpdateViewDefault(false); // keep to false otherwise double rendering

    chart.setAnimated(true);

    try {
      chart.screenshot(
          new File("target/" + SurfaceDemoEmulGL_Coplanar.class.getSimpleName() + ".png"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static Shape surface() {

    // ---------------------------
    // DEFINE SURFACE MATHS
    Mapper mapper = new Mapper() {
      @Override
      public double f(double x, double y) {
        return x * Math.sin(x * y);
      }
    };
    Range range = new Range(-3, 3);
    int steps = 50;

    // ---------------------------
    // CUSTOMIZE SURFACE BUILDER FOR JGL

    SurfaceBuilder builder = new SurfaceBuilder();

    Shape surface = builder.orthonormal(new OrthonormalGrid(range, steps, range, steps), mapper);

    ColorMapper colorMapper = new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(),
        surface.getBounds().getZmax(), new Color(1, 1, 1, ALPHA_FACTOR));// 0.65f));
    surface.setColorMapper(colorMapper);
    surface.setFaceDisplayed(true);
    surface.setWireframeDisplayed(true);
    surface.setWireframeColor(Color.BLACK);
    return surface;
  }

}
