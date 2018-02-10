package org.jzy3d.plot3d.primitives;

import org.jzy3d.colors.Color;
import org.jzy3d.plot3d.rendering.compat.GLES2CompatUtils;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

/**
 * Supports additional settings
 * 
 * @author Martin Pernollet
 */
public class Polygon extends AbstractGeometry {

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
    protected void begin(GL gl) {
        if (gl.isGL2()) {
            gl.getGL2().glBegin(GL2.GL_POLYGON);
        } else {
            GLES2CompatUtils.glBegin(GL2.GL_POLYGON);
        }
    }
}
