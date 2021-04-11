package org.jzy3d.plot3d.primitives.enlightables;

import java.util.ArrayList;
import java.util.List;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.events.DrawableChangedEvent;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Normal;
import org.jzy3d.maths.Utils;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Polygon;
import org.jzy3d.plot3d.primitives.PolygonFill;
import org.jzy3d.plot3d.primitives.PolygonMode;
import org.jzy3d.plot3d.transform.Transform;

public class EnlightablePolygon extends AbstractEnlightable {

  /**
   * Initializes an empty {@link Polygon} with face status defaulting to true, and wireframe status
   * defaulting to false.
   */
  public EnlightablePolygon() {
    super();
    points = new ArrayList<Point>(4);
    bbox = new BoundingBox3d();
    center = new Coord3d();
  }

  /**********************************************************************/

  @Override
  public void draw(IPainter painter) {
    doTransform(painter);

    applyMaterial(painter);

    Coord3d norm = Normal.compute(points.get(0).xyz, points.get(1).xyz, points.get(2).xyz);

    // Draw content of polygon

    if (faceDisplayed) {
      painter.glPolygonMode(PolygonMode.FRONT_AND_BACK, PolygonFill.FILL);

      if (wireframeDisplayed) {
        painter.glEnable_PolygonOffsetFill();
        painter.glPolygonOffset(1.0f, 1.0f);
      }

      painter.glBegin_Polygon();
      for (Point p : points) {
        if (mapper != null) {
          Color c = mapper.getColor(p.xyz);
          painter.color(c);
        } else
          painter.color(p.rgb);
        painter.vertex(p.xyz);
        painter.normal(norm);
      }
      painter.glEnd();
      if (wireframeDisplayed)
        painter.glDisable_PolygonOffsetFill();
    }

    // Draw edge of polygon
    if (wireframeDisplayed) {
      painter.glPolygonMode(PolygonMode.FRONT_AND_BACK, PolygonFill.LINE);

      painter.glEnable_PolygonOffsetFill();
      painter.glPolygonOffset(1.0f, 1.0f);

      painter.glColor4f(wireframeColor.r, wireframeColor.g, wireframeColor.b, 1);// wfcolor.a);
      painter.glLineWidth(wireframeWidth);

      painter.glBegin_Polygon();
      for (Point p : points) {
        painter.glVertex3f(p.xyz.x, p.xyz.y, p.xyz.z);
        painter.glNormal3f(norm.x, norm.y, norm.z);
      }
      painter.glEnd();
      painter.glDisable_PolygonOffsetFill();
    }

  }

  @Override
  public void applyGeometryTransform(Transform transform) {
    for (Point p : points) {
      p.xyz = transform.compute(p.xyz);
    }
    updateBounds();
  }

  @Override
  public void updateBounds() {
    bbox.reset();
    bbox.add(points);
    // recompute center
    updateCenter();
  }

  protected void updateCenter() {
    center = new Coord3d();
    for (Point p : points)
      center = center.add(p.xyz);
    center = center.div(points.size());
  }

  /**********************************************************************/

  /** Add a point to the polygon. */
  public void add(Point point) {
    if (point.rgb.a < 1f)
      hasAlpha = true;
    points.add(point);
    bbox.add(point);

    updateCenter();
  }

  // --- experimental code ------
  public boolean hasAlpha() {
    return hasAlpha;
  }

  private boolean hasAlpha = false;

  // ----------------------------

  @Override
  public Coord3d getBarycentre() {
    return center;
  }

  /**
   * Retrieve a point from the {@link Polygon}.
   * 
   * @return a Point3d.
   */
  public Point get(int p) {
    return points.get(p);
  }

  /**
   * Indicates the number of points in this {@link Polygon}.
   * 
   * @return the number of points
   */
  public int size() {
    return points.size();
  }

  /**********************************************************************/

  public void setColorMapper(ColorMapper mapper) {
    this.mapper = mapper;

    fireDrawableChanged(new DrawableChangedEvent(this, DrawableChangedEvent.FIELD_COLOR));
  }

  public ColorMapper getColorMapper() {
    return mapper;
  }

  /*
   * public void setColors(ColorMapper mapper){ for(Point p: points)
   * p.setColor(mapper.getColor(p.xyz)); }
   */

  public void setColor(Color color) {
    this.color = color;

    for (Point p : points)
      p.setColor(color);

    fireDrawableChanged(new DrawableChangedEvent(this, DrawableChangedEvent.FIELD_COLOR));
  }

  public Color getColor() {
    return color;
  }

  /**********************************************************************/

  @Override
  public String toString(int depth) {
    return (Utils.blanks(depth) + "(EnlightablePolygon) #points:" + points.size());
  }

  /**********************************************************************/
  protected ColorMapper mapper;
  protected List<Point> points;
  protected Color color;
  protected Coord3d center;
}
