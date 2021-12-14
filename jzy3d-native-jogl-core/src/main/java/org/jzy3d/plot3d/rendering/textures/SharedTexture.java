package org.jzy3d.plot3d.rendering.textures;

import java.io.File;
import java.io.IOException;
import org.jzy3d.painters.IPainter;
import org.jzy3d.painters.NativeDesktopPainter;
import org.jzy3d.plot3d.primitives.IGLBindedResource;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;
import com.jogamp.opengl.util.texture.TextureIO;

public class SharedTexture implements IGLBindedResource {
  protected Texture texture;
  protected String file;
  protected TextureCoords coords;
  protected float halfWidth;
  protected float halfHeight;
  protected boolean useMipMap = false;
  protected int textureMagnificationFilter = GL.GL_NEAREST;
  protected int textureMinificationFilter = GL.GL_NEAREST;

  protected SharedTexture() {
    this.texture = null;
  }

  public SharedTexture(String file) {
    this.texture = null;
    this.file = file;
  }

  public Texture getTexture(IPainter painter) {
    if (texture == null)
      mount(painter);
    return texture;
  }

  /** A GL2 context MUST be current. */
  @Override
  public void mount(IPainter painter) {
    GL gl = ((NativeDesktopPainter) painter).getGL();
    try {
      load(gl, file);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    coords = texture.getImageTexCoords();
    halfWidth = texture.getWidth() / 2;
    halfHeight = texture.getHeight() / 2;
  }

  @Override
  public boolean hasMountedOnce() {
    return texture != null;
  }

  protected void load(GL gl, String fileName) throws GLException, IOException {
    texture = TextureIO.newTexture(new File(fileName), useMipMap);

    if (textureMagnificationFilter != -1)
      texture.setTexParameteri(gl, GL.GL_TEXTURE_MAG_FILTER, textureMagnificationFilter);
    if (textureMinificationFilter != -1)
      texture.setTexParameteri(gl, GL.GL_TEXTURE_MIN_FILTER, textureMinificationFilter);
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
