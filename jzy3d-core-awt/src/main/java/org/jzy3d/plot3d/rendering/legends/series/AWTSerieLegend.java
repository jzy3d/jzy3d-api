package org.jzy3d.plot3d.rendering.legends.series;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import org.jzy3d.colors.AWTColor;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ISingleColorable;
import org.jzy3d.maths.Dimension;
import org.jzy3d.painters.Font;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot2d.primitive.AWTAbstractImageGenerator;
import org.jzy3d.plot2d.primitive.AWTColorbarImageGenerator;
import org.jzy3d.plot2d.primitives.Serie2d;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.rendering.legends.AWTLegend;
import org.jzy3d.plot3d.rendering.view.IImageViewport;

public class AWTSerieLegend extends AWTLegend implements IImageViewport {
  private static final int LINE_HEIGHT = 15;
  private static final int INTERLINE_HEIGHT = 5;
  private static final int FONT_HEIGHT = 12;

  private static final int LEGEND_BORDER_HEIGHT = 30;

  protected int margin = 25;

  protected Serie2d serie;
  
  // remember asked width to be able to reset image
  // without processing multiple time the margin
  protected int askedWidth;
  protected int askedHeight;




  public AWTSerieLegend(Serie2d serie) {
    this(serie.getDrawable());
    this.serie = serie;
  }

  public AWTSerieLegend(Drawable drawable) {
    this(drawable, Color.BLACK, Color.WHITE);
  }

  public AWTSerieLegend(Drawable drawable, Color foreground, Color background) {
    super(drawable, foreground, background);
    this.minimumDimension = new Dimension(AWTColorbarImageGenerator.MIN_BAR_WIDTH,
        AWTColorbarImageGenerator.MIN_BAR_HEIGHT);

    drawable.setLegend(this);
    initImageGenerator();
    imageGenerator.setHasBackground(true);
    imageGenerator.setAWTFont(new java.awt.Font("Helvetica", 0, 12));
    setGeneratorColors();

  }

  public void initImageGenerator() {
    imageGenerator = new AWTAbstractImageGenerator() {
      @Override
      public BufferedImage toImage(int width, int height) {
        askedWidth = width;
        askedHeight = height;

        Color color = getSerieColor();
        String text = getSerieText();

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphic = image.createGraphics();
        configureText(graphic);

        int y = LINE_HEIGHT;

        drawBackground(width, LEGEND_BORDER_HEIGHT, graphic);
        drawSerieLineAndNameAtY(color, text, graphic, y);
        drawLegendBorder(graphic, width, LEGEND_BORDER_HEIGHT);
        return image;
      }

      public void drawSerieLineAndNameAtY(Color color, String text, Graphics2D graphic, int y) {
        if (color != null) {
          graphic.setColor(AWTColor.toAWT(color));
          graphic.drawLine(0, y, LINE_HEIGHT, y);
        }
        graphic.drawString(text, LINE_HEIGHT + INTERLINE_HEIGHT, y + INTERLINE_HEIGHT);
      }

      private String getSerieText() {
        if (serie != null) {
          return serie.getName();
        }
        return "unknown";
      }

      public Color getSerieColor() {
        if (serie != null) {
          return serie.getColor();
        } else if (drawable instanceof ISingleColorable) {
          return ((ISingleColorable) drawable).getColor();
        }
        return null;
      }

    };
  }

  @Override
  public void setFont(Font font) {
    imageGenerator.setFont(font);
  }

  @Override
  public Font getFont() {
    return imageGenerator.getFont();
  }


  @Override
  public void render(IPainter painter) {
    painter.glEnable_Blend();
    super.render(painter);
  }

  @Override
  public BufferedImage toImage(int width, int height) {
    if (imageGenerator != null) {
      setGeneratorColors();

      int iWidth = Math.max(width - margin, 1);
      int iHeight = LEGEND_BORDER_HEIGHT;// (LINE_HEIGHT + INTERLINE_HEIGHT);
      return imageGenerator.toImage(iWidth, iHeight);
    }
    return null;
  }
  
  @Override
  public int getWidth() {
    return askedWidth;
  }

  @Override
  public int getHeight() {
    return askedHeight;
  }


}
