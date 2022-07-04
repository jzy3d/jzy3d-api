package org.jzy3d.chart;

import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.chart.controllers.keyboard.camera.ICameraKeyController;
import org.jzy3d.chart.controllers.mouse.camera.AWTCameraMouseController;
import org.jzy3d.chart.controllers.mouse.camera.ICameraMouseController;
import org.jzy3d.chart.factories.ChartFactory;
import org.jzy3d.chart.factories.EmulGLChartFactory;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.SampleGeom;
import org.jzy3d.plot3d.primitives.axis.layout.AxisLayout;
import org.jzy3d.plot3d.primitives.axis.layout.LabelOrientation;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.lights.Light;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.rendering.view.View2D;
import org.jzy3d.plot3d.rendering.view.ViewportMode;
import org.jzy3d.plot3d.rendering.view.modes.ViewBoundMode;
import org.jzy3d.plot3d.rendering.view.modes.ViewPositionMode;

public class TestChart {
  /**
   * Ensure mouse is automatically configured to be suitable with repaint on demand or continuous
   * repaint mode.
   */
  @Test
  public void whenChart_IS_Animated_ThenControllers_DO_NOT_UpdateViewUponRotation() {
    Quality q = Quality.Advanced();
    q.setAnimated(true);

    // When
    Assert.assertTrue(q.isAnimated());
    ChartFactory factory = new EmulGLChartFactory();
    Chart chart = factory.newChart(q);

    // Then
    Assert.assertTrue("Check chart is animated", chart.getQuality().isAnimated());

    // When
    ICameraMouseController mouse = chart.addMouse();
    // Then
    Assert.assertFalse(mouse.isUpdateViewDefault());
    Assert.assertFalse(mouse.getThread().isUpdateViewDefault());


    // When
    ICameraKeyController key = chart.addKeyboard();
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
    q.setAnimated(false);

    // When
    Assert.assertFalse(q.isAnimated());
    ChartFactory factory = new EmulGLChartFactory();
    Chart chart = factory.newChart(q);

    // Then
    Assert.assertFalse("Check chart is NOT animated", chart.getQuality().isAnimated());

    // When
    ICameraMouseController mouse = chart.addMouse();
    // Then
    Assert.assertTrue(mouse.isUpdateViewDefault());
    Assert.assertTrue(mouse.getThread().isUpdateViewDefault());

    // When
    ICameraKeyController key = chart.addKeyboard();
    // Then
    Assert.assertTrue(key.isUpdateViewDefault());

  }
  
