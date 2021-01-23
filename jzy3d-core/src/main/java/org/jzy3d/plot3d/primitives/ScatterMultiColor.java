package org.jzy3d.plot3d.primitives;

import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.IMultiColorable;
import org.jzy3d.events.DrawableChangedEvent;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.transform.Transform;

/**
 * A scatter plot supporting a colormap for shading each dot color and alpha.
 * 
 * @author Martin Pernollet
 * 
 */
public class ScatterMultiColor extends Drawable implements IMultiColorable {
  public ScatterMultiColor(Coord3d[] coordinates, Color[] colors, ColorMapper mapper) {
    this(coordinates, colors, mapper, 1.0f);
  }

  public ScatterMultiColor(Coord3d[] coordinates, ColorMapper mapper) {
    this(coordinates, null, mapper, 1.0f);
  }

  public ScatterMultiColor(Coord3d[] coordinates, Color[] colors, ColorMapper mapper, float width) {
    bbox = new BoundingBox3d();
    setData(coordinates);
    setColors(colors);
    setWidth(width);
    setColorMapper(mapper);
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

    if (coordinates != null) {
      for (Coord3d coord : coordinates) {
        Color color = mapper.getColor(coord);
        painter.color(color);
        painter.vertex(coord, spaceTransformer);
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

  @Override
  public void updateBounds() {
    bbox.reset();
    for (Coord3d c : coordinates)
      bbox.add(c);
  }

  /* */

  /**
   * Set the coordinates of the point.
   * 
   * @param xyz point's coordinates
   */
  public void setData(Coord3d[] coordinates) {
    this.coordinates = coordinates;

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
  public ColorMapper getColorMapper() {
    return mapper;
  }

  @Override
  public void setColorMapper(ColorMapper mapper) {
    this.mapper = mapper;
  }

  /**
   * Set the width of the point.
   * 
   * @param width point's width
   */
  public void setWidth(float width) {
    this.width = width;
  }

  /* */

  protected Coord3d[] coordinates;
  protected Color[] colors;
  protected float width;
  protected ColorMapper mapper;

}
