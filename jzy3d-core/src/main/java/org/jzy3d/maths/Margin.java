package org.jzy3d.maths;

import java.util.Objects;

public class Margin {
  protected int left = 0;
  protected int right = 0;
  protected int top = 0;
  protected int bottom = 0;
  
  public Margin() {
  }

  public Margin(int width, int height) {
    set(width, height);
  }

  public Margin(int left, int right, int top, int bottom) {
    set(left, right, top, bottom);
  }

  public Margin(float left, float right, float top, float bottom) {
    set(Math.round(left), Math.round(right), Math.round(top), Math.round(bottom));
  }

  /**
   * Set left and right margin to width/2, top and bottom margin to height/2.
   */
  public void set(int width, int height) {
    setWidth(width);
    setHeight(height);
  }
  
  public void set(int left, int right, int top, int bottom) {
    this.left = left;
    this.right = right;
    this.top = top;
    this.bottom = bottom;
  }

  /**
   * Set top and bottom margin to height/2.
   */

  public void setHeight(int height) {
    setTop(height/2);
    setBottom(height/2);
  }

  /**
   * Set left and right margin to width/2.
   */
  public void setWidth(int width) {
    setLeft(width/2);
    setRight(width/2);
  }
  
  /**
   * Return the sum of left and right margin
   */
  public int getWidth() {
    return left+right;
  }
  
  /**
   * Return the sum of left and right margin
   */
  public int getHeight() {
    return top+bottom;
  }
  
  public int getLeft() {
    return left;
  }

  public void setLeft(int left) {
    this.left = left;
  }

  public int getRight() {
    return right;
  }

  public void setRight(int right) {
    this.right = right;
  }

  public int getTop() {
    return top;
  }

  public void setTop(int top) {
    this.top = top;
  }

  public int getBottom() {
    return bottom;
  }

  public void setBottom(int bottom) {
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
    return bottom == other.bottom && left == other.left && right == other.right && top == other.top;
  }
  
  
}
