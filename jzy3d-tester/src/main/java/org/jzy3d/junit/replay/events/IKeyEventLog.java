package org.jzy3d.junit.replay.events;

public interface IKeyEventLog extends IEventLog {
  public KeyEventType getType();

  public int getKeyCode();

  public enum KeyEventType {
    KEY_PRESS, KEY_RELEASE, KEY_TYPED
  }
}
