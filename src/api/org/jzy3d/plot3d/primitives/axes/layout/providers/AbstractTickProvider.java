package org.jzy3d.plot3d.primitives.axes.layout.providers;


public abstract class AbstractTickProvider implements ITickProvider{
	@Override
	public float[] generateTicks(float min, float max) {
		return generateTicks( min, max, getDefaultSteps() );
	}
}
