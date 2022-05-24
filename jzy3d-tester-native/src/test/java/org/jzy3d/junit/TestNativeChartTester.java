package org.jzy3d.junit;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Shape;

/**
 * Testing the test tool!
 * 
 * @author martin
 *
 */
public class TestNativeChartTester {
  ChartTester test;

  @Before
  public void before() {
    test = new NativeChartTester();
  }

  @Test
  public void whenCompareImageWithItself_ThenNoTestFailureIsThrown() throws IOException {
    BufferedImage bi = test.loadBufferedImage("src/test/resources/testimage.png");
    try {
      test.compare(bi, bi);
    } catch (ChartTestFailed e) {
      Assert.fail(e.getMessage());
    }
    Assert.assertTrue(true);
  }

  @Test
  public void whenCompareImageWithADifferentOne_ThenATestFailureIsThrown() throws IOException {
    BufferedImage bi1 = test.loadBufferedImage("src/test/resources/testimage.png");
    BufferedImage bi2 = test.loadBufferedImage("src/test/resources/testimage2.png");
    try {
      test.compare(bi1, bi2);
    } catch (ChartTestFailed e) {
      Assert.assertTrue(e.getMessage(), true);
      return;
    }
    Assert.fail("two different image should throw an exception");
  }


  @Test
  public void whenCompareChartWithItsScreenshot_ThenNoTestFailureIsThrown() throws IOException {
    // Given
    String screenshotFilename = "target/" + TestNativeChartTester.class.getSimpleName() + ".png";

    AWTChartFactory f = new AWTChartFactory();
    f.getPainterFactory().setOffscreen(600, 600);
    Chart chart = f.newChart().add(surface());

    // When
    chart.screenshot(new File(screenshotFilename));

    // Then chart is similar to its own screenshot
    test.assertSimilar(chart, screenshotFilename);
  }

  @Test
  public void whenCompareChartWithItsScreenshotAtDifferentViewpoint_ThenTestFailureIsThrown()
      throws IOException {
    // Given
    String screenshotFilename = "target/" + TestNativeChartTester.class.getSimpleName() + ".png";

    AWTChartFactory f = new AWTChartFactory();
    f.getPainterFactory().setOffscreen(600, 600);
    Chart chart = f.newChart().add(surface());

    // When
    chart.screenshot(new File(screenshotFilename));
    chart.getView().rotate(new Coord2d(Math.PI, 0)); // make a change to chart

    // Then chart is different from its previous screenshot

    try {
      test.compare(chart, screenshotFilename);
    } catch (ChartTestFailed e) {
      Assert.assertTrue(e.getMessage(), true);
      return;
    }
    Assert.fail("Expected a ChartTestFailed");
  }

  protected Shape surface() {
    // Define a function to plot
    Mapper mapper = new Mapper() {
      @Override
      public double f(double x, double y) {
        return x * Math.sin(x * y);
      }
    };

    // Define range and precision for the function to plot
    Range range = new Range(-3, 3);
    int steps = 80;

    // Create the object to represent the function over the given range.
    final Shape surface =
        new SurfaceBuilder().orthonormal(new OrthonormalGrid(range, steps, range, steps), mapper);
    surface.setColorMapper(new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(),
        surface.getBounds().getZmax(), new Color(1, 1, 1, .5f)));
    surface.setFaceDisplayed(true);
    surface.setWireframeDisplayed(true);
    surface.setWireframeColor(Color.BLACK);
    return surface;
  }
}
