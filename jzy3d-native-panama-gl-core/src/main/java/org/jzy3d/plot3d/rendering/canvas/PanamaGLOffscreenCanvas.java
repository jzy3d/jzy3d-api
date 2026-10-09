/*******************************************************************************
 * Copyright (c) 2022, 2023 Martin Pernollet & contributors.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA
 *******************************************************************************/
package org.jzy3d.plot3d.rendering.canvas;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.imageio.ImageIO;
import org.jzy3d.chart.factories.APanamaGLPainterFactory;
import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Dimension;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.View;
import panamagl.GLProfile;
import panamagl.Image;
import panamagl.factory.PanamaGLFactory;
import panamagl.offscreen.FBO;
import panamagl.offscreen.FBOReader;
import panamagl.offscreen.FBOReader_AWT;
import panamagl.opengl.GL;
import panamagl.opengl.GLContext;
import panamagl.utils.ImageUtils;

/**
 * A canvas rendering a chart in an offscreen buffer without any window, e.g. to generate images on
 * a server or in tests.
 * 
 * The GL context is created and used by a dedicated thread : every rendering is performed by this
 * thread, and methods triggering a rendering wait for it to complete.
 */
public class PanamaGLOffscreenCanvas implements ICanvas {
  protected View view;
  protected Renderer3D renderer;

  protected PanamaGLFactory factory;
  protected GLContext context;
  protected GL gl;
  protected FBO fbo;
  protected FBOReader reader = new FBOReader_AWT();

  protected int width;
  protected int height;

  protected ExecutorService executor;
  protected Thread glThread;

  /** The last rendered image */
  protected BufferedImage image;

  protected List<ICanvasListener> canvasListeners = new ArrayList<>();

  public PanamaGLOffscreenCanvas(IChartFactory chartFactory, Scene scene, Quality quality,
      PanamaGLFactory factory, int width, int height) {
    this.factory = factory;
    this.width = width;
    this.height = height;

    this.executor = Executors.newSingleThreadExecutor(r -> {
      glThread = new Thread(r, "PanamaGL offscreen canvas");
      glThread.setDaemon(true);
      return glThread;
    });

    this.view = scene.newView(this, quality);
    this.view.getPainter().setCanvas(this);
    this.renderer = APanamaGLPainterFactory.newRenderer3D(chartFactory, view);

    runOnGLThread(() -> {
      context = factory.newGLContext();
      gl = factory.newGL();
      context.setProfile(new GLProfile(gl));

      fbo = factory.newFBO(width, height);
      fbo.prepare(gl);

      renderer.init(gl);
      render(true);
    });
  }

  /** Render the chart and keep the rendered image. Must be invoked by the GL thread. */
  protected void render(boolean reshape) {
    fbo.bind(gl);

    if (reshape) {
      renderer.reshape(gl, 0, 0, width, height);
    } else {
      renderer.display(gl);
    }

    Image<?> out = reader.read(fbo, gl);

    if (out != null) {
      // FBO rows are ordered from bottom to top
      image = ImageUtils.flipVertically((BufferedImage) out.getImage());
    }
  }

  /**
   * Run the task on the thread owning the GL context and wait for its completion. Run it
   * immediately if called from this thread.
   */
  protected void runOnGLThread(Runnable task) {
    if (Thread.currentThread() == glThread) {
      task.run();
      return;
    }
    try {
      executor.submit(task).get();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    } catch (ExecutionException e) {
      if (e.getCause() instanceof RuntimeException) {
        throw (RuntimeException) e.getCause();
      }
      throw new RuntimeException(e.getCause());
    }
  }

  @Override
  public void forceRepaint() {
    if (!executor.isShutdown()) {
      runOnGLThread(() -> render(false));
    }
  }

  /** Render the chart and return the image, a {@link BufferedImage}. */
  @Override
  public Object screenshot() {
    forceRepaint();
    return image;
  }

  @Override
  public void screenshot(File file) throws IOException {
    BufferedImage image = (BufferedImage) screenshot();

    if (image == null) {
      throw new IOException("Nothing was rendered, can't write " + file);
    }
    if (file.getAbsoluteFile().getParentFile() != null) {
      file.getAbsoluteFile().getParentFile().mkdirs();
    }
    ImageIO.write(image, "png", file);
  }

  @Override
  public void dispose() {
    if (executor.isShutdown()) {
      return;
    }
    runOnGLThread(() -> {
      if (fbo != null)
        fbo.release(gl);
      if (context != null)
        context.destroy();
    });
    executor.shutdown();
  }

  @Override
  public View getView() {
    return view;
  }

  public Renderer3D getRenderer() {
    return renderer;
  }

  @Override
  public boolean isNative() {
    return true;
  }

  @Override
  public int getRendererWidth() {
    return width;
  }

  @Override
  public int getRendererHeight() {
    return height;
  }

  @Override
  public Dimension getDimension() {
    return new Dimension(width, height);
  }

  @Override
  public String getDebugInfo() {
    if (context == null || context.getProfile() == null) {
      return null;
    }
    GLProfile p = context.getProfile();
    return "OpenGL " + p.getVersion() + " / " + p.getVendor() + " / " + p.getRenderer();
  }

  /** An offscreen canvas has no pixel scale. */
  @Override
  public void setPixelScale(float[] scale) {
  }

  @Override
  public Coord2d getPixelScale() {
    return new Coord2d(1, 1);
  }

  @Override
  public Coord2d getPixelScaleJVM() {
    return new Coord2d(1, 1);
  }

  @Override
  public double getLastRenderingTimeMs() {
    return renderer.getLastRenderingTimeMs();
  }

  @Override
  public void addMouseController(Object o) {
  }

  @Override
  public void addKeyController(Object o) {
  }

  @Override
  public void removeMouseController(Object o) {
  }

  @Override
  public void removeKeyController(Object o) {
  }

  @Override
  public void addCanvasListener(ICanvasListener listener) {
    canvasListeners.add(listener);
  }

  @Override
  public void removeCanvasListener(ICanvasListener listener) {
    canvasListeners.remove(listener);
  }

  @Override
  public List<ICanvasListener> getCanvasListeners() {
    return canvasListeners;
  }
}
