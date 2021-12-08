package org.jzy3d.io;

import org.jzy3d.colors.Color;
import com.diogonunes.jcolor.Ansi;
import com.diogonunes.jcolor.Attribute;

/**
 * A console output helper able to add coloring in console.
 * 
 * @author Martin Pernollet
 *
 */
public class Console {
  public static void print(String text, Color background) {
    System.out.print(Ansi.colorize(text, toBackground(background)));
  }

  public static void println(String text, Color background) {
    System.out.println(Ansi.colorize(text, toBackground(background)));
  }

  public static void print(Color background) {
    System.out.print(Ansi.colorize("  ", toBackground(background)));
  }

  public static void println(Color background) {
    System.out.println(Ansi.colorize("  ", toBackground(background)));
  }

  public static Attribute toBackground(Color background) {
    return Attribute.BACK_COLOR((int)(background.r*255), (int)(background.g*255), (int)(background.b*255));
  }
}
