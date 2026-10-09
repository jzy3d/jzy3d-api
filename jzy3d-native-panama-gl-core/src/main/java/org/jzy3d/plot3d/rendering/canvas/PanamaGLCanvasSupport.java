/*******************************************************************************
 * Copyright (c) 2022, 2023 Martin Pernollet & contributors.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 *******************************************************************************/
package org.jzy3d.plot3d.rendering.canvas;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.jzy3d.chart.IAnimator;
import org.jzy3d.painters.PanamaGLPainter;
import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.View;
import panamagl.GLEventListener;
import panamagl.GLProfile;
import panamagl.Image;
import panamagl.canvas.GLCanvas;
import panamagl.offscreen.OffscreenRenderer;
import panamagl.opengl.GLContext;

/**
 * Toolkit-agnostic composition helper shared by all {@link IPanamaGLCanvas}
 * implementations (Swing, JavaFX, SWT). It holds the non-UI state of a
 * PanamaGL canvas (view, renderer, animator, GLCanvas) and exposes the
 * lifecycle operations that do not depend on the hosting component.
 *
 * Toolkit-specific canvases compose with this class to avoid duplicating
 * the view/renderer/animator wiring.
 */
public class PanamaGLCanvasSupport {
  protected IScreenCanvas owner;
  protected GLCanvas glCanvas;
  protected View view;
  protected Renderer3D renderer;
  protected IAnimator animator;

  public PanamaGLCanvasSupport(IScreenCanvas owner, IChartFactory factory, Scene scene,
      Quality quality, GLCanvas glCanvas) {
    this.owner = owner;
    this.glCanvas = glCanvas;

    view = scene.newView(owner, quality);
    view.getPainter().setCanvas(owner);

    renderer = new Renderer3D(view);
    glCanvas.setGLEventListener(renderer);

    animator = factory.getPainterFactory().newAnimator(owner);
    if (quality.isAnimated()) {
      animator.start();
    } else {
      animator.stop();
    }
  }

  public View getView() {
    return view;
  }

  public IAnimator getAnimator() {
    return animator;
  }

  public Renderer3D getRenderer() {
    return renderer;
  }

  public GLCanvas getGLCanvas() {
    return glCanvas;
  }

  public GLEventListener getGLEventListener() {
    return glCanvas.getGLEventListener();
  }

  public void setGLEventListener(GLEventListener listener) {
    glCanvas.setGLEventListener(listener);
  }

  public void display() {
    glCanvas.display();
  }

  public void forceRepaint() {
    glCanvas.display();
  }

  /**
   * Return the last rendered image, once renderings requested before this call are done.
   * 
   * @return a {@link java.awt.image.BufferedImage} for AWT based canvases, or null if nothing was
   *         rendered yet.
   */
  public Object screenshot() {
    waitForPendingRendering();

    Image<?> image = glCanvas.getScreenshot();
    return image == null ? null : image.getImage();
  }

  /** Write the last rendered image to a PNG file. */
  public void screenshot(File file) throws IOException {
    waitForPendingRendering();

    Image<?> image = glCanvas.getScreenshot();
    if (image == null) {
      throw new IOException("Nothing was rendered yet, can't write " + file);
    }
    if (file.getAbsoluteFile().getParentFile() != null) {
      file.getAbsoluteFile().getParentFile().mkdirs();
    }
    image.save(file.getAbsolutePath());
  }

  /**
   * Wait until the renderings that were requested before this call are done, by queuing a task on
   * the thread rendering the canvas. Return immediately if called from that thread.
   */
  public void waitForPendingRendering() {
    if (view.getPainter() instanceof PanamaGLPainter
        && ((PanamaGLPainter) view.getPainter()).isGLThread()) {
      return;
    }

    OffscreenRenderer offscreen = glCanvas.getOffscreenRenderer();
    if (offscreen == null || offscreen.getThreadRedirect() == null) {
      return;
    }

    CountDownLatch rendered = new CountDownLatch(1);
    offscreen.getThreadRedirect().run(rendered::countDown);

    try {
      rendered.await(PENDING_RENDERING_TIMEOUT_MS, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  protected static final long PENDING_RENDERING_TIMEOUT_MS = 5000;

  /** OpenGL version, vendor and renderer of the canvas context, or null before initialization. */
  public String getDebugInfo() {
    GLContext context = glCanvas.getContext();
    if (context == null || context.getProfile() == null) {
      return null;
    }
    GLProfile profile = context.getProfile();
    return "OpenGL " + profile.getVersion() + " / " + profile.getVendor() + " / "
        + profile.getRenderer();
  }

  public void dispose() {
    if (animator != null) {
      animator.stop();
    }
  }
}
