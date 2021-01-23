package org.jzy3d.plot3d.primitives.pickable;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Sphere;

public class PickableSphere extends Sphere implements Pickable {
  public PickableSphere() {
    super();
  }

  public PickableSphere(Coord3d position, float radius, int slicing, Color color) {
    super(position, radius, slicing, color);
  }


  @Override
  public void setPickingId(int id) {
    this.pid = id;
  }

  @Override
  public int getPickingId() {
    return pid;
  }

  protected int pid = -1;
}
