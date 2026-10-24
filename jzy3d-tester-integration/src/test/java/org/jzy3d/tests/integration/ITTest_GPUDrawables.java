package org.jzy3d.tests.integration;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.imageio.ImageIO;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.chart.factories.ChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.junit.NativeChartTester;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.PlaneAxis;
import org.jzy3d.plot3d.primitives.textured.MaskPair;
import org.jzy3d.plot3d.primitives.textured.NativeDrawableImage;
import org.jzy3d.plot3d.primitives.textured.TexturedCube;
import org.jzy3d.plot3d.primitives.vbo.ShaderMeshDrawableVBO;
import org.jzy3d.plot3d.primitives.vbo.ShaderMeshVBOBuilder;
import org.jzy3d.plot3d.primitives.vbo.ShaderWaterfallDrawableVBO;
import org.jzy3d.plot3d.primitives.vbo.ShaderWaterfallVBOBuilder;
import org.jzy3d.plot3d.primitives.vbo.builders.VBOBuilderListCoord3d;
import org.jzy3d.plot3d.primitives.vbo.drawable.DrawableVBO2;
import org.jzy3d.plot3d.primitives.vbo.drawable.ScatterVBO;
import org.jzy3d.plot3d.primitives.vbo.drawable.SphereVBO;
import org.jzy3d.plot3d.primitives.volume.Texture3D;
import org.jzy3d.plot3d.rendering.textures.BufferedImageTexture;
import org.jzy3d.plot3d.rendering.view.HiDPI;
import org.jzy3d.plot3d.text.renderers.TextBitmapRenderer;

/**
 * Render GPU based drawables (vertex buffer objects, shaders, 3D textures) with JOGL and PanamaGL
 * on the same computer and verify both images are nearly identical.
 *
 * Images are written to <code>target/gpu-drawables/</code>.
 */
public class ITTest_GPUDrawables extends ITTest {
  static final String OUTPUT = "target/gpu-drawables/";

  /** Maximum ratio of pixels differing between JOGL and PanamaGL */
  static final double MAX_DIFF_RATIO = 0.01;

  @Test
  public void whenDrawableVBO2WithElements_ThenPanamaGLMatchesJOGL() throws IOException {
    assertParity("DrawableVBO2_Elements", chart -> {
      // A triangle mesh of a grid, colored by a colormap
      float[][] xyz = grid();
      float[] x = xyz[0];
      float[] y = xyz[1];
      float[] z = xyz[2];

      float[] vertices = new float[x.length * y.length * 3];
      for (int j = 0; j < y.length; j++) {
        for (int i = 0; i < x.length; i++) {
          int v = (i + j * x.length) * 3;
          vertices[v] = x[i];
          vertices[v + 1] = y[j];
          vertices[v + 2] = z[i + j * x.length];
        }
      }

      int[] elements = new int[(x.length - 1) * (y.length - 1) * 6];
      int e = 0;
      for (int j = 0; j < y.length - 1; j++) {
        for (int i = 0; i < x.length - 1; i++) {
          int a = i + j * x.length;
          int b = a + 1;
          int c = a + x.length;
          int d = c + 1;
          elements[e++] = a;
          elements[e++] = b;
          elements[e++] = d;
          elements[e++] = a;
          elements[e++] = d;
          elements[e++] = c;
        }
      }
      chart.add(new DrawableVBO2(vertices, 3, elements, new ColorMapRainbow()));
    });
  }

  @Test
  public void whenDrawableVBO2WithMultiDrawArrays_ThenPanamaGLMatchesJOGL() throws IOException {
    assertParity("DrawableVBO2_MultiDrawArrays", chart -> {
      // Two triangles described by their first vertex and number of vertices
      float[] points = {0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 1, 1, 2, 1, 1, 1, 2, 1};
      int[] starts = {0, 3};
      int[] lengths = {3, 3};
      float[] colors = {1, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 1};
      DrawableVBO2 vbo = new DrawableVBO2(points, 3, starts, lengths, colors);
      chart.add(vbo);
    });
  }

  @Test
  public void whenScatterVBO_ThenPanamaGLMatchesJOGL() throws IOException {
    assertParity("ScatterVBO", chart -> {
      List<Coord3d> points = new ArrayList<>();
      java.util.Random r = new java.util.Random(0);
      for (int i = 0; i < 5000; i++) {
        points.add(new Coord3d(r.nextFloat(), r.nextFloat(), r.nextFloat()));
      }
      ColorMapper mapper = new ColorMapper(new ColorMapRainbow(), 0, 1);
      ScatterVBO scatter = new ScatterVBO(new VBOBuilderListCoord3d(points, mapper));
      scatter.setWidth(3);
      chart.add(scatter);
    });
  }

  @Test
  public void whenSphereVBO_ThenPanamaGLMatchesJOGL() throws IOException {
    assertParity("SphereVBO", chart -> {
      chart.add(new SphereVBO(new Coord3d(0, 0, 0), 1, 20, 20, Color.BLUE));
    });
  }

  @Test
  public void whenShaderMesh_ThenPanamaGLMatchesJOGL() throws IOException {
    assertParity("ShaderMesh", chart -> {
      float[][] xyz = grid();
      ColorMapper mapper = new ColorMapper(new ColorMapRainbow(), -1, 1);
      ShaderMeshVBOBuilder builder = new ShaderMeshVBOBuilder(xyz[0], xyz[1], xyz[2], mapper);
      ShaderMeshDrawableVBO shape = new ShaderMeshDrawableVBO(builder, mapper);
      builder.earlyInitalise(shape);
      chart.add(shape);
    });
  }

