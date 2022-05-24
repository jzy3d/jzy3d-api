package org.jzy3d.plot3d.primitives;

import java.util.ArrayList;
import java.util.List;


/**
 * Allows building custom shapes defined by an {@link ArrayList} of {@link Polygon}s. Such
 * {@link ArrayList} must be defined by the user.
 * 
 * @author Martin Pernollet
 */
public class Shape extends Composite {

  /** Initialize a Shape. */
  public Shape() {
    super();
  }

  /**
   * Initialize a Shape and set its polygons (and bounding box).
   * 
   * @see setData for more information on the allowTransparency flag.
   */
  public Shape(List<? extends Drawable> drawables) {
    super();
    add(drawables);
  }
}
