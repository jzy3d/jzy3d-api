package org.jzy3d.plot3d.primitives.log.transformers;


public class AxeTransformLog implements AxeTransform{

	@Override
	public float compute(float value) {
		if(value <= 0) return 0;
		else return (float) Math.log(value);
	}	
}
