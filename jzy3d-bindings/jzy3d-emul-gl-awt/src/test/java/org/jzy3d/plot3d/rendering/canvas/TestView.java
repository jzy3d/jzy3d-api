package org.jzy3d.plot3d.rendering.canvas;

import org.junit.Test;
import org.jzy3d.chart.AWTChart;
import org.jzy3d.chart.EmulGLSkin;
import org.jzy3d.chart.factories.ChartFactory;
import org.jzy3d.chart.factories.EmulGLChartFactory;
import org.jzy3d.junit.Assert;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.Font;
import org.jzy3d.plot3d.primitives.SampleGeom;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.primitives.axis.layout.fonts.HiDPIProportionalFontSizePolicy;
import org.jzy3d.plot3d.primitives.axis.layout.fonts.HiDPITwoFontSizesPolicy;
import org.jzy3d.plot3d.rendering.legends.colorbars.AWTColorbarLegend;
import org.jzy3d.plot3d.rendering.view.AWTView;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.rendering.view.View2DLayout;
import org.jzy3d.plot3d.rendering.view.modes.ViewPositionMode;

/**
 * Integration test
 */
public class TestView {
  private static final double tolerance = 0.001;


  @Test
  public void whenPixelScaleChange_ThenTextFont_ofAxisAndColorbar_Changes() {
    
    HiDPITwoFontSizesPolicy fontSizePolicyUnderTest;
    
    // Given
    ChartFactory f = new EmulGLChartFactory();
    AWTChart chart = (AWTChart)f.newChart();
    Shape surface = SampleGeom.surface();
    chart.add(surface);
    AWTColorbarLegend legend = chart.colorbar(surface);
    EmulGLSkin skin = EmulGLSkin.on(chart);
    EmulGLCanvas canvas = skin.getCanvas();
    
    Font font_NoHiDPI = new Font("font_NoHiDPI", 10);
    Font font_HiDPI = new Font("font_HiDPI", 20);
    
    // Configure policy that is under test
    fontSizePolicyUnderTest = new HiDPITwoFontSizesPolicy(chart.getView());
    fontSizePolicyUnderTest.setFontHiDPI(font_HiDPI);
    fontSizePolicyUnderTest.setFontNoHiDPI(font_NoHiDPI);
    chart.getAxisLayout().setFontSizePolicy(fontSizePolicyUnderTest);
    
    // -----------------------------------
    // When
    canvas.firePixelScaleChanged(2, 2); // trigger update of axis font size and default font size
    chart.render(); // trigger update of legend font based on default font size

    // Then
    Assert.assertEquals(font_HiDPI, chart.getAxisLayout().getFont());
    Assert.assertEquals(font_HiDPI, legend.getFont());
    Assert.assertEquals(new Coord2d(2,2), legend.getPixelScale());

    // -----------------------------------
    // When
    canvas.firePixelScaleChanged(1, 1); // trigger update of axis font size and default font size
    chart.render(); // trigger update of legend font based on default font size
    
    // Then
    Assert.assertEquals(font_NoHiDPI, chart.getAxisLayout().getFont());
    Assert.assertEquals(font_NoHiDPI, legend.getFont());
    Assert.assertEquals(new Coord2d(1,1), legend.getPixelScale());

  }
  
  @Test
  public void whenPixelScaleChange_ThenLegendPixelScaleChangeWithoutNeedingRepaint() {
    
    // Given
    ChartFactory f = new EmulGLChartFactory();
    AWTChart chart = (AWTChart)f.newChart();
    Shape surface = SampleGeom.surface();
    chart.add(surface);
    AWTColorbarLegend legend = chart.colorbar(surface);
    EmulGLSkin skin = EmulGLSkin.on(chart);
    EmulGLCanvas canvas = skin.getCanvas();
    
    
    // -----------------------------------
    // When
    canvas.firePixelScaleChanged(2, 2); // trigger update of axis font size and default font size

    // Then
    Assert.assertEquals(new Coord2d(2,2), legend.getPixelScale());

    // -----------------------------------
    // When
    canvas.firePixelScaleChanged(1, 1); // trigger update of axis font size and default font size
    
    // Then
    Assert.assertEquals(new Coord2d(1,1), legend.getPixelScale());

  }
  
