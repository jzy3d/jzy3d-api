package org.jzy3d.bridge.newt.controllers;

import com.jogamp.newt.event.KeyEvent;

/**
 * Utilities for mapping key codes and events from awt to newt.
 *
 * @author Nils Hoffmann
 */
public class AWTToNewtUtilities {

  public static int mapKeyCode(KeyEvent event) {
    return jogamp.newt.awt.event.AWTNewtEventFactory.newtKeyCode2AWTKeyCode(event.getKeyCode());
  }

  public static int mask(com.jogamp.newt.event.MouseEvent event) {
    int newtMods = event.getModifiers();
    int awtMods = 0;
    if ((newtMods & com.jogamp.newt.event.InputEvent.SHIFT_MASK) != 0) {
      awtMods |= java.awt.event.InputEvent.SHIFT_MASK;
    }
    if ((newtMods & com.jogamp.newt.event.InputEvent.CTRL_MASK) != 0) {
      awtMods |= java.awt.event.InputEvent.CTRL_MASK;
    }
    if ((newtMods & com.jogamp.newt.event.InputEvent.META_MASK) != 0) {
      awtMods |= java.awt.event.InputEvent.META_MASK;
    }
    if ((newtMods & com.jogamp.newt.event.InputEvent.ALT_MASK) != 0) {
      awtMods |= java.awt.event.InputEvent.ALT_MASK;
    }
    if ((newtMods & com.jogamp.newt.event.InputEvent.ALT_GRAPH_MASK) != 0) {
      awtMods |= java.awt.event.InputEvent.ALT_GRAPH_MASK;
    }
    if (event.getButton() == com.jogamp.newt.event.MouseEvent.BUTTON1) {
      awtMods |= java.awt.event.InputEvent.BUTTON1_DOWN_MASK;
    }
    if (event.getButton() == com.jogamp.newt.event.MouseEvent.BUTTON2) {
      awtMods |= java.awt.event.InputEvent.BUTTON2_DOWN_MASK;
    }
    if (event.getButton() == com.jogamp.newt.event.MouseEvent.BUTTON3) {
      awtMods |= java.awt.event.InputEvent.BUTTON3_DOWN_MASK;
    }
    return awtMods;
  }

  public static int mask(com.jogamp.newt.event.KeyEvent event) {
    int newtMods = event.getModifiers();
    int awtMods = 0;
    if ((newtMods & com.jogamp.newt.event.InputEvent.SHIFT_MASK) != 0) {
      awtMods |= java.awt.event.InputEvent.SHIFT_MASK;
    }
    if ((newtMods & com.jogamp.newt.event.InputEvent.CTRL_MASK) != 0) {
      awtMods |= java.awt.event.InputEvent.CTRL_MASK;
    }
    if ((newtMods & com.jogamp.newt.event.InputEvent.META_MASK) != 0) {
      awtMods |= java.awt.event.InputEvent.META_MASK;
    }
    if ((newtMods & com.jogamp.newt.event.InputEvent.ALT_MASK) != 0) {
      awtMods |= java.awt.event.InputEvent.ALT_MASK;
    }
    if ((newtMods & com.jogamp.newt.event.InputEvent.ALT_GRAPH_MASK) != 0) {
      awtMods |= java.awt.event.InputEvent.ALT_GRAPH_MASK;
    }
    return awtMods;
  }
}
