package org.jzy3d.plot3d.primitives.vbo.drawable.loaders;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.io.BufferUtil;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.RandomGeom;

public class TestVBOBufferLoader {
  @Test
  public void givenTwoPolygonWithRepeatedVertice_WhenProcessingSimpleNormals_ThenNormalsAreAllThere() {

    // Given two horizontal polygons
    RandomGeom rg = new RandomGeom();
    List<Coord3d> polygons = new ArrayList<>();
    polygons.addAll(rg.poly(0, 0, 0).getCoordList());
    polygons.addAll(rg.poly(1, 0, 0).getCoordList());

    // When Computing simple normals
    VBOBufferLoader loader = new VBOBufferLoader();
    FloatBuffer b = loader.computeSimpleNormals(4, polygons);

    // Then got one normal per point, which are all pointing toward Z
    Coord3d expectVertical = new Coord3d(0, 0, 1);

    Assert.assertEquals(polygons.size() * 3, b.capacity());
    Assert.assertEquals(expectVertical, BufferUtil.getCoordAt(b, 0));

    for (int i = 0; i < polygons.size(); i++) {
      Assert.assertEquals(expectVertical, BufferUtil.getCoordAt(b, i * 3));
    }

  }

  /**
   * Test a non satisfying algorithm. We show why with algorithm output below
   * 
   * Addresses glMultiDrawElements
   */
  @Test
  public void givenACubeWithSharedVertice_WhenProcessingSharedNormalsForVariableGeometrySize_ThenNormalsAreAllThere() {

    // Given a cube defined by 8 points and an index
    double X0 = 0;
    double Y0 = 0;
    double Z0 = 0;
    double X1 = 1;
    double Y1 = 1;
    double Z1 = 1;

    List<Coord3d> points = new ArrayList<>();
    points.add(new Coord3d(X0, Y0, Z0));
    points.add(new Coord3d(X1, Y0, Z0));
    points.add(new Coord3d(X0, Y1, Z0));
    points.add(new Coord3d(X0, Y0, Z1));
    points.add(new Coord3d(X1, Y1, Z0));
    points.add(new Coord3d(X0, Y1, Z1));
    points.add(new Coord3d(X1, Y0, Z1));
    points.add(new Coord3d(X1, Y1, Z1));

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

    // When Computing shared normals
    VBOBufferLoader loader = new VBOBufferLoader();
    FloatBuffer b = loader.computeSharedNormals(elementIndices, points);
    List<Coord3d> normals = BufferUtil.getCoords(b);

    // Then got one normal per point

    Assert.assertEquals(points.size(), b.capacity() / 3);
    Assert.assertEquals(points.size(), normals.size());

    // None is along X/Y/Z axis, all are diagonals since each point is part of 3 cube side
    
    
    /// BUT WE DON T WANT THESE NORMALS. THEY ALL POINT IN THE SAME DIRECTION BECAUSE WE DON T KNOW 
    // IF A FACE IS TOWARD NORTH OR SOUTH. NEED A BETTER ALGORITHM FOR THIS
    Assert.assertEquals(new Coord3d(0.33333334, -0.33333334, 0.33333334), normals.get(0));
    Assert.assertEquals(new Coord3d(0.33333334, -0.33333334, 0.33333334), normals.get(1));
    Assert.assertEquals(new Coord3d(0.33333334, -0.33333334, 0.33333334), normals.get(2));
    Assert.assertEquals(new Coord3d(0.33333334, -0.33333334, 0.33333334), normals.get(3));
    Assert.assertEquals(new Coord3d(0.33333334, -0.33333334, 0.33333334), normals.get(4));
    Assert.assertEquals(new Coord3d(0.33333334, -0.33333334, 0.33333334), normals.get(5));
    Assert.assertEquals(new Coord3d(0.33333334, -0.33333334, 0.33333334), normals.get(6));
    Assert.assertEquals(new Coord3d(0.33333334, -0.33333334, 0.33333334), normals.get(7));

  }
  
