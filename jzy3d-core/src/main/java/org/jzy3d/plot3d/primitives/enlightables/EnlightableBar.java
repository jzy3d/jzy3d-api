package org.jzy3d.plot3d.primitives.enlightables;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Composite;
import org.jzy3d.plot3d.primitives.Tube;


public class EnlightableBar extends Composite {
  public EnlightableBar(Coord3d position, float height, float radius, Color color) {
    setData(position, height, radius, color);
  }

  public void setData(Coord3d position, float height, float radius, Color color) {
    int angles = 20;
    int loops = 1;

    // Tube
    Tube tube = new Tube(position, radius, height, angles, 1, color);
    add(tube);

    // Bottom disk
    Coord3d bottom = position;
    EnlightableDisk dsk1 = new EnlightableDisk(bottom, 0f, radius, angles, loops, color, false);
    add(dsk1);

    // Top disk
    Coord3d top = position;
    top.z = position.z + height;
    EnlightableDisk dsk2 = new EnlightableDisk(top, 0f, radius, angles, loops, color, true);
    add(dsk2);
  }
}
