package org.jzy3d.utils;

/** The Version provides an integer and string representation of the current library version.*/
public class Version {
	public static final int MAJOR  = 1;
	public static final int MINOR  = 0;
	public static final int PATCH  = 0;
	public static final String STR = new String(MAJOR + "." + MINOR + "." + PATCH);	
	
	public static void main(String[] args){
		System.out.println("Jzy3d version: " + STR);
		System.out.println("----------------------------------");
		
		try{
			System.out.println("Default settings:");
			System.out.println(org.jzy3d.chart.Settings.getInstance());
		}
		catch(Exception e){
			
		}
	}
}
