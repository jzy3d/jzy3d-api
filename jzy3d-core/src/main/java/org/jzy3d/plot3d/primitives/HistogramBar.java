package org.jzy3d.plot3d.primitives;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;


public class HistogramBar extends Composite {
  public HistogramBar() {
    this(10);
  }

  public HistogramBar(int slices) {
    this.slices = slices;
  }

  public void setData(Coord3d position, float height, float radius, Color color) {
    int loops = 5;

    // Tube
    Tube tube = new Tube(position, radius, height, slices, 1, color);
    add(tube);

    // Bottom disk
    Coord3d bottom = position;
    Disk dsk1 = new Disk(bottom, 0f, radius, slices, loops, color);
    add(dsk1);

    // Top disk
    Coord3d top = position;
    top.z = position.z + height;
    Disk dsk2 = new Disk(top, 0f, radius, slices, loops, color);
    add(dsk2);
  }

  protected int slices;
}
