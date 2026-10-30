package org.jzy3d.plot3d.transform.space;

/**
 * Apply log transform if value is greater than 0 (otherwise return 0).
 * 
 * @author
 */
public class SpaceTransformLog implements SpaceTransform {
  @Override
  public float compute(float value) {
    if (value <= 0)
      return 0;
    else
      return (float) Math.log(value);
  }
}
