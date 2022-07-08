package org.jzy3d.plot3d.rendering.legends.overlay;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.List;
import org.jzy3d.colors.AWTColor;
import org.jzy3d.maths.Lists;
import org.jzy3d.plot2d.primitives.Serie2d;
import org.jzy3d.plot2d.rendering.AWTGraphicsUtils;
import org.jzy3d.plot3d.rendering.legends.overlay.LegendLayout.Corner;
import org.jzy3d.plot3d.rendering.view.AWTRenderer2d;
import org.jzy3d.plot3d.rendering.view.AbstractAWTRenderer2d;

/**
 * 
 * Params - margin - corner - constraints : byDimension (maxWidth) or byText - interligne - font
 * size / font style
 * 
 * @author Martin Pernollet
 *
 */
public class OverlayLegendRenderer extends AbstractAWTRenderer2d implements AWTRenderer2d {
  protected List<Legend> info;
  protected LineLegendLayout layout = new LineLegendLayout();

  public OverlayLegendRenderer(Legend info) {
    this.info = Lists.of(info);
  }

  public OverlayLegendRenderer(Legend... info) {
    this.info = Lists.of(info);
  }

  public OverlayLegendRenderer(List<Legend> info) {
    super();
    this.info = info;
  }

  @Override
  public void paint(Graphics g, int canvasWidth, int canvasHeight) {
    Graphics2D g2d = (Graphics2D) g;
    
    // Ensure native overlay will place image at the appropriate location
    // Since native and emulgl deal differently with overlay when hiDPI
    if(view!=null && view.getCanvas().isNative()) {
      canvasHeight /= view.getPixelScale().y;
      canvasWidth /= view.getPixelScale().x;
    }


    AWTGraphicsUtils.configureRenderingHints(g2d);

    if (layout.font != null) {
      g2d.setFont(layout.font);
    }

    FontMetrics fm = g.getFontMetrics();
    int textHeight = fm.getHeight();
    int textWidthMax = maxStringWidth(fm);

    // Box dimensions
    
    int boxWidth = layout.txtMarginX + textWidthMax + layout.sampleLineMargin
        + layout.sampleLineLength + layout.txtMarginX;
    int boxHeight = layout.txtMarginY + (textHeight + layout.txtInterline) * (info.size() - 1)
        + textHeight + layout.txtMarginY;

    
    // Box offset to apply margins
    
    // horizontal left
    int xBoxPos = Math.round(layout.getMargin().getLeft());
    
    // horizontal right
    if (Corner.TOP_RIGHT.equals(layout.corner) || Corner.BOTTOM_RIGHT.equals(layout.corner)) {
      xBoxPos = Math.round(canvasWidth - layout.getMargin().getRight() - boxWidth);
    }
    
    // vertical top
    int yBoxPos = Math.round(layout.getMargin().getTop());

    // vertical bottom
    if (Corner.BOTTOM_LEFT.equals(layout.corner) || Corner.BOTTOM_RIGHT.equals(layout.corner)) {
      yBoxPos = Math.round(canvasHeight - layout.getMargin().getBottom() - boxHeight);
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
    if(layout.borderColor!=null) {
      g2d.setColor(AWTColor.toAWT(layout.borderColor));
      g2d.drawRect(xBoxPos, yBoxPos, boxWidth, boxHeight);
    }

  }


  public void paintLegend(Graphics2D g2d, int textHeight, int textWidthMax, int xTextPos,
      int yTextPos, Legend line) {
    // Text
    g2d.setColor(AWTColor.toAWT(layout.fontColor));
    g2d.drawString(line.label, xTextPos, yTextPos);

    // Line sample

    int xLineStart = xTextPos + textWidthMax + layout.sampleLineMargin;

    if(line.color!=null) {
      g2d.setColor(AWTColor.toAWT(line.color));
      g2d.drawLine(xLineStart, yTextPos - textHeight / 2, xLineStart + layout.sampleLineLength,
          yTextPos - textHeight / 2);
    }

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

  public LineLegendLayout getLayout() {
    return layout;
  }

  public void setLayout(LineLegendLayout layout) {
    this.layout = layout;
  }
}
