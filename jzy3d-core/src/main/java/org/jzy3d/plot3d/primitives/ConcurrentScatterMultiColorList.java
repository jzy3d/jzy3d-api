package org.jzy3d.plot3d.primitives;

import java.util.ArrayList;
import java.util.List;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.IMultiColorable;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.transform.Transform;

public class ConcurrentScatterMultiColorList extends ScatterMultiColorList
    implements IMultiColorable {
  public ConcurrentScatterMultiColorList(ColorMapper mapper) {
    this(new ArrayList<Coord3d>(), mapper, 1.0f);
  }

  public ConcurrentScatterMultiColorList(List<Coord3d> coordinates, ColorMapper mapper) {
    this(coordinates, mapper, 1.0f);
  }

  public ConcurrentScatterMultiColorList(List<Coord3d> coordinates, ColorMapper mapper,
      float width) {
    super(coordinates, mapper, width);
  }

  /* */

  @Override
  public void draw(IPainter painter) {
    doTransform(painter);

    painter.glPointSize(width);
    painter.glBegin_Point();

    if (coordinates != null) {
      synchronized (coordinates) { // here is the difference!
        for (Coord3d coord : coordinates) {
          painter.color(mapper.getColor(coord));
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

  @Override
  public void add(Coord3d c) {
    synchronized (coordinates) {
      coordinates.add(c);
    }
  }

  @Override
  public void clear() {
    synchronized (coordinates) {
      coordinates.clear();
    }
    updateBounds();
  }
}