  @Test
  public void whenShaderWaterfall_ThenPanamaGLMatchesJOGL() throws IOException {
    assertParity("ShaderWaterfall", chart -> {
      float[][] xyz = grid();
      ColorMapper mapper = new ColorMapper(new ColorMapRainbow(), -1, 1);
      ShaderWaterfallVBOBuilder builder =
          new ShaderWaterfallVBOBuilder(xyz[0], xyz[1], xyz[2], mapper);
      ShaderWaterfallDrawableVBO shape = new ShaderWaterfallDrawableVBO(builder, mapper);
      builder.earlyInitalise(shape);
      chart.add(shape);
    });
  }

  @Test
  public void whenTexture3D_ThenPanamaGLMatchesJOGL() throws IOException {
    assertParity("Texture3D", chart -> {
      ColorMapper mapper = new ColorMapper(new ColorMapRainbow(), 0, 1, new Color(1, 1, 1, .5f));

      ByteBuffer buffer = ByteBuffer.allocateDirect(10 * 10 * 10 * 4).order(ByteOrder.nativeOrder());
      for (int i = 0; i < 10; i++) {
        for (int j = 0; j < 10; j++) {
          for (int k = 0; k < 10; k++) {
            float x = i * 0.2f;
            float y = j * 0.2f;
            float z = k * 0.2f;
            buffer.putFloat((float) Math.sin(x * y * z));
          }
        }
      }
      // GL reads the texture from the buffer position
      buffer.rewind();
      chart.getView().getAxis().setTextRenderer(new TextBitmapRenderer());
      chart.add(new Texture3D(buffer, new int[] {10, 10, 10}, 0, 1, mapper,
          new BoundingBox3d(1, 10, 1, 10, 1, 10)));
    });
  }

  @Test
  public void whenTexturedImage_ThenPanamaGLMatchesJOGL() throws IOException {
    assertParity("TexturedImage", chart -> {
      List<Coord2d> mapping = new ArrayList<>();
      mapping.add(new Coord2d(-1, -1));
      mapping.add(new Coord2d(1, -1));
      mapping.add(new Coord2d(1, 1));
      mapping.add(new Coord2d(-1, 1));

      chart.add(new NativeDrawableImage(new BufferedImageTexture(image(Color.BLUE)), PlaneAxis.Z,
          0, mapping));
      chart.getView().setBoundsManual(new BoundingBox3d(-1, 1, -1, 1, -1, 1));
    });
  }

  @Test
  public void whenTexturedCube_ThenPanamaGLMatchesJOGL() throws IOException {
    assertParity("TexturedCube", chart -> {
      MaskPair masks = new MaskPair(new BufferedImageTexture(image(Color.WHITE)),
          new BufferedImageTexture(image(Color.RED)));
      chart.add(new TexturedCube(new Coord3d(), Color.CYAN, Color.RED, masks, 1f));
      chart.getView().setBoundsManual(new BoundingBox3d(-1, 1, -1, 1, -1, 1));
    });
  }

  // ---------------------------------------------------------------------------------------------

  /**
   * An asymmetric image (a disk in the top left corner, a bar at the bottom) to verify texture
   * orientation.
   */
  protected BufferedImage image(Color color) {
    BufferedImage image = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = image.createGraphics();
    g.setColor(java.awt.Color.YELLOW);
    g.fillRect(0, 0, 64, 64);
    g.setColor(new java.awt.Color(color.r, color.g, color.b));
    g.fillOval(4, 4, 24, 24);
    g.setColor(java.awt.Color.BLACK);
    g.fillRect(0, 52, 64, 12);
    g.dispose();
    return image;
  }

  /** A grid of 30x20 points for mesh and waterfall shaders */
  protected float[][] grid() {
    float[] x = new float[30];
    for (int i = 0; i < x.length; i++)
      x[i] = -3f + 6f * i / (x.length - 1);

    float[] y = new float[20];
    for (int i = 0; i < y.length; i++)
      y[i] = -3f + 6f * i / (y.length - 1);

    float[] z = new float[x.length * y.length];
    for (int i = 0; i < x.length; i++)
      for (int j = 0; j < y.length; j++)
        z[i + j * x.length] = (float) (Math.sin(x[i]) * Math.cos(y[j]));

    return new float[][] {x, y, z};
  }

  protected void assertParity(String name, Consumer<Chart> content) throws IOException {
    new File(OUTPUT).mkdirs();

    BufferedImage jogl = render(new AWTChartFactory(), content);
    ImageIO.write(jogl, "png", new File(OUTPUT + name + "_JOGL.png"));

    Assume.assumeTrue("PanamaGL is not in classpath", isPanamaGLAvailable());

    BufferedImage panama = render(newChartFactory(PANAMAGL_SWING_FACTORY), content);
    ImageIO.write(panama, "png", new File(OUTPUT + name + "_PanamaGL.png"));

    double ratio = ITTest_PanamaGLParity.diffRatio(jogl, panama);

    Assert.assertTrue(name + " : " + (100 * ratio) + "% of pixels differ between JOGL and PanamaGL",
        ratio <= MAX_DIFF_RATIO);
  }

  protected BufferedImage render(ChartFactory factory, Consumer<Chart> content)
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
}