  @Test
  public void whenChartAnimation_CHANGE_ThenControllersConfiguration_CHANGE() {
    Quality q = Quality.Advanced();
    q.setAnimated(false);

    // When non animated chart
    Assert.assertFalse(q.isAnimated());
    ChartFactory factory = new EmulGLChartFactory();
    Chart chart = factory.newChart(q);

    ICameraMouseController mouse = chart.addMouse();
    ICameraKeyController key = chart.addKeyboard();

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

  @Test
  public void givenEmulatedChart_whenAddSurface_thenViewIsInAutotFitMode() {

    // Given
    ChartFactory factory = new EmulGLChartFactory();
    factory.getPainterFactory().setOffscreen(500, 500);
    Chart chart = factory.newChart();

    // --------------------------
    // When

    chart.add(SampleGeom.surface());

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
    chart.add(SampleGeom.surface());

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
  
  @Test
  public void whenViewpointChange_thenCameraLightMovesAtSamePositionThanCameraEye() {
    // Given
    ChartFactory f = new EmulGLChartFactory();
    Chart c = f.newChart();
    c.add(SampleGeom.surface()); // need an object to have camera eye != {0,0,0}
    Light light = c.addLightOnCamera();
    
    Coord3d eye1 = c.getView().getCamera().getEye();
    
    Assert.assertEquals(eye1, light.getPosition());

    // When change polar viewpoint 
    c.setViewPoint(View.VIEWPOINT_AXIS_CORNER_TOUCH_BORDER);
    
    // --------------------------
    // Then camera cartesian eye position has changed
    Coord3d eye2 = c.getView().getCamera().getEye();
    Assert.assertNotEquals(eye2, eye1);
    
    // --------------------------
    // Then light cartesian position has changed to the same coordinate
    Assert.assertEquals(eye2, light.getPosition());
  }

  @Test
  public void whenToggling_3Dto_2D_XY_to3D_thenAxisSettingsAreRestored() {
    
    // Given
    ChartFactory f = new EmulGLChartFactory();
    Chart c = f.newChart();
    c.add(SampleGeom.surface());
    c.render();

    // ---------------------------
    // When
    AxisLayout axisLayout = c.getAxisLayout();
    axisLayout.setXAxisLabelOrientation(LabelOrientation.HORIZONTAL);
    axisLayout.setYAxisLabelOrientation(LabelOrientation.HORIZONTAL);
    axisLayout.setZAxisLabelOrientation(LabelOrientation.VERTICAL);
    
    View view = c.getView();
    
    // Then
    Assert.assertEquals(LabelOrientation.HORIZONTAL, axisLayout.getXAxisLabelOrientation());
    Assert.assertEquals(LabelOrientation.HORIZONTAL, axisLayout.getYAxisLabelOrientation());
    Assert.assertEquals(LabelOrientation.VERTICAL, axisLayout.getZAxisLabelOrientation());
    Assert.assertEquals(true, axisLayout.isTickLineDisplayed());
    Assert.assertEquals(true, axisLayout.isZAxisLabelDisplayed());
    Assert.assertEquals(true, axisLayout.isZTickLabelDisplayed());

    
    Assert.assertEquals(ViewPositionMode.FREE, view.getViewMode());
    Assert.assertEquals(View.VIEWPOINT_DEFAULT.getXY(), view.getViewPoint().getXY());
    Assert.assertEquals(ViewportMode.RECTANGLE_NO_STRETCH, view.getCamera().getViewportMode());
    
    Assert.assertTrue(view.getSquared());
    
    // ---------------------------
    // When Switch to 2D
    
    c.view2d();
    
    // Then
    Assert.assertEquals(LabelOrientation.HORIZONTAL, axisLayout.getXAxisLabelOrientation());
    Assert.assertEquals(LabelOrientation.VERTICAL, axisLayout.getYAxisLabelOrientation());
    Assert.assertEquals(false, axisLayout.isTickLineDisplayed());

    Assert.assertTrue(view.is2D());
    Assert.assertTrue(view.is2D_XY());
    Assert.assertEquals(ViewPositionMode.TOP, view.getViewMode());
    Assert.assertNotEquals(View.VIEWPOINT_DEFAULT.getXY(), view.getViewPoint().getXY());
    Assert.assertEquals(ViewportMode.STRETCH_TO_FILL, view.getCamera().getViewportMode());

    Assert.assertFalse("View should not be squared for accurate 2D layout", view.getSquared());

    
    // ---------------------------
    // When Switch back to 3D
    
    c.view3d();
    
    // Then
    Assert.assertEquals(LabelOrientation.HORIZONTAL, axisLayout.getXAxisLabelOrientation());
    Assert.assertEquals(LabelOrientation.HORIZONTAL, axisLayout.getYAxisLabelOrientation());
    Assert.assertEquals(LabelOrientation.VERTICAL, axisLayout.getZAxisLabelOrientation());
    Assert.assertEquals(true, axisLayout.isTickLineDisplayed());
    Assert.assertEquals(true, axisLayout.isZAxisLabelDisplayed());
    Assert.assertEquals(true, axisLayout.isZTickLabelDisplayed());


    // azimuth & elevation restored / should % with PI for a polar viewpoint
    //Assert.assertEquals(View.VIEWPOINT_DEFAULT.getXY(), view.getViewPoint().getXY());

    Assert.assertFalse(view.is2D());
    Assert.assertTrue(view.is3D());

    Assert.assertEquals(ViewPositionMode.FREE, view.getViewMode());
    Assert.assertEquals(ViewportMode.RECTANGLE_NO_STRETCH, view.getCamera().getViewportMode());

    Assert.assertTrue(view.getSquared());
    
    
  }
  
  @Test
  public void whenToggling_3Dto_2D_XZ_to3D_thenAxisSettingsAreRestored() {
    
    // Given
    ChartFactory f = new EmulGLChartFactory();
    Chart c = f.newChart();
    c.add(SampleGeom.surface());
    c.render();

    // ---------------------------
    // When
    AxisLayout axisLayout = c.getAxisLayout();
    axisLayout.setXAxisLabelOrientation(LabelOrientation.HORIZONTAL);
    axisLayout.setYAxisLabelOrientation(LabelOrientation.HORIZONTAL);
    axisLayout.setZAxisLabelOrientation(LabelOrientation.VERTICAL);
    
    View view = c.getView();
    
    // Then
    Assert.assertEquals(LabelOrientation.HORIZONTAL, axisLayout.getXAxisLabelOrientation());
    Assert.assertEquals(LabelOrientation.HORIZONTAL, axisLayout.getYAxisLabelOrientation());
    Assert.assertEquals(LabelOrientation.VERTICAL, axisLayout.getZAxisLabelOrientation());
    Assert.assertEquals(true, axisLayout.isTickLineDisplayed());

    
    Assert.assertEquals(ViewPositionMode.FREE, view.getViewMode());
    Assert.assertEquals(View.VIEWPOINT_DEFAULT.getXY(), view.getViewPoint().getXY());
    Assert.assertEquals(ViewportMode.RECTANGLE_NO_STRETCH, view.getCamera().getViewportMode());
    
    Assert.assertTrue(view.getSquared());
    
    // ---------------------------
    // When Switch to 2D
    
    c.view2d(View2D.XZ);
    
    // Then
    Assert.assertEquals(LabelOrientation.HORIZONTAL, axisLayout.getXAxisLabelOrientation());
    Assert.assertEquals(LabelOrientation.VERTICAL, axisLayout.getZAxisLabelOrientation());
    Assert.assertEquals(false, axisLayout.isTickLineDisplayed());

    Assert.assertTrue(view.is2D());
    Assert.assertTrue(view.is2D_XZ());
    Assert.assertEquals(ViewPositionMode.XZ, view.getViewMode());
    Assert.assertNotEquals(View.VIEWPOINT_DEFAULT.getXY(), view.getViewPoint().getXY());
    Assert.assertEquals(ViewportMode.STRETCH_TO_FILL, view.getCamera().getViewportMode());

    Assert.assertFalse("View should not be squared for accurate 2D layout", view.getSquared());

    
    // ---------------------------
    // When Switch back to 3D
    
    c.view3d();
    
    // Then
    Assert.assertEquals(LabelOrientation.HORIZONTAL, axisLayout.getXAxisLabelOrientation());
    Assert.assertEquals(LabelOrientation.HORIZONTAL, axisLayout.getYAxisLabelOrientation());
    Assert.assertEquals(LabelOrientation.VERTICAL, axisLayout.getZAxisLabelOrientation());
    Assert.assertEquals(true, axisLayout.isTickLineDisplayed());


    // azimuth & elevation restored / should % with PI for a polar viewpoint
    //Assert.assertEquals(View.VIEWPOINT_DEFAULT.getXY(), view.getViewPoint().getXY());

    Assert.assertFalse(view.is2D());
    Assert.assertTrue(view.is3D());
    Assert.assertEquals(ViewPositionMode.FREE, view.getViewMode());
    Assert.assertEquals(ViewportMode.RECTANGLE_NO_STRETCH, view.getCamera().getViewportMode());

    Assert.assertTrue(view.getSquared());
    
    
  }
  
  @Test
  public void whenToggling_3Dto_2D_YZ_to3D_thenAxisSettingsAreRestored() {
    
    // Given
    ChartFactory f = new EmulGLChartFactory();
    Chart c = f.newChart();
    c.add(SampleGeom.surface());
    c.render();

    // ---------------------------
    // When
    AxisLayout axisLayout = c.getAxisLayout();
    axisLayout.setXAxisLabelOrientation(LabelOrientation.HORIZONTAL);
    axisLayout.setYAxisLabelOrientation(LabelOrientation.HORIZONTAL);
    axisLayout.setZAxisLabelOrientation(LabelOrientation.VERTICAL);
    
    View view = c.getView();
    
    // Then
    Assert.assertEquals(LabelOrientation.HORIZONTAL, axisLayout.getXAxisLabelOrientation());
    Assert.assertEquals(LabelOrientation.HORIZONTAL, axisLayout.getYAxisLabelOrientation());
    Assert.assertEquals(LabelOrientation.VERTICAL, axisLayout.getZAxisLabelOrientation());
    Assert.assertEquals(true, axisLayout.isTickLineDisplayed());

    
    Assert.assertEquals(ViewPositionMode.FREE, view.getViewMode());
    Assert.assertEquals(View.VIEWPOINT_DEFAULT.getXY(), view.getViewPoint().getXY());
    Assert.assertEquals(ViewportMode.RECTANGLE_NO_STRETCH, view.getCamera().getViewportMode());
    
    Assert.assertTrue(view.getSquared());
    
    // ---------------------------
    // When Switch to 2D
    
    c.view2d(View2D.YZ);
    
    // Then
    Assert.assertEquals(LabelOrientation.HORIZONTAL, axisLayout.getXAxisLabelOrientation());
    Assert.assertEquals(LabelOrientation.VERTICAL, axisLayout.getZAxisLabelOrientation());
    Assert.assertEquals(false, axisLayout.isTickLineDisplayed());

    Assert.assertTrue(view.is2D());
    Assert.assertTrue(view.is2D_YZ());
    Assert.assertEquals(ViewPositionMode.YZ, view.getViewMode());
    Assert.assertNotEquals(View.VIEWPOINT_DEFAULT.getXY(), view.getViewPoint().getXY());
    Assert.assertEquals(ViewportMode.STRETCH_TO_FILL, view.getCamera().getViewportMode());

    Assert.assertFalse("View should not be squared for accurate 2D layout", view.getSquared());

    
    // ---------------------------
    // When Switch back to 3D
    
    c.view3d();
    
    // Then
    Assert.assertEquals(LabelOrientation.HORIZONTAL, axisLayout.getXAxisLabelOrientation());
    Assert.assertEquals(LabelOrientation.HORIZONTAL, axisLayout.getYAxisLabelOrientation());
    Assert.assertEquals(LabelOrientation.VERTICAL, axisLayout.getZAxisLabelOrientation());
    Assert.assertEquals(true, axisLayout.isTickLineDisplayed());


    // azimuth & elevation restored / should % with PI for a polar viewpoint
    //Assert.assertEquals(View.VIEWPOINT_DEFAULT.getXY(), view.getViewPoint().getXY());

    Assert.assertFalse(view.is2D());
    Assert.assertTrue(view.is3D());
    Assert.assertEquals(ViewPositionMode.FREE, view.getViewMode());
    Assert.assertEquals(ViewportMode.RECTANGLE_NO_STRETCH, view.getCamera().getViewportMode());

    Assert.assertTrue(view.getSquared());
    
    
  }
}
