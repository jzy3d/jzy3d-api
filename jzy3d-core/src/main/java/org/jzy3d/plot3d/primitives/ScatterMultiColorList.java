package org.jzy3d.plot3d.primitives;

import java.util.ArrayList;
import java.util.List;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.IMultiColorable;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.transform.Transform;

/**
 * A scatter plot supporting a List<Coord3d> as input.
 * 
 * @author Martin Pernollet
 */
public class ScatterMultiColorList extends Drawable implements IMultiColorable {
  public ScatterMultiColorList(ColorMapper mapper) {
    this(new ArrayList<Coord3d>(), mapper, 1.0f);
  }

  public ScatterMultiColorList(List<Coord3d> coordinates, ColorMapper mapper) {
    this(coordinates, mapper, 1.0f);
  }

  public ScatterMultiColorList(List<Coord3d> coordinates, ColorMapper mapper, float width) {
    bbox = new BoundingBox3d();
    setData(coordinates);
    setWidth(width);
    setColorMapper(mapper);
  }

  public void clear() {
    coordinates.clear();
    updateBounds();
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
        painter.color(mapper.getColor(coord));
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
  public void setData(List<Coord3d> coordinates) {
    this.coordinates = coordinates;

    bbox.reset();
    for (Coord3d c : coordinates)
      bbox.add(c);
  }



  public List<Coord3d> getData() {
    return coordinates;
  }

  public void add(Coord3d c) {
    coordinates.add(c);
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

  protected List<Coord3d> coordinates;
  protected float width;
  protected ColorMapper mapper;
}
