package org.jzy3d.plot3d.primitives.axes.layout.providers;


public class StaticTickProvider extends AbstractTickProvider implements ITickProvider{
	public StaticTickProvider(float[] values){
		this.values = values;
	}
	
	@Override
	public float[] generateTicks(float min, float max, int steps) {
		return values;
	}
	
	@Override
	public int getDefaultSteps() {
		return 0;
	}
	
	protected float[] values;
}
