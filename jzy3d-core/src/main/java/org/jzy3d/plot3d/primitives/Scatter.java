package org.jzy3d.plot3d.primitives;

import java.util.List;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ISingleColorable;
import org.jzy3d.events.DrawableChangedEvent;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Coord3ds;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.transform.Transform;

/**
 * A collection of coordinates rendered as dots.
 * 
 * Warning : dots having a width of 1 may not be visible in case HiDPI is ON.
 * 
 * @author Martin Pernollet
 * 
 */
public class Scatter extends Drawable implements ISingleColorable {

  public Scatter() {
    bbox = new BoundingBox3d();
    setWidth(1.0f);
    setColor(Color.BLACK);
  }

  public Scatter(Coord3d[] coordinates) {
    this(coordinates, Color.BLACK);
  }

  public Scatter(List<Coord3d> coordinates) {
    this(coordinates, Color.BLACK);
  }

  public Scatter(Coord3d[] coordinates, Color rgb) {
    this(coordinates, rgb, 1.0f);
  }

  public Scatter(List<Coord3d> coordinates, Color rgb) {
    this(coordinates, rgb, 1.0f);
  }

  public Scatter(Coord3d[] coordinates, Color rgb, float width) {
    this();
    setData(coordinates);
    setWidth(width);
    setColor(rgb);
  }

  public Scatter(List<Coord3d> coordinates, Color rgb, float width) {
    this();
    setData(coordinates);
    setWidth(width);
    setColor(rgb);
  }

  
  public Scatter(Coord3ds coords) {
    this(coords.coordsArray(), coords.colorsArray());
  }

  public Scatter(Coord3d[] coordinates, Color[] colors) {
    this(coordinates, colors, 1.0f);
  }

  public Scatter(Coord3d[] coordinates, Color[] colors, float width) {
    bbox = new BoundingBox3d();
    setData(coordinates);
    setWidth(width);
    setColors(colors);
  }

  public void clear() {
    coordinates = null;
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

    if (colors == null)
      painter.color(rgb);

    if (getData() != null) {
      int k = 0;
      for (Coord3d c : getData()) {
        if (colors != null) {
          painter.color(colors[k]);
          k++;
        }
        painter.vertex(c, spaceTransformer);
      }
    }
    painter.glEnd();
  }

  @Override
  public void applyGeometryTransform(Transform transform) {
    for (Coord3d c : coordinates) {
      c.set(transform.compute(c));
    }
    updateBounds();
  }

  /* */

  /**
   * Set the coordinates of the point.
   * 
   * @param xyz point's coordinates
   */
  public void setData(Coord3d[] coordinates) {
    this.coordinates = coordinates;

    updateBounds();
  }

  public void setData(List<Coord3d> coordinates) {
    this.coordinates = new Coord3d[coordinates.size()];
    int k = 0;
    for (Coord3d c : coordinates)
      this.coordinates[k++] = c;
    updateBounds();
  }

  @Override
  public void updateBounds() {
    bbox.reset();
    for (Coord3d c : coordinates)
      bbox.add(c);
  }

  public Coord3d[] getData() {
    return coordinates;
  }

  public void setColors(Color[] colors) {
    this.colors = colors;

    fireDrawableChanged(new DrawableChangedEvent(this, DrawableChangedEvent.FIELD_COLOR));
  }

  @Override
  public void setColor(Color color) {
    this.rgb = color;

    fireDrawableChanged(new DrawableChangedEvent(this, DrawableChangedEvent.FIELD_COLOR));
  }

  @Override
  public Color getColor() {
    return rgb;
  }

  /**
   * Set the width of the point.
   * 
   * @param width point's width
   */
  public void setWidth(float width) {
    this.width = width;
  }

  public Color[] getColors() {
    return colors;
  }

  public Coord3d[] getCoordinates() {
    return coordinates;
  }

  public float getWidth() {
    return width;
  }

  /**********************************************************************/

  public Color[] colors;
  public Coord3d[] coordinates;
  public Color rgb;
  public float width;
}
