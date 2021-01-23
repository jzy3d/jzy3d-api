package org.jzy3d.plot3d.rendering.view;

import org.jzy3d.plot3d.rendering.canvas.ICanvas;

/**
 * A {@link ViewportConfiguration} states how a particular GL rendering should occupy a canvas area.
 * 
 * It is define by a width and height, and support an X and Y offset
 * 
 * @see http://www.opengl.org/sdk/docs/man/xhtml/glViewport.xml
 * @author Martin Pernollet
 */
public class ViewportConfiguration {
  public ViewportConfiguration(int width, int height) {
    this.width = width;
    this.height = height;
  }

  public ViewportConfiguration(int width, int height, int x, int y) {
    this.width = width;
    this.height = height;
    this.x = x;
    this.y = y;
  }

  /** A viewport configuration that occupies the complete canvas area. */
  public ViewportConfiguration(ICanvas canvas) {
    this.width = canvas.getRendererWidth();
    this.height = canvas.getRendererHeight();
    this.x = 0;
    this.y = 0;
  }

  public int getWidth() {
    return width;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public int getHeight() {
    return height;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public int getX() {
    return x;
  }

  public void setX(int x) {
    this.x = x;
  }

  public int getY() {
    return y;
  }

  public void setY(int y) {
    this.y = y;
  }

  /** computes width/height */
  public float ratio() {
    return (float) width / (float) height;
  }

  public ViewportMode getMode() {
    return mode;
  }

  public void setMode(ViewportMode mode) {
    this.mode = mode;
  }

  @Override
  public String toString() {
    return "(ViewPort) width=" + width + " height=" + height + " x=" + x + " y=" + y;
  }

  protected int width;
  protected int height;
  protected int x;
  protected int y;
  protected ViewportMode mode;
}
