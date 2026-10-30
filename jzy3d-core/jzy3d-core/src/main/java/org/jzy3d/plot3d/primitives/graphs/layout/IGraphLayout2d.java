package org.jzy3d.plot3d.primitives.graphs.layout;

import java.util.Collection;
import org.jzy3d.maths.Coord2d;

public interface IGraphLayout2d<V> {
  public void setVertexPosition(V v, Coord2d position);

  public Coord2d getVertexPosition(V v);

  public Coord2d get(V v);

  public Collection<Coord2d> values();
}
