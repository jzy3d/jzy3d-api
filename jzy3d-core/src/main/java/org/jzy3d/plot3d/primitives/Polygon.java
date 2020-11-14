package org.jzy3d.plot3d.primitives;

import org.jzy3d.colors.Color;
import org.jzy3d.painters.Painter;

/**
 * Supports additional settings
 * 
 * @author Martin Pernollet
 */
public class Polygon extends Geometry {

    /**
     * Initializes an empty {@link Polygon} with face status defaulting to true,
     * and wireframe status defaulting to false.
     */
    public Polygon() {
        super();
    }
    
    public Polygon(Color wire, Color face){
        setWireframeColor(wire);
        setColor(face);
    }

    @Override
    protected void begin(Painter painter) {
    	painter.glBegin_Polygon();
    }
}
