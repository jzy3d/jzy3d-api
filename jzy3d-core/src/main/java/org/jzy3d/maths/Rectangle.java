package org.jzy3d.maths;

/**
 * Replacement for java.awt.Rectangle
 * 
 * @author cmh
 * 
 */
public class Rectangle {

  public int x, y, width, height;

  public Rectangle(int x, int y, int width, int height) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  public Rectangle(int width, int height) {
    this.width = width;
    this.height = height;
  }

  public Rectangle intersection(Rectangle r) {
    int tx1 = this.x;
    int ty1 = this.y;
    int rx1 = r.x;
    int ry1 = r.y;
    long tx2 = tx1;
    tx2 += this.width;
    long ty2 = ty1;
    ty2 += this.height;
    long rx2 = rx1;
    rx2 += r.width;
    long ry2 = ry1;
    ry2 += r.height;
    if (tx1 < rx1)
      tx1 = rx1;
    if (ty1 < ry1)
      ty1 = ry1;
    if (tx2 > rx2)
      tx2 = rx2;
    if (ty2 > ry2)
      ty2 = ry2;
    tx2 -= tx1;
    ty2 -= ty1;
    // tx2,ty2 will never overflow (they will never be
    // larger than the smallest of the two source w,h)
    // they might underflow, though...
    if (tx2 < Integer.MIN_VALUE)
      tx2 = Integer.MIN_VALUE;
    if (ty2 < Integer.MIN_VALUE)
      ty2 = Integer.MIN_VALUE;
    return new Rectangle(tx1, ty1, (int) tx2, (int) ty2);
  }
  
  public Rectangle clone() {
    return new Rectangle (x, y, width, height);
  }

}
