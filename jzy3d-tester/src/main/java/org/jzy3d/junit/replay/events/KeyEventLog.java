package org.jzy3d.junit.replay.events;

public class KeyEventLog extends AbstractEventLog implements IKeyEventLog {
  protected int keyCode;
  protected KeyEventType type;

  public KeyEventLog(KeyEventType type, int keyCode, long since) {
    this.keyCode = keyCode;
    this.type = type;
    this.since = since;
  }

  @Override
  public KeyEventType getType() {
    return type;
  }

  @Override
  public int getKeyCode() {
    return keyCode;
  }

  @Override
  public String toString() {
    return type + ", code:" + keyCode + ", since:" + since;
  }
}
