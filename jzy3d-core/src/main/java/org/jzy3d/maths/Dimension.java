package org.jzy3d.maths;

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
}
