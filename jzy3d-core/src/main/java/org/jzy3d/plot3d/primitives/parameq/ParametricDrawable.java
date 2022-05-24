package org.jzy3d.plot3d.primitives.parameq;

import java.util.List;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.parameq.ParametricEquation;
import org.jzy3d.plot3d.primitives.LineStrip;
import org.jzy3d.plot3d.primitives.Point;

public class ParametricDrawable extends LineStrip {
  public ParametricDrawable() {}

  public ParametricDrawable(ParametricEquation equation, double tmin, double tmax, int steps,
      Color color) {
    this.color = color;
    init(equation, tmin, tmax, steps);
  }

  public void init(ParametricEquation equation, double tmin, double tmax, int steps) {
    List<Coord3d> coords = equation.apply(tmin, tmax, steps);
    for (Coord3d coord : coords) {
      add(new Point(coord, color));
    }
  }

  protected Color color = Color.BLUE;
}
