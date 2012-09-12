package org.jzy3d.colors.colormaps;

import org.jzy3d.colors.Color;
import org.jzy3d.colors.IColorMappable;

/**
 * ColorMap interface.
 * <p>
 * This interface defines the set of methods that any concrete colormap
 * should define in order to be used by an object implementing the
 * ColorMappable interface.
 * 
 * The ColorMappable interface impose to an object to provide a Z-scaling,
 * that is, a minimum and maximum value on the Z axis.
 * These values are used by concrete colormaps in order to set an interval
 * for the possible colors.
 * 
 * @author Martin Pernollet
 */
public interface IColorMap {
	/**
	 * 
	 * @param colorable A @link ColorMappable object.
	 * @param x
	 * @param y
	 * @param z
	 * @return a color for the given point.
	 */
    Color getColor( IColorMappable colorable, float x, float y, float z );
	/**
	 * 
	 * @param colorable A @link ColorMappable object.
	 * @param v the variable that is Color-dependent, and can be independent of the coordinates.
	 * @return a color for the given point.
	 */

    Color getColor(IColorMappable colorable, float v);
   
    /**
     * Indicates if the colormap use the standard or reverted color direction
     * 
     * @param isStandard
     */
    public void setDirection(boolean isStandard);
    
    /**
     * 
     * @return the colormap direction: true if standard, false if reverted.
     */
    public boolean getDirection();
}
