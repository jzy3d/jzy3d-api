package org.jzy3d.junit.replay.events;

import org.jzy3d.maths.IntegerCoord2d;

public interface IMouseEventLog extends IEventLog {
  public MouseEventType getType();

  public IntegerCoord2d getCoord();

  public int getValue();

  public int getButton();

  public int getClicks();

  public enum MouseEventType {
    MOUSE_CLICKED, MOUSE_PRESSED, MOUSE_RELEASED, MOUSE_MOVED, MOUSE_DRAGGED, MOUSE_WHEEL
  }
}
