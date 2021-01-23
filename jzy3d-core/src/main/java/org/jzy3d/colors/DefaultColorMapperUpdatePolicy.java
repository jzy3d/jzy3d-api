package org.jzy3d.colors;

/** Allows any object to call {@link ColorMapper.preDraw(this)} */
public class DefaultColorMapperUpdatePolicy implements IColorMapperUpdatePolicy {
  @Override
  public boolean acceptsPreDraw(Object o) {
    return true;
  }

  @Override
  public boolean acceptsPostDraw(Object o) {
    return true;
  }
}
