package org.jzy3d.plot3d.primitives.axeTransformablePrimitive.axeTransformers;


public class LogAxeTransformer implements AxeTransformer{

	@Override
	public float compute(float value) {
		return (float) Math.log(value);
	}
	
}
