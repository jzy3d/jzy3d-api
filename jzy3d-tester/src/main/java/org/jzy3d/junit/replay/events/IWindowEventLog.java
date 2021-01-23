package org.jzy3d.junit.replay.events;

public interface IWindowEventLog extends IEventLog {
  public WindowEventType getType();

  public Object getValue();

  public enum WindowEventType {
    WINDOW_OPENED, WINDOW_CLOSING, WINDOW_CLOSED, WINDOW_MOVED,
  }
}
