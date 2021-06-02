package org.jzy3d.plot3d.primitives.vbo.drawable;

import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.colors.colormaps.IColorMap;
import org.jzy3d.plot3d.rendering.canvas.Quality;

/**
 * These test verify consistency of internal VBO state according to its configuration.
 * 
 * Despite not doing pixel-wise non regression, we still open a chart to get a GL instance available
 * as it is required to mount the VBO
 * 
 * @author martin
 */
public class TestDrawableVBO2 {
  
  /* *************************************************************/
  /* ************************* NORMALS ****************************/
  /* *************************************************************/

  @Test
  public void whenElementListGiven_ThenAverageNormalAreComputed() throws InterruptedException {
    Assert.assertTrue(DrawableVBO2.COMPUTE_NORMALS_IN_JAVA);
    
    // Given
    AWTChartFactory f = new AWTChartFactory();
    f.getPainterFactory().setOffscreen(800, 600);
    Chart chart = f.newChart(Quality.Intermediate());

    int dimensions = TestMesh.DIMENSIONS;
    double[] vertices = TestMesh.makeArray4();
    int[] elements = TestMesh.makeElementArray4();
    int nVertices = TestMesh.nVertices(vertices);

    // When
    DrawableVBO2 vbo = new DrawableVBO2(vertices, dimensions, elements, null);
    chart.add(vbo);

    // Then
    Assert.assertEquals(3 * nVertices, vbo.getVertices().capacity());
    Assert.assertEquals(3 * nVertices, vbo.getNormals().capacity());
    Assert.assertEquals(elements.length, vbo.getElements().capacity());
    
    Assert.assertNotEquals("An array ID was generated and is NOT 0", 0, vbo.getNormalArrayIds()[0]);
    Assert.assertNotEquals("An array ID was generated and is NOT 0", 0, vbo.getVertexArrayIds()[0]);
    Assert.assertNotEquals("An array ID was generated and is NOT 0", 0, vbo.getElementArrayIds()[0]);
  }

  @Test
  public void when_NO_ElementListGiven_ThenSimpleNormalAreComputed() throws InterruptedException {
    Assert.assertTrue(DrawableVBO2.COMPUTE_NORMALS_IN_JAVA);

    // Given
    AWTChartFactory f = new AWTChartFactory();
    f.getPainterFactory().setOffscreen(800, 600);
    Chart chart = f.newChart(Quality.Intermediate());

    int dimensions = TestMesh.DIMENSIONS;
    double[] vertices = TestMesh.makeArray1();
    int nVertices = TestMesh.nVertices(vertices);

    // When
    DrawableVBO2 vbo = new DrawableVBO2(vertices, dimensions, null, null);
    chart.add(vbo);

    // Then
    Assert.assertNull("There is NO vertex index defined", vbo.getElements());

    Assert.assertEquals(3 * nVertices, vbo.getVertices().capacity());
    Assert.assertEquals(3 * nVertices, vbo.getNormals().capacity());
    //Assert.assertEquals("There is one normal per vertice", vbo.getVertices().capacity(), vbo.getNormals().capacity());
    Assert.assertNotEquals("An array ID was generated and is NOT 0", 0, vbo.getNormalArrayIds()[0]);
    Assert.assertNotEquals("An array ID was generated and is NOT 0", 0, vbo.getVertexArrayIds()[0]);
    Assert.assertEquals("NO array ID was generated", 0, vbo.getElementArrayIds()[0]);
  }
  
  @Test
  public void whenJavaNormalDisabled_ThenNoNormalAreProcessed() throws InterruptedException {
    DrawableVBO2.COMPUTE_NORMALS_IN_JAVA = false;

    // Given
    AWTChartFactory f = new AWTChartFactory();
    f.getPainterFactory().setOffscreen(800, 600);
    Chart chart = f.newChart(Quality.Intermediate());

    int dimensions = TestMesh.DIMENSIONS;
    double[] vertices = TestMesh.makeArray1();
    int nVertices = TestMesh.nVertices(vertices);

    // When
    DrawableVBO2 vbo = new DrawableVBO2(vertices, dimensions, null, null);
    chart.add(vbo);

    // Then
    Assert.assertNull("There is NO normal defined", vbo.getNormals());
    Assert.assertEquals("NO array ID was generated", 0, vbo.getNormalArrayIds()[0]);
    Assert.assertEquals("NO array ID was generated", 0, vbo.getElementArrayIds()[0]);
    Assert.assertNotEquals("An array ID was generated and is NOT 0", 0, vbo.getVertexArrayIds()[0]);
  }
  
  /* *************************************************************/
  /* ************************* COLORS ****************************/
  /* *************************************************************/
  
  @Test
  public void when_NO_ColormapGiven_ThenNoColorbufferIsDefined() {

    // Given
    AWTChartFactory f = new AWTChartFactory();
    f.getPainterFactory().setOffscreen(800, 600);
    Chart chart = f.newChart(Quality.Intermediate());

    int dimensions = TestMesh.DIMENSIONS;
    double[] vertices = TestMesh.makeArray4();
    int[] elements = TestMesh.makeElementArray4();
    int nVertices = TestMesh.nVertices(vertices);

    // When
    DrawableVBO2 vbo = new DrawableVBO2(vertices, dimensions, elements, null);
    chart.add(vbo);

    // Then
    Assert.assertEquals(3 * nVertices, vbo.getVertices().capacity());

    Assert.assertFalse(vbo.isHasColorBuffer());
    Assert.assertEquals("No array ID was generated and remained 0", 0, vbo.getColorArrayIds()[0]);
    Assert.assertNull(vbo.getColors());

  }

  @Test
  public void whenColormapGiven_ThenColorbufferIsComputed() throws InterruptedException {

    // Given
    AWTChartFactory f = new AWTChartFactory();
    f.getPainterFactory().setOffscreen(800, 600);
    Chart chart = f.newChart(Quality.Intermediate());

    IColorMap colormap = new ColorMapRainbow();
    int dimensions = TestMesh.DIMENSIONS;
    double[] vertices = TestMesh.makeArray4();
    int[] elements = TestMesh.makeElementArray4();
    int nVertices = TestMesh.nVertices(vertices);


    // When
    DrawableVBO2 vbo = new DrawableVBO2(vertices, dimensions, elements, colormap);
    chart.add(vbo);

    // Then
    Assert.assertEquals(3 * nVertices, vbo.getVertices().capacity());
    
    Assert.assertTrue(vbo.isHasColorBuffer());
    Assert.assertNotEquals("An array ID was generated and is NOT 0", 0, vbo.getColorArrayIds()[0]);
    Assert.assertNotNull(vbo.getColors());
    Assert.assertEquals(3 * nVertices, vbo.getColors().capacity());

  }

}
