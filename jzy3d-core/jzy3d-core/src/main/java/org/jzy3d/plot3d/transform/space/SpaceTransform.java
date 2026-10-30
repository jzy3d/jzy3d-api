package org.jzy3d.plot3d.transform.space;

/**
 * Specify an axe transform (e.g. for log axes)
 * 
 * @author
 */
public interface SpaceTransform {
  public float compute(float value);
}
