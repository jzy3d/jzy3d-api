package org.jzy3d.maths;

public class Scale {
	
	public Scale(double min, double max){
		this.min = min;
		this.max = max;
	}
	
	public Scale clone(){
		return new Scale(min, max);
	}

	public double getMin(){
		return min;
	}
	
	public double getMax(){
		return max;
	}
	
	public double getRange(){
		return max-min;
	}
	
	public void setMin(double min){
		this.min = min;
	}
	
	public void setMax(double max){
		this.max = max;
	}
	
	public Scale add(double value){
		return new Scale(min+value, max+value);
	}
	
	public boolean contains(double value){
		if( min<=value && value<=max)
			return true;
		else
			return false;
	}
	
	public boolean isMaxNan(){
		return Double.isNaN(max);
	}
	
	public boolean isMinNan(){
		return Double.isNaN(min);
	}

	public boolean valid(){
		if(min<=max)
			return true;
		return false;
	}

	
	/**********************************************/
	
	public static Scale widest(Scale scale1, Scale scale2){
		double min = Math.min(scale1.min, scale2.min);
		double max = Math.max(scale1.max, scale2.max);
		
		return new Scale(min, max);
	}
	
	public static Scale thinest(Scale scale1, Scale scale2){
		double min = Math.max(scale1.min, scale2.min);
		double max = Math.min(scale1.max, scale2.max);
		
		return new Scale(min, max);
	}
	
	public static Scale enlarge(Scale input, double ratio){
		double offset = (input.getMax()-input.getMin()) * ratio;
		if(offset==0)
    		offset=1;
    	return new Scale(input.getMin()-offset, input.getMax()+offset);
	}
	
	/**********************************************/

	public String toString(){
		return new String("min="+min+" max="+max);
	}
	
	/**********************************************/
	
	protected double min, max;
}
