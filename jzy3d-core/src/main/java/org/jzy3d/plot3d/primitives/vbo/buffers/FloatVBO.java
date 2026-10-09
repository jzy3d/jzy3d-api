package org.jzy3d.plot3d.primitives.vbo.buffers;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.jzy3d.maths.BoundingBox3d;

public class FloatVBO implements VBO<FloatBuffer> {
  FloatBuffer vertices;
  IntBuffer indices;
  BoundingBox3d bounds;


  public FloatVBO(int verticeBufferSize, int indexBufferSize) {
    vertices = FloatBuffer.allocate(verticeBufferSize);
    indices = IntBuffer.allocate(indexBufferSize);
    bounds = new BoundingBox3d();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jzy3d.plot3d.primitives.vbo.buffers.VBO#getVertices()
   */
  @Override
  public FloatBuffer getVertices() {
    return vertices;
  }

  @Override
  public void setVertices(FloatBuffer vertices) {
    this.vertices = vertices;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jzy3d.plot3d.primitives.vbo.buffers.VBO#getIndices()
   */
  @Override
  public IntBuffer getIndices() {
    return indices;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jzy3d.plot3d.primitives.vbo.buffers.VBO#setIndices(java.nio.IntBuffer)
   */
  @Override
  public void setIndices(IntBuffer indices) {
    this.indices = indices;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jzy3d.plot3d.primitives.vbo.buffers.VBO#getBounds()
   */
  @Override
  public BoundingBox3d getBounds() {
    return bounds;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jzy3d.plot3d.primitives.vbo.buffers.VBO#setBounds(org.jzy3d.maths.BoundingBox3d)
   */
  @Override
  public void setBounds(BoundingBox3d bounds) {
    this.bounds = bounds;
  }


}
