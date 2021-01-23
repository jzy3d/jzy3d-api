package org.jzy3d.junit.replay.events;

import java.awt.Dimension;
import java.awt.Rectangle;

public class ComponentEventLog extends AbstractEventLog implements IComponentEventLog {
  protected ComponentEventType type;
  protected Object value;
  protected Dimension size;
  protected Rectangle bounds;

  public ComponentEventLog(ComponentEventType type, long since) {
    this.type = type;
    this.since = since;
  }

  public ComponentEventLog(ComponentEventType type, Object value, long since) {
    this.type = type;
    this.value = value;
    this.since = since;
  }

  public ComponentEventLog(ComponentEventType type, Dimension size, Rectangle bounds, long since) {
    this.type = type;
    this.since = since;
    this.size = size;
    this.bounds = bounds;
  }

  @Override
  public ComponentEventType getType() {
    return type;
  }

  @Override
  public Object getValue() {
    return value;
  }

  @Override
  public Dimension getSize() {
    return size;
  }

  @Override
  public Rectangle getBounds() {
    return bounds;
  }

  @Override
  public String toString() {
    if (value != null)
      return type + ", value:" + value + ", since:" + since;
    else if (size != null || bounds != null)
      return type + ", size:" + size + ", bounds:" + bounds + ", since:" + since;
    else
      return type + ", since:" + since;
  }

}
