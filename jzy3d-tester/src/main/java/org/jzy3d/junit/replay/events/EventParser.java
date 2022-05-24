package org.jzy3d.junit.replay.events;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jzy3d.junit.replay.events.IComponentEventLog.ComponentEventType;
import org.jzy3d.junit.replay.events.IKeyEventLog.KeyEventType;
import org.jzy3d.junit.replay.events.IMouseEventLog.MouseEventType;
import org.jzy3d.junit.replay.events.IWindowEventLog.WindowEventType;

public class EventParser {
  static String word = "\\w+";
  static String word_list = "[\\w|,]+";
  static String num = "\\d+";
  static String antislash = "\\\\";
  static String slash = "\\/";

  /*
   * COMPONENT_RESIZED, size:java.awt.Dimension[width=490,height=470],
   * bounds:java.awt.Rectangle[x=5,y=25,width=490,height=470], since:524 COMPONENT_MOVED,
   * size:java.awt.Dimension[width=498,height=471],
   * bounds:java.awt.Rectangle[x=1,y=28,width=498,height=471], since:785
   * 
   * WINDOW_OPENED, v:java.awt.Dimension[width=500,height=500], since:525 WINDOW_CLOSING,
   * v:java.awt.Dimension[width=500,height=500], since:5120 WINDOW_CLOSED, since:5120
   * 
   * MOUSE_MOVED, x:232, y:94, bt:0, since:702 MOUSE_PRESSED, x:244, y:245, bt:1, since:1397
   * MOUSE_DRAGGED, x:241, y:239, bt:0, since:1558 MOUSE_RELEASED, x:145, y:58, bt:1, since:16214
   * MOUSE_WHEEL, v:1, bt:0, since:3205
   * 
   * KEY_PRESS, code:83, since:6750 KEY_TYPED, code:0, since:6750 KEY_RELEASE, code:83, since:6946
   */

  static Pattern mouseMovedPattern = Pattern.compile(
      "MOUSE_MOVED, x:(" + num + "), y:(" + num + "), bt:(" + num + "), since:(" + num + ")");
  static Pattern mousePressedPattern = Pattern.compile(
      "MOUSE_PRESSED, x:(" + num + "), y:(" + num + "), bt:(" + num + "), since:(" + num + ")");
  static Pattern mouseDraggedPattern = Pattern.compile(
      "MOUSE_DRAGGED, x:(" + num + "), y:(" + num + "), bt:(" + num + "), since:(" + num + ")");
  static Pattern mouseReleasePattern = Pattern.compile(
      "MOUSE_RELEASED, x:(" + num + "), y:(" + num + "), bt:(" + num + "), since:(" + num + ")");
  static Pattern mouseWheelPattern =
      Pattern.compile("MOUSE_WHEEL, v:(" + num + "), bt:(" + num + "), since:(" + num + ")");

  static Pattern keyPressPattern =
      Pattern.compile("KEY_PRESS.*, code:(" + num + "), since:(" + num + ")");
  static Pattern keyTypedPattern =
      Pattern.compile("KEY_TYPED.*, code:(" + num + "), since:(" + num + ")");
  static Pattern keyReleasePattern =
      Pattern.compile("KEY_RELEASE.*, code:(" + num + "), since:(" + num + ")");

  static Pattern windowOpenedPattern = Pattern.compile("WINDOW_OPENED.*, since:(" + num + ")");
  static Pattern windowClosingPattern = Pattern.compile("WINDOW_CLOSING.*, since:(" + num + ")");
  static Pattern windowClosedPattern = Pattern.compile("WINDOW_CLOSED.*, since:(" + num + ")");

  static Pattern componentResizedPattern =
      Pattern.compile("COMPONENT_RESIZED, size:java.awt.Dimension[width=(" + num + "),height=("
          + num + ")], bounds:java.awt.Rectangle[x=(" + num + "),y=(" + num + "),width=(" + num
          + "),height=(" + num + ")] since:(" + num + ")");
  // static Pattern componentResizedPattern = Pattern.compile("COMPONENT_RESIZED.*,
  // since:("+num+")");
  static Pattern componentMovedPattern = Pattern.compile("COMPONENT_MOVED.*, since:(" + num + ")");

