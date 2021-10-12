package org.jzy3d.plot3d.primitives.vbo.drawable.loaders;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import org.jzy3d.io.IGLLoader;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Polygon;
import org.jzy3d.plot3d.primitives.vbo.drawable.DrawableVBO2;
import com.jogamp.common.nio.Buffers;

/**
 * ===================== WIP / Not working yet =====================
 * 
 * Limitations of primitive restart are discussed here :
 * https://community.khronos.org/t/using-glprimitiverestartindex-to-declare-multiple-geometries-in-the-same-vbo/107810/13
 * 
 * A utility class to build buffers to feed a {@link DrawableVBO2} using Primitive restart.
 * 
 * @author Martin Pernollet
 *
 */
public class VBOBufferLoaderForPolygonsPrimitiveRestart extends VBOBufferLoader
    implements IGLLoader<DrawableVBO2> {
  // IColorMap colormap;
  // float[] coloring;
  static int DIMENSIONS = 3;

  protected List<Polygon> polygons;
  protected int pointsPerPolygon;

  public VBOBufferLoaderForPolygonsPrimitiveRestart(List<Polygon> polygons, int pointsPerPolygon) {
    super();
    this.polygons = polygons;
    this.pointsPerPolygon = pointsPerPolygon;
  }

  @Override
  public void load(IPainter painter, DrawableVBO2 drawable) throws Exception {
    int pointsNumber = polygons.size() * pointsPerPolygon;

    // Temporary list, for computing color and normals later
    List<Coord3d> verticeList = new ArrayList<>();

    BoundingBox3d bounds = new BoundingBox3d();

    // -------------------------------------------
    // Vertices

    int colorChannels = drawable.getColorChannels();

    FloatBuffer vertices =
        Buffers.newDirectFloatBuffer(pointsNumber * DIMENSIONS + polygons.size());
    FloatBuffer colors = Buffers.newDirectFloatBuffer(pointsNumber * colorChannels);

    for (Polygon polygon : polygons) {

      for (Point point : polygon.getPoints()) {

        // Store coordinates
        vertices.put(point.xyz.x);
        vertices.put(point.xyz.y);
        vertices.put(point.xyz.z);

        // Store colors
        colors.put(point.rgb.r);
        colors.put(point.rgb.g);
        colors.put(point.rgb.b);

        if (colorChannels > 3)
          colors.put(point.rgb.a);

        // Hold bounds
        bounds.add(point.xyz);

        // Keep for later processing (normals)
        verticeList.add(point.xyz);

      }

      // indicate that geometry is done and drawing should restart
      vertices.put(DrawableVBO2.PRIMITIVE_RESTART_VALUE);

    }
    vertices.rewind();
    colors.rewind();

    // -------------------------------------------
    // Normals

    FloatBuffer normals = computeSimpleNormals(pointsPerPolygon, verticeList);


    // -------------------------------------------
    // Store data

    drawable.setHasNormalInVertexArray(false);
    drawable.setPrimitiveRestart(true);
    drawable.setVerticesPerGeometry(pointsPerPolygon);
    drawable.setData(painter, vertices, normals, colors, bounds);
  }
}
