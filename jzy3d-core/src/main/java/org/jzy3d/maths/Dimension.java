package org.jzy3d.maths;

import java.util.Objects;

/**
 * Replacement for java.awt.Dimension
 * 
 * @author cmh
 * 
 */
public class Dimension {

  public int width, height;

  public Dimension(int width, int height) {
    this.width = width;
    this.height = height;
  }
  
  public String toString() {
    return "width:" + width + " height:" + height;
  }

  @Override
  public int hashCode() {
    return Objects.hash(height, width);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Dimension other = (Dimension) obj;
    return height == other.height && width == other.width;
  }
}
