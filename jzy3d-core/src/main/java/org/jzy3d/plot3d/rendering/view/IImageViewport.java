package org.jzy3d.plot3d.rendering.view;

import org.jzy3d.maths.Dimension;
import org.jzy3d.painters.IPainter;

public interface IImageViewport {

  /**
   * Renders the picture into the window, according to the viewport settings.
   * 
   * If the picture is bigger than the viewport, it is simply centered in it, otherwise, it is
   * scaled in order to fit into the viewport.
   * 
   * @param painter TODO
   */
  public abstract void render(IPainter painter);

  /** Return the minimum size for this graphic. */
  public abstract Dimension getMinimumSize();

  /** Return the prefered size for this graphic. */
  public abstract Dimension getPreferedSize();


  public void setViewPort(int width, int height);

  public ViewportMode getMode();

  public void setViewportMode(ViewportMode mode);

  /**
   * Set the view port (size of the renderer).
   * 
   * @param width the width of the target window.
   * @param height the height of the target window.
   * @param left the width's ratio where this subscreen starts in the target window.
   * @param right the width's ratio where this subscreen stops in the target window.
   * 
   * @throws an IllegalArgumentException if right is not greater than left.
   */
  public void setViewPort(int width, int height, float left, float right);
  
  public int getSliceWidth(int width, float left, float right);

  public void setViewPort(ViewportConfiguration viewport);

  public ViewportConfiguration getLastViewPort();

}
