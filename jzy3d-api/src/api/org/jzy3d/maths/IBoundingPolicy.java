package org.jzy3d.maths;

import org.jzy3d.maths.BoundingBox3d;

public interface IBoundingPolicy {
    public abstract BoundingBox3d apply(BoundingBox3d box);
}