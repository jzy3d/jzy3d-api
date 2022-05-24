package org.jzy3d.plot3d.primitives.vbo.drawable;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.io.BufferUtil;
import org.jzy3d.maths.Array;
import org.jzy3d.maths.Coord3d;
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

public class TestDrawableVBO2_glMultiDrawArrays {

  float X0 = 0;
  float Y0 = 0;
  float Z0 = 0;

  float X1 = 1;
  float Y1 = 1;
  float Z1 = 1;
  
  Coord3d X1Y1Z1 = new Coord3d(X1,Y1,Z1);
  Coord3d X0Y0Z0 = new Coord3d(X0,Y0,Z0);    


  boolean offscreen = true;

  public static void main(String[] args) {
    new TestDrawableVBO2_glMultiDrawArrays().givenVerticesArray_WhenLoading_ThenBuffersAppropriatelyFilled();
  }

  /* *************************************************************/

  /**
   * Building VBO this way assume points are repeated and ordered correctly (no interlacing
   * geometries)
   */
  @Test
  public void givenVerticesArray_WhenLoading_ThenBuffersAppropriatelyFilled() {

    // Given
    AWTChartFactory f = new AWTChartFactory();
    if (offscreen)
      f.getPainterFactory().setOffscreen(800, 600);

    Quality q = Quality.Advanced().setAlphaActivated(false);
    Chart chart = f.newChart(q);

    if (!offscreen) {
      chart.open();
      chart.addMouseCameraController();
    }


    // -------------------------------------------
    // When
    float[] vertices = new float[72];
    int k = 0;

    // bottom
    vertices[k++] = X0;
    vertices[k++] = Y0;
    vertices[k++] = Z0;

    vertices[k++] = X1;
    vertices[k++] = Y0;
    vertices[k++] = Z0;

    vertices[k++] = X1;
    vertices[k++] = Y1;
    vertices[k++] = Z0;

    vertices[k++] = X0;
    vertices[k++] = Y1;
    vertices[k++] = Z0;

    // top
    vertices[k++] = X0;
    vertices[k++] = Y0;
    vertices[k++] = Z1;

    vertices[k++] = X1;
    vertices[k++] = Y0;
    vertices[k++] = Z1;

    vertices[k++] = X1;
    vertices[k++] = Y1;
    vertices[k++] = Z1;

    vertices[k++] = X0;
    vertices[k++] = Y1;
    vertices[k++] = Z1;

    // left
    vertices[k++] = X0;
    vertices[k++] = Y0;
    vertices[k++] = Z0;

    vertices[k++] = X0;
    vertices[k++] = Y0;
    vertices[k++] = Z1;

    vertices[k++] = X0;
    vertices[k++] = Y1;
    vertices[k++] = Z1;

    vertices[k++] = X0;
    vertices[k++] = Y1;
    vertices[k++] = Z0;

    // right
    vertices[k++] = X1;
    vertices[k++] = Y0;
    vertices[k++] = Z0;

    vertices[k++] = X1;
    vertices[k++] = Y0;
    vertices[k++] = Z1;

    vertices[k++] = X1;
    vertices[k++] = Y1;
    vertices[k++] = Z1;

    vertices[k++] = X1;
    vertices[k++] = Y1;
    vertices[k++] = Z0;

    // near
    vertices[k++] = X0;
    vertices[k++] = Y0;
    vertices[k++] = Z0;

    vertices[k++] = X0;
    vertices[k++] = Y0;
    vertices[k++] = Z1;

    vertices[k++] = X1;
    vertices[k++] = Y0;
    vertices[k++] = Z1;

    vertices[k++] = X1;
    vertices[k++] = Y0;
    vertices[k++] = Z0;

    // far
    vertices[k++] = X0;
    vertices[k++] = Y1;
    vertices[k++] = Z0;

    vertices[k++] = X0;
    vertices[k++] = Y1;
    vertices[k++] = Z1;

    vertices[k++] = X1;
    vertices[k++] = Y1;
    vertices[k++] = Z1;

    vertices[k++] = X1;
    vertices[k++] = Y1;
    vertices[k++] = Z0;

    int PTS = 4;

    int[] elementStarts = {0, 4, 8, 12, 16, 20};
    int[] elementLength = {PTS, PTS, PTS, PTS, PTS, PTS};

    float[] colors = Array.clone(vertices);

    DrawableVBO2 vbo = new DrawableVBO2(vertices, elementStarts, elementLength, colors);

    Assert.assertTrue(vbo.isComputeNormals());
    
    chart.add(vbo);
    chart.render();

    
    
    Assert.assertEquals(X1Y1Z1,
        chart.getView().getBounds().getCorners().getXmaxYmaxZmax());
    Assert.assertEquals(X0Y0Z0,
        chart.getView().getBounds().getCorners().getXminYminZmin());



    // -------------------------------------------
    // Then

    Assert.assertEquals(72, vbo.getVertices().capacity());
    Assert.assertEquals(72, vbo.getColors().capacity());
    Assert.assertEquals(72, vbo.getNormals().capacity());
    Assert.assertEquals(6, vbo.getElementsStarts().capacity());
    Assert.assertEquals(6, vbo.getElementsLength().capacity());

    Assert.assertNull(vbo.getElements());
    Assert.assertNull(vbo.getElementsCount());
    Assert.assertNull(vbo.getElementsIndices());

    Assert.assertNotEquals("An array ID was generated and is NOT 0", 0, vbo.getVertexArrayIds()[0]);
    Assert.assertNotEquals("An array ID was generated and is NOT 0", 0, vbo.getNormalArrayIds()[0]);
    Assert.assertNotEquals("An array ID was generated and is NOT 0", 0, vbo.getColorArrayIds()[0]);
    
    Assert.assertArrayEquals("Color buffer is equal to input array", colors, BufferUtil.copyFloat(vbo.getColors()), 0.00001f);

    
    // -------------------------------------------
    // When editing colors and rendering again

    for (int i = 0; i < colors.length; i+=3) {
      colors[i+0] = 0.1f;
      colors[i+1] = 0.1f;
      colors[i+2] = 0.8f;
    }
    
    vbo.setColors(colors);
        
    chart.render();
    
    // -------------------------------------------
    // Then buffer is updated

    Assert.assertArrayEquals("Color buffer is equal to input array", colors, BufferUtil.copyFloat(vbo.getColors()), 0.00001f);


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

    /*
     * try { chart.screenshot(new File("target/vbo-polygons.png")); } catch (IOException e) {
     * e.printStackTrace(); }
     */
    Assert.assertTrue(vbo.isHasColorBuffer());
    Assert.assertEquals(3, vbo.getColorChannels());

    Assert.assertEquals(3 * pointsPerPolygon * polygons.size(), vbo.vertices.capacity());
    Assert.assertEquals(vbo.getColorChannels() * pointsPerPolygon * polygons.size(),
        vbo.colors.capacity());

    Assert.assertEquals(polygons.size(), vbo.elementsStarts.capacity());
    Assert.assertEquals(polygons.size(), vbo.elementsLength.capacity());

    /*
     * for (int i = 0; i < vbo.geometryStarts.capacity(); i++) {
     * System.out.println(vbo.geometryStarts.get(i) + " - " + vbo.geometryLength.get(i)); }
     */

    /*
     * for (int i = 0; i < vbo.vertices.capacity(); i+=3) { System.out.println(vbo.vertices.get(i) +
     * ", " + vbo.vertices.get(i+1) + ", " + vbo.vertices.get(i+2)); }
     */

    Assert.assertNull(vbo.elements);

    Assert.assertEquals(3 * pointsPerPolygon * polygons.size(), vbo.normals.capacity());

  }
}
