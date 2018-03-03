package org.jzy3d.plot3d.rendering.legends.overlay;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.List;

import org.jzy3d.colors.ColorAWT;
import org.jzy3d.plot3d.rendering.legends.overlay.LegendLayout.Corner;
import org.jzy3d.plot3d.rendering.view.Renderer2d;

/**
 * 
 * Params - margin - corner - constraints : byDimension (maxWidth) or byText - interligne - font size / font style
 * 
 * @author Martin Pernollet
 *
 */
public class OverlayLegendRenderer implements Renderer2d {
    protected List<Legend> info;
    protected LegendLayout layout = new LegendLayout();

    public OverlayLegendRenderer(List<Legend> info) {
        super();
        this.info = info;
    }

    @Override
    public void paint(Graphics g, int canvasWidth, int canvasHeight) {
        Graphics2D g2d = (Graphics2D) g;
        if (layout.font != null)
            g2d.setFont(layout.font);
        FontMetrics fm = g.getFontMetrics();

        int textHeight = fm.getHeight();
        int textWidthMax = maxStringWidth(fm);

        // Box dimensions
        int xBoxPos = layout.boxMarginX;
        int yBoxPos = layout.boxMarginY;
        int boxWidth = layout.txtMarginX + textWidthMax + layout.sampleLineMargin + layout.sampleLineLength + layout.txtMarginX;
        int boxHeight = layout.txtMarginY + (textHeight + layout.txtInterline) * (info.size() - 1) + textHeight + layout.txtMarginY;

        if (Corner.TOP_RIGHT.equals(layout.corner) || Corner.BOTTOM_RIGHT.equals(layout.corner)) {
            xBoxPos = canvasWidth - layout.boxMarginX - boxWidth;
        }
        if (Corner.BOTTOM_LEFT.equals(layout.corner) || Corner.BOTTOM_RIGHT.equals(layout.corner)) {
            yBoxPos = canvasHeight - layout.boxMarginY - boxHeight;
        }
        
        // Background
        if (layout.backgroundColor != null) {
            g2d.setColor(ColorAWT.toAWT(layout.backgroundColor));
            g2d.fillRect(xBoxPos, yBoxPos, boxWidth, boxHeight);
        }


        // Text position
        int xTextPos = xBoxPos + layout.txtMarginX;
        int yTextPos = yBoxPos + layout.txtMarginY + textHeight;

        for (Legend line : info) {
            // Text
            g2d.setColor(ColorAWT.toAWT(layout.fontColor));
            g2d.drawString(line.label, xTextPos, yTextPos);

            // Line sample
            int xLineStart = xTextPos + textWidthMax + layout.sampleLineMargin;
            g2d.setColor(ColorAWT.toAWT(line.color));
            g2d.drawLine(xLineStart, yTextPos - textHeight / 2, xLineStart + layout.sampleLineLength, yTextPos - textHeight / 2);

            // Symbol
            if (line.shape != null) {
                AffineTransform at = new AffineTransform();
                at.translate(xLineStart, yTextPos - textHeight + 1);
                at.scale(0.10, 0.10);
                at.translate(25, 0);

                java.awt.Shape sh = at.createTransformedShape(line.shape);
                g2d.fill(sh);
            }
            // Shift
            yTextPos += (layout.txtInterline + textHeight);
        }

        
        // Border
        g2d.setColor(ColorAWT.toAWT(layout.borderColor));
        g2d.drawRect(xBoxPos, yBoxPos, boxWidth, boxHeight);
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

}
