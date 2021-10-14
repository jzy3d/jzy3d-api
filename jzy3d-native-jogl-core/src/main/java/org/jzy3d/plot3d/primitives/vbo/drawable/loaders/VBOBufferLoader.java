package org.jzy3d.plot3d.primitives.vbo.drawable.loaders;



import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.IColorMap;
import org.jzy3d.io.BufferUtil;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Normal;
import org.jzy3d.maths.Normal.NormalMode;
import org.jzy3d.plot3d.primitives.vbo.drawable.DrawableVBO2;
import com.google.common.collect.ArrayListMultimap;
import com.jogamp.common.nio.Buffers;

public class VBOBufferLoader {
  protected boolean verifyUniquePoints = false;

  // *************************** VERTICES ******************************/
  
  public FloatBuffer loadVerticesFromArray(float[] points, int pointDimensions,
      List<Coord3d> verticeList, BoundingBox3d bounds) {

    // input points may contain extra dimensions, so we first compute the number of points, and then
    // define the buffer capacity which will contain exactly 3 dimension, since we will
    // bypass possible extra dimensions
    int capa = (points.length / pointDimensions) * 3;

    FloatBuffer vertices = Buffers.newDirectFloatBuffer(capa);

    for (int i = 0; i < points.length; i += pointDimensions) {
      // Store values
      vertices.put((float) points[i]);
      vertices.put((float) points[i + 1]);
      vertices.put((float) points[i + 2]);

      // Hold bounds
      bounds.add(points[i], points[i + 1], points[i + 2]);

      // Keep for later processing
      Coord3d c = new Coord3d(points[i], points[i + 1], points[i + 2]);
      verticeList.add(c);
    }

    BufferUtil.rewind(vertices);

    return vertices;
  }

  // *************************** COLORS ******************************/

  public FloatBuffer loadColorBufferFromArray(float[] coloring) {
    FloatBuffer colors = Buffers.newDirectFloatBuffer(coloring);
    BufferUtil.rewind(colors);
    return colors;
  }

  public FloatBuffer loadColorBufferFromColormap(List<Coord3d> verticeList, BoundingBox3d bounds,
      int colorChannels, IColorMap colormap) {

    FloatBuffer colors = Buffers.newDirectFloatBuffer(verticeList.size() * colorChannels);

    ColorMapper colorMapper = new ColorMapper(colormap, bounds.getZmin(), bounds.getZmax());

    for (Coord3d c : verticeList) {
      Color color = colorMapper.getColor(c);

      colors.put(color.r);
      colors.put(color.g);
      colors.put(color.b);

      if (colorChannels > 3)
        colors.put(color.a);
    }

    BufferUtil.rewind(colors);

    return colors;
  }
  
  // *************************** NORMALS ******************************/
  
  public FloatBuffer loadNormalsFromArray(float[] points) {
    return Buffers.newDirectFloatBuffer(points);
  }

  /**
   * 
   * @param pointsPerGeometry 3 for a triangle, N for a triangle fan, 4 for a pseudo quad (a 2
   *        triangle fans)
   * @param verticeList
   * @return
   */
  public FloatBuffer computeSimpleNormals(int pointsPerGeometry, List<Coord3d> verticeList) {

    FloatBuffer normals =
        Buffers.newDirectFloatBuffer(verticeList.size() * DrawableVBO2.VERTEX_DIMENSIONS);

    for (int i = 0; i <= (verticeList.size() - pointsPerGeometry); i += pointsPerGeometry) {
      // gather coordinates of a triangle
      Coord3d c0 = verticeList.get(i + 0);
      Coord3d c1 = verticeList.get(i + 1);
      Coord3d c2 = verticeList.get(i + 2);

      // compute normal
      Coord3d normal = Normal.compute(c0, c1, c2);

      for (int j = 0; j < pointsPerGeometry; j++) {
        // repeat normals for each point of the geometry, even if the geometry
        // has more point than the three that were used for computing the normal
        normals.put(normal.x);
        normals.put(normal.y);
        normals.put(normal.z);
      }
    }

    BufferUtil.rewind(normals);

    return normals;
  }

  /**
   * When vertices are shared between multiple triangles, each vertex normal should be unique and
   * computed out of all triangles that share the vertex.
   * 
   * Indeed, the normal being computed out of the cross product of the three points of a face, a
   * vertex belonging to N faces will have N normals.
   * 
   * Getting a single normal for a shared vertex is made by computing the mean of all the normals
   * that may computed for this vertex, which has the great advantage of smoothing the light
   * reflection on the border (in other word this avoid the impression of a sharp border).
   * 
   * In case a sharp border effect is desired for this object, then one should not share vertices
   * between faces. To do so, do NOT provide any <code>geometry</code> array as {@link DrawableVBO2}
   * constructor.
   * 
   * @param geometries
   * @param geometrySize
   * @param verticeList
   * @return
   */
  public FloatBuffer computeSharedNormals(int[] geometries, int geometrySize,
      List<Coord3d> verticeList) {

    ArrayListMultimap<Coord3d, Coord3d> vertexNormals = ArrayListMultimap.create();

    Map<Coord3d, Integer> vertexPosition = new HashMap<>();

    for (int i = 0; i < (geometries.length - geometrySize); i += geometrySize) {
      // gather coordinates of a triangle
      Coord3d c0 = verticeList.get(geometries[i + 0]);
      Coord3d c1 = verticeList.get(geometries[i + 1]);
      Coord3d c2 = verticeList.get(geometries[i + 2]);

      // compute normal
      Coord3d normal = Normal.compute(c0, c1, c2);

      // append normals for each geometry
      vertexNormals.put(c0, normal);
      vertexNormals.put(c1, normal);
      vertexNormals.put(c2, normal);

      // Keep index of each vertex to later store the normal at appropriate position
      vertexPosition.put(c0, geometries[i + 0]);
      vertexPosition.put(c1, geometries[i + 1]);
      vertexPosition.put(c2, geometries[i + 2]);

    }


    // average all normals that come from all triangles sharing this
    // vertex
    return computeAverageNormalsForEachVertex(verticeList, vertexNormals);

  }


