package org.jzy3d.plot3d.rendering.legends.overlay;

import java.awt.Font;
import org.jzy3d.colors.Color;

public class LineLegendLayout extends LegendLayout {
  /** Internal margin : distance between legend border and text. */
  protected int txtMarginX = 5;
  /** Internal margin : distance between legend border and text. */
  protected int txtMarginY = 10;
  /** Number of pixel between two lines of text in the legend. */
  protected int txtInterline = 5;
  /** Minimum pixel distance between a legend text and a legend line. */
  protected int sampleLineMargin = 5;
  /** Number of pixel for a legend line. */
  protected int sampleLineLength = 15;
  /** Color of legend font. */
  protected Color fontColor = Color.BLACK;
  /** Color of legend border. */
  protected Color borderColor = Color.BLACK;
  /** Color of legend background. Translucent if none. */
  protected Color backgroundColor = null;
  /**
   * Legend font. Can be null, or otherwise initialized like
   * <code>new Font("Helvetica", Font.PLAIN, 11)</code>
   */
  protected Font font = null;
  
  public int getTxtMarginX() {
    return txtMarginX;
  }
  public void setTxtMarginX(int txtMarginX) {
    this.txtMarginX = txtMarginX;
  }
  public int getTxtMarginY() {
    return txtMarginY;
  }
  public void setTxtMarginY(int txtMarginY) {
    this.txtMarginY = txtMarginY;
  }
  public int getTxtInterline() {
    return txtInterline;
  }
  public void setTxtInterline(int txtInterline) {
    this.txtInterline = txtInterline;
  }
  public int getSampleLineMargin() {
    return sampleLineMargin;
  }
  public void setSampleLineMargin(int sampleLineMargin) {
    this.sampleLineMargin = sampleLineMargin;
  }
  public int getSampleLineLength() {
    return sampleLineLength;
  }
  public void setSampleLineLength(int sampleLineLength) {
    this.sampleLineLength = sampleLineLength;
  }
  public Color getFontColor() {
    return fontColor;
  }
  public void setFontColor(Color fontColor) {
    this.fontColor = fontColor;
  }
  public Color getBorderColor() {
    return borderColor;
  }
  public void setBorderColor(Color borderColor) {
    this.borderColor = borderColor;
  }
  public Color getBackgroundColor() {
    return backgroundColor;
  }
  public void setBackgroundColor(Color backgroundColor) {
    this.backgroundColor = backgroundColor;
  }
  public Font getFont() {
    return font;
  }
  public void setFont(Font font) {
    this.font = font;
  }
}
