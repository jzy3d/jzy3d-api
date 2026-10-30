package org.jzy3d.plot3d.rendering.image;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.image.BufferedImage;

public class AWTImageWrapper implements IImageWrapper {
  protected BufferedImage image;

  public AWTImageWrapper(BufferedImage image) {
    super();
    this.image = image;
  }

  public BufferedImage getImage() {
    return image;
  }

  public void setImage(BufferedImage image) {
    this.image = image;
  }

  public static AWTImageWrapper from(Shape shape) {
    return from(getImage(shape));
  }

  public static AWTImageWrapper from(Shape shape, Color color) {
    return from(getImage(shape, color));
  }

  public static AWTImageWrapper from(BufferedImage image) {
    return new AWTImageWrapper(image);
  }

  public static AWTImageWrapper from(IImageWrapper image) {
    return from(((AWTImageWrapper) image).getImage());
  }

  /* */

  public static int SPRITE_WIDTH = 100;
  public static int SPRITE_HEIGHT = SPRITE_WIDTH;

  public static BufferedImage getImage(Shape shape) {
    return getImage(shape, SPRITE_WIDTH, SPRITE_HEIGHT, null);
  }

  public static BufferedImage getImage(Shape shape, java.awt.Color color) {
    return getImage(shape, SPRITE_WIDTH, SPRITE_HEIGHT, color);
  }

  public static BufferedImage getImage(Shape shape, int width, int height) {
    return getImage(shape, width, height, null);
  }

  public static BufferedImage getImage(Shape shape, int width, int height, java.awt.Color color) {
    return getImage(shape, width, height, BufferedImage.TYPE_INT_ARGB, color); // BufferedImage.TYPE_4BYTE_ABGR
  }

  public static BufferedImage getImage(Shape shape, int width, int height, int imageType,
      java.awt.Color color) {
    BufferedImage image = new BufferedImage(width, height, imageType);
    Graphics2D g2d = image.createGraphics();

    if (color != null) {
      g2d.setColor(color);
    } else {
      g2d.setColor(java.awt.Color.BLACK);
    }

    g2d.fill(shape);
    g2d.dispose();
    return image;
  }

}
