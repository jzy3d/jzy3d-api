package org.jzy3d.events;

import java.util.EventObject;
import org.jzy3d.maths.Coord3d;


public class ViewPointChangedEvent extends EventObject {

  public ViewPointChangedEvent(Object source, Coord3d viewPoint) {
    super(source);
    this.viewPoint = viewPoint;
  }

  /**
   * Returns the viewpoint in polar coordinates. To get viewpoint in cartesian mode, use {@link Camera#getEye()}
   * @return
   */
  public Coord3d getViewPoint() {
    return viewPoint;
  }

  /***********************************************************/

  private Coord3d viewPoint;
  private static final long serialVersionUID = 6472340198525925419L;
}
