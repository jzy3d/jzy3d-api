package org.jzy3d.painters;

public class AWTFont {

  public static java.awt.Font toAWT(Font font) {
    return new java.awt.Font(font.getName(), java.awt.Font.PLAIN, font.getHeight());
  }

}
