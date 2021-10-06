package org.jzy3d.plot3d.primitives.vbo.drawable.loaders;



import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.IColorMap;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Normal;
import org.jzy3d.plot3d.primitives.vbo.drawable.BufferUtil;
import org.jzy3d.plot3d.primitives.vbo.drawable.DrawableVBO2;
import com.google.common.collect.ArrayListMultimap;
import com.jogamp.common.nio.Buffers;

public class VBOBufferLoader {

  public FloatBuffer loadVerticesFromArray(double[] points, int pointDimensions,
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

  public FloatBuffer computeSimpleNormals(int pointsPerGeometry, List<Coord3d> verticeList) {

    FloatBuffer normals =
        Buffers.newDirectFloatBuffer(verticeList.size() * DrawableVBO2.VERTEX_DIMENSIONS);

    for (int i = 0; i < (verticeList.size() - pointsPerGeometry); i += pointsPerGeometry) {
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

    for (int i = 0; i < geometries.length; i += geometrySize) {
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

    Coord3d[] averagedNormals = new Coord3d[vertexNormals.keySet().size()];

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
      int position = vertexPosition.get(vertex);

      averagedNormals[position] = averagedNormal;
    }

    // FloatBuffer normals = FloatBuffer.allocate(verticeList.size() * VERTEX_SIZE);
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

}