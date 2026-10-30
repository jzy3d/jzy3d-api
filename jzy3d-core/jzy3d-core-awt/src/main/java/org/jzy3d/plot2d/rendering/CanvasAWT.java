package org.jzy3d.plot2d.rendering;

import java.awt.Graphics2D;
import org.jzy3d.colors.Color;



public class CanvasAWT implements Canvas {

  /**
   * Creates a new instance of Pencil2dAWT.
   * 
   * A Pencil2dAWT provides an implementation for drawing wafer sites on AWT.
   */
  public CanvasAWT(Graphics2D graphic) {
    target = graphic;
  }

  @Override
  public void drawString(int x, int y, String text) {
    target.setColor(fgColor);
    target.drawString(text, x, y);
  }

  @Override
  public void drawRect(Color color, int x, int y, int width, int height, boolean border) {
    if (color != null) {
      target.setColor(awt(color));
      target.fillRect(x, y, width, height);
    }
    if (border) {
      target.setColor(BLACK);
      target.drawRect(x, y, width, height);
    }
  }

  @Override
  public void drawRect(Color color, int x, int y, int width, int height) {
    drawRect(color, x, y, width, height, true);
  }

  @Override
  public void drawDot(Color color, int x, int y) {
    target.setColor(awt(color));
    target.fillRect(x - PIXEL_WITH / 2, y - PIXEL_WITH / 2, PIXEL_WITH, PIXEL_WITH);
  }

  @Override
  public void drawOval(Color color, int x, int y, int width, int height) {
    target.setColor(awt(color));
    target.fillOval(x, y, width, height);
    target.setColor(BLACK);
    target.drawOval(x, y, width, height);
  }

  @Override
  public void drawBackground(Color color, int width, int height) {
    bgColor = awt(color);
    target.setColor(bgColor);
    target.fillRect(0, 0, width, height);
  }

  /**********************************************************************************/

  /**
   * Converts a {@link org.jzy3d.colors.Color Imaging Color} into a {@link java.awt.Color AWT
   * Color}.
   *
   * Note that this converter does not use the AWT color's alpha channel, in order to offer the same
   * behaviour than the SWT converter.
   */
  public static java.awt.Color awt(Color color) {
    return new java.awt.Color(color.r, color.g, color.b);
  }

  /**********************************************************************************/

  private Graphics2D target;

  private java.awt.Color bgColor = WHITE; // default bg
  private java.awt.Color fgColor = BLACK; // default bg

  private static java.awt.Color WHITE = java.awt.Color.WHITE;
  private static java.awt.Color BLACK = java.awt.Color.BLACK;

  private final static int PIXEL_WITH = 2;
}
