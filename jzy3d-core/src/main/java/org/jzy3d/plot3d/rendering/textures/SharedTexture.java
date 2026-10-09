package org.jzy3d.plot3d.rendering.textures;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.WeakHashMap;
import javax.imageio.ImageIO;
import org.jzy3d.io.BufferUtil;
import org.jzy3d.painters.GLConstants;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.primitives.IGLBindedResource;

/**
 * An image stored in a 2D texture, that can be shared by several drawables.
 * 
 * The texture is loaded in GPU through {@link IPainter} at mount, hence works with any native
 * painter. A texture may be shared by drawables rendered by several charts, hence in several GL
 * contexts : the texture is loaded once per painter.
 * 
 * Image rows are stored from top to bottom, hence {@link #getCoords()} gives a top coordinate of 0
 * and a bottom coordinate of 1.
 */
public class SharedTexture implements IGLBindedResource {
  protected String file;
  protected TextureCoords coords;
  protected float halfWidth;
  protected float halfHeight;
  protected int width;
  protected int height;

  protected boolean useMipMap = false;
  protected int textureMagnificationFilter = GLConstants.GL_NEAREST;
  protected int textureMinificationFilter = GLConstants.GL_NEAREST;

  /** Texture name of each painter in which this texture was loaded. */
  protected Map<IPainter, Integer> textureIds = new WeakHashMap<>();

  protected SharedTexture() {}

  public SharedTexture(String file) {
    this.file = file;
  }

  /** Load the texture in the GL context of this painter. A GL context MUST be current. */
  @Override
  public void mount(IPainter painter) {
    BufferedImage image;
    try {
      image = loadImage();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    width = image.getWidth();
    height = image.getHeight();
    halfWidth = width / 2;
    halfHeight = height / 2;
    coords = new TextureCoords(0, 1, 1, 0);

    int[] id = new int[1];
    painter.glGenTextures(1, id, 0);
    painter.glBindTexture(GLConstants.GL_TEXTURE_2D, id[0]);

    // do not let filtering blend opposite edges of the image
    painter.glTexParameteri(GLConstants.GL_TEXTURE_2D, GLConstants.GL_TEXTURE_WRAP_S,
        GLConstants.GL_CLAMP_TO_EDGE);
    painter.glTexParameteri(GLConstants.GL_TEXTURE_2D, GLConstants.GL_TEXTURE_WRAP_T,
        GLConstants.GL_CLAMP_TO_EDGE);

    if (textureMagnificationFilter != -1)
      painter.glTexParameteri(GLConstants.GL_TEXTURE_2D, GLConstants.GL_TEXTURE_MAG_FILTER,
          textureMagnificationFilter);
    if (textureMinificationFilter != -1)
      painter.glTexParameteri(GLConstants.GL_TEXTURE_2D, GLConstants.GL_TEXTURE_MIN_FILTER,
          useMipMap && textureMinificationFilter == GLConstants.GL_LINEAR
              ? GLConstants.GL_LINEAR_MIPMAP_LINEAR
              : textureMinificationFilter);
    if (useMipMap)
      painter.glTexParameteri(GLConstants.GL_TEXTURE_2D, GLConstants.GL_GENERATE_MIPMAP,
          GLConstants.GL_TRUE);

    painter.glPixelStorei(GLConstants.GL_UNPACK_ALIGNMENT, 1);
    painter.glTexImage2D(GLConstants.GL_TEXTURE_2D, 0, GLConstants.GL_RGBA, width, height, 0,
        GLConstants.GL_RGBA, GLConstants.GL_UNSIGNED_BYTE, rgba(image));

    synchronized (this) {
      textureIds.put(painter, id[0]);
    }
  }

  /** Bind this texture to GL_TEXTURE_2D, after loading it if not done yet for this painter. */
  public void bind(IPainter painter) {
    painter.glBindTexture(GLConstants.GL_TEXTURE_2D, getTextureId(painter));
  }

  /** The texture name in the GL context of this painter, after loading it if not done yet. */
  public int getTextureId(IPainter painter) {
    Integer id;
    synchronized (this) {
      id = textureIds.get(painter);
    }
    if (id == null) {
      mount(painter);
      synchronized (this) {
        id = textureIds.get(painter);
      }
    }
    return id;
  }

  @Override
  public synchronized boolean hasMountedOnce() {
    return !textureIds.isEmpty();
  }

  /** The image to store in the texture. */
  protected BufferedImage loadImage() throws IOException {
    BufferedImage image = ImageIO.read(new File(file));
    if (image == null) {
      throw new IOException("Unsupported image format : " + file);
    }
    return image;
  }

  /** RGBA bytes of an image, rows ordered from top to bottom. */
  protected static ByteBuffer rgba(BufferedImage image) {
    int w = image.getWidth();
    int h = image.getHeight();
    int[] argb = image.getRGB(0, 0, w, h, null, 0, w);

    ByteBuffer buffer = BufferUtil.newDirectByteBuffer(w * h * 4);
    for (int p : argb) {
      buffer.put((byte) ((p >> 16) & 0xFF));
      buffer.put((byte) ((p >> 8) & 0xFF));
      buffer.put((byte) (p & 0xFF));
      buffer.put((byte) ((p >> 24) & 0xFF));
    }
    BufferUtil.rewind(buffer);
    return buffer;
  }

  public String getFile() {
    return file;
  }

  public TextureCoords getCoords() {
    return coords;
  }

  public float getHalfWidth() {
    return halfWidth;
  }

  public float getHalfHeight() {
    return halfHeight;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public boolean isUseMipMap() {
    return useMipMap;
  }

  /**
   * Will apply if set before actually loading the texture.
   * 
   * @param useMipMap
   */
  public void setUseMipMap(boolean useMipMap) {
    this.useMipMap = useMipMap;
  }

  public int getTextureMagnificationFilter() {
    return textureMagnificationFilter;
  }

  /**
   * Will apply if set before actually loading the texture.
   * 
   * Possible values documented in
   * https://www.khronos.org/registry/OpenGL-Refpages/gl4/html/glTexParameter.xhtml (see parameter
   * GL_TEXTURE_MAG_FILTER)
   * 
   * Use -1 to avoid magnification
   * 
   * @param textureMagnificationFilter
   */
  public void setTextureMagnificationFilter(int textureMagnificationFilter) {
    this.textureMagnificationFilter = textureMagnificationFilter;
  }

  public int getTextureMinificationFilter() {
    return textureMinificationFilter;
  }

  /**
   * Will apply if set before actually loading the texture.
   * 
   * Possible values documented in
   * https://www.khronos.org/registry/OpenGL-Refpages/gl4/html/glTexParameter.xhtml (see parameter
   * GL_TEXTURE_MIN_FILTER)
   * 
   * Use -1 to avoid minification
   * 
   * @param textureMinificationFilter
   */
  public void setTextureMinificationFilter(int textureMinificationFilter) {
    this.textureMinificationFilter = textureMinificationFilter;
  }
}
