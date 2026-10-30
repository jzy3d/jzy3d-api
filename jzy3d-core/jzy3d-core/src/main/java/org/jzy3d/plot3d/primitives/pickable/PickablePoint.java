package org.jzy3d.plot3d.primitives.pickable;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Point;

public class PickablePoint extends Point implements Pickable {
  public PickablePoint() {
    super();
  }

  public PickablePoint(Coord3d xyz, Color rgb, float width) {
    super(xyz, rgb, width);
  }

  public PickablePoint(Coord3d xyz, Color rgb) {
    super(xyz, rgb);
  }

  public PickablePoint(Coord3d xyz) {
    super(xyz);
  }

  @Override
  public void setPickingId(int id) {
    this.id = id;
  }

  @Override
  public int getPickingId() {
    return id;
  }

  @Override
  public String toString() {
    return "(IPickable) " + id + ": " + super.toString();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + id;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    PickablePoint other = (PickablePoint) obj;
    if (id != other.id)
      return false;
    return true;
  }

  protected int id;
}