  /**
   * 
   * @param elementIndices
   * @param verticeList
   * @return
   */
  public FloatBuffer computeSharedNormals(int[][] elementIndices, List<Coord3d> verticeList) {

    if (verifyUniquePoints) {
      verifyDoublons(verticeList);
    }

    ArrayListMultimap<Coord3d, Coord3d> vertexNormals = ArrayListMultimap.create();

    // For each geometry
    for (int i = 0; i < elementIndices.length; i++) {

      // get index of points of this geometry
      int[] geometryIndex = elementIndices[i];

      // check we can compute a normal out of it
      if (geometryIndex.length < 3) {
        throw new IllegalArgumentException(
            "Can not process normals from a geometry with less than 3 points");
      }

      if (geometryIndex.length != 4) {
        throw new IllegalArgumentException("Unexpected!");
      }

      // gather coordinates of a triangle
      Coord3d c0 = verticeList.get(geometryIndex[0]);
      Coord3d c1 = verticeList.get(geometryIndex[1]);
      Coord3d c2 = verticeList.get(geometryIndex[2]);

      // compute normal
      Coord3d normal = Normal.compute(c0, c1, c2);

      // append normals for each point of this geometry (not only the 3 first)
      vertexNormals.put(c0, normal);
      vertexNormals.put(c1, normal);
      vertexNormals.put(c2, normal);

      for (int j = 3; j < geometryIndex.length; j++) {
        Coord3d cJ = verticeList.get(geometryIndex[j]);
        // System.out.println("adding normal for " + j);
        vertexNormals.put(cJ, normal);
      }
    }

    // average all normals that come from all triangles sharing this
    // vertex
    return computeAverageNormalsForEachVertex(verticeList, vertexNormals);
  }

  /**
   * Process the average normal of a points, based on parameter <code>vertexNormals</code> point is
   * mapped to a list of all normals of all polygons that have this point in common.
   * 
   * @param verticeList provides all vertices in the order they are sent to the GPU via the VBO.
   *        List<Coord3d> is easier to process than the original vertex FloatBuffer
   * @param vertexNormals associate coordinate -> <normal1, normal2, ... normalN>.
   * @return a buffer of average normals of the same size of the vertex buffer that was built at the
   *         same time than verticeList
   */
  protected FloatBuffer computeAverageNormalsForEachVertex(List<Coord3d> verticeList,
      ArrayListMultimap<Coord3d, Coord3d> vertexNormals) {
    Coord3d[] averagedNormals = new Coord3d[vertexNormals.keySet().size()];

    //assert vertexNormals.keySet().size() == verticeList.size();

    for (Coord3d vertex : vertexNormals.keySet()) {
      Coord3d averagedNormal = new Coord3d();

      List<Coord3d> normals = vertexNormals.get(vertex);

      for (Coord3d normal : normals) {
        averagedNormal.addSelf(normal);
      }
      averagedNormal.x /= normals.size();
      averagedNormal.y /= normals.size();
      averagedNormal.z /= normals.size();

      // Get vertex position
      int position = verticeList.indexOf(vertex);

      averagedNormals[position] = averagedNormal;
    }

    FloatBuffer normals =
        Buffers.newDirectFloatBuffer(verticeList.size() * DrawableVBO2.VERTEX_DIMENSIONS);

    for (Coord3d averagedNormal : averagedNormals) {
      normals.put(averagedNormal.x);
      normals.put(averagedNormal.y);
      normals.put(averagedNormal.z);
    }
    BufferUtil.rewind(normals);

    // Build a Buffer with vertices AND normals. Nice but less readable than a separate
    // normal buffer.
    //
    // FloatBuffer verticeAndNormals = FloatBuffer.allocate(verticeList.size() * VERTEX_SIZE *
    // 2);
    // for (int j = 0; j < verticeList.size(); j++) {
    // Coord3d v = verticeList.get(j);
    // Coord3d n = averagedNormals[j];
    //
    // verticeAndNormals.put(v.x);
    // verticeAndNormals.put(v.y);
    // verticeAndNormals.put(v.z);
    // verticeAndNormals.put(n.x);
    // verticeAndNormals.put(n.y);
    // verticeAndNormals.put(n.z);
    // }
    // verticeAndNormals.rewind();

    return normals;
  }
  
  // ******************************************************* //
  
  
  
  protected void verifyDoublons(List<Coord3d> verticeList) {
    Set<Coord3d> uniquePoints = new HashSet<>(verticeList);

    if (uniquePoints.size() != verticeList.size()) {
      throw new IllegalArgumentException(verticeList.size() + " points but only "
          + uniquePoints.size() + " are unique. Either fix the input geometry or use NormalMode."
          + NormalMode.REPEATED);
    }
  }


  public boolean isVerifyUniquePoints() {
    return verifyUniquePoints;
  }


  public void setVerifyUniquePoints(boolean verifyUniquePoints) {
    this.verifyUniquePoints = verifyUniquePoints;
  }

  

}
