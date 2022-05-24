package org.jzy3d.events;

import java.util.EventObject;
import org.jzy3d.maths.Scale;


public class ScaleChangedEvent extends EventObject {

  public ScaleChangedEvent(Object source, Scale scaling, int scaleId) {
    super(source);
    this.scaling = scaling;
    this.scaleId = scaleId;
  }

  public ScaleChangedEvent(Object source, Scale scaling) {
    super(source);
    this.scaling = scaling;
  }

  public Scale getScaling() {
    return scaling;
  }

  public int getScaleId() {
    return scaleId;
  }

  /*****************************************************************/

  @Override
  public String toString() {
    return new String(getClass() + ":id=" + scaleId + ", scale=" + scaling);
  }

  /*****************************************************************/

  private Scale scaling;
  private int scaleId;

  private static final long serialVersionUID = 6611155309944419920L;
}
