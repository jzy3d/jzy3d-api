package org.jzy3d.plot3d.builder;

import java.util.List;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Range;


public abstract class Grid {
  public Grid(Range xyrange, int xysteps) {
    this.xrange = xyrange;
    this.yrange = xyrange;
    this.xsteps = xysteps;
    this.ysteps = xysteps;
  }

  public Grid(Range xrange, int xsteps, Range yrange, int ysteps) {
    this.xrange = xrange;
    this.yrange = yrange;
    this.xsteps = xsteps;
    this.ysteps = ysteps;
  }

  public abstract List<Coord3d> apply(Mapper mapper);

  /************************************************/

  protected Range xrange;
  protected Range yrange;
  protected int xsteps;
  protected int ysteps;
}
