package org.jzy3d.plot3d.primitives.axes.layout.providers;


public abstract class AbstractTickProvider implements ITickProvider{
	@Override
	public double[] generateTicks(double min, double max) {
		return generateTicks( min, max, getDefaultSteps() );
	}
}
