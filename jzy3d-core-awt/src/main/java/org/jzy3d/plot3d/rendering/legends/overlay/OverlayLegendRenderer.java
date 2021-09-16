package org.jzy3d.plot3d.rendering.legends.overlay;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.List;
import org.jzy3d.colors.AWTColor;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.plot2d.rendering.AWTGraphicsUtils;
import org.jzy3d.plot3d.rendering.legends.overlay.LegendLayout.Corner;
import org.jzy3d.plot3d.rendering.view.AWTRenderer2d;

/**
 * 
 * Params - margin - corner - constraints : byDimension (maxWidth) or byText - interligne - font
 * size / font style
 * 
 * @author Martin Pernollet
 *
 */
public class OverlayLegendRenderer implements AWTRenderer2d {
  protected List<Legend> info;
  protected LegendLayout layout = new LegendLayout();

  Coord2d scale = new Coord2d(1,1);

  public OverlayLegendRenderer(List<Legend> info) {
    super();
    this.info = info;
  }

  @Override
  public void paint(Graphics g, int canvasWidth, int canvasHeight) {
    Graphics2D g2d = (Graphics2D) g;
    
    
    //g2d.scale(2, 2);
    g2d.scale(scale.x, scale.y);
    
    AWTGraphicsUtils.configureRenderingHints(g2d);

    if (layout.font != null)
      g2d.setFont(layout.font);

    FontMetrics fm = g.getFontMetrics();
    int textHeight = fm.getHeight();
    int textWidthMax = maxStringWidth(fm);

    // Box dimensions
    int xBoxPos = layout.boxMarginX;
    int yBoxPos = layout.boxMarginY;
    int boxWidth = layout.txtMarginX + textWidthMax + layout.sampleLineMargin
        + layout.sampleLineLength + layout.txtMarginX;
    int boxHeight = layout.txtMarginY + (textHeight + layout.txtInterline) * (info.size() - 1)
        + textHeight + layout.txtMarginY;

    if (Corner.TOP_RIGHT.equals(layout.corner) || Corner.BOTTOM_RIGHT.equals(layout.corner)) {
      xBoxPos = canvasWidth - layout.boxMarginX - boxWidth;
    }
    if (Corner.BOTTOM_LEFT.equals(layout.corner) || Corner.BOTTOM_RIGHT.equals(layout.corner)) {
      yBoxPos = canvasHeight - layout.boxMarginY - boxHeight;
    }

    // Background
    if (layout.backgroundColor != null) {
      g2d.setColor(AWTColor.toAWT(layout.backgroundColor));
      g2d.fillRect(xBoxPos, yBoxPos, boxWidth, boxHeight);
    }


    // Text position
    int xTextPos = xBoxPos + layout.txtMarginX;
    int yTextPos = yBoxPos + layout.txtMarginY + textHeight;

    for (Legend line : info) {
      paintLegend(g2d, textHeight, textWidthMax, xTextPos, yTextPos, line);

      // Shift
      yTextPos += (layout.txtInterline + textHeight);
    }


    // Border
    g2d.setColor(AWTColor.toAWT(layout.borderColor));
    g2d.drawRect(xBoxPos, yBoxPos, boxWidth, boxHeight);
    
    
    // Reset scale for other renderers
    //g2d.scale(1/scale.x, 1/scale.y);

  }



  public void paintLegend(Graphics2D g2d, int textHeight, int textWidthMax, int xTextPos,
      int yTextPos, Legend line) {
    // Text
    g2d.setColor(AWTColor.toAWT(layout.fontColor));
    g2d.drawString(line.label, xTextPos, yTextPos);

    // Line sample
    int xLineStart = xTextPos + textWidthMax + layout.sampleLineMargin;
    g2d.setColor(AWTColor.toAWT(line.color));
    g2d.drawLine(xLineStart, yTextPos - textHeight / 2, xLineStart + layout.sampleLineLength,
        yTextPos - textHeight / 2);

    // Symbol
    if (line.shape != null) {
      AffineTransform at = makeShapeTransform(line, textHeight, yTextPos, xLineStart);

      java.awt.Shape sh = at.createTransformedShape(line.shape);
      g2d.fill(sh);
      // g2d.fill(line.shape);
    }
  }

  protected AffineTransform makeShapeTransform(Legend line, int textHeight, int yTextPos,
      int xLineStart) {
    AffineTransform at = new AffineTransform();
    at.translate(xLineStart, yTextPos - textHeight + 1);
    at.scale(0.15, 0.15);
    // at.translate(25, 0);
    return at;
  }

  protected int maxStringWidth(FontMetrics fm) {
    int maxTxtWidth = 0;
    for (Legend line : info) {
      int lineWidth = fm.stringWidth(line.label);
      if (lineWidth > maxTxtWidth) {
        maxTxtWidth = lineWidth;
      }
    }
    return maxTxtWidth;
  }

  public List<Legend> getInfo() {
    return info;
  }

  public void setInfo(List<Legend> info) {
    this.info = info;
  }

  public LegendLayout getLayout() {
    return layout;
  }

  public void setLayout(LegendLayout layout) {
    this.layout = layout;
  }

  public Coord2d getScale() {
    return scale;
  }

  public void setScale(Coord2d scale) {
    this.scale = scale;
  }
}
