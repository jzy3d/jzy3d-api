package org.jzy3d.plot3d.primitives;

import org.jzy3d.colors.Color;
import org.jzy3d.colors.ISingleColorable;
import org.jzy3d.events.DrawableChangedEvent;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.transform.Transform;

public class Disk extends Wireframeable implements ISingleColorable {

  /** Initialize a Cylinder at the origin. */
  public Disk() {
    super();
    bbox = new BoundingBox3d();
    setPosition(Coord3d.ORIGIN);
    setVolume(0f, 10f);
    setSlicing(15, 15);
    setColor(Color.BLACK);
  }

  /** Initialize a cylinder with the given parameters. */
  public Disk(Coord3d position, float radiusInner, float radiusOuter, int slices, int loops,
      Color color) {
    super();
    bbox = new BoundingBox3d();
    setPosition(position);
    setVolume(radiusInner, radiusOuter);
    setSlicing(slices, loops);
    setColor(color);
  }

  /* */

  @Override
  public void draw(IPainter painter) {
    doTransform(painter);
    doDrawDisk(painter);
    doDrawBoundsIfDisplayed(painter);
  }

  protected void doDrawDisk(IPainter painter) {
    painter.glTranslatef(x, y, z);
    painter.glLineWidth(wireframeWidth);

    // Draw

    if (faceDisplayed) {
      if (wireframeDisplayed) {
        painter.glEnable_PolygonOffsetFill();
        painter.glPolygonOffset(1.0f, 1.0f);
      }

      painter.glPolygonMode(PolygonMode.FRONT_AND_BACK, PolygonFill.FILL);
      painter.color(color);
      painter.gluDisk(radiusInner, radiusOuter, slices, loops);

      if (wireframeDisplayed)
        painter.glDisable_PolygonOffsetFill();

    }
    if (wireframeDisplayed) {
      painter.glPolygonMode(PolygonMode.FRONT_AND_BACK, PolygonFill.LINE);
      painter.glColor4f(wireframeColor.r, wireframeColor.g, wireframeColor.b, wireframeColor.a);
      painter.gluDisk(radiusInner, radiusOuter, slices, loops);
    }
  }

  /* */

  public void setData(Coord3d position, float radiusInner, float radiusOuter, int slices,
      int loops) {
    setPosition(position);
    setVolume(radiusInner, radiusOuter);
    setSlicing(slices, loops);
  }

  public void setPosition(Coord3d position) {
    this.x = position.x;
    this.y = position.y;
    this.z = position.z;

    updateBounds();
  }

  public void setVolume(float radiusInner, float radiusOuter) {
    if (radiusOuter < radiusInner)
      throw new IllegalArgumentException("inner radius must be smaller than outer radius");

    this.radiusInner = radiusInner;
    this.radiusOuter = radiusOuter;

    updateBounds();
  }

  @Override
  public void updateBounds() {
    bbox.reset();
    bbox.add(x + radiusOuter, y + radiusOuter, z);
    bbox.add(x - radiusOuter, y - radiusOuter, z);
  }

  public void setSlicing(int verticalWires, int horizontalWires) {
    this.slices = verticalWires;
    this.loops = horizontalWires;
  }

  /* */

  @Override
  public void setColor(Color color) {
    this.color = color;

    fireDrawableChanged(new DrawableChangedEvent(this, DrawableChangedEvent.FIELD_COLOR));
  }

  @Override
  public Color getColor() {
    return color;
  }

  @Override
  public void applyGeometryTransform(Transform transform) {
    Coord3d change = transform.compute(new Coord3d(x, y, z));
    x = change.x;
    y = change.y;
    z = change.z;
    updateBounds();
  }

  /* */

  private float x;
  private float y;
  private float z;

  private int slices;
  private int loops;
  private float radiusInner;
  private float radiusOuter;

  private Color color;
}
