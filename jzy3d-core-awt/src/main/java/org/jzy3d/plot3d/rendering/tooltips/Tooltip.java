package org.jzy3d.plot3d.rendering.tooltips;

import org.jzy3d.maths.Coord3d;

public class Tooltip {
  public Tooltip(int x, int y, Coord3d target) {
    this("", x, y, target);
  }

  public Tooltip(String text, int x, int y, Coord3d target) {
    this.text = text;
    this.x = x;
    this.y = y;
    this.target = target;
  }

  public String text;
  public int x;
  public int y;
  public Coord3d target;
}
