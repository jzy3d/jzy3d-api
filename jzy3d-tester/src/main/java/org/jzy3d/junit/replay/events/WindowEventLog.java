package org.jzy3d.junit.replay.events;

public class WindowEventLog extends AbstractEventLog implements IWindowEventLog {
  public WindowEventLog(WindowEventType type, long since) {
    this.since = since;
    this.type = type;
  }

  public WindowEventLog(WindowEventType type, Object value, long since) {
    this.since = since;
    this.type = type;
    this.value = value;
  }

  @Override
  public WindowEventType getType() {
    return type;
  }

  @Override
  public Object getValue() {
    return value;
  }

  @Override
  public String toString() {
    if (value != null)
      return type + ", v:" + value + ", since:" + since;
    else
      return type + ", since:" + since;
  }

  protected WindowEventType type;
  protected Object value;
}
