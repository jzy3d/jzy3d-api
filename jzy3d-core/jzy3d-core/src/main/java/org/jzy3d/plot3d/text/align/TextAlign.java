package org.jzy3d.plot3d.text.align;

public class TextAlign {
  protected Horizontal horizontal;
  protected Vertical vertical;

  public TextAlign() {
  }
  
  public TextAlign(Horizontal horizontal, Vertical vertical) {
    this.horizontal = horizontal;
    this.vertical = vertical;
  }

  public Horizontal horizontal() {
    return horizontal;
  }

  public void horizontal(Horizontal horizontal) {
    this.horizontal = horizontal;
  }

  public Vertical vertical() {
    return vertical;
  }

  public void vertical(Vertical vertical) {
    this.vertical = vertical;
  }
}
