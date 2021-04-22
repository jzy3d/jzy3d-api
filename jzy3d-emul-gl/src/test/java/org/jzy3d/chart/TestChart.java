package org.jzy3d.chart;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.jzy3d.chart.controllers.keyboard.camera.ICameraKeyController;
import org.jzy3d.chart.controllers.mouse.camera.AWTCameraMouseController;
import org.jzy3d.chart.controllers.mouse.camera.ICameraMouseController;
import org.jzy3d.chart.factories.ChartFactory;
import org.jzy3d.chart.factories.EmulGLChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Range;
import org.jzy3d.painters.IPainter.Font;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.primitives.axis.AxisBox;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.modes.ViewBoundMode;
import org.jzy3d.plot3d.text.renderers.TextBitmapRenderer;

public class TestChart {
  /**
   * Ensure mouse is automatically configured to be suitable with repaint on demand or continuous
   * repaint mode.
   */
  @Test
  public void whenChart_IS_Animated_ThenControllers_DO_NOT_UpdateViewUponRotation() {
    Quality q = Quality.Advanced();

    // When
    Assert.assertTrue(q.isAnimated());
    ChartFactory factory = new EmulGLChartFactory();
    Chart chart = factory.newChart(q);

    // Then
    Assert.assertTrue("Check chart is animated", chart.getQuality().isAnimated());

    // When
    ICameraMouseController mouse = chart.addMouseCameraController();
    // Then
    Assert.assertFalse(mouse.isUpdateViewDefault());
    Assert.assertFalse(mouse.getThread().isUpdateViewDefault());


    // When
    ICameraKeyController key = chart.addKeyboardCameraController();
    // Then
    Assert.assertFalse(key.isUpdateViewDefault());

  }

  /**
   * Ensure mouse is automatically configured to be suitable with repaint on demand or continuous
   * repaint mode.
   */
  @Test
  public void whenChart_ISNOT_Animated_ThenControllers_DO_UpdateViewUponRotation() {
    Quality q = Quality.Advanced();

    // When
    q.setAnimated(false);
    Assert.assertFalse(q.isAnimated());
    ChartFactory factory = new EmulGLChartFactory();
    Chart chart = factory.newChart(q);

    // Then
    Assert.assertFalse("Check chart is NOT animated", chart.getQuality().isAnimated());

    // When
    ICameraMouseController mouse = chart.addMouseCameraController();
    // Then
    Assert.assertTrue(mouse.isUpdateViewDefault());
    Assert.assertTrue(mouse.getThread().isUpdateViewDefault());

    // When
    ICameraKeyController key = chart.addKeyboardCameraController();
    // Then
    Assert.assertTrue(key.isUpdateViewDefault());

  }
  
  @Test
  public void whenChartAnimation_CHANGE_ThenControllersConfiguration_CHANGE() {
    Quality q = Quality.Advanced();

    // When non animated chart
    q.setAnimated(false);
    Assert.assertFalse(q.isAnimated());
    ChartFactory factory = new EmulGLChartFactory();
    Chart chart = factory.newChart(q);

    ICameraMouseController mouse = chart.addMouseCameraController();
    ICameraKeyController key = chart.addKeyboardCameraController();

    // Then animated controllers
    Assert.assertFalse("Check chart is NOT animated", chart.getQuality().isAnimated());
    Assert.assertTrue(key.isUpdateViewDefault());
    Assert.assertTrue(mouse.isUpdateViewDefault());
    Assert.assertTrue(mouse.getThread().isUpdateViewDefault());

    // When change 
    chart.setAnimated(true);

    // Then non animated controllers
    Assert.assertTrue("Check chart IS animated", chart.getQuality().isAnimated());
    Assert.assertFalse(key.isUpdateViewDefault());
    Assert.assertFalse(mouse.isUpdateViewDefault());
    Assert.assertFalse(mouse.getThread().isUpdateViewDefault());
  }

  @Ignore
  @Test
  public void whenChartAxisLayoutHasCustomFont_ThenAxisBoxHasThisFont() {
    Chart chart = new EmulGLChartFactory().newChart();
    chart.getAxisLayout().setFont(Font.TimesRoman_24);

    AxisBox axis = (AxisBox) chart.getView().getAxis();

    TextBitmapRenderer tbr = (TextBitmapRenderer) axis.getTextRenderer();
    Assert.assertEquals(Font.TimesRoman_24, tbr.getFont());
  }

  @Test
  public void givenEmulatedChart_whenAddSurface_thenViewIsInAutotFitMode() {

    // Given
    ChartFactory factory = new EmulGLChartFactory();
    factory.getPainterFactory().setOffscreen(500, 500);
    Chart chart = factory.newChart();

    // --------------------------
    // When

    chart.add(surface());

    // --------------------------
    // Then

    Assert.assertEquals(ViewBoundMode.AUTO_FIT, chart.getView().getBoundsMode());
    Assert.assertFalse("Near clipping plane != 0", 0 == chart.getView().getCamera().getNear());
    Assert.assertFalse("Far clipping plane != 0", 0 == chart.getView().getCamera().getFar());
  }

  @Test
  public void givenEmulatedChart_whenAddMouseController_thenViewIsController() {

    // Given
    ChartFactory factory = new EmulGLChartFactory();
    factory.getPainterFactory().setOffscreen(500, 500);
    Chart chart = factory.newChart();
    chart.add(surface());

    // --------------------------
    // When

    AWTCameraMouseController mouse = (AWTCameraMouseController) chart.addMouseCameraController();

    mouse.setUpdateViewDefault(true);

    // --------------------------
    // Then

    Assert.assertNotNull("Mouse is defined", mouse);
    Assert.assertEquals("Mouse has its owner chart as target", chart, mouse.getChart());
    Assert.assertTrue("Mouse will refresh view upon action", mouse.isUpdateViewDefault());

    // when mouse drag, viewpoint change
    // when viewpoint change, clear picture is invoked

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
