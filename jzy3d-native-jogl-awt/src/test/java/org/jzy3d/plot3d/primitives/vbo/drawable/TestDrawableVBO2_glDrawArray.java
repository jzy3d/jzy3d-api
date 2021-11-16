package org.jzy3d.plot3d.primitives.vbo.drawable;

import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.maths.Array;
import org.jzy3d.plot3d.rendering.canvas.Quality;

/**
 * These test verify consistency of internal VBO state according to its configuration.
 * 
 * Despite not doing pixel-wise non regression, we still open a chart to get a GL instance available
 * as it is required to mount the VBO
 * 
 * @author martin
 */

//@Ignore // TRY TO SEE IF GITLAB CI BETTER PASS WITHOUT THIS TEST

public class TestDrawableVBO2_glDrawArray {

  
  @Test
  public void givenGivenVerticeOnly_WhenLoading_ThenSimpleNormalAreComputed() throws InterruptedException {

    // Given
    AWTChartFactory f = new AWTChartFactory();
    f.getPainterFactory().setOffscreen(800, 600);
    Chart chart = f.newChart(Quality.Intermediate());

    int dimensions = TestMesh.DIMENSIONS;
    float[] vertices = Array.cloneFloat(TestMesh.makeArray4());
    int nVertices = TestMesh.nVertices(vertices);

    // When
    DrawableVBO2 vbo = new DrawableVBO2(vertices, dimensions);
    
    Assert.assertTrue(vbo.isComputeNormals());
    
    chart.add(vbo);

    // Then
    Assert.assertNull("There is NO vertex index defined", vbo.getElements());

    Assert.assertEquals(3 * nVertices, vbo.getVertices().capacity());
    Assert.assertEquals(3 * nVertices, vbo.getNormals().capacity());
    Assert.assertEquals("There is one normal per vertice", vbo.getVertices().capacity(), vbo.getNormals().capacity());
    
    Assert.assertNotEquals("An array ID was generated and is NOT 0", 0, vbo.getNormalArrayIds()[0]);
    Assert.assertNotEquals("An array ID was generated and is NOT 0", 0, vbo.getVertexArrayIds()[0]);
    Assert.assertEquals("NO array ID was generated", 0, vbo.getElementArrayIds()[0]);
  }

  @Test
  public void givenNormalDisabled_WhenLoading_ThenNoNormalAreProcessed() throws InterruptedException {

    // Given
    AWTChartFactory f = new AWTChartFactory();
    f.getPainterFactory().setOffscreen(800, 600);
    Chart chart = f.newChart(Quality.Intermediate());

    int dimensions = TestMesh.DIMENSIONS;
    float[] vertices = Array.cloneFloat(TestMesh.makeArray1());

    // When
    DrawableVBO2 vbo = new DrawableVBO2(vertices, dimensions);
    
    vbo.setComputeNormals(false);
    
    chart.add(vbo);

    // Then
    Assert.assertNull("There is NO normal defined", vbo.getNormals());
    Assert.assertEquals("NO array ID was generated", 0, vbo.getNormalArrayIds()[0]);
    Assert.assertEquals("NO array ID was generated", 0, vbo.getElementArrayIds()[0]);
    Assert.assertNotEquals("An array ID was generated and is NOT 0", 0, vbo.getVertexArrayIds()[0]);
  }

}
