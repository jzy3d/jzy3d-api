package org.jzy3d.plot3d.primitives;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Range;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.pipelines.NotImplementedException;
import org.jzy3d.plot3d.primitives.PolygonFill;
import org.jzy3d.plot3d.primitives.PolygonMode;
import org.jzy3d.plot3d.primitives.Wireframeable;
import org.jzy3d.plot3d.transform.Rotate;
import org.jzy3d.plot3d.transform.Transform;

public class Teapot extends Wireframeable {
  protected float scale = 1;
  protected Color color = Color.GRAY;
  
  public Teapot() {
    Range xRange = new Range(-1.50, 1.73);
    Range yRange = new Range(-0.75, 0.83);
    Range zRange = new Range(-1.00, 1.00);
    bbox = new BoundingBox3d(xRange, yRange, zRange);
    
    Transform transform = new Transform();
    transform.add(new Rotate(90, new Coord3d(1,0,0)));
    setTransformBefore(transform);
  }
  
  @Override
  public void draw(IPainter painter) {
    
    doTransform(painter);
    
    if (isReflectLight()) {
      applyMaterial(painter);
    }

    if(faceDisplayed) {
      painter.color(color);
      
      painter.glPolygonMode(PolygonMode.FRONT_AND_BACK, PolygonFill.FILL);
      
      painter.glutSolidTeapot(scale);
    }
    
    if(wireframeDisplayed) {
      painter.color(wireframeColor);
      painter.glLineWidth(wireframeWidth);

      painter.glPolygonMode(PolygonMode.FRONT_AND_BACK, PolygonFill.LINE);

      painter.glutWireTeapot(scale);
    }
  }

  public Color getColor() {
    return color;
  }

  public void setColor(Color color) {
    this.color = color;
  }
  
  @Override
  public void updateBounds() {
    // data is static, so bounds are as well
    // no need to update anything
  }
  
  @Override
  public void applyGeometryTransform(Transform transform) {
    throw new NotImplementedException();
  }

}
