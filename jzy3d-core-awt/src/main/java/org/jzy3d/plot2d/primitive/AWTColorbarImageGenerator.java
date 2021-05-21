package org.jzy3d.plot2d.primitive;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import org.jzy3d.colors.AWTColor;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.IColorMap;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot2d.rendering.AWTGraphicsUtils;
import org.jzy3d.plot3d.primitives.axis.layout.IAxisLayout;
import org.jzy3d.plot3d.primitives.axis.layout.providers.ITickProvider;
import org.jzy3d.plot3d.primitives.axis.layout.renderers.ITickRenderer;


/**
 * @author Martin Pernollet
 */
public class AWTColorbarImageGenerator extends AWTAbstractImageGenerator
    implements AWTImageGenerator {

  public static final int MIN_BAR_WIDTH = 110;
  public static final int MIN_BAR_HEIGHT = 100;

  protected ColorMapper mapper;
  protected ITickProvider provider;
  protected ITickRenderer renderer;
  protected double min;
  protected double max;

  public static int BAR_WIDTH_DEFAULT = 20;
  protected int barWidth;
  protected int textToBarHorizontalMargin = 2;

  protected Coord2d pixelScale = new Coord2d(1, 1);

  public AWTColorbarImageGenerator(IColorMap map, float min, float max, ITickProvider provider,
      ITickRenderer renderer) {
    this(new ColorMapper(map, min, max), provider, renderer);
  }

  public AWTColorbarImageGenerator(ColorMapper mapper, ITickProvider provider,
      ITickRenderer renderer) {
    this.mapper = mapper;
    this.provider = provider;
    this.renderer = renderer;
    this.min = mapper.getMin();
    this.max = mapper.getMax();
    
    setFont(IAxisLayout.FONT_DEFAULT);
  }


  // public

  @Override
  public BufferedImage toImage(int width, int height) {
    return toImage(width, height, BAR_WIDTH_DEFAULT);
  }

  /** Renders the {@link AWTColorbarImageGenerator} to an image. */
  public BufferedImage toImage(int width, int height, int barWidth) {
    if (barWidth > width)
      return null;

    this.barWidth = width;


    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D graphic = image.createGraphics();

    // graphic.scale(pixelScale.x, pixelScale.y);

    AWTGraphicsUtils.configureRenderingHints(graphic);

    graphic.setColor(AWTColor.toAWT(Color.WHITE));
    graphic.fillRect(0, 0, width, height);

    configureText(graphic);
    drawBackground(width, height, graphic);
    drawBarColors(height, barWidth, graphic);
    drawBarContour(height, barWidth, graphic);
    drawTextAnnotations(height, barWidth, graphic);
    return image;
  }

  /**
   * Draw a bar border. Keep a space of half text height between the top of image and top of
   * colorbar (same for bottom) to ensure a tick label at max or min position would not be cut at
   * image border.
   * 
   * The text size is given by {@link #setFont(org.jzy3d.painters.Font)}
   * 
   * @param height
   * @param barWidth
   * @param graphic
   */
  public void drawBarContour(int height, int barWidth, Graphics2D graphic) {
    graphic.setColor(AWTColor.toAWT(foregroundColor));
    graphic.drawRect(0, textSize / 2, barWidth, height - textSize);
  }

  /**
   * Draw a bar content using the provided color mapper.
   * 
   * @param height
   * @param barWidth
   * @param graphic
   */
  public void drawBarColors(int height, int barWidth, Graphics2D graphic) {
    for (int h = textSize / 2; h <= (height - textSize / 2); h++) {
      // Compute value & color
      double v = min + (max - min) * (h) / (height - textSize);
      Color c = mapper.getColor(v); // To allow the Color to be a variable independent of the
                                    // coordinates

      // Draw line
      graphic.setColor(AWTColor.toAWT(c));
      graphic.drawLine(0, height - h, barWidth, height - h);
    }
  }

  public void drawTextAnnotations(int height, int barWidth, Graphics2D graphic) {
    if (provider != null) {
      double[] ticks = provider.generateTicks(min, max);
      int ypos;
      String txt;
      for (int t = 0; t < ticks.length; t++) {
        ypos = (int) (textSize
            + (height - textSize - (height - textSize) * ((ticks[t] - min) / (max - min))));
        txt = renderer.format(ticks[t]);
        graphic.drawString(txt, barWidth + textToBarHorizontalMargin, ypos);
      }
    }
  }

  public Coord2d getPixelScale() {
    return pixelScale;
  }

  public void setPixelScale(Coord2d pixelScale) {
    this.pixelScale = pixelScale;
  }

  /* */

  /**
   * Compute the optimal image width to contain the text as defined by the tick provided and
   * renderer.
   */
  public int getPreferedWidth(IPainter painter) {
    int maxWidth = getMaxTickLabelWidth(painter);
    return getPreferedWidth(maxWidth);
  }

  public int getPreferedWidth(int maxTextWidth) {
    return maxTextWidth + textToBarHorizontalMargin + BAR_WIDTH_DEFAULT;
  }

  public int getTextToBarHorizontalMargin() {
    return textToBarHorizontalMargin;
  }

  public void setTextToBarHorizontalMargin(int textToBarHorizontalMargin) {
    this.textToBarHorizontalMargin = textToBarHorizontalMargin;
  }

  public int getMaxTickLabelWidth(IPainter painter) {
    int maxWidth = 0;
    if (provider != null) {
      double[] ticks = provider.generateTicks(min, max);
      String tickLabel;
      for (int t = 0; t < ticks.length; t++) {
        tickLabel = renderer.format(ticks[t]);

        int stringWidth = painter.getTextLengthInPixels(font, tickLabel);

        if (maxWidth < stringWidth) {
          maxWidth = stringWidth;
        }
      }
    }
    return maxWidth;
  }
}
