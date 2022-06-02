package org.jzy3d.maths;

import java.util.Objects;

public class Margin {
  protected float left = 0;
  protected float right = 0;
  protected float top = 0;
  protected float bottom = 0;
  
  public Margin() {
  }

  public Margin(float width, float height) {
    set(width, height);
  }

  public Margin(float left, float right, float top, float bottom) {
    set(left, right, top, bottom);
  }
  
  /**
   * Set left and right margin to width/2, top and bottom margin to height/2.
   */
  public void set(float width, float height) {
    setWidth(width);
    setHeight(height);
  }
  
  public void set(float left, float right, float top, float bottom) {
    this.left = left;
    this.right = right;
    this.top = top;
    this.bottom = bottom;
  }

  /**
   * Set top and bottom margin to height/2.
   */

  public void setHeight(float height) {
    setTop(height/2);
    setBottom(height/2);
  }

  /**
   * Set left and right margin to width/2.
   */
  public void setWidth(float width) {
    setLeft(width/2);
    setRight(width/2);
  }
  
  /**
   * Return the sum of left and right margin
   */
  public float getWidth() {
    return left+right;
  }
  
  /**
   * Return the sum of left and right margin
   */
  public float getHeight() {
    return top+bottom;
  }
  
  public float getLeft() {
    return left;
  }

  public void setLeft(float left) {
    this.left = left;
  }

  public float getRight() {
    return right;
  }

  public void setRight(float right) {
    this.right = right;
  }

  public float getTop() {
    return top;
  }

  public void setTop(float top) {
    this.top = top;
  }

  public float getBottom() {
    return bottom;
  }

  public void setBottom(float bottom) {
    this.bottom = bottom;
  }

  public String toString() {
    return "left:" + left + " right:" + right + " top:" + top + " bottom:" + bottom;
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(bottom, left, right, top);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Margin other = (Margin) obj;
    return Float.floatToIntBits(bottom) == Float.floatToIntBits(other.bottom)
        && Float.floatToIntBits(left) == Float.floatToIntBits(other.left)
        && Float.floatToIntBits(right) == Float.floatToIntBits(other.right)
        && Float.floatToIntBits(top) == Float.floatToIntBits(other.top);
  }
  
  
}
