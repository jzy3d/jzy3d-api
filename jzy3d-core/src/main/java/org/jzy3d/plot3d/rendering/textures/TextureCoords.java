package org.jzy3d.plot3d.rendering.textures;

/**
 * Texture coordinates of the corners of an image stored in a texture.
 * 
 * @author Martin Pernollet
 */
public class TextureCoords {
  protected float left;
  protected float bottom;
  protected float right;
  protected float top;

  public TextureCoords(float left, float bottom, float right, float top) {
    this.left = left;
    this.bottom = bottom;
    this.right = right;
    this.top = top;
  }

  public float left() {
    return left;
  }

  public float right() {
    return right;
  }

  public float bottom() {
    return bottom;
  }

  public float top() {
    return top;
  }

  @Override
  public String toString() {
    return "TextureCoords[left=" + left + ", bottom=" + bottom + ", right=" + right + ", top="
        + top + "]";
  }
}