  @Test
  public void whenPixelScaleChange_ThenFontPolicyChangeSizeProportionnaly() {
    
    HiDPIProportionalFontSizePolicy fontSizePolicyUnderTest;
    
    // Given
    ChartFactory f = new EmulGLChartFactory();
    AWTChart chart = (AWTChart)f.newChart();
    Shape surface = SampleGeom.surface();
    chart.add(surface);
    EmulGLSkin skin = EmulGLSkin.on(chart);
    EmulGLCanvas canvas = skin.getCanvas();
    
    // Configure policy that is under test
    fontSizePolicyUnderTest = new HiDPIProportionalFontSizePolicy(chart.getView());
    chart.getAxisLayout().setFontSizePolicy(fontSizePolicyUnderTest);
    chart.getAxisLayout().setFont(new Font("BaseFont-Size-10", 10));
    
    // -----------------------------------
    // When
    canvas.firePixelScaleChanged(1, 1); // trigger update of axis font size and default font size
    chart.render(); // trigger update of legend font based on default font size

    // Then
    Assert.assertEquals(10, chart.getAxisLayout().getFont().getHeight());

    // -----------------------------------
    // When
    canvas.firePixelScaleChanged(1.5, 1.5); // trigger update of axis font size and default font size
    chart.render(); // trigger update of legend font based on default font size

    // Then
    Assert.assertEquals(15, chart.getAxisLayout().getFont().getHeight());

    // -----------------------------------
    // When
    canvas.firePixelScaleChanged(2, 2); // trigger update of axis font size and default font size
    chart.render(); // trigger update of legend font based on default font size
    
    // Then
    Assert.assertEquals(20, chart.getAxisLayout().getFont().getHeight());
    
    // -----------------------------------
    // When
    canvas.firePixelScaleChanged(1, 1); // trigger update of axis font size and default font size
    chart.render(); // trigger update of legend font based on default font size
    
    // Then
    Assert.assertEquals(10, chart.getAxisLayout().getFont().getHeight());

  }
  
  @Test
  public void whenPixelScaleIsNan_ThenViewKeepsPixelScaleAt1() {
    
    // Given
    ChartFactory f = new EmulGLChartFactory();
    AWTChart chart = (AWTChart)f.newChart();
    EmulGLSkin skin = EmulGLSkin.on(chart);
    EmulGLCanvas canvas = skin.getCanvas();
    AWTView view = chart.getView();

    
    // -----------------------------------
    // When init

    // Then
    Assert.assertEquals(new Coord2d(1,1), view.getPixelScale());
    
    // -----------------------------------
    // When NAN
    canvas.firePixelScaleChanged(Double.NaN, Double.NaN); 

    // Then
    Assert.assertEquals(new Coord2d(1,1), view.getPixelScale());

    // -----------------------------------
    // When NAN partial
    canvas.firePixelScaleChanged(2, Double.NaN); 

    // Then
    Assert.assertEquals(new Coord2d(2,1), view.getPixelScale());
    
    // -----------------------------------
    // When NAN partial
    canvas.firePixelScaleChanged(Double.NaN, 2); 

    // Then
    Assert.assertEquals(new Coord2d(1,2), view.getPixelScale());

    // -----------------------------------
    // When 0
    canvas.firePixelScaleChanged(0, 0); 

    // Then
    Assert.assertEquals(new Coord2d(1,1), view.getPixelScale());

    // -----------------------------------
    // When 0 partial
    canvas.firePixelScaleChanged(0, 2); 

    // Then
    Assert.assertEquals(new Coord2d(1,2), view.getPixelScale());

    // -----------------------------------
    // When 0 partial
    canvas.firePixelScaleChanged(2, 0); 

    // Then
    Assert.assertEquals(new Coord2d(2,1), view.getPixelScale());

  }
  
  
  /*@Test
  public void whenPixelScaleChange_ThenTextFont_ofAxisAndColorbar_Changes() {
    // Given
    ChartFactory f = new EmulGLChartFactory();
    AWTChart chart = (AWTChart)f.newChart();
    Shape surface = surface();
    chart.add(surface);
    
    // -----------------------------------
    // When
    chart.view2d();


  }*/
  
