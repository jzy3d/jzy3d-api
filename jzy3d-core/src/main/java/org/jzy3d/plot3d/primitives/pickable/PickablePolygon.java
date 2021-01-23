package org.jzy3d.plot3d.primitives.pickable;

import org.jzy3d.plot3d.primitives.Polygon;


public class PickablePolygon extends Polygon implements Pickable {
  public PickablePolygon() {
    super();
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
