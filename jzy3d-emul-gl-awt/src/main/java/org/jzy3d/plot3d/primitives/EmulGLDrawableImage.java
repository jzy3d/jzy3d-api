package org.jzy3d.plot3d.primitives;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import org.apache.logging.log4j.LogManager;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.EmulGLPainter;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.transform.Transform;

/**
 * Renders an image at the specified 3d position.
 * 
 * The equivalent of {@link DrawableTexture}.
 */
public class EmulGLDrawableImage extends DrawableImage {
  protected BufferedImage image;
  protected Coord3d position;

  public EmulGLDrawableImage(BufferedImage image) {
    this.image = image;
    this.position = Coord3d.ORIGIN.clone();
  }

  public EmulGLDrawableImage(BufferedImage image, Coord3d position) {
    this.image = image;
    this.position = position;
  }

  @Override
  public void draw(IPainter painter) {
    if (painter instanceof EmulGLPainter) {
      EmulGLPainter emulgl = (EmulGLPainter) painter;

      Coord3d screenPosition = painter.getCamera().modelToScreen(painter, position);

      emulgl.getGL().appendImageToDraw(image, (int) screenPosition.x - image.getWidth() / 2,
          (int) screenPosition.y - image.getHeight() / 2);
    }
  }

  @Override
  public void applyGeometryTransform(Transform transform) {
    LogManager.getLogger(EmulGLDrawableImage.class).warn("not implemented");
  }

  @Override
  public void updateBounds() {
    LogManager.getLogger(EmulGLDrawableImage.class).warn("not implemented");
  }

  public Coord3d getPosition() {
    return position;
  }

  public void setPosition(Coord3d position) {
    this.position = position;
  }

  /* ****************************************** */

  public static int DEFAULT_IMG_WIDTH = 50;
  public static int DEFAULT_IMG_HEIGHT = DEFAULT_IMG_WIDTH;

  public static BufferedImage getImage(Shape shape) {
    return getImage(shape, DEFAULT_IMG_WIDTH, DEFAULT_IMG_HEIGHT, null);
  }

  public static BufferedImage getImage(Shape shape, int width, int height) {
    return getImage(shape, width, height, null);
  }

  public static BufferedImage getImage(Shape shape, int width, int height, java.awt.Color color) {
    return getImage(shape, width, height, BufferedImage.TYPE_4BYTE_ABGR, color);
  }

  public static BufferedImage getImage(Shape shape, int width, int height, int imageType,
      java.awt.Color color) {
    BufferedImage bimage = new BufferedImage(width, height, imageType);
    Graphics2D g2d = bimage.createGraphics();

    if (color != null) {
      g2d.setColor(color);
    }

    g2d.fill(shape);
    g2d.dispose();
    return bimage;
  }

}

