package org.jzy3d.plot2d.rendering;

import org.jzy3d.colors.Color;

/**
 * Canvas interface.
 * <p>
 * This interface defines the set of methods that any concrete canvas should define.
 * 
 * Concrete canvases should be defined in order to provide a way to draw a wafer representation on
 * various window toolkig (AWT, SWT, etc).
 * 
 * @author Martin Pernollet
 */
public interface Canvas {
  /**
   * Draws a rectangle.
   * 
   * @param Color color of the square
   * @param x x value of the square.
   * @param y y value of the square.
   * @param width width of the square.
   * @param heigth heigth of the square.
   */
  public void drawRect(Color color, int x, int y, int width, int heigth, boolean border);

  /** Draws a rectangle. */
  public void drawRect(Color color, int x, int y, int width, int heigth);

  /**
   * Draws a dot, represented by a small rectangle.
   * 
   * @param color the pixel.
   * @param x x value of the dot.
   * @param y y value of the dot.
   */
  public void drawDot(Color color, int x, int y);

  /**
   * Draws an oval.
   * 
   * @param Color color of the oval
   * @param x x value of the oval center.
   * @param y y value of the oval center.
   * @param width width of the wafer circle.
   * @param height height of the wafer circle.
   */
  public void drawOval(Color color, int x, int y, int width, int height);

  /**
   * Draws a text.
   * 
   * @param x x value of the text.
   * @param y y value of the text.
   * @param text text to be displayed.
   */
  public void drawString(int x, int y, String text);

  /**
   * Draws the picture background in the given color.
   * 
   * @param color the background color.
   * @param width width of the picture.
   * @param height height of the picture.
   */
  public void drawBackground(Color color, int width, int height);
}
