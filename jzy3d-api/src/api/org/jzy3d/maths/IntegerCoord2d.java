package org.jzy3d.maths;

public class IntegerCoord2d {
	public IntegerCoord2d(){
		this.x = 0;
		this.y = 0;
	}
	
	public IntegerCoord2d(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public String toString(){
		return "(IntegerCoord2d) " + x + "," + y;
	}
	
	public int x, y;
}
