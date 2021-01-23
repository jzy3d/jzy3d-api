package org.jzy3d.io;

import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.Drawable;

/**
 * Defines a loader that requires a GL context.
 * 
 * @author Martin Pernollet
 */
public interface IGLLoader<T extends Drawable> {
  public void load(IPainter painter, T drawable) throws Exception;
}
