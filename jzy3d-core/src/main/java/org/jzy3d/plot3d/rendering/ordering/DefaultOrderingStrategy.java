package org.jzy3d.plot3d.rendering.ordering;

import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Drawable;

/**
 * The default ordering strategy let drawables be displayed in their original order and as thus no
 * computation cost.
 */
public class DefaultOrderingStrategy extends AbstractOrderingStrategy {
  @Override
  public int compare(Drawable o1, Drawable o2) {
    return 0;
  }

  @Override
  public double score(Drawable drawable) {
    return 0;
  }

  @Override
  public double score(Coord3d coord) {
    return 0;
  }
}
