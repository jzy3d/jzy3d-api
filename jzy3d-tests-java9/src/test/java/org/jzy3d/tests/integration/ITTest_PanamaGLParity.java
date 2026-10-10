package org.jzy3d.tests.integration;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
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
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Range;
import org.jzy3d.plot2d.primitives.Serie2d;
import org.jzy3d.plot3d.builder.Func3D;
import org.jzy3d.plot3d.primitives.axis.ContourAxisBox;
import org.jzy3d.plot3d.primitives.axis.layout.AxisLayout;
import org.jzy3d.junit.NativeChartTester;
import org.jzy3d.painters.Font;
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

    BufferedImage jogl = render(WT.Native_Swing, chart -> chart.add(joglScatter));
    BufferedImage panama = render(WT.PanamaGL_Swing, chart -> chart.add(panamaScatter));
    System.out.println("JOGL     image center : " + center(jogl));
    System.out.println("PanamaGL image center : " + center(panama));

    System.out.println("JOGL     GL state : " + joglScatter.state);
    System.out.println("PanamaGL GL state : " + panamaScatter.state);
    System.out.println("JOGL     frames : \n  " + String.join("\n  ", joglScatter.history));
    System.out.println("PanamaGL frames : \n  " + String.join("\n  ", panamaScatter.history));

    Assert.assertFalse("JOGL scatter was not drawn", joglScatter.state.isEmpty());
    Assert.assertFalse("PanamaGL scatter was not drawn", panamaScatter.state.isEmpty());
    Assert.assertEquals("GL state differs", joglScatter.state, panamaScatter.state);
  }

  /** Same as {@link #whenScatter_ThenPanamaGLMatchesJOGL()} without axis and with fixed bounds */
  @Test
  public void whenScatterWithoutAxis_ThenPanamaGLMatchesJOGL() throws IOException {
    assertParity("ScatterWithoutAxis", chart -> {
      chart.add(scatter(50000));
      chart.getView().setAxisDisplayed(false);
      chart.getView().setBoundsManual(new BoundingBox3d(-0.5f, 0.5f, -0.5f, 0.5f, -0.5f, 0.5f));
    });
  }

  /** Same as {@link #whenScatter_ThenPanamaGLMatchesJOGL()} with fixed bounds */
  @Test
  public void whenScatterWithFixedBounds_ThenPanamaGLMatchesJOGL() throws IOException {
    assertParity("ScatterWithFixedBounds", chart -> {
      chart.add(scatter(50000));
      chart.getView().setBoundsManual(new BoundingBox3d(-0.5f, 0.5f, -0.5f, 0.5f, -0.5f, 0.5f));
    });
  }

  /** Same as {@link #whenScatter_ThenPanamaGLMatchesJOGL()} with axis but without text */
  @Test
  public void whenScatterWithoutAxisText_ThenPanamaGLMatchesJOGL() throws IOException {
    assertParity("ScatterWithoutAxisText", chart -> {
      chart.add(scatter(50000));
      AxisLayout layout = chart.getAxisLayout();
      layout.setXTickLabelDisplayed(false);
      layout.setYTickLabelDisplayed(false);
      layout.setZTickLabelDisplayed(false);
      layout.setXAxisLabelDisplayed(false);
      layout.setYAxisLabelDisplayed(false);
      layout.setZAxisLabelDisplayed(false);
    });
  }

  // Scatters match when axis text is hidden. The tests below hide axis text and replay part of
  // the text rendering right before drawing the scatter to find which part changes the scatter.

  /** A single label drawn before the scatter */
  @Test
  public void whenScatterAfterText_ThenPanamaGLMatchesJOGL() throws IOException {
    assertParityAfter("ScatterAfterText", painter -> painter.drawText(Font.Helvetica_12, "A",
        new Coord3d(0.5f, 0.5f, 0.5f), Color.BLACK, 0));
  }

  /** A texture created, filled and deleted before the scatter, without drawing it */
  @Test
  public void whenScatterAfterTextureUpload_ThenPanamaGLMatchesJOGL() throws IOException {
    assertParityAfter("ScatterAfterTextureUpload", painter -> {
      int[] id = new int[1];
      painter.glGenTextures(1, id, 0);
      painter.glBindTexture(0x0DE1, id[0]);
      painter.glTexImage2D(0x0DE1, 0, 0x1908, 4, 4, 0, 0x1908, 0x1401,
          ByteBuffer.allocateDirect(4 * 4 * 4));
      painter.glDeleteTextures(1, id, 0);
    });
  }

  /** A textured quad drawn before the scatter, as text is drawn by PanamaGL */
  @Test
  public void whenScatterAfterTexturedQuad_ThenPanamaGLMatchesJOGL() throws IOException {
    assertParityAfter("ScatterAfterTexturedQuad", painter -> {
      int[] id = new int[1];
      painter.glGenTextures(1, id, 0);
      painter.glEnable(0x0DE1);
      painter.glBindTexture(0x0DE1, id[0]);
      painter.glTexImage2D(0x0DE1, 0, 0x1908, 4, 4, 0, 0x1908, 0x1401,
          ByteBuffer.allocateDirect(4 * 4 * 4));
      painter.glColor4f(1, 1, 1, 1);
      painter.glBegin(0x0007);
      painter.glTexCoord2f(0, 0);
      painter.glVertex3f(0.4f, 0.4f, 0.4f);
      painter.glTexCoord2f(1, 0);
      painter.glVertex3f(0.5f, 0.4f, 0.4f);
      painter.glTexCoord2f(1, 1);
      painter.glVertex3f(0.5f, 0.5f, 0.4f);
      painter.glTexCoord2f(0, 1);
      painter.glVertex3f(0.4f, 0.5f, 0.4f);
      painter.glEnd();
      painter.glDisable(0x0DE1);
      painter.glDeleteTextures(1, id, 0);
    });
  }

  /** Attributes pushed then popped before the scatter, as done when PanamaGL draws text */
  @Test
  public void whenScatterAfterPushPopAttrib_ThenPanamaGLMatchesJOGL() throws IOException {
    assertParityAfter("ScatterAfterPushPopAttrib", painter -> {
      gl(painter, "glPushAttrib", 0x47009);
      gl(painter, "glPopAttrib");
    });
  }

  /** Matrices pushed, set to an orthographic projection, then popped before the scatter */
  @Test
  public void whenScatterAfterPushPopMatrix_ThenPanamaGLMatchesJOGL() throws IOException {
    assertParityAfter("ScatterAfterPushPopMatrix", painter -> {
      gl(painter, "glMatrixMode", 0x1701);
      gl(painter, "glPushMatrix");
      gl(painter, "glLoadIdentity");
      gl(painter, "glOrtho", 0d, 100d, 0d, 100d, -1d, 1d);
      gl(painter, "glMatrixMode", 0x1700);
      gl(painter, "glPushMatrix");
      gl(painter, "glLoadIdentity");
      gl(painter, "glMatrixMode", 0x1700);
      gl(painter, "glPopMatrix");
      gl(painter, "glMatrixMode", 0x1701);
      gl(painter, "glPopMatrix");
      gl(painter, "glMatrixMode", 0x1700);
    });
  }

  /** Texture environment, pixel store and texture parameters set before the scatter */
  @Test
  public void whenScatterAfterTextureSettings_ThenPanamaGLMatchesJOGL() throws IOException {
    assertParityAfter("ScatterAfterTextureSettings", painter -> {
      int[] id = new int[1];
      painter.glGenTextures(1, id, 0);
      painter.glBindTexture(0x0DE1, id[0]);
      painter.glTexParameteri(0x0DE1, 0x2801, 0x2601);
      painter.glTexParameteri(0x0DE1, 0x2800, 0x2601);
      painter.glTexEnvi(0x2300, 0x2200, 0x2100);
      painter.glPixelStorei(0x0CF5, 4);
      painter.glDeleteTextures(1, id, 0);
    });
  }

  /** The GL calls PanamaGL makes to draw a label, with an image not holding text */
  @Test
  public void whenScatterAfterImage_ThenPanamaGLMatchesJOGL() throws IOException {
    assertParityAfter("ScatterAfterImage",
        painter -> drawImage(painter, new BufferedImage(20, 14, BufferedImage.TYPE_INT_ARGB)));
  }

  /** The GL calls PanamaGL makes to draw a label, with an opaque black image */
  @Test
  public void whenScatterAfterOpaqueImage_ThenPanamaGLMatchesJOGL() throws IOException {
    assertParityAfter("ScatterAfterOpaqueImage", painter -> {
      BufferedImage image = new BufferedImage(20, 14, BufferedImage.TYPE_INT_ARGB);
      java.awt.Graphics2D g = image.createGraphics();
      g.setColor(java.awt.Color.BLACK);
      g.fillRect(0, 0, 20, 14);
      g.dispose();
      drawImage(painter, image);
    });
  }

  /** The GL calls PanamaGL makes to draw a label, with an image holding a text */
  @Test
  public void whenScatterAfterTextImage_ThenPanamaGLMatchesJOGL() throws IOException {
    assertParityAfter("ScatterAfterTextImage", painter -> {
      BufferedImage image = new BufferedImage(20, 14, BufferedImage.TYPE_INT_ARGB);
      java.awt.Graphics2D g = image.createGraphics();
      g.setRenderingHint(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING,
          java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      g.setFont(new java.awt.Font("Helvetica", java.awt.Font.PLAIN, 12));
      g.setColor(java.awt.Color.BLACK);
      g.drawString("A", 0, 11);
      g.dispose();
      drawImage(painter, image);
    });
  }

  /** Draw an image with the PanamaGL painter, do nothing with the JOGL one */
  protected static void drawImage(IPainter painter, BufferedImage image) {
    try {
      painter.getClass().getMethod("drawImage", BufferedImage.class, float.class, float.class)
          .invoke(painter, image, 10f, 10f);
    } catch (NoSuchMethodException e) {
      // JOGL painter
    } catch (ReflectiveOperationException e) {
      throw new RuntimeException(e);
    }
  }

  /** The AWT text image PanamaGL builds to draw a label, without any GL call */
  @Test
  public void whenScatterAfterAWTText_ThenPanamaGLMatchesJOGL() throws IOException {
    assertParityAfter("ScatterAfterAWTText", painter -> {
      java.awt.Font font = new java.awt.Font("Helvetica", java.awt.Font.PLAIN, 12);
      BufferedImage metrics = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
      java.awt.Graphics2D mg = metrics.createGraphics();
      java.awt.FontMetrics fm = mg.getFontMetrics(font);
      mg.dispose();

      BufferedImage image = new BufferedImage(Math.max(1, fm.stringWidth("A")),
          fm.getAscent() + fm.getDescent(), BufferedImage.TYPE_INT_ARGB);
      java.awt.Graphics2D g = image.createGraphics();
      g.setRenderingHint(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING,
          java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      g.setFont(font);
      g.setColor(java.awt.Color.BLACK);
      g.drawString("A", 0, fm.getAscent());
      g.dispose();
    });
  }

  /** The projection of a label position, done with native GLU by PanamaGL */
  @Test
  public void whenScatterAfterProjection_ThenPanamaGLMatchesJOGL() throws IOException {
    assertParityAfter("ScatterAfterProjection",
        painter -> painter.modelToScreen(new Coord3d(0.5f, 0.5f, 0.5f)));
  }

  /**
   * Invoke a GL function on the GL object of a JOGL or PanamaGL painter, which may not offer it.
   * Done by reflection since this test does not compile against PanamaGL.
   */
  protected static void gl(IPainter painter, String function, Object... args) {
    try {
      Object gl = painter.getClass().getMethod("getGL").invoke(painter);
      try {
        gl = gl.getClass().getMethod("getGL2").invoke(gl);
      } catch (NoSuchMethodException e) {
        // PanamaGL GL has no GL2 profile
      }
      for (java.lang.reflect.Method m : gl.getClass().getMethods()) {
        if (m.getName().equals(function) && m.getParameterCount() == args.length) {
          m.setAccessible(true);
          m.invoke(gl, args);
          return;
        }
      }
      throw new IllegalArgumentException(function + " not found in " + gl.getClass());
    } catch (ReflectiveOperationException e) {
      throw new RuntimeException(e);
    }
  }

  /** Render a scatter with hidden axis text, invoking the given GL calls right before it */
  protected void assertParityAfter(String name, Consumer<IPainter> before) throws IOException {
    assertParity(name, chart -> {
      hideAxisText(chart);
      Scatter scatter = scatter(50000);
      chart.add(new Scatter(scatter.getData(), scatter.getColors(), scatter.getWidth()) {
        @Override
        public void draw(IPainter painter) {
          before.accept(painter);
          super.draw(painter);
        }
      });
    });
  }

  protected static void hideAxisText(Chart chart) {
    AxisLayout layout = chart.getAxisLayout();
    layout.setXTickLabelDisplayed(false);
    layout.setYTickLabelDisplayed(false);
    layout.setZTickLabelDisplayed(false);
    layout.setXAxisLabelDisplayed(false);
    layout.setYAxisLabelDisplayed(false);
    layout.setZAxisLabelDisplayed(false);
  }

  /** Same as {@link #whenScatter_ThenPanamaGLMatchesJOGL()} without axis */
  @Test
  public void whenScatterWithoutAxisOnly_ThenPanamaGLMatchesJOGL() throws IOException {
    assertParity("ScatterWithoutAxisOnly", chart -> {
      chart.add(scatter(50000));
      chart.getView().setAxisDisplayed(false);
    });
  }

  /** Same as {@link #whenScatter_ThenPanamaGLMatchesJOGL()} without transparency */
  @Test
  public void whenOpaqueScatter_ThenPanamaGLMatchesJOGL() throws IOException {
    assertParity("ScatterOpaque", chart -> {
      Scatter scatter = scatter(50000);
      for (Color c : scatter.getColors()) {
        c.a = 1;
      }
      chart.add(scatter);
    });
  }

  /**
   * Draw a single point with each library and list its pixels, to compare the point size and the
   * blended color exactly.
   */
  @Test
  public void whenSinglePoint_ThenPanamaGLMatchesJOGL() throws IOException {
    Assume.assumeTrue("PanamaGL is not in classpath", isPanamaGLAvailable());

    StringBuilder sb = new StringBuilder();
    boolean same = true;

    for (float width : new float[] {1, 3}) {
      for (float alpha : new float[] {1, 0.75f}) {
        Consumer<Chart> content = chart -> {
          Scatter point = new Scatter(new Coord3d[] {new Coord3d(0, 0, 0)},
              new Color[] {new Color(0.2f, 0.4f, 0.6f, alpha)}, width);
          chart.add(point);
          chart.getView().setAxisDisplayed(false);
          chart.getView().setBoundsManual(new BoundingBox3d(-1, 1, -1, 1, -1, 1));
        };
        String jogl = pixels(render(WT.Native_Swing, content));
        String panama = pixels(render(WT.PanamaGL_Swing, content));

        sb.append("\nwidth " + width + " alpha " + alpha + "\n  JOGL     " + jogl
            + "\n  PanamaGL " + panama);
        same &= jogl.equals(panama);
      }
    }
    System.out.println("Single point pixels :" + sb);
    Assert.assertTrue("Single point pixels differ :" + sb, same);
  }

  /** Sum the colors of the 64x64 block at the center of an image */
  protected static String center(BufferedImage image) {
    int size = 64;
    long rgb = 0, alpha = 0;
    int drawn = 0;
    for (int y = image.getHeight() / 2 - size / 2; y < image.getHeight() / 2 + size / 2; y++) {
      for (int x = image.getWidth() / 2 - size / 2; x < image.getWidth() / 2 + size / 2; x++) {
        int p = image.getRGB(x, y);
        int r = (p >> 16) & 0xFF, g = (p >> 8) & 0xFF, b = p & 0xFF;
        if (r < 250 || g < 250 || b < 250) {
          drawn++;
        }
        rgb += r + g + b;
        alpha += (p >>> 24) & 0xFF;
      }
    }
    return drawn + " non white pixels, rgb sum " + rgb + ", alpha sum " + alpha;
  }

  /** A text image where darker characters show darker areas, to see an image in a CI log */
  protected static String thumbnail(BufferedImage image) {
    String shades = " .:-=+*#%@";
    int cols = 64, rows = 24;
    StringBuilder sb = new StringBuilder();
    for (int r = 0; r < rows; r++) {
      for (int c = 0; c < cols; c++) {
        long sum = 0, n = 0;
        for (int y = r * image.getHeight() / rows; y < (r + 1) * image.getHeight() / rows; y++) {
          for (int x = c * image.getWidth() / cols; x < (c + 1) * image.getWidth() / cols; x++) {
            int p = image.getRGB(x, y);
            sum += ((p >> 16) & 0xFF) + ((p >> 8) & 0xFF) + (p & 0xFF);
            n++;
          }
        }
        double darkness = 1 - sum / (3.0 * 255 * Math.max(n, 1));
        sb.append(shades.charAt((int) Math.min(shades.length() - 1, darkness * shades.length())));
      }
      sb.append("|\n");
    }
    return sb.toString();
  }

  /** List the pixels differing from the background (top left pixel) */
  protected static String pixels(BufferedImage image) {
    int background = image.getRGB(0, 0);
    StringBuilder sb = new StringBuilder();
    int n = 0;
    for (int y = 0; y < image.getHeight(); y++) {
      for (int x = 0; x < image.getWidth(); x++) {
        int p = image.getRGB(x, y);
        if (p != background && n++ < 20) {
          sb.append(x + "," + y + "=" + Integer.toHexString(p) + " ");
        }
      }
    }
    return n + " pixels : " + sb;
  }

  /** A scatter that keeps the GL state it was drawn with */
  static class StateScatter extends Scatter {
    static final String[] NAMES = {"CLAMP_VERTEX_COLOR", "CLAMP_FRAGMENT_COLOR",
        "CLAMP_READ_COLOR", "BLEND", "BLEND_SRC_RGB", "BLEND_DST_RGB", "BLEND_SRC_ALPHA",
        "BLEND_DST_ALPHA", "BLEND_EQUATION_RGB", "DEPTH_TEST", "ALPHA_TEST", "LIGHTING",
        "COLOR_MATERIAL", "POINT_SMOOTH", "MULTISAMPLE", "SAMPLES", "FRAMEBUFFER_SRGB",
        "DITHER", "RED_BITS", "ALPHA_BITS", "SHADE_MODEL", "COLOR_LOGIC_OP", "TEXTURE_2D",
        "TEXTURE_BINDING_2D", "ACTIVE_TEXTURE", "ALPHA_TEST_FUNC", "DEPTH_FUNC",
        "DEPTH_WRITEMASK", "POINT_SPRITE", "CURRENT_PROGRAM", "STENCIL_TEST", "SCISSOR_TEST",
        "FOG", "CULL_FACE", "POLYGON_OFFSET_POINT", "VERTEX_ARRAY", "COLOR_ARRAY",
        "ARRAY_BUFFER_BINDING", "DRAW_FRAMEBUFFER_BINDING", "MATRIX_MODE", "PROGRAM_POINT_SIZE"};
    static final int[] PNAMES = {0x891A, 0x891B, 0x891C, 0x0BE2, 0x80C9, 0x80C8, 0x80CB,
        0x80CA, 0x8009, 0x0B71, 0x0BC0, 0x0B50, 0x0B57, 0x0B10, 0x809D, 0x80A9, 0x8DB9, 0x0BD0,
        0x0D52, 0x0D55, 0x0B54, 0x0BF2, 0x0DE1, 0x8069, 0x84E0, 0x0BC1, 0x0B74, 0x0B72, 0x8861,
        0x8B8D, 0x0B90, 0x0C11, 0x0B60, 0x0B44, 0x2A01, 0x8074, 0x8076, 0x8894, 0x8CA6, 0x0BA0,
        0x8642};

    String state = "";
    List<String> history = new ArrayList<>();

    StateScatter(Scatter scatter) {
      super(scatter.getData(), scatter.getColors(), scatter.getWidth());
    }

    @Override
    public void draw(IPainter painter) {
      String before = state(painter);
      String frame = frame(painter);
      super.draw(painter);
      history.add(frame + " | after draw : " + frame(painter));
      state = "before draw : " + before + "\n after draw : " + state(painter);
    }

    /**
     * Describe the camera and the content already drawn around the scene center before drawing
     * the scatter, to know if a previous frame was not cleared.
     */
    String frame(IPainter painter) {
      float[] proj = new float[16];
      float[] model = new float[16];
      painter.glGetFloatv(0x0BA7, proj, 0);
      painter.glGetFloatv(0x0BA6, model, 0);
      int[] viewport = new int[4];
      painter.glGetIntegerv(0x0BA2, viewport, 0);

      int size = 64;
      ByteBuffer pixels = ByteBuffer.allocateDirect(size * size * 4);
      painter.glReadPixels(viewport[2] / 2 - size / 2, viewport[3] / 2 - size / 2, size, size,
          0x1908, 0x1401, pixels);
      int drawn = 0;
      long rgb = 0, alpha = 0;
      for (int i = 0; i < size * size; i++) {
        int r = pixels.get(i * 4) & 0xFF, g = pixels.get(i * 4 + 1) & 0xFF;
        int b = pixels.get(i * 4 + 2) & 0xFF, a = pixels.get(i * 4 + 3) & 0xFF;
        if (r < 250 || g < 250 || b < 250) {
          drawn++;
        }
        rgb += r + g + b;
        alpha += a;
      }
      return String.format(
          "proj %.4f %.4f model %.4f %.4f %.4f, %d non white pixels at center, rgb sum %d,"
              + " alpha sum %d",
          proj[0], proj[5], model[0], model[5], model[14], drawn, rgb, alpha);
    }

    String state(IPainter painter) {
      StringBuilder sb = new StringBuilder();
      int[] value = new int[4];
      for (int i = 0; i < PNAMES.length; i++) {
        value[0] = -1;
        painter.glGetIntegerv(PNAMES[i], value, 0);
        sb.append(NAMES[i] + "=0x" + Integer.toHexString(value[0]) + " ");
      }
      float[] size = new float[4];
      painter.glGetFloatv(0x0B11, size, 0);
      sb.append("POINT_SIZE=" + size[0] + " ");
      painter.glGetIntegerv(0x0BA2, value, 0);
      sb.append("VIEWPORT=" + value[0] + "," + value[1] + "," + value[2] + "," + value[3] + " ");
      painter.glGetFloatv(0x0BC2, size, 0);
      sb.append("ALPHA_TEST_REF=" + size[0] + " ");
      painter.glGetFloatv(0x0B03, size, 0);
      sb.append("TEXCOORD=" + size[0] + "," + size[1] + "," + size[2] + "," + size[3] + " ");
      sb.append("VERSION=" + painter.glGetString(0x1F02));
      return sb.toString();
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
      System.out.println(name + " JOGL\n" + thumbnail(jogl) + name + " PanamaGL\n"
          + thumbnail(panama));
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
      System.out.println(name + " JOGL\n" + thumbnail(jogl) + name + " PanamaGL\n"
          + thumbnail(panama));
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
    long n = 0, r = 0, g = 0, b = 0, a = 0;
    for (int x = 0; x < image.getWidth(); x++) {
      for (int y = 0; y < image.getHeight(); y++) {
        int p = image.getRGB(x, y);
        if (p != background) {
          n++;
          r += (p >> 16) & 0xFF;
          g += (p >> 8) & 0xFF;
          b += p & 0xFF;
          a += (p >>> 24) & 0xFF;
        }
      }
    }
    double ratio = 100.0 * n / (image.getWidth() * image.getHeight());
    return String.format("%.1f%% drawn, mean color %d,%d,%d alpha %d, background %08x, type %d",
        ratio, n == 0 ? 0 : r / n, n == 0 ? 0 : g / n, n == 0 ? 0 : b / n, n == 0 ? 0 : a / n,
        background, image.getType());
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
