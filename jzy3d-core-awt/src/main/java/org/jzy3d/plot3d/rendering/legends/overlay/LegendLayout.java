package org.jzy3d.plot3d.rendering.legends.overlay;

import java.awt.Font;
import org.jzy3d.colors.Color;

public class LegendLayout {
  /** External margin : distance between legend border canvas border. */
  public int boxMarginX = 5;
  /** External margin : distance between legend border canvas border. */
  public int boxMarginY = 5;
  /** Internal margin : distance between legend border and text. */
  public int txtMarginX = 5;
  /** Internal margin : distance between legend border and text. */
  public int txtMarginY = 10;
  /** Number of pixel between two lines of text in the legend. */
  public int txtInterline = 5;
  /** Minimum pixel distance between a legend text and a legend line. */
  public int sampleLineMargin = 5;
  /** Number of pixel for a legend line. */
  public int sampleLineLength = 15;
  /** Color of legend font. */
  public Color fontColor = Color.BLACK;
  /** Color of legend border. */
  public Color borderColor = Color.BLACK;
  /** Color of legend background. Translucent if none. */
  public Color backgroundColor = null;
  /** Legend position. */
  public Corner corner = Corner.TOP_LEFT;
  /**
   * Legend font. Can be null, or otherwise initialized like
   * <code>new Font("Helvetica", Font.PLAIN, 11)</code>
   */
  public Font font = null;

  public enum Corner {
    TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
  }
}
