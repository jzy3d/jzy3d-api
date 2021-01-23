package org.jzy3d.colors;

/**
 * Color interface.
 * <p>
 * The Color interface provide a representation of a color, independant from the target Window
 * Toolkit (AWT, SWT, etc).
 * 
 * @author Martin Pernollet
 */
public class AWTColor {

  public static java.awt.Color toAWT(Color c) {
    return new java.awt.Color(c.r, c.g, c.b, c.a);
  }

  public static Color fromAWT(java.awt.Color c) {
    return new Color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
  }

}