  /*
   * Need a triangle case for testing shared normals
   * 
   * Addresses glMultiDrawArrays
   
  @Test
  public void givenACubeWithSharedVertice_WhenProcessingSharedNormalsForFixedGeometrySize_ThenNormalsAreAllThere() {

    // Given a cube defined by 6 faces of 4 points 
    double X0 = 0;
    double Y0 = 0;
    double Z0 = 0;
    double X1 = 1;
    double Y1 = 1;
    double Z1 = 1;

    List<Coord3d> points = new ArrayList<>();
    
    // bottom
    points.add(new Coord3d(X0, Y0, Z0));
    points.add(new Coord3d(X1, Y0, Z0));
    points.add(new Coord3d(X1, Y1, Z0));
    points.add(new Coord3d(X0, Y1, Z0));
    
    // top
    points.add(new Coord3d(X0, Y0, Z1));
    points.add(new Coord3d(X1, Y0, Z1));
    points.add(new Coord3d(X1, Y1, Z1));
    points.add(new Coord3d(X0, Y1, Z1));
    
    // left
    points.add(new Coord3d(X0, Y0, Z0));
    points.add(new Coord3d(X0, Y0, Z1));
    points.add(new Coord3d(X0, Y1, Z1));
    points.add(new Coord3d(X0, Y1, Z0));

    // right
    points.add(new Coord3d(X1, Y0, Z0));
    points.add(new Coord3d(X1, Y0, Z1));
    points.add(new Coord3d(X1, Y1, Z1));
    points.add(new Coord3d(X1, Y1, Z0));

    // near
    points.add(new Coord3d(X0, Y0, Z0));
    points.add(new Coord3d(X0, Y0, Z1));
    points.add(new Coord3d(X1, Y0, Z1));
    points.add(new Coord3d(X1, Y0, Z0));

    // far
    points.add(new Coord3d(X0, Y1, Z0));
    points.add(new Coord3d(X0, Y1, Z1));
    points.add(new Coord3d(X1, Y1, Z1));
    points.add(new Coord3d(X1, Y1, Z0));

    
    int[] elementStarts = {0, 4, 8, 12, 16, 20}; 
    int SIZE = 4;


    // When Computing shared normals
    VBOBufferLoader loader = new VBOBufferLoader();
    FloatBuffer b = loader.computeSharedNormals(elementStarts, SIZE, points);
    List<Coord3d> normals = BufferUtil.getCoords(b);

    // Then got one normal per point

    Assert.assertEquals(points.size(), b.capacity() / 3);
    Assert.assertEquals(points.size(), normals.size());

    // None is along X/Y/Z axis, all are diagonals since each point is part of 3 cube side
    
    
    /// BUT WE DON T WANT THESE NORMALS. THEY ALL POINT IN THE SAME DIRECTION BECAUSE WE DON T KNOW 
    // IF A FACE IS TOWARD NORTH OR SOUTH. NEED A BETTER ALGORITHM FOR THIS
    Assert.assertEquals(new Coord3d(0.33333334, -0.33333334, 0.33333334), normals.get(0));
    Assert.assertEquals(new Coord3d(0.33333334, -0.33333334, 0.33333334), normals.get(1));
    Assert.assertEquals(new Coord3d(0.33333334, -0.33333334, 0.33333334), normals.get(2));
    Assert.assertEquals(new Coord3d(0.33333334, -0.33333334, 0.33333334), normals.get(3));
    Assert.assertEquals(new Coord3d(0.33333334, -0.33333334, 0.33333334), normals.get(4));
    Assert.assertEquals(new Coord3d(0.33333334, -0.33333334, 0.33333334), normals.get(5));
    Assert.assertEquals(new Coord3d(0.33333334, -0.33333334, 0.33333334), normals.get(6));
    Assert.assertEquals(new Coord3d(0.33333334, -0.33333334, 0.33333334), normals.get(7));

  }*/

}
