package org.jzy3d.plot3d.primitives.axeTransformablePrimitive.axeTransformers;


public class LogAxeTransformer implements AxeTransformer{

	@Override
	public float compute(float value) {
		if(value <= 0) return 0;
		else return (float) Math.log(value);
	}
	
}
