package org.jzy3d.maths;

/** For some naming relevance, a simple extention of Scale.*/
public class Range extends Scale {
	public Range(double min, double max){
		super(min, max);
	}
	
	public void enlarge(double ratio){
		double offset = (max-min) * ratio;
		if(offset==0)
    		offset=1;
		min-=offset;
		max+=offset;
	}
	
	public Range createEnlarge(double ratio){
		double offset = (max-min) * ratio;
		if(offset==0)
    		offset=1;
		return new Range(min, max);
	}
}
