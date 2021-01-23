package org.jzy3d.maths;


public interface IBoundingPolicy {
  public abstract BoundingBox3d apply(BoundingBox3d box);
}
