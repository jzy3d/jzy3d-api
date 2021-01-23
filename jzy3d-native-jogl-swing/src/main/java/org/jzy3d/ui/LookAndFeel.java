package org.jzy3d.ui;

import javax.swing.UIManager;

public class LookAndFeel {
  public static void apply() {
    if (!done) {
      try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } catch (Exception e) {
        e.printStackTrace();
      }
      done = true;
    }
  }

  public static String getNoInsets() {
    return "insets 0 0 0 0";
  }

  public static String getDefaultInsets() {
    return "";
  }

  protected static boolean done = false;
}
