package org.jzy3d.plot3d.rendering.textures;

import java.awt.image.BufferedImage;
import org.jzy3d.painters.GLConstants;

/** A {@link SharedTexture} holding an image in memory. */
public class BufferedImageTexture extends SharedTexture {
  protected BufferedImage image;

  public BufferedImageTexture(BufferedImage image) {
    super();
    this.image = image;
    this.textureMagnificationFilter = GLConstants.GL_LINEAR;
    this.textureMinificationFilter = GLConstants.GL_LINEAR;
  }

  @Override
  protected BufferedImage loadImage() {
    return image;
  }

  public BufferedImage getImage() {
    return image;
  }
}
