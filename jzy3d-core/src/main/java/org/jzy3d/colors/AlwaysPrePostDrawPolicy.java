package org.jzy3d.colors;

/**
 * Allows only composite objects to call {@link ColorMapper.preDraw(this)}, to avoid having all
 * children re-initializing min/max score in the scenegraph
 */
public class AlwaysPrePostDrawPolicy implements IColorMapperUpdatePolicy {
  @Override
  public boolean acceptsPreDraw(Object o) {
    return true;
  }

  @Override
  public boolean acceptsPostDraw(Object o) {
    return true;
  }
}
