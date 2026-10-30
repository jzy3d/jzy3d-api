package org.jzy3d.plot3d.primitives;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.transform.Transform;

public class DrawableBoundingBox extends Wireframeable{
  
  public DrawableBoundingBox(BoundingBox3d box) {
    super();
    this.bbox = box;
    
    setWireframeColor(Color.BLUE);
    setWireframeWidth(1);
    setFaceDisplayed(false); // not supported, so put this in a consistent state
    
  }

  @Override
  public void draw(IPainter painter) {
    painter.box(bbox, getWireframeColor(), getWireframeWidth(), spaceTransformer);
  }

  @Override
  public void applyGeometryTransform(Transform transform) {
  }

  @Override
  public void updateBounds() {
  }
  
}
