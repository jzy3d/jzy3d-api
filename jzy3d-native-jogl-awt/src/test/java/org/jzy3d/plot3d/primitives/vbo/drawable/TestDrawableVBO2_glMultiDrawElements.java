package org.jzy3d.plot3d.primitives.vbo.drawable;

import java.nio.Buffer;
import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.io.BufferUtil;
import org.jzy3d.maths.Array;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Normal.NormalMode;
import org.jzy3d.plot3d.rendering.canvas.Quality;

/**
 * These test verify consistency of internal VBO state according to its configuration.
 * 
 * Despite not doing pixel-wise non regression, we still open a chart to get a GL instance available
 * as it is required to mount the VBO
 * 
 * @author martin
 */
//@Ignore // crashing JVM 11 locally !!!
public class TestDrawableVBO2_glMultiDrawElements {

  float X0 = 0;
  float Y0 = 0;
  float Z0 = 0;

  float X1 = 1;
  float Y1 = 1;
  float Z1 = 1;
  
  boolean offscreen = true;
  
  public static void main(String[] args) {
    TestDrawableVBO2_glMultiDrawElements t = new TestDrawableVBO2_glMultiDrawElements();
    t.offscreen = false;
    
    t.givenVerticesAndIndiceArray_WhenLoading_ThenBuffersAppropriatelyFilled();
  }

  /* *************************************************************/

  
  /**
   * Building VBO this way avoid repeating geometries and allows defining an index to get them
   */
  @Test
  public void givenVerticesAndIndiceArray_WhenLoading_ThenBuffersAppropriatelyFilled() {

    // -------------------------------------------
    // Given
    AWTChartFactory f = new AWTChartFactory();
    if(offscreen)
      f.getPainterFactory().setOffscreen(800, 600);
    
    Quality q = Quality.Advanced().setAlphaActivated(false);
    Chart chart = f.newChart(q);
    
    if(!offscreen) {
      chart.open();
      chart.addMouseCameraController();
    }

    // -------------------------------------------
    // When loading an array
    
    int VERTICES = 8;
    int DIMS = 3;
    
    float[] vertices = new float[VERTICES*DIMS];
    int k = 0;
    
    // 0 : bottom left near (z,x,y)
    vertices[k++] = X0; // 0
    vertices[k++] = Y0;
    vertices[k++] = Z0;

    // 1 : bottom right near
    vertices[k++] = X1; // 1
    vertices[k++] = Y0;
    vertices[k++] = Z0;

    // 2 : bottom left far
    vertices[k++] = X0; // 2
    vertices[k++] = Y1;
    vertices[k++] = Z0;

    // 3 : top left near
    vertices[k++] = X0; // 3
    vertices[k++] = Y0;
    vertices[k++] = Z1;
    
    // 4 : bottom right far
    vertices[k++] = X1; // 4
    vertices[k++] = Y1;
    vertices[k++] = Z0;

    // 5 : top left far
    vertices[k++] = X0;
    vertices[k++] = Y1;
    vertices[k++] = Z1;

    // 6 : top right near
    vertices[k++] = X1;
    vertices[k++] = Y0;
    vertices[k++] = Z1;

    // 7 : top right far
    vertices[k++] = X1;
    vertices[k++] = Y1;
    vertices[k++] = Z1;


    int PTS = 4;
    int SIDES = 6;
    
    int[][] elementIndices = new int[SIDES][PTS];
    
    // bottom side
    elementIndices[0][0] = 0;
    elementIndices[0][1] = 1;
    elementIndices[0][2] = 4;
    elementIndices[0][3] = 2;

    // top side
    elementIndices[1][0] = 3;
    elementIndices[1][1] = 6;
    elementIndices[1][2] = 7;
    elementIndices[1][3] = 5;

    // near side
    elementIndices[2][0] = 0;
    elementIndices[2][1] = 1;
    elementIndices[2][2] = 6;
    elementIndices[2][3] = 3;

    // far side
    elementIndices[3][0] = 2;
    elementIndices[3][1] = 4;
    elementIndices[3][2] = 7;
    elementIndices[3][3] = 5;

    // left side
    elementIndices[4][0] = 0;
    elementIndices[4][1] = 2;
    elementIndices[4][2] = 5;
    elementIndices[4][3] = 3;

    // far side
    elementIndices[5][0] = 1;
    elementIndices[5][1] = 4;
    elementIndices[5][2] = 7;
    elementIndices[5][3] = 6;

    
    float[] colors = Array.clone(vertices);
    
    DrawableVBO2 vbo = new DrawableVBO2(vertices, elementIndices, colors, NormalMode.SHARED);
    
    Assert.assertTrue(vbo.isComputeNormals());
    
    chart.add(vbo);
    chart.addLightOnCamera();

    // -------------------------------------------
    // Then
    Assert.assertEquals(VERTICES*DIMS, vbo.getVertices().capacity());
    Assert.assertEquals(VERTICES*DIMS, vbo.getColors().capacity());
    Assert.assertEquals(VERTICES*DIMS, vbo.getNormals().capacity());
    Assert.assertEquals(6, vbo.getElementsIndices().capacity());
    Assert.assertEquals(6, vbo.getElementsCount().capacity());
    
    Assert.assertNull(vbo.getElements());
    Assert.assertNull(vbo.getElementsStarts());
    Assert.assertNull(vbo.getElementsLength());
    
    
    for (int i = 0; i < 6; i++) {
      Buffer b = vbo.getElementsIndices().getReferencedBuffer(i);
      Assert.assertEquals(PTS, b.capacity());
    }

    Assert.assertNotEquals("An array ID was generated and is NOT 0", 0, vbo.getVertexArrayIds()[0]);
    Assert.assertNotEquals("An array ID was generated and is NOT 0", 0, vbo.getNormalArrayIds()[0]);
    Assert.assertNotEquals("An array ID was generated and is NOT 0", 0, vbo.getColorArrayIds()[0]);
    
    Assert.assertArrayEquals("Color buffer is equal to input array", colors, BufferUtil.copyFloat(vbo.getColors()), 0.00001f);
    
    // Elements not binded on purpose
    //Assert.assertNotEquals("An array ID was generated and is NOT 0", 0, vbo.getElementArrayIds()[0]);
    
    Assert.assertEquals(new Coord3d(1,1,1), chart.getView().getBounds().getCorners().getXmaxYmaxZmax());
    Assert.assertEquals(new Coord3d(0,0,0), chart.getView().getBounds().getCorners().getXminYminZmin());
    
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
}
