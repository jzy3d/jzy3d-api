package org.jzy3d.plot3d.rendering.legends.overlay;

public class LegendLayout {

  /** External margin : distance between legend border canvas border. */
  protected int boxMarginX = 5;
  /** External margin : distance between legend border canvas border. */
  protected int boxMarginY = 5;
  /** Legend position. */
  protected Corner corner = Corner.TOP_LEFT;

  public enum Corner {
    TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
  }

  public int getBoxMarginX() {
    return boxMarginX;
  }

  public void setBoxMarginX(int boxMarginX) {
    this.boxMarginX = boxMarginX;
  }

  public int getBoxMarginY() {
    return boxMarginY;
  }

  public void setBoxMarginY(int boxMarginY) {
    this.boxMarginY = boxMarginY;
  }

  public Corner getCorner() {
    return corner;
  }

  public void setCorner(Corner corner) {
    this.corner = corner;
  }
  
  
}
