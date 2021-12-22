package org.jzy3d.plot3d.primitives.vbo.builders;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Collection;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.io.IGLLoader;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.vbo.buffers.FloatVBO;
import org.jzy3d.plot3d.primitives.vbo.drawable.DrawableVBO;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import com.jogamp.opengl.GL;

/**
 * The {@link VBOBuilder} is responsible for sizing a {@link FloatVBO}, filling it with vertex
 * coordinates and colors properties, and configure a {@link DrawableVBO} with filled buffer.
 * 
 * The builder will be called via method {@link load()} once GL context is ready to store geometry
 * in GPU for the given DrawableVBO instance. This is where builder actually draw the object by
 * setting coordinates, colors, and rendering parameters.
 * 
 * 
 * A {@link Quality} setting can be given to configure GL properties of the drawable:
 * <ul>
 * <li>
 * <li>
 * <li>
 * </ul>
 * 
 * 
 * 
 * @author Martin Pernollet
 *
 */
public abstract class VBOBuilder implements IGLLoader<DrawableVBO> {
  static Logger logger = LogManager.getLogger(VBOBuilder.class);


  /* */

  protected void fillWithCollection(DrawableVBO drawable, Collection<Coord3d> coordinates,
      ColorMapper colors, FloatBuffer vertices, IntBuffer indices, BoundingBox3d bounds) {
    drawable.setHasColorBuffer(colors != null);

    int size = 0;
    for (Coord3d c : coordinates) {
      indices.put(size++);
      putCoord(vertices, c);
      bounds.add(c);
      if (colors != null) {
        putColor(vertices, colors.getColor(c));
      }
    }
    vertices.rewind();
    indices.rewind();
  }

  protected void fillWithCollection(DrawableVBO drawable, List<Coord3d> coordinates, FloatVBO vbo) {
    fillWithCollection(drawable, coordinates, vbo.getVertices(), vbo.getIndices(), vbo.getBounds());
  }

  protected void fillWithCollection(DrawableVBO drawable, List<Coord3d> coordinates, FloatVBO vbo,
      ColorMapper coloring) {
    fillWithCollection(drawable, coordinates, coloring, vbo.getVertices(), vbo.getIndices(),
        vbo.getBounds());
  }

  protected void fillWithCollection(DrawableVBO drawable, Collection<Coord3d> coordinates,
      FloatBuffer vertices, IntBuffer indices, BoundingBox3d bounds) {
    fillWithCollection(drawable, coordinates, null, vertices, indices, bounds);
  }

  /* */

  protected void fillWithRandomBar(int n, DrawableVBO drawable, FloatBuffer vertices,
      IntBuffer indices, BoundingBox3d bounds, ColorMapper colors) {
    drawable.setHasColorBuffer(colors != null);

    int size = 0;
    for (int i = 0; i < n; i++) {

      float z = (float) Math.random() * 100;
      float y = (float) Math.random() * 1;

      indices.put(size++);
      Coord3d c1 = new Coord3d(i, 0, 0);
      putCoord(vertices, c1);
      putColor(vertices, Color.RED);
      bounds.add(c1);

      indices.put(size++);
      Coord3d c2 = new Coord3d(i, y, z);
      putCoord(vertices, c2);
      putColor(vertices, Color.RED);
      bounds.add(c2);
    }
    vertices.rewind();
    indices.rewind();
  }

  protected void fillWithRandomBar(int n, DrawableVBO drawable, FloatVBO vbo, ColorMapper colors) {
    fillWithRandomBar(n, drawable, vbo.getVertices(), vbo.getIndices(), vbo.getBounds(), colors);
  }

  /* */

  protected void putBar(FloatVBO vbo, int size, float x, float y, float z, Color color) {
    Coord3d c1 = new Coord3d(x, 0, 0);
    Coord3d c2 = new Coord3d(x, y, z);

    putPoint(vbo, size++, color, c1);
    putPoint(vbo, size++, color, c2);
  }

  protected void putPoint(FloatVBO vbo, int id, Color color, Coord3d coord) {
    vbo.getIndices().put(id);
    putCoord(vbo, coord);
    putColor(vbo, color);
    vbo.getBounds().add(coord);
  }

