package org.jzy3d.events;

import java.util.EventObject;
import org.jzy3d.plot3d.rendering.view.modes.ViewPositionMode;



public class ViewModeChangedEvent extends EventObject {

  public ViewModeChangedEvent(Object source, ViewPositionMode mode) {
    super(source);
    this.mode = mode;
  }

  public ViewPositionMode getMode() {
    return mode;
  }

  /*************************************************************************/

  private ViewPositionMode mode;
  private static final long serialVersionUID = 7467846578948284603L;
}
