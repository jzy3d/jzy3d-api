package org.jzy3d.plot3d.builder.concrete;

import java.awt.image.BufferedImage;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.plot3d.builder.Mapper;

/**
 * Mapper which reads height information from the grayscale values of a BufferedImage, normalized to
 * range [0..1].
 * 
 * @author Nils Hoffmann
 */
public class BufferedImageMapper extends Mapper {

  private final BufferedImage image;
  private final int maxRow;
  private final Rectangle maxViewPort;

  public BufferedImageMapper(BufferedImage bi) {
    this.image = bi;
    this.maxRow = this.image.getHeight() - 1;
    this.maxViewPort = new Rectangle(0, 0, bi.getWidth(), bi.getHeight());
  }

  /**
   * Returns the intersection of this BufferedImage's dimensions with those passed in in Rectangle
   * roi, if there is one. Otherwise, the returned rectangle may be empty.
   * 
   * @param roi
   * @return
   */
  public Rectangle getClippedViewport(Rectangle roi) {
    return this.maxViewPort.intersection(roi);
  }

  @Override
  public double f(double x, double y) {
    if (x == Double.NaN || y == Double.NaN) {
      return Double.NaN;
    }
    int rbg = image.getRGB((int) x, (maxRow) - ((int) y));
    float red = ((rbg >> 16) & 0xFF) / 255.0f;
    float green = ((rbg >> 8) & 0xFF) / 255.0f;
    float blue = ((rbg) & 0xFF) / 255.0f;
    return ((red * 0.3f) + (green * 0.59f) + (blue * 0.11f));
  }
}
