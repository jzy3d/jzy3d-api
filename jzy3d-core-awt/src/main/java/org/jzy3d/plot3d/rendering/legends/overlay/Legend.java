package org.jzy3d.plot3d.rendering.legends.overlay;

import java.awt.Shape;
import org.jzy3d.colors.Color;

public class Legend {
  public String label;
  public Color color;
  public Shape shape;

  public Legend(String label, Color color, Shape shape) {
    super();
    this.label = label;
    this.color = color;
    this.shape = shape;
  }

  public Legend(String label, Color color) {
    super();
    this.label = label;
    this.color = color;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public Color getColor() {
    return color;
  }

  public void setColor(Color color) {
    this.color = color;
  }

  public Shape getShape() {
    return shape;
  }

  public void setShape(Shape shape) {
    this.shape = shape;
  }
}
