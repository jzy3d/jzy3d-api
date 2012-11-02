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

    public Color getColor( IColorMappable colorable, double x, double y, double z ){
        return getColor( x, y, z, colorable.getMin(), colorable.getMax() );
    }

    /**
     * A helper for getColor( ColorMappable waferview, Point3d pt ) that calls
     * other helper functions
     */
    public Color getColor( double x, double y, double z, double zMin, double zMax ){

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

        float b = (float) colorComponentAbsolute( rel_value, -0.250, +0.875, +0.250, +0.500 );
        float v = (float) colorComponentAbsolute( rel_value, +0.125, +0.875, +0.500, +0.500 );
        float r = (float) colorComponentAbsolute( rel_value, +0.125, +1.250, +0.500, +0.750 );

        return new Color( r, v, b );
    }


    public Color getColor( IColorMappable colorable, double z ){
        return getColor( 0.0f, 0.0f, z, colorable.getMin(), colorable.getMax() );        //To re-use the existing code
    }

    /*private double creneau_relatif( double value, double center, double topwidth, double bottomwidth ){
        return creneau_absolu( value, center-(bottomwidth/2), center+(bottomwidth/2), center-(topwidth/2), center+(topwidth/2) );
    }*/


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
