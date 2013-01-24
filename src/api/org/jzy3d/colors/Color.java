package org.jzy3d.colors;

import java.util.Random;


/**
 * Color interface.
 * <p>
 * The Color interface provide a representation of a color, 
 * independant from the target Window Toolkit (AWT, SWT, etc).
 * 
 * @author Martin Pernollet
 */
public class Color {
	
	/** Black color. */
	public static final Color BLACK   = new Color(0.0f, 0.0f, 0.0f);
	/** White color. */
	public static final Color WHITE   = new Color(1.0f, 1.0f, 1.0f);
	/** Gray color. */
	public static final Color GRAY    = new Color(0.5f, 0.5f, 0.5f);
	
	/** Red color. */
	public static final Color RED     = new Color(1.0f, 0.0f, 0.0f);
	/** Green color. */
	public static final Color GREEN   = new Color(0.0f, 1.0f, 0.0f);
	/** Blue color. */
	public static final Color BLUE    = new Color(0.0f, 0.0f, 1.0f);
	
	/** Yellow color. */
	public static final Color YELLOW  = new Color(1.0f, 1.0f, 0.0f);
	/** Magenta color. */
	public static final Color MAGENTA = new Color(1.0f, 0.0f, 1.0f);
	/** Cyan color. */
	public static final Color CYAN    = new Color(0.0f, 1.0f, 1.0f);
	
	/*************************************************************/

	/** Initialize a color with an alpha channel set to 1.*/
	public Color(float r, float g, float b){
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = 1.0f;
	}
	
	/** Initialize a color with an alpha channel set to 255.*/
	public Color(int r, int g, int b){
		this.r = (float)r/255;
		this.g = (float)g/255;
		this.b = (float)b/255;
		this.a = 1.0f;
	}
	
	/** Initialize a color with values between 0 and 1.*/
	public Color(float r, float g, float b, float a){
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
	
	/** Initialize a color with values between 0 and 255.*/
	public Color(int r, int g, int b, int a){
		this.r = (float)r/255;
		this.g = (float)g/255;
		this.b = (float)b/255;
		this.a = (float)a/255;
	}
	
	public void mul(Color factor){
		this.r *= factor.r;
		this.g *= factor.g;
		this.b *= factor.b;
		this.a *= factor.a;
	}
	
	public Color alphaSelf(float alpha){
		this.a = alpha;
		return this;
	}
	
	/** Return the hexadecimal representation of this color.*/
	public String toHex(){
		String hexa = "#";
		hexa += Integer.toHexString((int)r*255);
		hexa += Integer.toHexString((int)g*255);
		hexa += Integer.toHexString((int)b*255);
		return hexa;
	}
	
	public String toString(){
		return new String("(Color) r=" + r + " g=" + g + " b=" + b + " a=" + a);
	}
	
	public Color clone(){
		return new Color(r, g, b, a);
	}
	
	public float[] toArray(){
		float array[] = { r, g, b, a };
		return array;
	}
	
	public Color negative(){
		return new Color(1 - r, 1 - g, 1 - b);
	}
	
	public static Color random(){
		return new Color(rng.nextFloat(), rng.nextFloat(), rng.nextFloat());
	}
	
	public static Random rng = new Random();
	
	/*************************************************************/

	public java.awt.Color awt(){
		return new java.awt.Color(r, g, b, a);
	}
	
	public Color(java.awt.Color c){
		this.r = c.getRed();
		this.g = c.getGreen();
		this.b = c.getBlue();
		this.a = c.getAlpha();
	}
	
	public float r;
	public float g;
	public float b;
	public float a;
	
}
