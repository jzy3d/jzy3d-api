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
import java.util.ArrayList;
import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.jzy3d.chart.IAnimator;
import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Dimension;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.View;
import panamagl.GLEventListener;
import panamagl.canvas.GLCanvas;
import panamagl.canvas.swt.GLCanvasSWT;
import panamagl.factory.PanamaGLFactory;

/**
 * An SWT {@link Composite} hosting a PanamaGL-backed {@link GLCanvasSWT}.
 *
 * Ships the very same {@link PanamaGLCanvasSupport} plumbing as the Swing and JavaFX panels, while
 * exposing itself as a regular SWT {@link Composite} so it can be dropped into any SWT widget
 * hierarchy.
 */
public class PanamaGLSWTCanvas extends Composite implements IPanamaGLCanvas {

  protected GLCanvasSWT glCanvas;
  protected PanamaGLCanvasSupport support;
  protected List<ICanvasListener> canvasListeners = new ArrayList<>();

  public PanamaGLSWTCanvas(IChartFactory factory, Scene scene, Quality quality, Composite parent,
      PanamaGLFactory panamaGLFactory) {
    super(parent, SWT.NONE);
    setLayout(new FillLayout());
    this.glCanvas = new GLCanvasSWT(this, SWT.NONE, panamaGLFactory);
    preSizeToOnePixel(this.glCanvas);
    this.support = new PanamaGLCanvasSupport(this, factory, scene, quality, glCanvas);

    wirePixelScaleForwarding();
    installFirstRealLayoutRenderer(this.glCanvas);
    addDisposeListener(e -> support.dispose());
  }

  /**
   * Advanced / test-friendly constructor: inject a pre-built {@link GLCanvasSWT}. The supplied
   * canvas is re-parented to this composite so that the {@link FillLayout} can drive its size.
   * Use the main constructor in application code.
   */
  public PanamaGLSWTCanvas(IChartFactory factory, Scene scene, Quality quality, Composite parent,
      GLCanvasSWT glCanvas) {
    super(parent, SWT.NONE);
    setLayout(new FillLayout());
    this.glCanvas = glCanvas;
    if (glCanvas.getParent() != this) {
      glCanvas.setParent(this);
    }
    preSizeToOnePixel(glCanvas);
    this.support = new PanamaGLCanvasSupport(this, factory, scene, quality, glCanvas);

    wirePixelScaleForwarding();
    installFirstRealLayoutRenderer(glCanvas);
    addDisposeListener(e -> support.dispose());
  }

  /**
   * Forward PanamaGL pixel-scale changes to Jzy3D's {@link ICanvasListener#pixelScaleChanged}.
   * View.configureHiDPIListener listens for that event to drive font resize and legend update.
   */
  private void wirePixelScaleForwarding() {
    glCanvas.addPixelScaleListener(
        (oldScale, newScale) -> firePixelScaleChanged(newScale.x(), newScale.y()));
  }

  protected void firePixelScaleChanged(double pixelScaleX, double pixelScaleY) {
    for (ICanvasListener listener : canvasListeners) {
      listener.pixelScaleChanged(pixelScaleX, pixelScaleY);
    }
  }

  /**
   * Force the {@link GLCanvasSWT} to a 1x1 size before {@link PanamaGLCanvasSupport} wires the
   * GL listener.
   *
   * <p>Fixes <b>{@code Incomplete framebuffer: GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT (36054)}</b>
   * raised by {@code panamagl.platform.macos.FBO_macOS.prepare} during construction.
   *
   * <p><b>Root cause.</b> A freshly-created SWT widget sits at 0x0 until its parent's layout
   * pass runs (typically when {@code shell.open()} is called). However,
   * {@code support.<init>} synchronously triggers {@code glCanvas.setGLEventListener(renderer)}
   * via {@link panamagl.offscreen.ThreadRedirect_SWT}, which walks
   * {@code Renderer3D.init -> View.init -> View.initResources -> updateBounds -> shoot ->
   * canvas.forceRepaint() -> glCanvas.display() -> AOffscreenRenderer.renderGLToImage} with the
   * canvas still at 0x0. {@code renderGLToImage} resizes the FBO to {@code (0, 0)} and the
   * macOS attachment check fails.
   *
   * <p><b>Workaround.</b> Calling {@code setSize(1, 1)} before the listener is wired guarantees
   * a strictly positive width and height for the construction-time render. The first real
   * resize fired when SWT lays out the parent shell will replace the 1x1 FBO with a properly
   * sized one. Mirrors the strategy used by {@code PanamaGLJavaFXCanvas#newInitiallySizedCanvas}.
   */
  private static void preSizeToOnePixel(GLCanvasSWT glCanvas) {
    glCanvas.setSize(1, 1);
  }

