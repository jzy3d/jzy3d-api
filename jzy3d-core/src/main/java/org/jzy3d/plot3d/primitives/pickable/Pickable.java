package org.jzy3d.plot3d.primitives.pickable;

import org.jzy3d.plot3d.primitives.IGLRenderer;
import org.jzy3d.plot3d.transform.Transform;

public interface Pickable extends IGLRenderer {
  public void setPickingId(int id);

  public int getPickingId();

  // required method from an AbstractDrawable
  public void setTransform(Transform transform);
}
