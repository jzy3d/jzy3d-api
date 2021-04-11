package org.jzy3d.events;

import java.util.EventObject;
import org.jzy3d.chart.controllers.ControllerType;


public class ControllerEvent extends EventObject {


  public ControllerEvent(Object source, ControllerType type, Object value) {
    super(source);
    this.type = type;
    this.value = value;
  }

  public ControllerType getType() {
    return type;
  }

  public Object getValue() {
    return value;
  }

  @Override
  public String toString() {
    return ("ControllerEvent(type,value): " + type + ", " + value);
  }

  /*****************************************************************/
  private ControllerType type;
  private Object value;
  private static final long serialVersionUID = 2397806577447019679L;

}
