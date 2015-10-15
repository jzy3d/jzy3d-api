package org.jzy3d.io;

import org.jzy3d.plot3d.primitives.AbstractDrawable;

import com.jogamp.opengl.GL;

/** Defines a loader that requires a GL context.
 * 
 * @author Martin Pernollet
 */
public interface IGLLoader<T extends AbstractDrawable> {
    public void load(GL gl, T drawable) throws Exception;
}