  /**
   * Recover the very first real layout pass when {@link GLCanvasSWT}'s built-in resize handler
   * drops it.
   *
   * <p><b>Symptom.</b> The chart window stays blank (or shows a stretched 1x1 image) until the
   * user manually resizes it.
   *
   * <p><b>Root cause.</b> The construction-time render at 1x1 (see
   * {@link #preSizeToOnePixel(GLCanvasSWT)}) calls {@code glCanvas.display() ->
   * AOffscreenRenderer.onDisplay -> setScreenshot(out) -> redraw()}. The {@code redraw()} merely
   * <i>queues</i> an SWT {@code Paint} event; the {@code rendering} flag stays {@code true}
   * until that paint actually fires and {@code paintComponentNow} clears it. The next event in
   * the SWT queue is typically the {@code SWT.Resize} fired by the parent layout pass on
   * {@code shell.open()} — and {@link GLCanvasSWT}'s {@code ResizeHandler} bails out early when
   * {@code isRendering()} is true, so the resize never reaches
   * {@code AOffscreenRenderer.onResize} and the FBO is never re-rendered at the proper size.
   *
   * <p><b>Workaround.</b> Listen for {@code SWT.Resize} ourselves. When a non-trivial resize
   * arrives, schedule a {@code display()} via {@link Display#asyncExec(Runnable)} so it runs
   * <i>after</i> the construction-time paint has cleared {@code rendering} — bypassing the
   * built-in handler's drop. Self-uninstalls after the first successful real-size render so it
   * doesn't double-render on every subsequent user resize (the built-in handler covers those).
   */
  private void installFirstRealLayoutRenderer(GLCanvasSWT glCanvas) {
    final org.eclipse.swt.widgets.Listener[] holder = new org.eclipse.swt.widgets.Listener[1];
    holder[0] = event -> {
      if (glCanvas.isDisposed()) {
        return;
      }
      org.eclipse.swt.graphics.Rectangle bounds = glCanvas.getBounds();
      if (bounds.width <= 1 || bounds.height <= 1) {
        // Still the pre-size or an intermediate notification — wait for the real layout.
        return;
      }
      glCanvas.removeListener(SWT.Resize, holder[0]);
      glCanvas.getDisplay().asyncExec(() -> {
        if (!glCanvas.isDisposed()) {
          glCanvas.display();
        }
      });
    };
    glCanvas.addListener(SWT.Resize, holder[0]);
  }

  /**
   * Methods that simply forward to {@link #glCanvas} go through the field directly rather than
   * through {@link #support}.
   *
   * <p>SWT's {@link panamagl.offscreen.ThreadRedirect_SWT} runs the GL init synchronously when
   * already on the display thread, which is exactly the case while {@link PanamaGLCanvasSupport}'s
   * constructor wires {@code glCanvas.setGLEventListener(renderer)}. The init walks back through
   * {@code Renderer3D.init -> View.init} into the canvas — first
   * {@link org.jzy3d.painters.PanamaGLPainter#configureGL} calls {@link #getGLCanvas()}, then
   * {@code View.initResources -> updateBounds -> shoot} calls {@link #forceRepaint()} — all
   * <i>before</i> {@code support} has been assigned. Calling {@link #glCanvas} keeps these
   * paths NPE-free; the field and {@code support.getGLCanvas()} reference the same object.
   */
  @Override
  public GLCanvas getGLCanvas() {
    return glCanvas;
  }

  @Override
  public GLEventListener getGLEventListener() {
    return glCanvas.getGLEventListener();
  }

  @Override
  public void setGLEventListener(GLEventListener listener) {
    glCanvas.setGLEventListener(listener);
  }

  @Override
  public boolean isNative() {
    return true;
  }

  @Override
  public View getView() {
    return support.getView();
  }

  @Override
  public int getRendererWidth() {
    return getBounds().width;
  }

