package org.jzy3d.plot3d.primitives.parameq;

import java.util.List;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.parameq.ParametricEquation2;
import org.jzy3d.plot3d.primitives.LineStrip;
import org.jzy3d.plot3d.primitives.Point;

public class ParametricDrawable2 extends LineStrip {
  public ParametricDrawable2() {}

  public ParametricDrawable2(ParametricEquation2 equation, double tmin, double tmax, int tsteps,
      double umin, double umax, int usteps, Color color) {
    this.color = color;
    init(equation, tmin, tmax, tsteps, umin, umax, usteps);
  }

  public void init(ParametricEquation2 equation, double tmin, double tmax, int tsteps, double umin,
      double umax, int usteps) {
    List<Coord3d> coords = equation.apply(tmin, tmax, tsteps, umin, umax, usteps);
    for (Coord3d coord : coords) {
      add(new Point(coord, color));
    }
  }

  protected Color color = Color.BLUE;
}
