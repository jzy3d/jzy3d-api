package org.jzy3d.plot3d.text.align;

import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Coord3d;

/**
 * A helper to process text position according to a {@link Horizontal}, {@link Vertical}, text height and
 * width.
 * 
 * @author Martin Pernollet
 */
public class TextLayout {
  /**
   * Compute final text position on screen according to the layout parameters and initial screen
   * position.
   * 
   * @param textWidth width of the text in pixel
   * @param textHeight height of the text font in pixel
   * @param horizontal horizontal alignment
   * @param vertical vertical alignment
   * @param offset an (x,y) offset to apply to the base position once X and Y alignment are
   *        processed
   * @param screen base 2D position of the text on screen before applying layout (Z dimension is
   *        used to indicate how far is the text in the 3D scene, given as a ratio between the near
   *        and far clipping plane of the camera)
   */
  public Coord3d align(float textWidth, float textHeight, Horizontal horizontal, Vertical vertical,
      Coord2d offset, Coord3d screen) {
    float x = computeXAlign(textWidth, horizontal, screen, 0.0f);
    float y = computeYAlign(textHeight, vertical, screen, 0.0f);

    return new Coord3d(x + offset.x, y + offset.y, screen.z);
  }

  /**
   * Compute final text position on screen according to the layout parameters and initial screen
   * position.
   * 
   * @param textWidth width of the text in pixel
   * @param textHeight height of the text font in pixel
   * @param horizontal horizontal alignment
   * @param vertical vertical alignment
   * @param screen base 2D position of the text on screen before applying layout (Z dimension is
   *        used to indicate how far is the text in the 3D scene, given as a ratio between the near
   *        and far clipping plane of the camera)
   */
  public Coord3d align(float textWidth, float textHeight, Horizontal horizontal, Vertical vertical,
      Coord3d screen) {
    return align(textWidth, textHeight, horizontal, vertical, new Coord2d(), screen);
  }

  public float computeXAlign(float textWidth, Horizontal horizontal, Coord3d screenPosition, float x) {
    if (horizontal == Horizontal.RIGHT)
      x = screenPosition.x;
    else if (horizontal == Horizontal.CENTER)
      x = screenPosition.x - textWidth / 2;
    else if (horizontal == Horizontal.LEFT)
      x = screenPosition.x - textWidth;
    return x;
  }

  public float computeYAlign(float textHeight, Vertical vertical, Coord3d screenPosition, float y) {
    if (vertical == Vertical.TOP)
      y = screenPosition.y;
    else if (vertical == Vertical.GROUND)
      y = screenPosition.y;
    else if (vertical == Vertical.CENTER)
      y = screenPosition.y - textHeight / 2;
    else if (vertical == Vertical.BOTTOM)
      y = screenPosition.y - textHeight;
    return y;
  }
}
