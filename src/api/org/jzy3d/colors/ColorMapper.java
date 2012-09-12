package org.jzy3d.colors;

import org.jzy3d.colors.colormaps.IColorMap;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Range;
import org.jzy3d.maths.Scale;


/** A {@link ColorMapper} is a {@link IColorMappable} and a {@link IColorMap} at the same time. 
 * It allows getting a color from a Coord3d instead of attaching a {@link IColorMap} 
 * to a {@link IColorMappable} object.
 * 
 * @author Martin Pernollet
 *
 */
public class ColorMapper implements IColorMappable{
	public ColorMapper(IColorMap colormap, float zmin, float zmax){
		this.colormap = colormap;
		this.zmin     = zmin;
		this.zmax     = zmax;
	}
	
	public ColorMapper(IColorMap colormap, float zmin, float zmax, Color factor){
		this.colormap = colormap;
		this.zmin     = zmin;
		this.zmax     = zmax;
		this.factor   = factor;
	}
	
	public ColorMapper(ColorMapper colormapper, Color factor){
		this.colormap = colormapper.colormap;
		this.zmin     = colormapper.zmin;
		this.zmax     = colormapper.zmax;
		this.factor   = factor;
	}
	
	/*****************************************************************/

	public Color getColor(Coord3d coord){
		Color out = colormap.getColor(this, coord.x, coord.y, coord.z);
		
		if(factor!=null)
			out.mul(factor);
		return out;
	}
	/*****************************************************************/
	public Color getColor(float v){   //To allow the Color to be a variable independent of the coordinates
		Color out = colormap.getColor(this, v);
		
		if(factor!=null)
			out.mul(factor);
		return out;
	}	
	/*****************************************************************/
	
	public IColorMap getColorMap(){
		return colormap;
	}
	
	public float getZMin(){
		return zmin;
	}

	public float getZMax(){
		return zmax;
	}

	public void setZMin(float value){
		zmin = value;
	}
	
	public void setZMax(float value){
		zmax = value;
	}
	
	public void setRange(Range range){
		zmin = (float)range.getMin();
		zmax = (float)range.getMax();
	}
	
	public Range getRange(){
		return new Range(zmin, zmax);
	}
	
	public void setScale(Scale range){
		zmin = (float)range.getMin();
		zmax = (float)range.getMax();
	}
	
	public Scale getScale(){
		return new Scale(zmin, zmax);
	}
	
	/*****************************************************************/

	public String toString(){
		return "(ColorMapper)" + colormap + " zmin:" + zmin + " zmax:" + zmax + " factor:" + factor;
	}
	
	/*****************************************************************/
		
	private float    zmin;
	private float    zmax;
	private IColorMap colormap;
	private Color    factor = null;
}
