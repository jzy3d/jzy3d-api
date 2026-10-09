package org.jzy3d.plot3d.rendering.ddp;

import org.jzy3d.colors.Color;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.rendering.canvas.Renderer3D;
import org.jzy3d.plot3d.rendering.ddp.algorithms.IDepthPeelingAlgorithm;
import org.jzy3d.plot3d.rendering.ddp.algorithms.PeelingMethod;
import org.jzy3d.plot3d.rendering.view.View;
import panamagl.opengl.GL;

/**
 * Execute depth peeling methods with PanamaGL.
 * 
 * This renderer does what DepthPeelingRenderer3d does with JOGL : the scene is rendered by a
 * {@link IDepthPeelingAlgorithm} to perform order independent transparency.
 * 
 * @author Martin Pernollet
 */
public class PanamaGLDepthPeelingRenderer3D extends Renderer3D {
  protected IDepthPeelingAlgorithm algorithm;
  protected boolean algorithmInitialized = false;

  public PanamaGLDepthPeelingRenderer3D(View view) {
    this(PeelingMethod.WEIGHTED_AVERAGE_MODE, view);
  }

  public PanamaGLDepthPeelingRenderer3D(PeelingMethod method, View view) {
    super(view);
    this.algorithm = method.newAlgorithm();
    this.algorithm.setTasksToRender(painter -> view.render());

    // Depth peeling renders translucent polygons in any order, sorting them is useless
    if (view != null && view.getScene() != null) {
      view.getScene().getGraph().setSort(false);
    }
  }

  @Override
  public void init(GL canvas) {
    super.init(canvas);

    bindPainterToCurrentThread();
    beginRendering();
    try {
      algorithm.init(painter(), Math.max(1, width), Math.max(1, height));
      algorithmInitialized = true;
    } finally {
      endRendering();
    }
  }

  @Override
  public void display(GL canvas) {
    profileDisplayTimer.tic();

    bindPainterToCurrentThread();
    beginRendering();

    try {
      if (view != null && canvas != null && algorithmInitialized) {
        view.clear();

        Color bg = view.getBackgroundColor();
        algorithm.setBackground(new float[] {bg.r, bg.g, bg.b});

        // render the view through the depth peeling passes
        algorithm.display(painter());
      }
    } finally {
      endRendering();
    }

    profileDisplayTimer.toc();
    lastRenderingTimeMs = profileDisplayTimer.elapsedMilisecond();
  }

  /** Rebuild all depth peeling buffers for the new size, then render. */
  @Override
  public void reshape(GL canvas, int x, int y, int width, int height) {
    boolean resized = this.width != width || this.height != height;

    this.width = width;
    this.height = height;

    if (resized && algorithmInitialized) {
      bindPainterToCurrentThread();
      beginRendering();
      try {
        algorithm.reshape(painter(), width, height);
      } finally {
        endRendering();
      }
    }

    display(canvas);
  }

  @Override
  public void dispose(GL gl) {
    if (algorithmInitialized) {
      algorithm.dispose(painter());
      algorithmInitialized = false;
    }
    super.dispose(gl);
  }

  public IDepthPeelingAlgorithm getAlgorithm() {
    return algorithm;
  }

  protected IPainter painter() {
    return view.getPainter();
  }
}
