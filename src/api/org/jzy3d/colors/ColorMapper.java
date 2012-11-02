package org.jzy3d.colors;

import org.jzy3d.colors.colormaps.IColorMap;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Range;
import org.jzy3d.maths.Scale;


/** A {@link ColorMapper} uses a {@link IColorMap} to process a color for a given {@link Coord3d}.
 *
 *
 * An optional global color factor let you filter the {@link Color} computed by the colormap:
 * <code>
 * public Color getColor(Coord3d coord){
 *   Color out = colormap.getColor(this, v);
 *   if(factor!=null)
 *     out.mul(factor);
 * }
 * </code>
 *
 * @author Martin Pernollet
 */
public class ColorMapper implements IColorMappable{
	public ColorMapper(){
    }
    public ColorMapper(IColorMap colormap, Color factor){
        this.colormap = colormap;
        this.factor   = factor;
        this.min     = 0;
        this.max     = 1;
    }

    public ColorMapper(IColorMap colormap, double min, double max){
		this.colormap = colormap;
		this.min     = min;
		this.max     = max;
	}

	public ColorMapper(IColorMap colormap, double min, double max, Color factor){
		this.colormap = colormap;
		this.min     = min;
		this.max     = max;
		this.factor   = factor;
	}

	public ColorMapper(ColorMapper colormapper, Color factor){
		this.colormap = colormapper.colormap;
		this.min     = colormapper.min;
		this.max     = colormapper.max;
		this.factor   = factor;
	}

	/* */

	/** call a colormap with a three dimensions coordinate*/
	public Color getColor(Coord3d coord){
		Color out = colormap.getColor(this, coord.x, coord.y, coord.z);

		if(factor!=null)
			out.mul(factor);
		return out;
	}

	/** call a colormap with a single dimension coordinate*/
	public Color getColor(double v){
		Color out = colormap.getColor(this, v);

		if(factor!=null)
			out.mul(factor);
		return out;
	}

	/* */

	/**
	 * A hook method to implement to prepare colormapper for the current draw call.
	 * The input parameter o must be the object calling preDraw. Indeed, the mapper
	 * is supposed to be able to check wether preDraw is actually allowed for the caller.
	 *
	 *  @see {@link IColorMapperUpdatePolicy}
	 */
	public void preDraw(Object o){
	}

	public void postDraw(Object o){
    }


	/* */

	public IColorMap getColorMap(){
		return colormap;
	}

	public double getMin(){
		return min;
	}

	public double getMax(){
		return max;
	}

	public void setMin(double value){
		min = value;
	}

	public void setMax(double value){
		max = value;
	}

	public void setRange(Range range){
		min = (double)range.getMin();
		max = (double)range.getMax();
	}

	public Range getRange(){
		return new Range(min, max);
	}

	public void setScale(Scale range){
		min = (double)range.getMin();
		max = (double)range.getMax();
	}

	public Scale getScale(){
		return new Scale(min, max);
	}

	/* */

	public String toString(){
		return "(ColorMapper)" + colormap + " min:" + min + " max:" + max + " factor:" + factor;
	}

	/* */

	protected double    min;
	protected double    max;
	protected IColorMap colormap;
	protected Color    factor = null;
}
