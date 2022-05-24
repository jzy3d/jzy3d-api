package org.jzy3d.plot3d.rendering.canvas;

import org.jzy3d.plot3d.rendering.view.Renderer3d;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.util.texture.TextureData;

public interface INativeCanvas {
  /** Returns the GLDrawable associated with the canvas */
  public GLAutoDrawable getDrawable();

  public Renderer3d getRenderer();

  /** Returns an image with the current renderer's size */
  public TextureData screenshot();


}
