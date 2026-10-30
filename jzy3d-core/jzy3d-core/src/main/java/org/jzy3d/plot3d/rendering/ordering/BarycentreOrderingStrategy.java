package org.jzy3d.plot3d.rendering.ordering;

import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.rendering.view.View;


/**
 * The {@link BarycentreOrderingStrategy} compare two {@link Drawable}s by computing their
 * respective distances to the {@link Camera}, which must be referenced prior to any comparison.
 * 
 * @author Martin Pernollet
 */
public class BarycentreOrderingStrategy extends AbstractOrderingStrategy {
  public BarycentreOrderingStrategy() {
    super();
  }

  public BarycentreOrderingStrategy(View view) {
    super();
    this.view = view;
  }

  /**
   * Operation must be: symetric: compare(a,b)=-compare(b,a) transitive: ((compare(x, y)>0) &&
   * (compare(y, z)>0)) implies compare(x, z)>0 true if all Drawables and the Camera don't change
   * position! consistency?: compare(x, y)==0 implies that sgn(compare(x, z))==sgn(compare(y, z))
   */
  @Override
  public int compare(Drawable d1, Drawable d2) {
    if (d1.equals(d2))
      return 0;
    return comparison(score(d1), score(d2));
  }

  @Override
  public double score(Drawable d) {
    if (view != null)
      return camera.getDistance(d, view.getLastViewScaling());
    else
      return camera.getDistance(d);
  }

  @Override
  public double score(Coord3d coord) {
    if (view != null)
      return camera.getDistance(coord, view.getLastViewScaling());
    else
      return camera.getDistance(coord);
  }
}
