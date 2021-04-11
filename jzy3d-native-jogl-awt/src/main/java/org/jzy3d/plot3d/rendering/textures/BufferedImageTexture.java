package org.jzy3d.plot3d.rendering.textures;

import java.awt.image.BufferedImage;
import org.jzy3d.painters.IPainter;
import org.jzy3d.painters.NativeDesktopPainter;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

public class BufferedImageTexture extends SharedTexture {
  public BufferedImageTexture(BufferedImage image) {
    super();
    this.image = image;
    this.textureMagnificationFilter = GL.GL_LINEAR;
    this.textureMinificationFilter = GL.GL_LINEAR;
  }

  @Override
  public Texture getTexture(IPainter painter) {

    if (texture == null)
      mount(painter);
    else { // execute onmount even if we did not mount
           // if( action != null )
           // action.execute();
    }
    return texture;
  }

  /** A GL2 context MUST be current. */
  @Override
  public void mount(IPainter painter) {
    GL gl = ((NativeDesktopPainter) painter).getGL();

    try {
      load(gl, image);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    coords = texture.getImageTexCoords();
    halfWidth = texture.getWidth() / 2;
    halfHeight = texture.getHeight() / 2;
  }

  protected void load(GL gl, BufferedImage image) {
    texture = AWTTextureIO.newTexture(gl.getGLProfile(), image, useMipMap); // GLProfile.getDefault()

    if (textureMagnificationFilter != -1)
      texture.setTexParameteri(gl, GL.GL_TEXTURE_MAG_FILTER, textureMagnificationFilter);
    if (textureMinificationFilter != -1)
      texture.setTexParameteri(gl, GL.GL_TEXTURE_MIN_FILTER, textureMinificationFilter);
  }

  protected BufferedImage image;
}
