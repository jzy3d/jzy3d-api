package org.jzy3d.chart.controllers.mouse;

import com.jogamp.newt.event.InputEvent;
import com.jogamp.newt.event.MouseEvent;

public class NewtMouseUtilities {
  public static boolean isDoubleClick(MouseEvent e) {
    return (e.getClickCount() > (short) 1);
  }

  public static boolean isLeftDown(MouseEvent e) {
    return e.isButtonDown(MouseEvent.BUTTON1);
  }

  public static boolean isRightDown(MouseEvent e) {
    return e.isButtonDown(MouseEvent.BUTTON3);
  }

  public static boolean isRightClick(MouseEvent e) {
    return (e.getModifiers() & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK;
  }

  public static float convertWheelRotation(MouseEvent e, float offset, float stepSize) {
    return offset + ((e.getRotation()[1] * e.getRotationScale()) / stepSize);
  }
}
