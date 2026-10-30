package org.jzy3d.plot3d.primitives;

import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.IMultiColorable;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.transform.Transform;

public class ConcurrentScatterMultiColor extends ScatterMultiColor implements IMultiColorable {
  public ConcurrentScatterMultiColor(Coord3d[] coordinates, Color[] colors, ColorMapper mapper) {
    this(coordinates, colors, mapper, 1.0f);
  }

  public ConcurrentScatterMultiColor(Coord3d[] coordinates, ColorMapper mapper) {
    this(coordinates, null, mapper, 1.0f);
  }

  public ConcurrentScatterMultiColor(Coord3d[] coordinates, Color[] colors, ColorMapper mapper,
      float width) {
    super(coordinates, colors, mapper, width);
  }

  @Override
  public void draw(IPainter painter) {
    doTransform(painter);

    painter.glPointSize(width);
    painter.glBegin_Point();

    if (coordinates != null) {
      synchronized (coordinates) { // difference with super type is here
        for (Coord3d coord : coordinates) {
          Color color = mapper.getColor(coord);
          painter.color(color);
          painter.vertex(coord, spaceTransformer);
        }
      }
    }
    painter.glEnd();

    doDrawBoundsIfDisplayed(painter);
  }

  @Override
  public void applyGeometryTransform(Transform transform) {
    synchronized (coordinates) {
      for (Coord3d c : coordinates) {
        c.set(transform.compute(c));
      }
    }
    updateBounds();
  }

  @Override
  public void updateBounds() {
    bbox.reset();
    synchronized (coordinates) {
      for (Coord3d c : coordinates)
        bbox.add(c);
    }
  }

}
