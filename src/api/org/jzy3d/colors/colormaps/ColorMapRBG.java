package org.jzy3d.colors.colormaps;

import org.jzy3d.colors.Color;
import org.jzy3d.colors.IColorMappable;

public class ColorMapRBG implements IColorMap {
	    

    public ColorMapRBG() {
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
        
        float v = (float) colorComponentRelative( rel_value, 0.25f, 0.25f, 0.75f );
        float b = (float) colorComponentRelative( rel_value, 0.50f, 0.25f, 0.75f ); 
        float r = (float) colorComponentRelative( rel_value, 0.75f, 0.25f, 0.75f );
        
        return new Color( r, v, b );
    }
    
    public Color getColor( IColorMappable colorable, float z ){
        return getColor( 0.0f, 0.0f, z, colorable.getZMin(), colorable.getZMax() );        //To re-use the existing code
    }

    /**
     * Return the influence of a color, by comparing the input value to the color spectrum.
     * 
     * The design of a colormap implies defining the influence of each base color
     * (red, green, and blue) over the range of input data.
     * For this reason, the value given to this function is a number between 0 and 1,
     * indicating the position of the input value in the colormap. 
     * Any value standing outside of colormap boundaries should have the "maximal" or
     * "minimal" color.
     * 
     * Exemple:
     * A rainbow colormap is a progressive transition from blue, to green and red.
     * The mix between these 3 colors, may be expressed by the definition of 3 functions:
     * <code>
     *       blue     green     red
     *     /-------\/-------\/-------\
     *    /        /\       /\        \
     *   /        /  \     /  \        \
     *  /        /    \   /    \        \  
     * |----------------|----------------|
     * 0               0.5               1
     * </code>
     * 
     * In order to get the color of an input value standing between 0 and 1, the user
     * should call:
     * 
     * <code>
     * float blue  = (float) creneau_relatif( rel_value, 0.25, 0.25, 0.75 );
     * float green = (float) creneau_relatif( rel_value, 0.50, 0.25, 0.75 );
     * float red   = (float) creneau_relatif( rel_value, 0.75, 0.25, 0.75 );
     * 
     * return new Color4f( red, green, blue, 1.0f );
     * </code>
     * 
     * @param value
     * @param center
     * @param topwidth
     * @param bottomwidth
     * @return color influence.
     */
    private float colorComponentRelative( float value, float center, float topwidth, float bottomwidth ){
        return colorComponentAbsolute( value, center-(bottomwidth/2), center+(bottomwidth/2), center-(topwidth/2), center+(topwidth/2) );
    }
    
    /**
     * Defines a simple function for a color.
     * 
     * @param value
     * @param bLeft under this value, the color is 0
     * @param bRight up to this value, the color is 0
     * @param tLeft from this value until @link tRight, the color is 1
     * @param tRight from @link tLeft until this value, the color is 1
     * 
     * Between bLeft and tLeft, as well as tRight and bRight, the output value is a linear
     * interpolation between 0 and 1.
     * 
     * <code>
     *                      tLeft       tRight
     *                        /-----------\
     *                       /             \ 
     *                      /               \
     *              -------|-----------------|--------
     *                    bLeft            bRight  
     * </code>
     * 
     * @return color influence.
     */
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
