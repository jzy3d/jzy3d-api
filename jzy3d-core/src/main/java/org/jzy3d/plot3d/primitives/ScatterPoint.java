package org.jzy3d.plot3d.primitives;

import java.util.ArrayList;
import java.util.List;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ISingleColorable;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.transform.Transform;

/**
 * Experimental 3d object.
 * 
 * @author Martin Pernollet
 * 
 */
public class ScatterPoint extends Drawable implements ISingleColorable {

  public ScatterPoint() {
    bbox = new BoundingBox3d();
    setWidth(1.0f);
    setPoints(new ArrayList<LightPoint>());
  }

  public ScatterPoint(List<LightPoint> points, float width) {
    bbox = new BoundingBox3d();
    setPoints(points);
    setWidth(width);
  }

  public void clear() {
    points.clear();;
    bbox.reset();
  }

  /* */

  @Override
  public void draw(IPainter painter) {
    doTransform(painter);
    doDrawPoints(painter);
    doDrawBoundsIfDisplayed(painter);
  }

  protected void doDrawPoints(IPainter painter) {
    painter.glPointSize(width);
    painter.glBegin_Point();

    if (points != null) {
      for (LightPoint p : points) {
        painter.color(p.rgb);
        painter.vertex(p.xyz, spaceTransformer);

      }
    }
    painter.glEnd();
  }

  @Override
  public void applyGeometryTransform(Transform transform) {
    for (LightPoint p : points) {
      Coord3d c = p.xyz;
      c.set(transform.compute(c));
    }
    updateBounds();
  }

  /* */

  public void add(LightPoint point) {
    this.points.add(point);
    updateBounds();
  }

  public void setPoints(List<LightPoint> points) {
    this.points = points;
    updateBounds();
  }

  @Override
  public void updateBounds() {
    bbox.reset();
    for (LightPoint c : points)
      bbox.add(c.xyz);
  }

  public List<LightPoint> getData() {
    return points;
  }

  @Override
  public void setColor(Color color) {}

  @Override
  public Color getColor() {
    return null;
  }

  /**
   * Set the width of the point.
   * 
   * @param width point's width
   */
  public void setWidth(float width) {
    this.width = width;
  }

  public List<LightPoint> points;
  public float width;
}
