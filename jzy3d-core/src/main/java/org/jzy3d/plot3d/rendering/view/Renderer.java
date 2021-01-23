package org.jzy3d.plot3d.rendering.view;

import org.jzy3d.plot3d.rendering.canvas.ICanvas;

public interface Renderer {
  public void init(ICanvas canvas);

  public void dispose(ICanvas canvas);

  public void display(ICanvas canvas);

  public void reshape(ICanvas canvas, int x, int y, int width, int height);
}
