package org.jzy3d.colors;

import org.jzy3d.plot3d.primitives.Composite;

/**
 * Allows only composite objects to call {@link ColorMapper.preDraw(this)}, to avoid having all
 * children re-initializing min/max score in the scenegraph
 */
public class CompositeColorMapperUpdatePolicy implements IColorMapperUpdatePolicy {
  @Override
  public boolean acceptsPreDraw(Object o) {
    if (o instanceof Composite)
      return true;
    else
      return false;
  }

  @Override
  public boolean acceptsPostDraw(Object o) {
    return true;
  }
}
