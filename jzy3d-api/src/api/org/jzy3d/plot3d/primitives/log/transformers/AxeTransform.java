package org.jzy3d.plot3d.primitives.log.transformers;

/**
 * Specify an axe transform (e.g. for log axes)
 * 
 * @author 
 */
public interface AxeTransform {
	public float compute(float value);
}
