package org.jzy3d.colors;

public interface IColorMapperUpdatePolicy {
  public boolean acceptsPreDraw(Object o);

  public boolean acceptsPostDraw(Object o);
}
