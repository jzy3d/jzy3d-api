package org.jzy3d.plot3d.primitives.vbo;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.IPainter;
import org.jzy3d.painters.NativeDesktopPainter;
import org.jzy3d.plot3d.primitives.vbo.buffers.FloatVBO;
import org.jzy3d.plot3d.primitives.vbo.builders.VBOBuilder;
import org.jzy3d.plot3d.primitives.vbo.drawable.DrawableVBO;
import com.jogamp.opengl.GL2;

public class ShaderWaterfallVBOBuilder extends VBOBuilder {

  private float[] x;
  private float[] y;
  private float[] z;
  private ColorMapper mapper;
  private FloatVBO vbo;

  public ShaderWaterfallVBOBuilder(float[] x, float[] y, float[] z, ColorMapper mapper) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.mapper = mapper;
  }

  public int getOutlineIndexSize() {
    return y.length * x.length * 4;
  }

  public int getFillIndexSize() {
    return y.length * (x.length - 1) * 6;
    // return 0;
  }

  public void earlyInitalise(DrawableVBO drawable) {
    int size = getOutlineIndexSize() + getFillIndexSize();
    // xyz for quads give 3*4
    vbo = new FloatVBO(x.length * y.length * 3 * 2, size);
    fillFromArray(drawable, x, y, z, mapper, vbo);
    drawable.doSetBoundingBox(vbo.getBounds());
  }

  @Override
  public void load(IPainter painter, DrawableVBO drawable) throws Exception {
    drawable.setData(((NativeDesktopPainter) painter).getGL(), vbo);
    drawable.setGeometry(GL2.GL_TRIANGLES);
  }

  private void fillFromArray(DrawableVBO drawable, float[] x, float[] y, float[] z,
      ColorMapper colors, FloatVBO vbo) {
    FloatBuffer vertices = vbo.getVertices();
    IntBuffer indices = vbo.getIndices();
    indices.rewind();
    vertices.rewind();
    drawable.setHasColorBuffer(false);

    float minY = Float.MAX_VALUE;
    float maxY = -Float.MAX_VALUE;
    float minX = Float.MAX_VALUE;
    float maxX = -Float.MAX_VALUE;


    for (int i = 0; i < x.length; i++) {
      float t = x[i];
      if (t < minX)
        minX = t;
      if (t > maxX)
        maxX = t;
    }

    for (int i = 0; i < y.length; i++) {
      float t = y[i];
      if (t < minY)
        minY = t;
      if (t > maxY)
        maxY = t;
    }

    if (x.length * y.length != z.length)
      throw new IllegalArgumentException("length of y must equal x.length*z.length");

    // Calculate min and max to determine sensible vale
    float min = Float.POSITIVE_INFINITY;
    float max = Float.NEGATIVE_INFINITY;

    for (int i = 0; i < z.length; i++) {
      if (z[i] < min)
        min = z[i];
      if (z[i] > max)
        max = z[i];
    }

    // Apply small offset to min for drawing bottom line
    if (min == max) {
      min = min - 10E-3f;
    } else {
      min = min - ((max - min) / 10E3f);
    }
    int c = 0;
    for (int i = 0; i < y.length; i++) {

      for (int j = 0; j < x.length; j++) {

        Coord3d c1 = new Coord3d(x[j], y[i], z[j + (i * x.length)]);
        putCoord(vertices, c1);
        indices.put(c++);
        indices.put(c);
        // putColor(vertices, new Color(0, 0, 255));

      }

      for (int j = x.length - 1; j >= 0; j--) {

        Coord3d cMin = new Coord3d(x[j], y[i], min);
        putCoord(vertices, cMin);
        indices.put(c++);
        if (j != 0) {
          indices.put(c);
        } else {
          indices.put(i * (x.length * 2));
        }
      }

    }

    int[] ind = new int[(x.length - 1) * 6];
    int maxInd = x.length * 2 - 1;
    for (int i = 0; i < x.length - 1; i++) {
      int idx00 = maxInd - i;
      int idx01 = maxInd - i - 1;
      int idx10 = i;
      int idx11 = i + 1;
      // Triangle1

      int p = i * 6;
      ind[p++] = idx00;
      ind[p++] = idx01;
      ind[p++] = idx10;
      ind[p++] = idx01;
      ind[p++] = idx11;
      ind[p++] = idx10;

    }

    indices.put(ind);
    for (int yi = 1; yi < y.length; yi++) {
      for (int i = 0; i < ind.length; i++) {
        ind[i] += (maxInd + 1);
      }

      indices.put(ind);
    }
    //

    // for (int j = 0; j < x.length; j++) {
    // Coord3d c0 = new Coord3d(x[j], y[i], min);
    //
    // Coord3d c1 = new Coord3d(x[j],y[i], z[j + (i*x.length)]);
    // Coord3d c2 = new Coord3d(x[j+1],y[i], z[j + (i*x.length) +1]);
    //
    // Coord3d c3 = new Coord3d(x[j+1],y[i], min);
    //
    // indices.put(size++);
    // putCoord(vertices, c0);
    //
    // if (j == 0 || j == x.length-2) {
    // putColor(vertices, new Color(0, 0, 255));
    // } else {
    // putColor(vertices, new Color(255, 0, 0));
    // }
    //
    //// putColor(vertices, new Color(255, 0, 0));
    // indices.put(size++);
    // putCoord(vertices, c1);
    // putColor(vertices, new Color(0, 255, 0));
    // indices.put(size++);
    // putCoord(vertices, c2);
    // if (j == 0 || j == x.length-2) {
    // putColor(vertices, new Color(0, 0, 255));
    // } else {
    // putColor(vertices, new Color(0, 255, 0));
    // }
    // indices.put(size++);
    // putCoord(vertices, c3);
    // putColor(vertices, new Color(255, 0, 0));
    //
    // }
    // }

    vertices.rewind();
    indices.rewind();
    vbo.setBounds(new BoundingBox3d(minX, maxX, minY, maxY, min, max));
  }
}

