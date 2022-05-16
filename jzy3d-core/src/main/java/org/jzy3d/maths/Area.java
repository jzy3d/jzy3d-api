package org.jzy3d.maths;

public class Area {
  public float width, height;

  public Area(float width, float height) {
    super();
    this.width = width;
    this.height = height;
  }

  public Area(Area area) {
    this(area.width, area.height);
  }

  public String toString() {
    return "Area width=" + width + " height=" + height;
  }
}
