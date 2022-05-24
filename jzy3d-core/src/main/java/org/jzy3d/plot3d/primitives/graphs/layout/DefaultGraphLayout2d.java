package org.jzy3d.plot3d.primitives.graphs.layout;

import java.util.HashMap;
import org.jzy3d.maths.Coord2d;

public class DefaultGraphLayout2d<V> extends HashMap<V, Coord2d> implements IGraphLayout2d<V> {
  @Override
  public Coord2d getVertexPosition(V v) {
    return get(v);
  }

  @Override
  public void setVertexPosition(V v, Coord2d position) {
    put(v, position);
  }

  private static final long serialVersionUID = 5111236329021291792L;
}
