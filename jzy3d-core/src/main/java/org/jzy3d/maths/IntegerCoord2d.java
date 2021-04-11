package org.jzy3d.maths;

public class IntegerCoord2d {
  public int x, y;

  public IntegerCoord2d() {
    this.x = 0;
    this.y = 0;
  }

  public IntegerCoord2d(int x, int y) {
    this.x = x;
    this.y = y;
  }

  @Override
  public String toString() {
    return "(IntegerCoord2d) " + x + "," + y;
  }
  
  public IntegerCoord2d mul(Coord2d mul) {
    return new IntegerCoord2d((int)(x * mul.x), (int)(y * mul.y));
  }
  
  public IntegerCoord2d div(Coord2d div) {
    return new IntegerCoord2d((int)(x / div.x), (int)(y / div.y));
  }

}