  @Test
  public void whenViewModeChange_ThenCameraSettingAreUpdatedAccordingly() {
    
    
    // Given
    ChartFactory f = new EmulGLChartFactory();
    AWTChart chart = (AWTChart)f.newChart();
    Shape surface = SampleGeom.surface();
    chart.add(surface);

    AWTView view = chart.getView();
    
    //View view = Mocks.View();
    View2DLayout viewLayout = view.get2DLayout();

    // -----------------------------------------------------------
    // When 2D XY
    
    viewLayout.setHorizontalAxisFlip(false);    
    viewLayout.setVerticalAxisFlip(false);    
    view.setViewPositionMode(ViewPositionMode.TOP);
    view.shoot();
    
    // Then
    Assert.assertEquals(new Coord3d(0,1,0), view.getCamera().getUp());
    Assert.assertEquals(View.AZIMUTH_FACING_X_INCREASING, view.getViewPoint().x, tolerance);
    Assert.assertEquals(View.ELEVATION_ON_TOP, view.getViewPoint().y, tolerance);

    
    // When 2D XY, with decreasing X
    viewLayout.setHorizontalAxisFlip(true);    
    viewLayout.setVerticalAxisFlip(false);    
    view.setViewPositionMode(ViewPositionMode.TOP);
    view.shoot();

    // Then
    Assert.assertEquals(new Coord3d(0,1,0), view.getCamera().getUp());
    Assert.assertEquals(View.AZIMUTH_FACING_X_DECREASING, view.getViewPoint().x, tolerance);
    Assert.assertEquals(View.ELEVATION_ON_BOTTOM, view.getViewPoint().y, tolerance);

    // When 2D XY, with decreasing Y
    viewLayout.setHorizontalAxisFlip(false);    
    viewLayout.setVerticalAxisFlip(true); 
    view.setViewPositionMode(ViewPositionMode.TOP);
    view.shoot();

    // Then
    Assert.assertEquals(new Coord3d(0,-1,0), view.getCamera().getUp());
    Assert.assertEquals(View.AZIMUTH_FACING_X_INCREASING, view.getViewPoint().x, tolerance);
    Assert.assertEquals(View.ELEVATION_ON_BOTTOM, view.getViewPoint().y, tolerance);

    // When 2D XY, both decreasing 
    viewLayout.setHorizontalAxisFlip(true);    
    viewLayout.setVerticalAxisFlip(true); 
    view.setViewPositionMode(ViewPositionMode.TOP);
    view.shoot();

    // Then
    Assert.assertEquals(new Coord3d(0,-1,0), view.getCamera().getUp());
    Assert.assertEquals(View.AZIMUTH_FACING_X_INCREASING, view.getViewPoint().x, tolerance);
    Assert.assertEquals(View.ELEVATION_ON_TOP, view.getViewPoint().y, tolerance);

    
    // -----------------------------------------------------------
    // When 2D XZ
    
    viewLayout.setHorizontalAxisFlip(false);    
    viewLayout.setVerticalAxisFlip(false); 
    view.setViewPositionMode(ViewPositionMode.XZ);
    view.shoot();
    
    // Then
    Assert.assertEquals(new Coord3d(0,0,1), view.getCamera().getUp());
    Assert.assertEquals(View.AZIMUTH_FACING_X_INCREASING, view.getViewPoint().x, tolerance);
    Assert.assertEquals(View.ELEVATION_0, view.getViewPoint().y, tolerance);

    // When 2D XZ, X decreasing
    
    viewLayout.setHorizontalAxisFlip(true);    
    viewLayout.setVerticalAxisFlip(false); 
    view.setViewPositionMode(ViewPositionMode.XZ);
    view.shoot();
    
    // Then
    Assert.assertEquals(new Coord3d(0,0,1), view.getCamera().getUp());
    Assert.assertEquals(View.AZIMUTH_FACING_X_DECREASING, view.getViewPoint().x, tolerance);
    Assert.assertEquals(View.ELEVATION_0, view.getViewPoint().y, tolerance);

    // When 2D XZ, Z decreasing
    
    viewLayout.setHorizontalAxisFlip(false);    
    viewLayout.setVerticalAxisFlip(true); 
    view.setViewPositionMode(ViewPositionMode.XZ);
    view.shoot();
    
    // Then
    Assert.assertEquals(new Coord3d(0,0,-1), view.getCamera().getUp());
    Assert.assertEquals(View.AZIMUTH_FACING_X_DECREASING, view.getViewPoint().x, tolerance);
    Assert.assertEquals(View.ELEVATION_0, view.getViewPoint().y, tolerance);

    // When 2D XZ, both decreasing
    
    viewLayout.setHorizontalAxisFlip(true);    
    viewLayout.setVerticalAxisFlip(true); 
    view.setViewPositionMode(ViewPositionMode.XZ);
    view.shoot();
    
    // Then
    Assert.assertEquals(new Coord3d(0,0,-1), view.getCamera().getUp());
    Assert.assertEquals(View.AZIMUTH_FACING_X_INCREASING, view.getViewPoint().x, tolerance);
    Assert.assertEquals(View.ELEVATION_0, view.getViewPoint().y, tolerance);

    // -----------------------------------------------------------
    // When 2D YZ

    viewLayout.setHorizontalAxisFlip(false);    
    viewLayout.setVerticalAxisFlip(false);    

    view.setViewPositionMode(ViewPositionMode.YZ);
    view.shoot();

    // Then
    Assert.assertEquals(new Coord3d(0,0,1), view.getCamera().getUp());
    Assert.assertEquals(View.AZIMUTH_FACING_Y_INCREASING+View.JGL_CORRECT_YZ, view.getViewPoint().x, tolerance);
    Assert.assertEquals(View.ELEVATION_0+View.JGL_CORRECT_YZ, view.getViewPoint().y, tolerance);
    // ! include verification of jGL workaround

    
    // When 2D YZ, Y decreasing

    viewLayout.setHorizontalAxisFlip(true);    
    viewLayout.setVerticalAxisFlip(false);    

    view.setViewPositionMode(ViewPositionMode.YZ);
    view.shoot();

    // Then
    Assert.assertEquals(new Coord3d(0,0,1), view.getCamera().getUp());
    Assert.assertEquals(View.AZIMUTH_FACING_Y_DECREASING+View.JGL_CORRECT_YZ, view.getViewPoint().x, tolerance);
    Assert.assertEquals(View.ELEVATION_0+View.JGL_CORRECT_YZ, view.getViewPoint().y, tolerance);
    // ! include verification of jGL workaround

    // When 2D YZ, Z decreasing

    viewLayout.setHorizontalAxisFlip(false);    
    viewLayout.setVerticalAxisFlip(true);    

    view.setViewPositionMode(ViewPositionMode.YZ);
    view.shoot();

    // Then
    Assert.assertEquals(new Coord3d(0,0,-1), view.getCamera().getUp());
    Assert.assertEquals(View.AZIMUTH_FACING_Y_DECREASING+View.JGL_CORRECT_YZ, view.getViewPoint().x, tolerance);
    Assert.assertAngleEquals(View.ELEVATION_0+View.JGL_CORRECT_YZ, view.getViewPoint().y, tolerance);
    // ! include verification of jGL workaround

    // When 2D YZ, both decreasing

    viewLayout.setHorizontalAxisFlip(true);    
    viewLayout.setVerticalAxisFlip(true);    

    view.setViewPositionMode(ViewPositionMode.YZ);
    view.shoot();

    // Then
    Assert.assertEquals(new Coord3d(0,0,-1), view.getCamera().getUp());
    Assert.assertEquals(View.AZIMUTH_FACING_Y_INCREASING+View.JGL_CORRECT_YZ, view.getViewPoint().x, tolerance);
    Assert.assertAngleEquals(View.ELEVATION_0+View.JGL_CORRECT_YZ, view.getViewPoint().y, tolerance);
    // ! include verification of jGL workaround
    
    
    // -----------------------------------------------------------
    // -----------------------------------------------------------
    // When 3D
    
    view.setViewPositionMode(ViewPositionMode.FREE);
    view.shoot();
    
    // Then
    Assert.assertEquals(new Coord3d(0,0,1), view.getCamera().getUp());

    // -----------------------------------------------------------
    // When 3D on top
    
    view.setViewPositionMode(ViewPositionMode.FREE);
    Coord3d viewpoint = view.getViewPoint().clone();
    viewpoint.y = View.PI_div2;
    view.setViewPoint(viewpoint);
    
    // Then
    Coord2d dir = new Coord2d(viewpoint.x, viewpoint.z).cartesian();
    Assert.assertEquals(new Coord3d(-dir.x,-dir.y,0), view.getCamera().getUp());

    // When 3D on bottom
    view.setViewPositionMode(ViewPositionMode.FREE);
    viewpoint = view.getViewPoint().clone();
    viewpoint.y = -View.PI_div2;
    view.setViewPoint(viewpoint);
    
    // Then
    dir = new Coord2d(viewpoint.x, viewpoint.z).cartesian();
    Assert.assertEquals(new Coord3d(dir.x,dir.y,0), view.getCamera().getUp());

  }
  

}
