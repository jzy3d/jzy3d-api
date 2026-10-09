package org.jzy3d.tests.integration;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;
import javax.imageio.ImageIO;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import java.awt.Graphics;
import org.jzy3d.chart.AWTChart;
import org.jzy3d.chart.Chart;
import org.jzy3d.junit.NativeChartTester;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.view.AWTRenderer2d;
import org.jzy3d.plot3d.rendering.view.HiDPI;
import org.jzy3d.plot3d.rendering.view.AWTView;

/**
 * Render the same chart with JOGL and PanamaGL on the same computer and verify both images are
 * nearly identical.
 *
 * Unlike baseline image tests, this test does not depend on the platform used to generate baseline
 * images, so it can run on any computer having both JOGL and PanamaGL (Java 22+).
 *
 * Images are not strictly equal since text antialiasing differs between JOGL's TextRenderer and the
 * AWT text rendering used by PanamaGL. In case of failure, both images are written to
 * <code>target/panamagl-parity/</code>.
 */
public class ITTest_PanamaGLParity extends ITTest {
  /** A channel difference above this value makes a pixel different */
  static final int PIXEL_TOLERANCE = 32;

  /** Maximum ratio of different pixels */
  static final double MAX_DIFF_RATIO = 0.01;

  static final String OUTPUT = "target/panamagl-parity/";

  @Test
  public void whenSurface_ThenPanamaGLMatchesJOGL() throws IOException {
    assertParity("Surface", chart -> chart.add(surface()));
  }

  @Test
  public void whenScatter_ThenPanamaGLMatchesJOGL() throws IOException {
    assertParity("Scatter", chart -> chart.add(scatter(50000)));
  }

  @Test
  public void when2DSurface_ThenPanamaGLMatchesJOGL() throws IOException {
    assertParity("Surface2D", chart -> {
      chart.add(surface());
      chart.view2d();
    });
  }

  @Test
  public void whenColorbar_ThenPanamaGLMatchesJOGL() throws IOException {
    assertParity("Colorbar", chart -> {
      Shape surface = surface();
      chart.add(surface);
      ((AWTChart) chart).colorbar(surface);
    });
  }

  @Test
  public void whenOverlay_ThenPanamaGLMatchesJOGL() throws IOException {
    assertParity("Overlay", chart -> {
      chart.add(surface());
      ((AWTChart) chart).addRenderer(new AWTRenderer2d() {
        AWTView view;

        @Override
        public void setView(AWTView view) {
          this.view = view;
        }

        @Override
        public AWTView getView() {
          return view;
        }

        @Override
        public void paint(Graphics g, int canvasWidth, int canvasHeight) {
          g.setColor(java.awt.Color.RED);
          g.fillRect(20, 20, 100, 50);
          g.setColor(java.awt.Color.BLUE);
          g.drawString("Overlay", 30, 50);
        }
      });
    });
  }

  // ---------------------------------------------------------------------------------------------

  protected void assertParity(String name, Consumer<Chart> content) throws IOException {
    Assume.assumeTrue("PanamaGL is not in classpath", isPanamaGLAvailable());

    BufferedImage jogl = render(WT.Native_Swing, content);
    BufferedImage panama = render(WT.PanamaGL_Swing, content);

    double ratio = diffRatio(jogl, panama);

    if (ratio > MAX_DIFF_RATIO) {
      new File(OUTPUT).mkdirs();
      ImageIO.write(jogl, "png", new File(OUTPUT + name + "_JOGL.png"));
      ImageIO.write(panama, "png", new File(OUTPUT + name + "_PanamaGL.png"));
    }

    Assert.assertTrue(name + " : " + (100 * ratio) + "% of pixels differ between JOGL and PanamaGL",
        ratio <= MAX_DIFF_RATIO);
  }

  protected BufferedImage render(WT toolkit, Consumer<Chart> content) throws IOException {
    Chart chart = chart(toolkit, HiDPI.OFF);
    content.accept(chart);

    chart.open(toolkit.name(), offscreenDimension.width, offscreenDimension.height);
    chart.render();
    chart.render(2);

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

  /** Ratio of pixels having a channel differing by more than {@link #PIXEL_TOLERANCE} */
  protected static double diffRatio(BufferedImage a, BufferedImage b) {
    Assert.assertEquals("image width", a.getWidth(), b.getWidth());
    Assert.assertEquals("image height", a.getHeight(), b.getHeight());

    int diff = 0;
    for (int x = 0; x < a.getWidth(); x++) {
      for (int y = 0; y < a.getHeight(); y++) {
        int pa = a.getRGB(x, y);
        int pb = b.getRGB(x, y);

        for (int shift = 0; shift <= 16; shift += 8) {
          if (Math.abs(((pa >> shift) & 0xFF) - ((pb >> shift) & 0xFF)) > PIXEL_TOLERANCE) {
            diff++;
            break;
          }
        }
      }
    }
    return diff / (double) (a.getWidth() * a.getHeight());
  }
}
