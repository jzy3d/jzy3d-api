package org.jzy3d.plot3d.rendering.ddp;

import java.awt.image.BufferedImage;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.PanamaGLDepthPeelingChartFactory;
import org.jzy3d.chart.factories.PanamaGLDepthPeelingPainterFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.plot3d.primitives.ParallelepipedComposite;
import org.jzy3d.plot3d.primitives.ParallelepipedComposite.PolygonType;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.ddp.algorithms.PeelingMethod;

public class TestPanamaGLDepthPeelingRenderer3D {
  @Test
  public void whenRenderingTranslucentCubes_ThenEachPeelingMethodRendersThem() {
    for (PeelingMethod method : PeelingMethod.values()) {
      BufferedImage image = render(method);

      // Given a red translucent cube in front of a white background
      int center = image.getRGB(image.getWidth() / 2, image.getHeight() / 2);
      int r = (center >> 16) & 0xFF;
      int g = (center >> 8) & 0xFF;
      int b = center & 0xFF;

      Assert.assertTrue(method + " : cube should be red " + r + "," + g + "," + b,
          r > 200 && g < 200 && b < 200);
    }
  }

  protected BufferedImage render(PeelingMethod method) {
    PanamaGLDepthPeelingChartFactory factory =
        new PanamaGLDepthPeelingChartFactory(new PanamaGLDepthPeelingPainterFactory(method));
    factory.getPainterFactory().setOffscreen(200, 200);

    Chart chart;
    try {
      chart = factory.newChart(Quality.Advanced());
    } catch (Throwable t) {
      Assume.assumeNoException("No OpenGL context available", t);
      return null;
    }

    try {
      chart.getView().setAxisDisplayed(false);

      ParallelepipedComposite cube =
          new ParallelepipedComposite(new BoundingBox3d(-1, 1, -1, 1, -1, 1), PolygonType.SIMPLE);
      cube.setColor(Color.RED.alpha(.5f));
      cube.setWireframeDisplayed(false);
      chart.add(cube);

      BufferedImage image = (BufferedImage) chart.screenshot();
      Assert.assertNotNull(method + " : nothing rendered", image);
      return image;
    } finally {
      chart.dispose();
    }
  }
}
