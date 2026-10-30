package org.jzy3d.plot3d.primitives;

import org.jzy3d.painters.IPainter;

/**
 * Defines an objects that uses resources that should be loaded using an available GL context.
 * 
 * Resource that require a GL context are usually textures, VBO, etc.
 * 
 * @author Martin Pernollet
 */
public interface IGLBindedResource {
  /** Mount resources to gl context */
  public void mount(IPainter painter);

  /** Return true if mount(...) has been called at least one time */
  public boolean hasMountedOnce();
}
