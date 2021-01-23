package org.jzy3d.maths;

import java.util.ArrayList;
import java.util.Collection;

public class Polygon2d extends ArrayList<Coord2d> {
  public Polygon2d() {
    super();
  }

  public Polygon2d(Collection<? extends Coord2d> c) {
    super(c);
  }

  public Polygon2d(int initialCapacity) {
    super(initialCapacity);
  }

  private static final long serialVersionUID = 7096041682981860291L;
}
