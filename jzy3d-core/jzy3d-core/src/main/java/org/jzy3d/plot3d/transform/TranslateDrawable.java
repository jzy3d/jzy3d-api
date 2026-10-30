package org.jzy3d.plot3d.transform;

import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.Drawable;

/**
 * Translate drawable to (0,0,0) or back to its previous position according to the input parameters.
 */
public class TranslateDrawable implements Transformer {
  public TranslateDrawable(Drawable drawable, boolean reverse) {
    super();
    this.reverse = reverse;
    this.drawable = drawable;
  }

  @Override
  public void execute(IPainter painter) {
    if (drawable != null) {
      BoundingBox3d bounds = drawable.getBounds();
      if (bounds != null) {
        Coord3d center = bounds.getCenter();
        translateTo(painter, center, reverse);
      }
    }
  }

  public void translateTo(IPainter painter, Coord3d center, boolean reverse) {
    if (reverse)
      painter.glTranslatef(-center.x / 2, -center.y / 2, -center.z / 2);
    else
      painter.glTranslatef(center.x / 2, center.y / 2, center.z / 2);
  }

  @Override
  public Coord3d compute(Coord3d input) {
    if (drawable != null) {
      BoundingBox3d bounds = drawable.getBounds();
      if (bounds != null) {
        Coord3d center = bounds.getCenter();
        if (reverse)
          return input.sub(center.div(2));
        else
          return input.add(center.div(2));
      }
    }
    return null;
  }

  public Drawable getDrawable() {
    return drawable;
  }

  public void setDrawable(Drawable drawable) {
    this.drawable = drawable;
  }

  public boolean isReverse() {
    return reverse;
  }

  public void setReverse(boolean reverse) {
    this.reverse = reverse;
  }

  @Override
  public String toString() {
    return "(TranslateDrawable)" + drawable;
  }

  protected Drawable drawable;
  protected boolean reverse;
}
