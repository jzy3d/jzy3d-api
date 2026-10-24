package org.jzy3d.plot3d.primitives.pickable;

import java.util.List;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.PlaneAxis;
import org.jzy3d.plot3d.primitives.textured.NativeDrawableImage;
import org.jzy3d.plot3d.rendering.textures.SharedTexture;

public class PickableTexture extends NativeDrawableImage implements Pickable {
  public PickableTexture(SharedTexture resource, PlaneAxis orientation, float axisValue,
      List<Coord2d> coords, Color filter) {
    super(resource, orientation, axisValue, coords, filter);
  }

  public PickableTexture(SharedTexture resource, PlaneAxis orientation, float axisValue,
      List<Coord2d> coords) {
    super(resource, orientation, axisValue, coords);
  }

  public PickableTexture(SharedTexture resource, PlaneAxis orientation, float axisValue) {
    super(resource, orientation, axisValue);
  }

  public PickableTexture(SharedTexture resource, PlaneAxis orientation, float axisValue,
      Color color) {
    super(resource, orientation, axisValue, color);
  }

  public PickableTexture(SharedTexture resource, PlaneAxis orientation) {
    super(resource, orientation);
  }

  public PickableTexture(SharedTexture resource) {
    super(resource);
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
    PickableTexture other = (PickableTexture) obj;
    if (id != other.id)
      return false;
    return true;
  }

  protected int id;
}
