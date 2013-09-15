package org.jzy3d.plot3d.builder;

public abstract class Mapper {
	public abstract double f(double x, double y);
	
	/*************************************************************************************/
	
	/** Default implementation providing iterative call to {@link f(double x, double y)}.*/
	public double[] f(double[] x, double[] y){
		double[] z = new double[x.length];
		
		for(int i=0; i<x.length; i++)
			z[i] = f(x[i], y[i]);
		return z;
	}
	
	/** Default implementation providing iterative call to {@link f(double x, double y)}.*/
	public double[] f(double[][] xy){
		double[] z = new double[xy.length];
		
		for(int i=0; i<xy.length; i++)
			z[i] = f(xy[i][0], xy[i][1]);
		return z;
	}

	/** Default implementation providing iterative call to {@link f(double x, double y)}.*/
	public float[] fAsFloat(double[] x, double[] y){
		float[] z = new float[x.length];
		
		for(int i=0; i<x.length; i++)
			z[i] = (float)f(x[i], y[i]);
		return z;
	}

	/** Default implementation providing iterative call to {@link f(double x, double y)}.*/
	public float[] fAsFloat(float[] x, float[] y){
		float[] z = new float[x.length];
		
		for(int i=0; i<x.length; i++)
			z[i] = (float)f(x[i], y[i]);
		return z;
	}

	/** Default implementation providing iterative call to {@link f(double x, double y)}.*/
	public float[] fAsFloat(double[][] xy){
		float[] z = new float[xy.length];
		
		for(int i=0; i<xy.length; i++)
			z[i] = (float)f(xy[i][0], xy[i][1]);
		return z;
	}
	
	/** Default implementation providing iterative call to {@link f(double x, double y)}.*/
	public float[] fAsFloat(float[][] xy){
		float[] z = new float[xy.length];
		
		for(int i=0; i<xy.length; i++)
			z[i] = (float)f(xy[i][0], xy[i][1]);
		return z;
	}
}
