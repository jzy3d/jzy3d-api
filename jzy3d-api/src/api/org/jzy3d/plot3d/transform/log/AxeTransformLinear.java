package org.jzy3d.plot3d.transform.log;

/**
 * Do not apply any transform (return input value).
 * 
 * @author 
 */
public class AxeTransformLinear implements AxeTransform {

	@Override
	public float compute(float value) {
		return value;
	}

}
