package org.jzy3d.emulgl.unit;

import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import java.awt.event.ComponentEvent;
import org.junit.Test;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.mouse.camera.AWTCameraMouseController;
import org.jzy3d.chart.controllers.thread.camera.CameraThreadController;
import org.jzy3d.chart.factories.EmulGLChartFactory;
import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.EmulGLCanvas;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.AWTView;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.mockito.Mockito;

/**
 * Warning : execution is slow when using mocks
 * 
 * @author martin
 */
public class TestContinuousAndOnDemandRendering {
  @Test
  public void whenComponentResizeWithoutAnimator_thenViewRender() {
    // LoggerUtils.minimal();

    // ---------------------
    // JZY3D CONTENT
    // EmulGLChartFactory factory = new SpyEmulGLChartFactory();

    EmulGLChartFactory factory = new EmulGLChartFactory() {
      @Override
      public AWTView newView(IChartFactory factory, Scene scene, ICanvas canvas, Quality quality) {
        AWTView view = Mockito.spy((AWTView) super.newView(factory, scene, canvas, quality));
        view.initInstance(factory, scene, canvas, quality);
        return view;

      }

      @Override
      public Camera newCamera(Coord3d center) {
        Camera camera = Mockito.spy((Camera) super.newCamera(center));
        return camera;
      }

    };

    Quality q = Quality.Nicest();
    q.setAlphaActivated(true);

    Chart chart = factory.newChart(q);
    chart.add(surface());

    CameraThreadController rotation = new CameraThreadController(chart);
    rotation.setStep(0.005f);
    rotation.setUpdateViewDefault(true);

    AWTCameraMouseController mouse = (AWTCameraMouseController) chart.addMouseCameraController();
    mouse.setUpdateViewDefault(true);
    mouse.addSlaveThreadController(rotation);

    // -----------------------------------
    // When Trigger canvas
    EmulGLCanvas canvas = (EmulGLCanvas) chart.getCanvas();

    /// needed 7-10 sec up to there

    // this does not change anything
    ComponentEvent event = new ComponentEvent(canvas, ComponentEvent.COMPONENT_RESIZED);
    if (false) {
      canvas.processEvent(event); // 2.5s
      canvas.processEvent(event); // 2.5s
      canvas.doRender(); // 10
      canvas.doRender(); // 10*/
      // canvas.doDisplay(); // 10
    }

    // this change test result
    if (false) {
      chart.getView().shoot();
      chart.getView().shoot();
    }

    // -----------------------------------
    // Then view was called
    verify(chart.getView(), atLeast(1)).initInstance(factory, chart.getScene(), canvas,
        chart.getQuality());
    verify(chart.getView(), atLeast(1)).shoot();

    // undesired
    verify(chart.getView(), atLeast(1)).shoot(); // VIEW IS CALLED 2 OR 3 TIMES !!!!!!!!!!!!!!!!!!!!!!

    // Then camera was called at least once
    verify(chart.getView().getCamera(), atLeast(1)).shoot(chart.getPainter(),
        chart.getView().getCameraMode());

    /*
     * try { chart.screenshot(new
     * File("target/whenComponentResizeWithoutAnimator_thenViewRender.png")); } catch (IOException
     * e) { e.printStackTrace(); }
     */
  }

  // @Test
  public void whenMouseControlWithoutAnimator_thenViewRender() {}

  private static Shape surface() {
    Mapper mapper = new Mapper() {
      @Override
      public double f(double x, double y) {
        return x * Math.sin(x * y);
      }
    };
    Range range = new Range(-3, 3);
    int steps = 60;

    SurfaceBuilder builder = new SurfaceBuilder();

    Shape surface = builder.orthonormal(new OrthonormalGrid(range, steps, range, steps), mapper);
    // surface.setPolygonOffsetFillEnable(false);

    ColorMapper colorMapper = new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(),
        surface.getBounds().getZmax(), new Color(1, 1, 1, 0.650f));
    surface.setColorMapper(colorMapper);
    surface.setFaceDisplayed(true);
    surface.setWireframeDisplayed(true);
    surface.setWireframeColor(Color.BLACK);
    return surface;
  }

}
