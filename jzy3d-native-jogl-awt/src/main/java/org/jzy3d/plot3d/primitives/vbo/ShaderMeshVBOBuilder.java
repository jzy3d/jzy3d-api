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

public class ShaderMeshVBOBuilder extends VBOBuilder {

  private float[] x;
  private float[] y;
  private float[] z;
  private ColorMapper mapper;
  private FloatVBO vbo;

  public ShaderMeshVBOBuilder(float[] x, float[] y, float[] z, ColorMapper mapper) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.mapper = mapper;
  }

  public void earlyInitalise(DrawableVBO drawable) {
    // vbo = initFloatVBO(drawable, true, (y.length-1)*(x.length-1)*4);
    int size = (y.length - 1) * (x.length - 1);
    // xyz for quads give 3*4
    vbo = new FloatVBO(x.length * y.length * 3, size * 6);
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

    Coord3d c = new Coord3d();
    float minY = Float.MAX_VALUE;
    float maxY = -Float.MAX_VALUE;
    float minX = Float.MAX_VALUE;
    float maxX = -Float.MAX_VALUE;
    float minZ = Float.MAX_VALUE;
    float maxZ = -Float.MAX_VALUE;

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
    // add Coords
    for (int yi = 0; yi < y.length; yi++) {
      for (int xi = 0; xi < x.length; xi++) {
        int pos = xi + yi * x.length;
        c.x = x[xi];
        c.y = y[yi];
        c.z = z[pos];
        if (c.z < minZ)
          minZ = c.z;
        if (c.z > maxZ)
          maxZ = c.z;

        putCoord(vertices, c);
      }
    }

    // add indicies
    for (int yi = 0; yi < y.length - 1; yi++) {
      for (int xi = 0; xi < x.length - 1; xi++) {
        int idx00 = xi + yi * x.length;
        int idx01 = xi + yi * x.length + 1;
        int idx10 = xi + (yi + 1) * x.length;
        int idx11 = xi + (yi + 1) * x.length + 1;
        // Triangle1
        indices.put(idx00);
        indices.put(idx01);
        indices.put(idx10);

        // Triangle2
        indices.put(idx01);
        indices.put(idx11);
        indices.put(idx10);
      }
    }

    // for(int yi=0; yi<y.length-1; yi++){
    // for(int xi=0; xi<x.length-1; xi++){
    //
    // int pos = xi+yi*x.length;
    // int posNext = xi+(yi+1)*x.length;
    //
    // indices.put(size++);
    // c.x = x[xi];
    // c.y = y[yi+1];
    // c.z = z[posNext];
    //
    // if (c.z < minZ) minZ = c.z;
    // if (c.z > maxZ) maxZ = c.z;
    //
    // putCoord(vertices, c);
    //// putColor(vertices, colors.getColor(c));
    //
    // indices.put(size++);
    // c.x = x[xi];
    // c.y = y[yi];
    // c.z = z[pos];
    //
    // if (c.z < minZ) minZ = c.z;
    // if (c.z > maxZ) maxZ = c.z;
    //
    // putCoord(vertices, c);
    //// putColor(vertices, colors.getColor(c));
    //
    // indices.put(size++);
    // c.x = x[xi+1];
    // c.y = y[yi];
    // c.z = z[pos+1];
    //
    // if (c.z < minZ) minZ = c.z;
    // if (c.z > maxZ) maxZ = c.z;
    //
    // putCoord(vertices, c);
    //// putColor(vertices, colors.getColor(c));
    //
    //
    // indices.put(size++);
    // c.x = x[xi+1];
    // c.y = y[yi+1];
    // c.z = z[posNext+1];
    //
    // if (c.z < minZ) minZ = c.z;
    // if (c.z > maxZ) maxZ = c.z;
    //
    // putCoord(vertices, c);
    //// putColor(vertices, colors.getColor(c));
    //
    // }
    // }

    vertices.rewind();
    indices.rewind();
    vbo.setBounds(new BoundingBox3d(minX, maxX, minY, maxY, minZ, maxZ));
  }
}

