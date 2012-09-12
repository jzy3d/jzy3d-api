package org.jzy3d.colors.colormaps;

import org.jzy3d.colors.Color;
import org.jzy3d.colors.IColorMappable;


public class ColorMapHotCold implements IColorMap {
	    
    /**
     * Creates a new instance of ColorMapHotCold.
     * 
     * A ColorMapHotCold objects provides a color for points standing
     * between a Zmin and Zmax values.
     * 
     * The points standing outside these [Zmin;Zmax] boundaries are assigned
     * to the same color than the points standing on the boundaries. 
     * 
     * The hot-cold colormap is a progressive transition from blue, 
     * to white and last, red.
     */
    public ColorMapHotCold() {
    	direction = true;
    }
    
    public void setDirection(boolean isStandard){
    	direction = isStandard;
    }
    
    public boolean getDirection(){
    	return direction;
    }
    
    public Color getColor( IColorMappable colorable, float x, float y, float z ){
        return getColor( x, y, z, colorable.getZMin(), colorable.getZMax() );        
    }
    
    /**
     * A helper for getColor( ColorMappable waferview, Point3d pt ) that calls
     * other helper functions
     */
    private Color getColor( float x, float y, float z, float zMin, float zMax ){
        
        float rel_value = 0;
        
        if( z < zMin )
            rel_value = 0;
        else if( z > zMax )
            rel_value = 1;
        else{
        	if(direction)
        		rel_value = ( z - zMin ) / ( zMax - zMin );
        	else
        		rel_value = ( zMax - z ) / ( zMax - zMin );
        }
        
        float b = (float) colorComponentAbsolute( rel_value, -0.250f, +0.875f, +0.250f, +0.500f );
        float v = (float) colorComponentAbsolute( rel_value, +0.125f, +0.875f, +0.500f, +0.500f );
        float r = (float) colorComponentAbsolute( rel_value, +0.125f, +1.250f, +0.500f, +0.750f );
        
        return new Color( r, v, b );
    }
    
    
    public Color getColor( IColorMappable colorable, float z ){
        return getColor( 0.0f, 0.0f, z, colorable.getZMin(), colorable.getZMax() );        //To re-use the existing code
    }
  
    /*private float creneau_relatif( float value, float center, float topwidth, float bottomwidth ){
        return creneau_absolu( value, center-(bottomwidth/2), center+(bottomwidth/2), center-(topwidth/2), center+(topwidth/2) );
    }*/
    
    
    private float colorComponentAbsolute( float value, float bLeft, float bRight, float tLeft, float tRight ){
        float output = 0;
        // a gauche ou a droite du creneau
        if( (value < bLeft) || (value >= bRight) ){
            output = 0;
        }
        // sur le plateau haut
        else if( (value >= tLeft) && (value < tRight) ){
            output = 1;
        }
        // sur la pente gauche du creneau
        else if( (value >= bLeft) && (value < tLeft) ){
            output = (value-bLeft)/(tLeft-bLeft);
        }
        // sur la pente droite du creneau
        else if( (value >= tRight) && (value < bRight) ){
            output = (value-bRight)/(tRight-bRight);
        }
        return output;
    }

    /**************************************************************************/
    
    private boolean direction;
	
}
