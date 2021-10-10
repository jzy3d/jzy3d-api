package org.jzy3d.demos.teapot;

import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Range;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.Wireframeable;
import org.jzy3d.plot3d.transform.Transform;

public class Teapot extends Wireframeable {
  float scale = 1;
  Color color = Color.GRAY;
  
  public Teapot() {
    //bbox = new BoundingBox3d(new Coord3d(), 3);
    Range xRange = new Range(-1.50, 1.73);
    Range yRange = new Range(-0.75, 0.83);
    Range zRange = new Range(-1.00, 1.00);
    bbox = new BoundingBox3d(xRange, yRange, zRange);
  }
  
  @Override
  public void draw(IPainter painter) {
    doTransform(painter);

    if(faceDisplayed) {
      painter.color(color);
      painter.glutSolidTeapot(scale);
    }
    
    if(wireframeDisplayed) {
      painter.color(wireframeColor);
      painter.glLineWidth(wireframeWidth);
      painter.glutWireTeapot(scale);
    }
  }

  @Override
  public void applyGeometryTransform(Transform transform) {

  }

  @Override
  public void updateBounds() {

  }

  public Color getColor() {
    return color;
  }

  public void setColor(Color color) {
    this.color = color;
  }
  
  

}