  protected void putCoord(FloatVBO vbo, Coord3d c) {
    vbo.getVertices().put(c.x);
    vbo.getVertices().put(c.y);
    vbo.getVertices().put(c.z);
  }

  protected void putCoord(FloatBuffer vertices, Coord3d c) {
    vertices.put(c.x);
    vertices.put(c.y);
    vertices.put(c.z);
  }

  protected void putColor(FloatVBO vbo, Color color) {
    vbo.getVertices().put(color.r);
    vbo.getVertices().put(color.g);
    vbo.getVertices().put(color.b);
  }

  protected void putColor(FloatBuffer vertices, Color color) {
    vertices.put(color.r);
    vertices.put(color.g);
    vertices.put(color.b);
  }

  /* */

  /**
   * Setup buffers dimensions according to various parameters:
   * <ul>
   * <li>target drawable representation type (point, line, etc),
   * <li>having color per vertex or not,
   * <li>having normals defined (default to false)
   * <li>number of dimensions (default to 3)
   * </ul>
   * 
   */
  protected FloatVBO initFloatVBO(DrawableVBO drawable, boolean hasNormal, boolean hasColor, int n,
      int dimension) {
    int geometrySize = computeGeometrySize(drawable);
    int verticeBufferSize = computeVerticeBufferSize(drawable.getGeometry(), n, dimension,
        geometrySize, hasNormal, hasColor);
    int indexBufferSize = computeIndexBufferSize(n, geometrySize, hasColor);
    logger.info(indexBufferSize + " " + verticeBufferSize);
    FloatVBO vbo = new FloatVBO(verticeBufferSize, indexBufferSize);
    return vbo;
  }

  private int computeIndexBufferSize(int n, int geometrySize, boolean hasColor) {
    // if(hasColor)
    return n * geometrySize;
  }

  protected int geometryTypeToMultiplier(int geometrySize) {
    if (geometrySize == GL.GL_POINTS) {
      return 1;
    } else if (geometrySize == GL.GL_LINES) {
      return 2;
    } else if (geometrySize == GL.GL_TRIANGLES) {
      return 3;
    }
    return 1;
  }

  /**
   * Setup buffers dimensions according to various parameters:
   * <ul>
   * <li>target drawable representation type (point, line, etc),
   * <li>having color per vertex or not,
   * <li>having normals defined (default to false)
   * <li>3 dimensions
   * </ul>
   * 
   */

  protected FloatVBO initFloatVBO(DrawableVBO drawable, boolean hasNormal, boolean hasColor,
      int n) {
    return initFloatVBO(drawable, hasNormal, hasColor, n, 3);
  }

  /**
   * Setup buffers dimensions according to various parameters:
   * <ul>
   * <li>target drawable representation type (point, line, etc),
   * <li>having color per vertex or not,
   * <li>no normals defined
   * <li>3 dimensions
   * </ul>
   * 
   */
  protected FloatVBO initFloatVBO(DrawableVBO drawable, boolean hasColor, int n) {
    return initFloatVBO(drawable, false, hasColor, n, 3);
  }

  /* */
  protected int computeVerticeBufferSize(int type, int n, int dim, int geometrySize,
      boolean hasNormal, boolean hasColor) {
    if (type == GL.GL_LINES) {
      if (hasColor) {
        return n * (dim * 2 * 2) * geometrySize;// *2 points for lines,
                                                // *2 for having a color
      } else {
        return n * (dim * 2) * geometrySize;// *2 lines
      }
    } else if (type == GL.GL_LINE_STRIP) {
      if (hasColor) {
        return n * (dim * 2) * geometrySize; // *2 for having a color
      } else {
        return n * dim * geometrySize;// *2 lines
      }
    } else {
      if (hasColor) {
        return n * (dim * 2) * geometrySize;// *2 colors
      }
      if (hasNormal) {
        return n * (dim * 2) * geometrySize;// *2 normals
      }

      return n * dim * geometrySize;
    }
  }

  protected int computeGeometrySize(DrawableVBO drawable) {
    if (drawable.getGeometry() == GL.GL_POINTS) {
      return 1;
    }
    if (drawable.getGeometry() == GL.GL_LINES) {
      return 2;
    } else if (drawable.getGeometry() == GL.GL_TRIANGLES) {
      return 3;
    }
    return 2;
  }

}
