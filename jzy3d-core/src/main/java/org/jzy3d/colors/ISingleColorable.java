package org.jzy3d.colors;

/** {@link ISingleColorable} objects have a single plain color and a must define a setter for it. */
public interface ISingleColorable {
  /**
   * Set the color.
   * 
   * @param color the color
   */
  public void setColor(Color color);

  /**
   * Get the color.
   * 
   * @return color the color.
   */
  public Color getColor();
}
