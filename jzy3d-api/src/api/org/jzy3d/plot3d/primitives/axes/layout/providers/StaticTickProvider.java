package org.jzy3d.plot3d.primitives.axes.layout.providers;


public class StaticTickProvider extends AbstractTickProvider implements ITickProvider{
	public StaticTickProvider(double[] values){
		this.values = values;
	}
	
	@Override
	public double[] generateTicks(double min, double max, int steps) {
		return values;
	}
	
	@Override
	public int getDefaultSteps() {
		return 0;
	}
	
	protected double[] values;
}
