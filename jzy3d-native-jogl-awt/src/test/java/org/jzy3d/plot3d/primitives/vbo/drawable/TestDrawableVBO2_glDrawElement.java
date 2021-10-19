package org.jzy3d.plot3d.primitives.vbo.drawable;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.colors.colormaps.IColorMap;
import org.jzy3d.maths.Array;
import org.jzy3d.plot3d.primitives.Composite;
import org.jzy3d.plot3d.primitives.Polygon;
import org.jzy3d.plot3d.primitives.RandomGeom;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Decomposition;

/**
 * These test verify consistency of internal VBO state according to its configuration.
 * 
 * Despite not doing pixel-wise non regression, we still open a chart to get a GL instance available
 * as it is required to mount the VBO
 * 
 * @author martin
 */

//@Ignore // TRY TO SEE IF GITLAB CI BETTER PASS WITHOUT THIS TEST

public class TestDrawableVBO2_glDrawElement {

  /* *************************************************************/
  /* ************************* NORMALS ****************************/
  /* *************************************************************/

  @Test
  public void givenElementArray_WhenLoading_ThenAverageNormalAreComputed() throws InterruptedException {

    // Given
    AWTChartFactory f = new AWTChartFactory();
    f.getPainterFactory().setOffscreen(800, 600);
    Chart chart = f.newChart(Quality.Intermediate());

    int dimensions = TestMesh.DIMENSIONS;
    float[] vertices = Array.cloneFloat(TestMesh.makeArray4());
    int[] elements = TestMesh.makeElementArray4();
    int nVertices = TestMesh.nVertices(vertices);

    // When
    DrawableVBO2 vbo = new DrawableVBO2(vertices, dimensions, elements);
    
    Assert.assertTrue(vbo.isComputeNormals());
    
    chart.add(vbo);

    // Then
    Assert.assertEquals(3 * nVertices, vbo.getVertices().capacity());
    Assert.assertEquals(3 * nVertices, vbo.getNormals().capacity());
    Assert.assertEquals(elements.length, vbo.getElements().capacity());

    Assert.assertNotEquals("An array ID was generated and is NOT 0", 0, vbo.getNormalArrayIds()[0]);
    Assert.assertNotEquals("An array ID was generated and is NOT 0", 0, vbo.getVertexArrayIds()[0]);
    Assert.assertNotEquals("An array ID was generated and is NOT 0", 0,
        vbo.getElementArrayIds()[0]);
  }


  /* *************************************************************/
  /* ************************* COLORS ****************************/
  /* *************************************************************/

  @Test
  public void given_NO_Colormap_WhenLoading_ThenNoColorbufferIsDefined() {

    // Given
    AWTChartFactory f = new AWTChartFactory();
    f.getPainterFactory().setOffscreen(800, 600);
    Chart chart = f.newChart(Quality.Intermediate());

    int dimensions = TestMesh.DIMENSIONS;
    float[] vertices = Array.cloneFloat(TestMesh.makeArray4());
    int[] elements = TestMesh.makeElementArray4();
    int nVertices = TestMesh.nVertices(vertices);

    // When
    DrawableVBO2 vbo = new DrawableVBO2(vertices, dimensions, elements);
    chart.add(vbo);

    // Then
    Assert.assertEquals(3 * nVertices, vbo.getVertices().capacity());

    Assert.assertFalse(vbo.isHasColorBuffer());
    Assert.assertEquals("No array ID was generated and remained 0", 0, vbo.getColorArrayIds()[0]);
    Assert.assertNull(vbo.getColors());

  }

  @Test
  public void givenColormap_WhenLoading_ThenColorbufferIsComputed() throws InterruptedException {

    // Given
    AWTChartFactory f = new AWTChartFactory();
    f.getPainterFactory().setOffscreen(800, 600);
    Chart chart = f.newChart(Quality.Intermediate());

    IColorMap colormap = new ColorMapRainbow();
    int dimensions = TestMesh.DIMENSIONS;
    float[] vertices = Array.cloneFloat(TestMesh.makeArray4());
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

  @Test
  public void givenPolygons_WhenLoading_ThenBuffersAppropriatelyFilled() {

    // Given
    RandomGeom g = new RandomGeom();
    List<Composite> cubes = g.spinningCubes(2, 0);
    List<Polygon> polygons = Decomposition.getPolygonDecomposition(cubes);
    int pointsPerPolygon = 4;

    // Given
    AWTChartFactory f = new AWTChartFactory();
    f.getPainterFactory().setOffscreen(800, 600);
    Chart chart = f.newChart(Quality.Intermediate());

    // When
    DrawableVBO2 vbo = new DrawableVBO2(polygons);
    chart.add(vbo);

    /*try {
      chart.screenshot(new File("target/vbo-polygons.png"));
    } catch (IOException e) {
      e.printStackTrace();
    }*/
    Assert.assertTrue(vbo.isHasColorBuffer());
    Assert.assertEquals(3, vbo.getColorChannels());

    Assert.assertEquals(3 * pointsPerPolygon * polygons.size(), vbo.vertices.capacity());
    Assert.assertEquals(vbo.getColorChannels() * pointsPerPolygon * polygons.size(), vbo.colors.capacity());

    Assert.assertEquals(polygons.size(), vbo.elementsStarts.capacity());
    Assert.assertEquals(polygons.size(), vbo.elementsLength.capacity());

    /*for (int i = 0; i < vbo.geometryStarts.capacity(); i++) {
      System.out.println(vbo.geometryStarts.get(i) + " - " + vbo.geometryLength.get(i));
    }
    
    for (int i = 0; i < vbo.vertices.capacity(); i+=3) {
      System.out.println(vbo.vertices.get(i) + ", " + vbo.vertices.get(i+1) + ", " + vbo.vertices.get(i+2));
    }*/
    
    Assert.assertNull(vbo.elements);

    Assert.assertEquals(3 * pointsPerPolygon * polygons.size(), vbo.normals.capacity());

  }

}
