package org.jzy3d.plot3d.primitives.vbo.buffers;

import java.nio.IntBuffer;
import org.jzy3d.maths.BoundingBox3d;

public interface VBO<T> {
  public T getVertices();

  public void setVertices(T vertices);

  public IntBuffer getIndices();

  public void setIndices(IntBuffer indices);

  public BoundingBox3d getBounds();

  public void setBounds(BoundingBox3d bounds);

}
