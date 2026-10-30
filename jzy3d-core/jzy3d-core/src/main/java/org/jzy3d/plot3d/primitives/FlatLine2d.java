package org.jzy3d.plot3d.primitives;

import java.util.ArrayList;
import java.util.List;
import org.jzy3d.maths.Coord3d;


public class FlatLine2d extends Composite {
  public FlatLine2d() {}

  public FlatLine2d(float[] x, float[] y, float depth) {
    setData(x, y, depth);
  }

  public void setData(float[] x, float[] y, float depth) {
    if (x.length != y.length)
      throw new IllegalArgumentException("x and y must have equal length");
    List<Drawable> quads = new ArrayList<Drawable>(x.length * y.length);
    for (int i = 0; i < x.length - 1; i++)
      quads.add(getLineElement(x[i], x[i + 1], y[i], y[i + 1], depth));
    add(quads);
  }

  protected Drawable getLineElement(float x1, float x2, float y1, float y2, float depth) {
    Quad q = new Quad();
    q.add(new Point(new Coord3d(0f, x1, y1)));
    q.add(new Point(new Coord3d(0f, x2, y2)));
    q.add(new Point(new Coord3d(depth, x2, y2)));
    q.add(new Point(new Coord3d(depth, x1, y1)));
    return q;
  }
}
