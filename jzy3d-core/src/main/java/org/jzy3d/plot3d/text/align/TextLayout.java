package org.jzy3d.plot3d.text.align;

import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;

/**
 * A helper to process text position according to a {@link Halign}, {@link Valign}, text height and
 * width.
 * 
 * @author Martin Pernollet
 */
public class TextLayout {
  /**
   * Compute final text position according to the below layout parameters and initial screen
   * position.
   * 
   * @param textWidth width of the text in pixel
   * @param textHeight height of the text font in pixel
   * @param halign horizontal alignment
   * @param valign vertical alignment
   * @param offset an (x,y) offset to apply to the base position once X and Y alignment are
   *        processed
   * @param screen base position of the text on screen (Z dimension is used to indicate how far is
   *        the text in the 3D scene)
   */
  public static Coord3d align(float textWidth, float textHeight, Halign halign, Valign valign,
      Coord2d offset, Coord3d screen) {
    float x = computeXAlign(textWidth, halign, screen, 0.0f);
    float y = computeYAlign(textHeight, valign, screen, 0.0f);

    return new Coord3d(x + offset.x, y + offset.y, screen.z);
  }

  public static Coord3d align(float textWidth, float textHeight, Halign halign, Valign valign,
      Coord3d screen) {
    return align(textWidth, textHeight, halign, valign, new Coord2d(), screen);
  }

  public static float computeXAlign(float textWidth, Halign halign, Coord3d screenPosition,
      float x) {
    if (halign == Halign.RIGHT)
      x = screenPosition.x;
    else if (halign == Halign.CENTER)
      x = screenPosition.x - textWidth / 2;
    else if (halign == Halign.LEFT)
      x = screenPosition.x - textWidth;
    return x;
  }

  public static float computeYAlign(float textHeight, Valign valign, Coord3d screenPosition,
      float y) {
    if (valign == Valign.TOP)
      y = screenPosition.y;
    else if (valign == Valign.GROUND)
      y = screenPosition.y;
    else if (valign == Valign.CENTER)
      y = screenPosition.y - textHeight / 2;
    else if (valign == Valign.BOTTOM)
      y = screenPosition.y - textHeight;
    return y;
  }
}
