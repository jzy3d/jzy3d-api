package org.jzy3d.plot3d.builder;

import org.jzy3d.maths.Coord3d;

public interface IObjectTopology<O> {
  public Coord3d getCoord(O object);

  public String getXAxisLabel();

  public String getYAxisLabel();

  public String getZAxisLabel();
}
