package org.jzy3d.tests.integration;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.ChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.factories.DepthPeelingChartFactory;
import org.jzy3d.factories.DepthPeelingPainterFactory;
import org.jzy3d.junit.NativeChartTester;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.ParallelepipedComposite;
import org.jzy3d.plot3d.primitives.ParallelepipedComposite.PolygonType;
import org.jzy3d.plot3d.primitives.PolygonMode;
import org.jzy3d.plot3d.rendering.ddp.algorithms.PeelingMethod;
import org.jzy3d.plot3d.rendering.view.HiDPI;

/**
 * Render translucent intersecting cubes with depth peeling, with JOGL and PanamaGL, and verify both
 * images are nearly identical.
 *
 * Axes are not displayed : depth peeling shaders do not render text properly.
 *
 * Images are written to <code>target/depth-peeling/</code>.
 */
public class ITTest_DepthPeeling extends ITTest {
  static final String OUTPUT = "target/depth-peeling/";

  static final String PANAMAGL_DEPTH_PEELING_FACTORY =
      "org.jzy3d.chart.factories.PanamaGLDepthPeelingChartFactory";

  static final String PANAMAGL_DEPTH_PEELING_PAINTER_FACTORY =
      "org.jzy3d.chart.factories.PanamaGLDepthPeelingPainterFactory";

  /** Maximum ratio of pixels differing between JOGL and PanamaGL */
  static final double MAX_DIFF_RATIO = 0.01;

  @Test
  public void whenDualPeeling_ThenPanamaGLMatchesJOGL() throws Exception {
    assertParity(PeelingMethod.DUAL_PEELING_MODE);
  }

  @Test
  public void whenFrontToBackPeeling_ThenPanamaGLMatchesJOGL() throws Exception {
    assertParity(PeelingMethod.F2B_PEELING_MODE);
  }

  @Test
  public void whenWeightedAveragePeeling_ThenPanamaGLMatchesJOGL() throws Exception {
    assertParity(PeelingMethod.WEIGHTED_AVERAGE_MODE);
  }

  @Test
  public void whenWeightedSumPeeling_ThenPanamaGLMatchesJOGL() throws Exception {
    assertParity(PeelingMethod.WEIGHTED_SUM_MODE);
  }

  // ---------------------------------------------------------------------------------------------

  protected void assertParity(PeelingMethod method) throws Exception {
    new File(OUTPUT).mkdirs();

    DepthPeelingPainterFactory joglPainter = new DepthPeelingPainterFactory();
    joglPainter.setPeelingMethod(method);
    BufferedImage jogl = render(new DepthPeelingChartFactory(joglPainter));
    ImageIO.write(jogl, "png", new File(OUTPUT + method + "_JOGL.png"));

    Assume.assumeTrue("PanamaGL depth peeling is not in classpath", isPanamaGLDepthPeelingAvailable());

    Object panamaPainter = Class.forName(PANAMAGL_DEPTH_PEELING_PAINTER_FACTORY)
        .getDeclaredConstructor(PeelingMethod.class).newInstance(method);
    ChartFactory panamaFactory = (ChartFactory) Class.forName(PANAMAGL_DEPTH_PEELING_FACTORY)
        .getDeclaredConstructor(org.jzy3d.chart.factories.IPainterFactory.class)
        .newInstance(panamaPainter);
    BufferedImage panama = render(panamaFactory);
    ImageIO.write(panama, "png", new File(OUTPUT + method + "_PanamaGL.png"));

    double ratio = ITTest_PanamaGLParity.diffRatio(jogl, panama);

    Assert.assertTrue(method + " : " + (100 * ratio) + "% of pixels differ between JOGL and PanamaGL",
        ratio <= MAX_DIFF_RATIO);
  }

  protected static boolean isPanamaGLDepthPeelingAvailable() {
    try {
      Class.forName(PANAMAGL_DEPTH_PEELING_FACTORY);
      return true;
    } catch (ClassNotFoundException e) {
      return false;
    }
  }

  protected BufferedImage render(ChartFactory factory) throws IOException {
    factory.getPainterFactory().setOffscreen(offscreenDimension.clone());
    Chart chart = factory.newChart(quality(HiDPI.OFF));

    // Axis labels are rendered through peeling shaders, which do not support text, with JOGL
    // as well as with PanamaGL, hence only compare the peeled geometries
    chart.getView().setAxisDisplayed(false);

    // Given three intersecting cubes, two of them being translucent
    float width = 0.01f;
    cube(chart, width, Coord3d.ORIGIN, Color.BLUE /* no alpha */, Color.BLACK);
    cube(chart, width, new Coord3d(0.005f, 0.005f, 0.005f), Color.RED.alpha(.5f), Color.BLACK);
    cube(chart, width, new Coord3d(0.01f, 0.01f, 0.01f), Color.GREEN.alpha(.5f), Color.BLACK);

    try {
      if (isPanamaGL(chart)) {
        return (BufferedImage) chart.screenshot();
      } else {
        return new NativeChartTester() {
          public BufferedImage image(Chart c) throws IOException {
            return getBufferedImage(c);
          }
        }.image(chart);
      }
    } finally {
      chart.dispose();
    }
  }

  protected static void cube(Chart chart, float width, Coord3d position, Color face,
      Color wireframe) {
    BoundingBox3d bounds =
        new BoundingBox3d(position.x - width / 2, position.x + width / 2, position.y - width / 2,
            position.y + width / 2, position.z - width / 2, position.z + width / 2);
    ParallelepipedComposite p = new ParallelepipedComposite(bounds, PolygonType.SIMPLE);
    p.setPolygonMode(PolygonMode.FRONT_AND_BACK);
    p.setPolygonOffsetFill(true);
    p.setColor(face);
    p.setWireframeColor(wireframe);
    p.setWireframeDisplayed(true);
    chart.add(p);
  }
}
