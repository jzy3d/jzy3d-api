package org.jzy3d.plot3d.transform.space;

/**
 * Apply log(e) transform if value is greater than 0 (otherwise return 0).
 * 
 * @author Martin Pernollet
 */
public class SpaceTransformLogX implements SpaceTransform {
  float base = 10;

  public SpaceTransformLogX(float base) {
    this.base = base;
  }

  @Override
  public float compute(float value) {
    if (value <= 0)
      return 0;
    else
      return log(value, base);
  }

  protected float log(float x, float base) {
    return (float) (Math.log(x) / Math.log(base));
  }
}
