package org.jzy3d.plot3d.primitives.axes.layout.providers;

public interface ITickProvider {
	public float[] generateTicks(float min, float max);
	public float[] generateTicks(float min, float max, int steps);
	public int getDefaultSteps();
}
