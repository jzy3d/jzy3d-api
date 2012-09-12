package org.jzy3d.plot3d.primitives.axes.layout.providers;


/** Compute the ticks placements automatically between values min and max.
 * 
 * @author Jerome Kodjabachian
 */
public class SmartTickProvider extends AbstractTickProvider implements ITickProvider{
	public SmartTickProvider(){
		this(5);
	}
	
	public SmartTickProvider(int steps){
		this.steps = steps;
	}
	
	@Override
	public float[] generateTicks(float min, float max, int steps) {
		if(min == max){
			float[] ticks = new float[1];
			ticks[0] = min;
			return ticks;
		}
		else if(min > max)
			return new float[0];
		
		double absscale = Math.floor(Math.log10(max-min));
		double relscale = Math.log10(max-min) - absscale;
		float ticksize = 0;
		
		if( relscale < Math.log10(0.2*steps) )
		    ticksize = (float) (Math.pow(10,absscale)*0.2);
		else if( relscale < Math.log10(0.5*steps) )
		    ticksize = (float) (Math.pow(10,absscale)*0.5);
		else if( relscale < Math.log10(1*steps) )
		    ticksize = (float) Math.pow(10,absscale)*1;
		else
		    ticksize = (float) Math.pow(10,absscale)*2;
		
		int start = (int)Math.ceil(min/ticksize);
		int stop  = (int)Math.floor(max/ticksize);

		float[] ticks = new float[stop-start+1];

		for(int t=start; t<=stop; t++)
			ticks[t-start] = (t*ticksize);
		
		return ticks;
	}

	@Override
	public int getDefaultSteps() {
		return steps;
	}
	
	protected int steps;
}