  @Override
  public int getRendererHeight() {
    return getBounds().height;
  }

  @Override
  public void screenshot(File file) throws IOException {
    // TODO Auto-generated method stub
  }

  @Override
  public Object screenshot() {
    return glCanvas.getScreenshot();
  }

  @Override
  public void display() {
    glCanvas.display();
  }

  @Override
  public void forceRepaint() {
    glCanvas.display();
  }

  @Override
  public void dispose() {
    if (support != null) {
      support.dispose();
    }
    super.dispose();
  }

  /**
   * Register an SWT controller on the underlying {@link GLCanvasSWT}.
   *
   * <p>The controller is expected to implement one or more of SWT's mouse listener interfaces
   * ({@link org.eclipse.swt.events.MouseListener}, {@link org.eclipse.swt.events.MouseMoveListener},
   * {@link org.eclipse.swt.events.MouseWheelListener},
   * {@link org.eclipse.swt.events.MouseTrackListener}). Each implemented interface is wired up
   * to the canvas; missing ones are silently ignored — same lenient behavior as the AWT
   * canvas's {@code addMouseController(Object)}.
   */
  @Override
  public void addMouseController(Object o) {
    if (o instanceof org.eclipse.swt.events.MouseListener) {
      glCanvas.addMouseListener((org.eclipse.swt.events.MouseListener) o);
    }
    if (o instanceof org.eclipse.swt.events.MouseMoveListener) {
      glCanvas.addMouseMoveListener((org.eclipse.swt.events.MouseMoveListener) o);
    }
    if (o instanceof org.eclipse.swt.events.MouseWheelListener) {
      glCanvas.addMouseWheelListener((org.eclipse.swt.events.MouseWheelListener) o);
    }
    if (o instanceof org.eclipse.swt.events.MouseTrackListener) {
      glCanvas.addMouseTrackListener((org.eclipse.swt.events.MouseTrackListener) o);
    }
  }

  @Override
  public void addKeyController(Object o) {
    if (o instanceof org.eclipse.swt.events.KeyListener) {
      glCanvas.addKeyListener((org.eclipse.swt.events.KeyListener) o);
    }
  }

  @Override
  public void removeMouseController(Object o) {
    if (glCanvas.isDisposed()) {
      return;
    }
    if (o instanceof org.eclipse.swt.events.MouseListener) {
      glCanvas.removeMouseListener((org.eclipse.swt.events.MouseListener) o);
    }
    if (o instanceof org.eclipse.swt.events.MouseMoveListener) {
      glCanvas.removeMouseMoveListener((org.eclipse.swt.events.MouseMoveListener) o);
    }
    if (o instanceof org.eclipse.swt.events.MouseWheelListener) {
      glCanvas.removeMouseWheelListener((org.eclipse.swt.events.MouseWheelListener) o);
    }
    if (o instanceof org.eclipse.swt.events.MouseTrackListener) {
      glCanvas.removeMouseTrackListener((org.eclipse.swt.events.MouseTrackListener) o);
    }
  }

  @Override
  public void removeKeyController(Object o) {
    if (glCanvas.isDisposed()) {
      return;
    }
    if (o instanceof org.eclipse.swt.events.KeyListener) {
      glCanvas.removeKeyListener((org.eclipse.swt.events.KeyListener) o);
    }
  }

  @Override
  public String getDebugInfo() {
    return null;
  }

  /** @see PanamaGLSwingCanvas#setPixelScale(float[]) */
  @Override
  public void setPixelScale(float[] scale) {
    boolean wantIdentity = scale != null && scale.length >= 2 && scale[0] == 1f && scale[1] == 1f;
    glCanvas.setHiDPIEnabled(!wantIdentity);
  }

  @Override
  public Coord2d getPixelScale() {
    panamagl.canvas.PixelScale s = glCanvas.getPixelScale();
    return new Coord2d(s.x(), s.y());
  }

  @Override
  public Coord2d getPixelScaleJVM() {
    return getPixelScale();
  }

  @Override
  public double getLastRenderingTimeMs() {
    return 0;
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

  @Override
  public IAnimator getAnimation() {
    return support.getAnimator();
  }

  @Override
  public Dimension getDimension() {
    return new Dimension(getRendererWidth(), getRendererHeight());
  }
}
