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
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.chart.factories.ChartFactory;
import org.jzy3d.chart.factories.ContourChartFactory;
import org.jzy3d.chart2d.Chart2d;
import org.jzy3d.chart2d.Chart2dFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.contour.DefaultContourColoringPolicy;
import org.jzy3d.contour.MapperContourMeshGenerator;
import org.jzy3d.maths.Range;
import org.jzy3d.plot2d.primitives.Serie2d;
import org.jzy3d.plot3d.builder.Func3D;
import org.jzy3d.plot3d.primitives.axis.ContourAxisBox;
import org.jzy3d.junit.NativeChartTester;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.Scatter;
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

  static final String PANAMAGL_CHART2D_FACTORY = "org.jzy3d.chart2d.PanamaGLChart2dFactory";

  static final String PANAMAGL_CONTOUR_FACTORY =
      "org.jzy3d.chart.factories.PanamaGLContourChartFactory";

  @Test
  public void whenSurface_ThenPanamaGLMatchesJOGL() throws IOException {
    assertParity("Surface", chart -> chart.add(surface()));
  }

  @Test
  public void whenScatter_ThenPanamaGLMatchesJOGL() throws IOException {
    assertParity("Scatter", chart -> chart.add(scatter(50000)));
  }

  /**
   * Scatter colors have negative components. Report the GL state the scatter is drawn with to
   * understand a difference between both contexts.
   */
  @Test
  public void whenScatterGLState_ThenPanamaGLMatchesJOGL() throws IOException {
    Assume.assumeTrue("PanamaGL is not in classpath", isPanamaGLAvailable());

    StateScatter joglScatter = new StateScatter(scatter(50000));
    StateScatter panamaScatter = new StateScatter(scatter(50000));

    render(WT.Native_Swing, chart -> chart.add(joglScatter));
    render(WT.PanamaGL_Swing, chart -> chart.add(panamaScatter));

    Assert.assertEquals("GL state differs", joglScatter.state, panamaScatter.state);
  }

  /** A scatter that keeps the GL state it was drawn with */
  static class StateScatter extends Scatter {
    static final String[] NAMES = {"CLAMP_VERTEX_COLOR", "CLAMP_FRAGMENT_COLOR",
        "CLAMP_READ_COLOR", "BLEND", "BLEND_SRC_RGB", "BLEND_DST_RGB", "BLEND_SRC_ALPHA",
        "BLEND_DST_ALPHA", "BLEND_EQUATION_RGB", "DEPTH_TEST", "ALPHA_TEST", "LIGHTING",
        "COLOR_MATERIAL", "POINT_SMOOTH", "MULTISAMPLE", "SAMPLES", "FRAMEBUFFER_SRGB",
        "DITHER", "RED_BITS", "ALPHA_BITS", "SHADE_MODEL", "COLOR_LOGIC_OP"};
    static final int[] PNAMES = {0x891A, 0x891B, 0x891C, 0x0BE2, 0x80C9, 0x80C8, 0x80CB,
        0x80CA, 0x8009, 0x0B71, 0x0BC0, 0x0B50, 0x0B57, 0x0B10, 0x809D, 0x80A9, 0x8DB9, 0x0BD0,
        0x0D52, 0x0D55, 0x0B54, 0x0BF2};

    String state = "";

    StateScatter(Scatter scatter) {
      super(scatter.getData(), scatter.getColors(), scatter.getWidth());
    }

    @Override
    public void draw(IPainter painter) {
      super.draw(painter);

      StringBuilder sb = new StringBuilder();
      int[] value = new int[4];
      for (int i = 0; i < PNAMES.length; i++) {
        value[0] = -1;
        painter.glGetIntegerv(PNAMES[i], value, 0);
        sb.append(NAMES[i] + "=0x" + Integer.toHexString(value[0]) + " ");
      }
      sb.append("VERSION=" + painter.glGetString(0x1F02));
      state = sb.toString();
    }
  }

  @Test
  public void whenScatterOfThinPoints_ThenPanamaGLMatchesJOGL() throws IOException {
    assertParity("ScatterThin", chart -> {
      Scatter scatter = scatter(50000);
      scatter.setWidth(1);
      chart.add(scatter);
    });
  }

  @Test
  public void whenScatterOffscreen_ThenPanamaGLMatchesJOGL() throws IOException {
    assertFactoryParity("ScatterOffscreen", new AWTChartFactory(), PANAMAGL_SWING_FACTORY,
        chart -> chart.add(scatter(50000)));
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

  @Test
  public void whenOffscreen_ThenPanamaGLMatchesJOGL() throws IOException {
    assertFactoryParity("Offscreen", new AWTChartFactory(), PANAMAGL_SWING_FACTORY,
        chart -> chart.add(surface()));
  }

  @Test
  public void whenChart2d_ThenPanamaGLMatchesJOGL() throws IOException {
    assertFactoryParity("Chart2d", new Chart2dFactory(), PANAMAGL_CHART2D_FACTORY, chart -> {
      Serie2d serie = ((Chart2d) chart).getSerie("sine", Serie2d.Type.LINE);
      serie.setColor(Color.BLUE);
      for (int i = 0; i < 100; i++) {
        serie.add(i, Math.sin(i / 10.0));
      }
    });
  }

  @Test
  public void whenContour_ThenPanamaGLMatchesJOGL() throws IOException {
    assertFactoryParity("Contour", new ContourChartFactory(), PANAMAGL_CONTOUR_FACTORY, chart -> {
      Shape surface = surface();
      chart.add(surface);

      Range range = new Range(-3, 3);
      MapperContourMeshGenerator contour = new MapperContourMeshGenerator(
          new Func3D((x, y) -> x * Math.sin(x * y)), range, range);
      ContourAxisBox axis = (ContourAxisBox) chart.getView().getAxis();
      axis.setContourMesh(contour.getContourMesh(
          new DefaultContourColoringPolicy(surface.getColorMapper()), 200, 200, 10, 0, false));
    });
  }

  /**
   * Render the same content offscreen with a JOGL chart factory and a PanamaGL one, loaded by
   * name, and verify both images are nearly identical.
   */
  protected void assertFactoryParity(String name, ChartFactory joglFactory,
      String panamaFactoryClass, Consumer<Chart> content) throws IOException {
    Assume.assumeTrue("PanamaGL is not in classpath", isPanamaGLAvailable());

    BufferedImage jogl = renderOffscreen(joglFactory, content);
    BufferedImage panama = renderOffscreen(newChartFactory(panamaFactoryClass), content);

    double ratio = diffRatio(jogl, panama);

    if (ratio > MAX_DIFF_RATIO) {
      new File(OUTPUT).mkdirs();
      ImageIO.write(jogl, "png", new File(OUTPUT + name + "_JOGL.png"));
      ImageIO.write(panama, "png", new File(OUTPUT + name + "_PanamaGL.png"));
    }

    Assert.assertTrue(name + " : " + (100 * ratio) + "% of pixels differ between JOGL and PanamaGL"
        + " (JOGL " + describe(jogl) + ", PanamaGL " + describe(panama) + ")",
        ratio <= MAX_DIFF_RATIO);
  }

  protected BufferedImage renderOffscreen(ChartFactory factory, Consumer<Chart> content)
      throws IOException {
    factory.getPainterFactory().setOffscreen(offscreenDimension.clone());
    Chart chart = factory.newChart(quality(HiDPI.OFF));
    content.accept(chart);

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

    Assert.assertTrue(name + " : " + (100 * ratio) + "% of pixels differ between JOGL and PanamaGL"
        + " (JOGL " + describe(jogl) + ", PanamaGL " + describe(panama) + ")",
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

  /**
   * Summarize an image to help understanding a difference without seeing the images : the ratio of
   * pixels differing from the background (the top left pixel) and their mean color.
   */
  protected static String describe(BufferedImage image) {
    int background = image.getRGB(0, 0);
    long n = 0, r = 0, g = 0, b = 0;
    for (int x = 0; x < image.getWidth(); x++) {
      for (int y = 0; y < image.getHeight(); y++) {
        int p = image.getRGB(x, y);
        if (p != background) {
          n++;
          r += (p >> 16) & 0xFF;
          g += (p >> 8) & 0xFF;
          b += p & 0xFF;
        }
      }
    }
    double ratio = 100.0 * n / (image.getWidth() * image.getHeight());
    return String.format("%.1f%% drawn, mean color %d,%d,%d", ratio, n == 0 ? 0 : r / n,
        n == 0 ? 0 : g / n, n == 0 ? 0 : b / n);
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
