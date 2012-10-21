package org.jzy3d.colors.colormaps;

import org.jzy3d.colors.Color;
import org.jzy3d.colors.IColorMappable;


public class ColorMapGrayscale implements IColorMap{
    
	/**
     * Creates a new instance of ColorMapGrayscale.
     * 
     * A ColorMapGrayscale objects provides a color for points standing
     * between a Zmin and Zmax values.
     * 
     * The points standing outside these [Zmin;Zmax] boundaries are assigned
     * to the same color than the points standing on the boundaries. 
     * 
     * The grayscale colormap is a progressive transition from black to white.
     */
    public ColorMapGrayscale() {
    	direction = true;
    }
    
    public void setDirection(boolean isStandard){
    	direction = isStandard;
    }
    
    public boolean getDirection(){
    	return direction;
    }

    public Color getColor( IColorMappable colorable, double x, double y, double z ){
        return getColor( x, y, z, colorable.getMin(), colorable.getMax() );        
    }
    
    public Color getColor(  double x, double y, double z, double zMin, double zMax ){
        
        double rel_value = 0;
        
        if( z < zMin )
            rel_value = 0;
        else if( z > zMax )
            rel_value = 1;
        else
            rel_value = ( z - zMin ) / ( zMax - zMin );
        
        float b = (float) rel_value;
        float v = (float) rel_value;
        float r = (float) rel_value;
        
        return new Color( r, v, b );
    }
    
    public Color getColor( IColorMappable colorable, double z ){
        return getColor( 0.0f, 0.0f, z, colorable.getMin(), colorable.getMax() );        //To re-use the existing code
    }

    /**************************************************************************/
    
    private boolean direction;
}
