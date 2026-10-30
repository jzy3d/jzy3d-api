package org.jzy3d.plot3d.builder.concrete;

import java.util.ArrayList;
import java.util.List;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.builder.Grid;
import org.jzy3d.plot3d.builder.Mapper;


/** CustomGrid allows using a specific projection mapping. For debugging purpose. */
public class CustomGrid extends Grid {
  public CustomGrid(double[][] coordinates) {
    super(null, 0);
    this.coordinates = coordinates;
  }

  @Override
  public List<Coord3d> apply(Mapper mapper) {
    List<Coord3d> output = new ArrayList<Coord3d>(coordinates.length);
    for (int i = 0; i < coordinates.length; i++)
      output.add(new Coord3d(coordinates[i][0], coordinates[i][1],
          mapper.f(coordinates[i][0], coordinates[i][1])));
    return output;
  }

  protected double[][] coordinates;
}
