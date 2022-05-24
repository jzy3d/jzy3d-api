package org.jzy3d.plot3d.builder.concrete;

import java.util.ArrayList;
import java.util.List;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Mapper;


public class RingGrid extends OrthonormalGrid {

  public RingGrid(float radius, int xysteps) {
    this(radius, xysteps, 0);
  }

  public RingGrid(float radius, int xysteps, int enlargeSteps) {
    super(new Range(-radius - (enlargeSteps * radius / xysteps),
        radius + (enlargeSteps * radius / xysteps)), xysteps);
    sqradius =
        (radius + (enlargeSteps * radius / xysteps)) * (radius + (enlargeSteps * radius / xysteps));
  }

  @Override
  public List<Coord3d> apply(Mapper mapper) {
    double xstep = xrange.getRange() / (double) xsteps;
    double ystep = yrange.getRange() / (double) ysteps;

    List<Coord3d> output = new ArrayList<Coord3d>((xsteps - 1) * (ysteps - 1));

    for (int xi = -(xsteps - 1) / 2; xi <= (xsteps - 1) / 2; xi++) {
      for (int yi = -(ysteps - 1) / 2; yi <= (ysteps - 1) / 2; yi++) {
        double xval = xi * xstep;
        double yval = yi * ystep;

        if (sqradius >= xval * xval + yval * yval) {
          output.add(new Coord3d(xval, yval, mapper.f(xval, yval)));
        }
      }
    }
    return output;
  }

  protected double sqradius;

  protected static final int ENLARGE_STEP = 3;
}
