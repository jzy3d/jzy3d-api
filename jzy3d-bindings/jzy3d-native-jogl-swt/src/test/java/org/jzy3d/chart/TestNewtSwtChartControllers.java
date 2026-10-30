package org.jzy3d.chart;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.jzy3d.chart.factories.CanvasNewtSWT;
import org.jzy3d.chart.factories.SWTChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Shape;

/**
 * Tests are passing when run from IDE, but maven in console complains with @Before
 * 
 * Hence the test is marked @Ignore, but assertions are actually true.
 * 
 * 
 * org.eclipse.swt.SWTException: Invalid thread access at org.eclipse.swt.SWT.error(Unknown Source)
 * at org.eclipse.swt.SWT.error(Unknown Source) at org.eclipse.swt.SWT.error(Unknown Source) at
 * org.eclipse.swt.widgets.Display.error(Unknown Source) at
 * org.eclipse.swt.widgets.Display.createDisplay(Unknown Source) at
 * org.eclipse.swt.widgets.Display.create(Unknown Source) at
 * org.eclipse.swt.graphics.Device.<init>(Unknown Source) at
 * org.eclipse.swt.widgets.Display.<init>(Unknown Source) at
 * org.eclipse.swt.widgets.Display.<init>(Unknown Source) at
 * org.jzy3d.chart.TestNewtSwtChartControllers.before(TestNewtSwtChartControllers.java:27)
 * 
 * @author martin
 *
 */
@Ignore
public class TestNewtSwtChartControllers {
  Display display;
  Shell shell;

  @Before
  public void before() {
    display = new Display();
    shell = new Shell(display);
    shell.setLayout(new FillLayout());
  }

  @After
  public void after() {
    display.dispose();
  }

  @Test
  public void givenNewtSwtChart_whenAddMouseCameraController_ThenCanvasHasMouseListener() {
    // Given
    Chart chart = new SWTChartFactory(shell).newChart();
    chart.add(surface());
    CanvasNewtSWT canvas = (CanvasNewtSWT) chart.getCanvas();

    // Then
    Assert.assertEquals(0, canvas.getWindow().getMouseListeners().length);

    // When
    chart.addMouseCameraController();

    // Then
    Assert.assertEquals(1, canvas.getWindow().getMouseListeners().length);

  }

  @Test
  public void givenNewtSwtChart_whenAddMousePickingController_ThenCanvasHasMouseListener() {
    // Given
    Chart chart = new SWTChartFactory(shell).newChart();
    chart.add(surface());
    CanvasNewtSWT canvas = (CanvasNewtSWT) chart.getCanvas();

    // Then
    Assert.assertEquals(0, canvas.getWindow().getMouseListeners().length);

    // When
    chart.addMousePickingController(10);

    // Then
    Assert.assertEquals(1, canvas.getWindow().getMouseListeners().length);
  }

  @Test
  public void givenNewtSwtChart_whenAddKeyboardCameraController_ThenCanvasHasKeyboardListener() {
    // Given
    Chart chart = new SWTChartFactory(shell).newChart();
    chart.add(surface());
    CanvasNewtSWT canvas = (CanvasNewtSWT) chart.getCanvas();

    // Then
    Assert.assertEquals(0, canvas.getWindow().getKeyListeners().length);

    // When
    chart.addKeyboardCameraController();

    // Then
    Assert.assertEquals(1, canvas.getWindow().getKeyListeners().length);
  }

  @Test
  public void givenNewtSwtChart_whenAddKeyboardScreenshotController_ThenCanvasHasKeyboardListener() {
    // Given
    Chart chart = new SWTChartFactory(shell).newChart();
    chart.add(surface());
    CanvasNewtSWT canvas = (CanvasNewtSWT) chart.getCanvas();

    // Then
    Assert.assertEquals(0, canvas.getWindow().getKeyListeners().length);

    // When
    chart.addKeyboardScreenshotController();

    // Then
    Assert.assertEquals(1, canvas.getWindow().getKeyListeners().length);
  }



  private Shape surface() {
    Mapper mapper = new Mapper() {
      @Override
      public double f(double x, double y) {
        return x * Math.sin(x * y);
      }
    };

    Range range = new Range(-3, 3);
    int steps = 80;

    final Shape surface =
        new SurfaceBuilder().orthonormal(new OrthonormalGrid(range, steps, range, steps), mapper);
    surface.setColorMapper(new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(),
        surface.getBounds().getZmax(), new Color(1, 1, 1, .5f)));
    surface.setFaceDisplayed(true);
    surface.setWireframeDisplayed(false);
    return surface;
  }

}
