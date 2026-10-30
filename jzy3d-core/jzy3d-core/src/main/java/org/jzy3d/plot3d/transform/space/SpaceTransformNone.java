package org.jzy3d.plot3d.transform.space;

/**
 * Do not apply any transform (return input value).
 * 
 * @author
 */
public class SpaceTransformNone implements SpaceTransform {

  @Override
  public float compute(float value) {
    return value;
  }

}
