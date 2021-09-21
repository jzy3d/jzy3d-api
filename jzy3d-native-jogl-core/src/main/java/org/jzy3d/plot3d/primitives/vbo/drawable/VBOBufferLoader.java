package org.jzy3d.plot3d.primitives.vbo.drawable;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.IColorMap;
import org.jzy3d.io.IGLLoader;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Normal;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.vbo.drawable.DrawableVBO2.NormalMode;
import com.google.common.collect.ArrayListMultimap;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

/**
 * A utility class to build buffers to feed a {@link DrawableVBO2}.
 * 
 * @author Martin Pernollet
 *
 */
public class VBOBufferLoader implements IGLLoader<DrawableVBO2> {
  double[] points;
  int pointDimensions;
  int[] geometries;
  int geometrySize;
  IColorMap colormap;
  float[] coloring;
  NormalMode normalMode;

  public VBOBufferLoader(double[] points, int pointDimensions, int[] geometries, int geometrySize,
      IColorMap colormap, float[] coloring, NormalMode normalMode) {
    super();
    this.points = points;
    this.pointDimensions = pointDimensions;
    this.geometries = geometries;
    this.geometrySize = geometrySize;
    this.colormap = colormap;
    this.coloring = coloring;
    this.normalMode = normalMode;
  }

  @Override
  public void load(IPainter painter, DrawableVBO2 drawable) throws Exception {

    drawable.geometrySize = geometrySize;
    if (geometrySize == DrawableVBO2.TRIANGLE_SIZE) {
      drawable.glGeometryType = GL.GL_TRIANGLES;
    } else if (geometrySize == DrawableVBO2.QUAD_SIZE) {
      drawable.glGeometryType = GL2.GL_POLYGON;// FAN TIMEOUT ON GPU! GL2.GL_TRIANGLE_FAN;
    } else {
      throw new IllegalArgumentException("Unsupported geometry size : " + geometrySize);
    }

    // -------------------------------
    // Vertices
    FloatBuffer vertices = Buffers.newDirectFloatBuffer((points.length / pointDimensions) * 3);
    // FloatBuffer vertices = FloatBuffer.allocate((points.length / pointDimensions) * 3);

    // Temporary list, for computing color and normals later
    List<Coord3d> verticeList = new ArrayList<>();

    BoundingBox3d bounds = new BoundingBox3d();

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
    vertices.rewind();


    // -------------------------------
    // Colors

    FloatBuffer colors = null;

    // drawable.setColorChannels(4);

    if (colormap != null && coloring != null) {
      throw new IllegalArgumentException(
          "Should either define colormap or colors array, or none, but not both");
    } else if (colormap != null) {
      // colors = FloatBuffer.allocate(verticeList.size() * drawable.colorChannels);
      colors = Buffers.newDirectFloatBuffer(verticeList.size() * drawable.getColorChannels());

      ColorMapper colorMapper = new ColorMapper(colormap, bounds.getZmin(), bounds.getZmax());

      for (Coord3d c : verticeList) {
        Color color = colorMapper.getColor(c);

        colors.put(color.r);
        colors.put(color.g);
        colors.put(color.b);

        if (drawable.getColorChannels() > 3)
          colors.put(color.a);
      }
      colors.rewind();
    } else if (coloring != null) {
      colors = Buffers.newDirectFloatBuffer(coloring);
    }

    // -------------------------------
    // Triangles

    IntBuffer elements = null;

    if (geometries != null) {
      // elements = IntBuffer.allocate(geometries.length);
      elements = Buffers.newDirectIntBuffer(geometries.length);
      elements.put(geometries);
      elements.rewind();
    }

    // -------------------------------
    // Normals

    FloatBuffer normals = null;

    if (DrawableVBO2.COMPUTE_NORMALS_IN_JAVA) {
      if (geometries != null && NormalMode.SHARED.equals(normalMode)) {
        normals = computeSharedNormals(geometries, geometrySize, verticeList);
      } else {
        normals = computeSimpleNormals(verticeList);
      }
    }


    // -------------------------------
    // Store data

    drawable.setHasNormalInVertexArray(false);
    drawable.setData(painter, elements, vertices, normals, colors, bounds);

    // drawable.setHasNormalInVertexArray(true);
    // drawable.setData(painter, elements, verticeAndNormals, null, colors, bounds);
  }

  public FloatBuffer computeSimpleNormals(List<Coord3d> verticeList) {
    // FloatBuffer simpleNormals = FloatBuffer.allocate(verticeList.size() * VERTEX_SIZE);
    FloatBuffer simpleNormals =
        Buffers.newDirectFloatBuffer(verticeList.size() * DrawableVBO2.VERTEX_SIZE);

    for (int i = 0; i < verticeList.size(); i += geometrySize) {
      // gather coordinates of a triangle
      Coord3d c0 = verticeList.get(i + 0);
      Coord3d c1 = verticeList.get(i + 1);
      Coord3d c2 = verticeList.get(i + 2);

      // compute normal
      Coord3d normal = Normal.compute(c0, c1, c2);

      for (int j = 0; j < geometrySize; j++) {
        simpleNormals.put(normal.x);
        simpleNormals.put(normal.y);
        simpleNormals.put(normal.z);
      }
    }
    simpleNormals.rewind();

    return simpleNormals;
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
        Buffers.newDirectFloatBuffer(verticeList.size() * DrawableVBO2.VERTEX_SIZE);

    for (Coord3d averagedNormal : averagedNormals) {
      normals.put(averagedNormal.x);
      normals.put(averagedNormal.y);
      normals.put(averagedNormal.z);
    }
    normals.rewind();

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
