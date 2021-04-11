package org.jzy3d.junit.replay.events;

import java.awt.event.InputEvent;
import org.jzy3d.maths.IntegerCoord2d;

public class MouseEventLog extends AbstractEventLog implements IMouseEventLog {
  public MouseEventLog(MouseEventType type, int x, int y, int button, long since) {
    this.type = type;
    this.button = button;
    this.since = since;
    this.coord = new IntegerCoord2d(x, y);
  }

  public MouseEventLog(MouseEventType type, int value, int button, long since) {
    this.type = type;
    this.button = button;
    this.value = value;
    this.since = since;
  }

  @Override
  public IntegerCoord2d getCoord() {
    return coord;
  }

  @Override
  public int getButton() {
    return button;
  }

  @Override
  public int getClicks() {
    return clicks;
  }

  @Override
  public MouseEventType getType() {
    return type;
  }

  @Override
  public int getValue() {
    return value;
  }

  @Override
  public String toString() {
    if (coord != null)
      return type + ", x:" + coord.x + ", y:" + coord.y + ", bt:" + button + ", since:" + since;
    else
      return type + ", v:" + value + ", bt:" + button + ", since:" + since;
  }

  protected MouseEventType type;
  protected IntegerCoord2d coord;
  protected int value;

  protected int button = InputEvent.BUTTON1_MASK;
  protected int clicks;

}
