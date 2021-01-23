package org.jzy3d.junit.replay.events;

import java.awt.Dimension;
import java.awt.Rectangle;

public interface IComponentEventLog extends IEventLog {
  public ComponentEventType getType();

  public Object getValue();

  public Dimension getSize();

  public Rectangle getBounds();

  public enum ComponentEventType {
    COMPONENT_HIDDEN, COMPONENT_RESIZED, COMPONENT_SHOWN, COMPONENT_MOVED,
  }
}
