package org.jzy3d.chart.controllers.mouse;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

public class AWTMouseUtilities {
  public static boolean isDoubleClick(MouseEvent e) {
    return (e.getClickCount() > 1);
  }

  public static boolean isLeftDown(MouseEvent e) {
    return (e.getModifiersEx() & InputEvent.BUTTON1_DOWN_MASK) == InputEvent.BUTTON1_DOWN_MASK;
  }

  public static boolean isRightDown(MouseEvent e) {
    return (e.getModifiersEx() & InputEvent.BUTTON3_DOWN_MASK) == InputEvent.BUTTON3_DOWN_MASK;
  }

  public static boolean isRightClick(MouseEvent e) {
    return (e.getModifiersEx() & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK;
  }
}
