package org.jzy3d.plot3d.rendering.legends.overlay;

import org.jzy3d.maths.Margin;

public class LegendLayout {

  protected Margin margin = new Margin(10,10);
  
  /** Legend position. */
  protected Corner corner = Corner.TOP_LEFT;

  public enum Corner {
    TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
  }

  public Margin getMargin() {
    return margin;
  }

  public void setMargin(Margin margin) {
    this.margin = margin;
  }

  public Corner getCorner() {
    return corner;
  }

  public void setCorner(Corner corner) {
    this.corner = corner;
  }
  
  
}
