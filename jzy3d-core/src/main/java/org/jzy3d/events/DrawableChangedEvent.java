package org.jzy3d.events;

import java.util.EventObject;

public class DrawableChangedEvent extends EventObject {
  public static final int FIELD_DATA = 0;
  public static final int FIELD_TRANSFORM = 1;
  public static final int FIELD_COLOR = 2;
  public static final int FIELD_METADATA = 3;
  public static final int FIELD_DISPLAYED = 4;

  public DrawableChangedEvent(Object source, int what) {
    super(source);
    this.what = what;
  }

  public int what() {
    return what;
  }

  /*************************************************************************/


  private int what = -1;

  private static final long serialVersionUID = 7467846578948284603L;
}