  public IEventLog parse(String event) {
    // MOUSE
    Matcher mouseMovedMatcher = mouseMovedPattern.matcher(event);
    if (mouseMovedMatcher.matches())
      return parseMouseEvent(mouseMovedMatcher, MouseEventType.MOUSE_MOVED);

    Matcher mousePressedMatcher = mousePressedPattern.matcher(event);
    if (mousePressedMatcher.matches())
      return parseMouseEvent(mousePressedMatcher, MouseEventType.MOUSE_PRESSED);

    Matcher mouseDraggedMatcher = mouseDraggedPattern.matcher(event);
    if (mouseDraggedMatcher.matches())
      return parseMouseEvent(mouseDraggedMatcher, MouseEventType.MOUSE_DRAGGED);

    Matcher mouseReleasedMatcher = mouseReleasePattern.matcher(event);
    if (mouseReleasedMatcher.matches())
      return parseMouseEvent(mouseReleasedMatcher, MouseEventType.MOUSE_RELEASED);

    Matcher mouseWheelMatcher = mouseWheelPattern.matcher(event);
    if (mouseWheelMatcher.matches())
      return parseMouseWheelEvent(mouseWheelMatcher);

    // KEYBOARD
    Matcher windowOpenMatcher = windowOpenedPattern.matcher(event);
    if (windowOpenMatcher.matches())
      return parseWindowEvent(windowOpenMatcher, WindowEventType.WINDOW_OPENED);

    Matcher windowClosingMatcher = windowClosingPattern.matcher(event);
    if (windowClosingMatcher.matches())
      return parseWindowEvent(windowClosingMatcher, WindowEventType.WINDOW_CLOSING);

    Matcher windowClosedMatcher = windowClosedPattern.matcher(event);
    if (windowClosedMatcher.matches())
      return parseWindowEvent(windowClosedMatcher, WindowEventType.WINDOW_CLOSED);

    // WINDOW
    Matcher keyPressMatcher = keyPressPattern.matcher(event);
    if (keyPressMatcher.matches())
      return parseKeyEvent(keyPressMatcher, KeyEventType.KEY_PRESS);

    Matcher keyTypedMatcher = keyTypedPattern.matcher(event);
    if (keyTypedMatcher.matches())
      return parseKeyEvent(keyTypedMatcher, KeyEventType.KEY_TYPED);

    Matcher keyReleaseMatcher = keyReleasePattern.matcher(event);
    if (keyReleaseMatcher.matches())
      return parseKeyEvent(keyReleaseMatcher, KeyEventType.KEY_RELEASE);

    // COMPONENT
    Matcher componentResizedMatcher = componentResizedPattern.matcher(event);
    if (componentResizedMatcher.matches())
      return parseComponentEvent(componentResizedMatcher, ComponentEventType.COMPONENT_RESIZED);

    Matcher componentMovedMatcher = componentMovedPattern.matcher(event);
    if (componentMovedMatcher.matches())
      return parseComponentEvent(componentMovedMatcher, ComponentEventType.COMPONENT_MOVED);

    return null;
  }

  protected IMouseEventLog parseMouseEvent(Matcher matcher, MouseEventType type) {
    int x = Integer.parseInt(matcher.group(1));
    int y = Integer.parseInt(matcher.group(2));
    int b = Integer.parseInt(matcher.group(3));
    int s = Integer.parseInt(matcher.group(4));
    return new MouseEventLog(type, x, y, b, s);
  }

  protected IMouseEventLog parseMouseWheelEvent(Matcher matcher) {
    int v = Integer.parseInt(matcher.group(1));
    int b = Integer.parseInt(matcher.group(2));
    int s = Integer.parseInt(matcher.group(3));
    return new MouseEventLog(MouseEventType.MOUSE_WHEEL, v, b, s);
  }

  protected IKeyEventLog parseKeyEvent(Matcher matcher, KeyEventType type) {
    int c = Integer.parseInt(matcher.group(1));
    int s = Integer.parseInt(matcher.group(2));
    return new KeyEventLog(type, c, s);
  }

  protected IWindowEventLog parseWindowEvent(Matcher matcher, WindowEventType type) {
    int s = Integer.parseInt(matcher.group(1));
    return new WindowEventLog(type, s);
  }

  protected IComponentEventLog parseComponentEvent(Matcher matcher, ComponentEventType type) {
    int w = Integer.parseInt(matcher.group(1));
    int h = Integer.parseInt(matcher.group(2));
    int rx = Integer.parseInt(matcher.group(3));
    int ry = Integer.parseInt(matcher.group(4));
    int rw = Integer.parseInt(matcher.group(5));
    int rh = Integer.parseInt(matcher.group(6));
    int s = Integer.parseInt(matcher.group(7));

    return new ComponentEventLog(type, new Dimension(w, h), new Rectangle(rx, ry, rw, rh), s);
  }
}
