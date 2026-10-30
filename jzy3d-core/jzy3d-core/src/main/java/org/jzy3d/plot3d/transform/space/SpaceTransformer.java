package org.jzy3d.plot3d.transform.space;

import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;

/**
 * A helper to apply 3 {@link SpaceTransform} on each dimension of a {@link Coord3d}.
 * 
 * @author
 *
 */
public class SpaceTransformer {
  protected SpaceTransform x;
  protected SpaceTransform y;
  protected SpaceTransform z;

  public SpaceTransformer(SpaceTransform x, SpaceTransform y, SpaceTransform z) {
    this.x = x != null ? x : new SpaceTransformNone();
    this.y = y != null ? y : new SpaceTransformNone();
    this.z = z != null ? z : new SpaceTransformNone();
  }

  public SpaceTransformer() {
    this.x = new SpaceTransformNone();
    this.y = new SpaceTransformNone();
    this.z = new SpaceTransformNone();
  }

  public SpaceTransform getX() {
    return x;
  }

  public void setX(SpaceTransform x) {
    this.x = x;
  }

  public SpaceTransform getY() {
    return y;
  }

  public void setY(SpaceTransform y) {
    this.y = y;
  }

  public SpaceTransform getZ() {
    return z;
  }

  public void setZ(SpaceTransform z) {
    this.z = z;
  }

  public Coord3d compute(Coord3d point) {
    return new Coord3d(getX().compute(point.x), getY().compute(point.y), getZ().compute(point.z));
  }

  public Coord3d computeSelf(Coord3d point) {
    point.x = getX().compute(point.x);
    point.y = getY().compute(point.y);
    point.z = getZ().compute(point.z);
    return point;
  }


  public Coord2d compute(Coord2d point) {
    return new Coord2d(getX().compute(point.x), getY().compute(point.y));
  }

  public BoundingBox3d compute(BoundingBox3d bounds) {
    return bounds.transform(this);
  }
}
