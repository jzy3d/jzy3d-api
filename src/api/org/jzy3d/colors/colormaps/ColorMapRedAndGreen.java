package org.jzy3d.colors.colormaps;

import org.jzy3d.colors.Color;
import org.jzy3d.colors.IColorMappable;


public class ColorMapRedAndGreen implements IColorMap {

    public ColorMapRedAndGreen() {
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
    
    private Color getColor( double x, double y, double z, double zMin, double zMax ){
        
        double rel_value = 0;
        
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
        
        float b = 0;
        float v = (float) colorComponentRelative( rel_value, 0.375f, 0.25f, 0.75f );
        float r = (float) colorComponentRelative( rel_value, 0.625f, 0.25f, 0.75f );
        
        return new Color( r, v, b );
    }
    
    public Color getColor( IColorMappable colorable, double z ){
        return getColor( 0.0f, 0.0f, z, colorable.getMin(), colorable.getMax() );        //To re-use the existing code
    }

    private double colorComponentRelative( double value, double center, double topwidth, double bottomwidth ){
        return colorComponentAbsolute( value, center-(bottomwidth/2), center+(bottomwidth/2), center-(topwidth/2), center+(topwidth/2) );
    }
    
    private double colorComponentAbsolute( double value, double bLeft, double bRight, double tLeft, double tRight ){
        double output = 0;
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
