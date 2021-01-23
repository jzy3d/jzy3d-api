package org.jzy3d.plot2d.primitives;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.ConcurrentLineStripSplitted;
import org.jzy3d.plot3d.primitives.Point;

public class LineSerie2dSplitted extends LineSerie2d implements Serie2d {
  public LineSerie2dSplitted(String name) {
    super(name);
    this.line = new ConcurrentLineStripSplitted();
  }

  public void addAndSplit(float x, float y, Color color) {
    line().addAndSplit(new Point(new Coord3d(x, y, 0), color));
  }

  @Override
  public void add(float x, float y, Color color) {
    line().add(new Point(new Coord3d(x, y, 0), color));
  }

  public ConcurrentLineStripSplitted line() {
    return (ConcurrentLineStripSplitted) line;
  }
}

