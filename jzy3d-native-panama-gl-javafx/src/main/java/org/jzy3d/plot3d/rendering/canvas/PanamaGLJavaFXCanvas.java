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
import org.jzy3d.chart.IAnimator;
import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.javafx.canvas.ResizableCanvas;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Dimension;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.View;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import panamagl.GLEventListener;
import panamagl.canvas.GLCanvas;
import panamagl.canvas.GLCanvasJFX;
import panamagl.factory.PanamaGLFactory;

/**
 * A JavaFX {@link BorderPane} hosting a PanamaGL-backed {@link javafx.scene.canvas.Canvas}.
 *
 * Ships the very same {@link PanamaGLCanvasSupport} plumbing as the Swing and
 * SWT panels, while exposing itself as a regular JavaFX {@link Node} so it can be
 * dropped into any scene graph.
 */
public class PanamaGLJavaFXCanvas extends BorderPane implements IPanamaGLCanvas {

  protected ResizableCanvas fxCanvas;
  protected PanamaGLCanvasSupport support;
  protected List<ICanvasListener> canvasListeners = new ArrayList<>();

  public PanamaGLJavaFXCanvas(IChartFactory factory, Scene scene, Quality quality,
      PanamaGLFactory panamaGLFactory) {
    this(factory, scene, quality, newInitiallySizedCanvas(), panamaGLFactory);
  }

  /**
   * Build a {@link ResizableCanvas} already sized to 1x1 (not 0x0 as the JavaFX default).
   *
   * <p>Fixes <b>{@code Incomplete framebuffer: GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT (36054)}</b>
   * raised by {@code panamagl.platform.macos.FBO_macOS.prepare} on the very first JavaFX layout
   * pass.
   *
   * <p><b>Root cause.</b> {@link GLCanvasJFX} attaches two independent listeners on the inner
   * JavaFX {@link javafx.scene.canvas.Canvas} {@code widthProperty()} and
   * {@code heightProperty()}. JavaFX layout calls {@link ResizableCanvas#resize(double, double)},
   * which in turn calls {@code setWidth} and {@code setHeight} sequentially. With a zero-sized
   * canvas, the first notification fires with a non-zero width but a still-zero height, so the
   * offscreen renderer prepares a FBO with a zero dimension — attachment check fails.
   *
   * <p><b>Workaround.</b> Starting the canvas at 1x1 guarantees that every intermediate
   * notification seen by {@link GLCanvasJFX}'s {@code ResizeHandler} has strictly positive
   * width <i>and</i> height, so every FBO preparation passes the completeness check.
   */
  private static ResizableCanvas newInitiallySizedCanvas() {
    ResizableCanvas c = new ResizableCanvas();
    c.setWidth(1);
    c.setHeight(1);
    return c;
  }

  private PanamaGLJavaFXCanvas(IChartFactory factory, Scene scene, Quality quality,
      ResizableCanvas fxCanvas, PanamaGLFactory panamaGLFactory) {
    this(factory, scene, quality, fxCanvas, new GLCanvasJFX(panamaGLFactory, fxCanvas));
  }

  /**
   * Advanced / test-friendly constructor: inject a pre-built {@link GLCanvasJFX} wrapping the
   * supplied {@link ResizableCanvas}. Use the main constructor in application code.
   */
  public PanamaGLJavaFXCanvas(IChartFactory factory, Scene scene, Quality quality,
      ResizableCanvas fxCanvas, GLCanvasJFX glCanvas) {
    this.fxCanvas = fxCanvas;
    // Putting the ResizableCanvas as BorderPane's center node lets the layout manager call
    // ResizableCanvas.resize(w, h) which setWidth/setHeight directly. Do NOT bind the
    // width/height properties: ResizableCanvas.resize() tries to setWidth() and a bound
    // property cannot be set, which would crash the layout pass when the stage is shown.
    setCenter(fxCanvas);

    this.support = new PanamaGLCanvasSupport(this, factory, scene, quality, glCanvas);

    glCanvas.addPixelScaleListener(
        (oldScale, newScale) -> firePixelScaleChanged(newScale.x(), newScale.y()));
  }

  protected void firePixelScaleChanged(double pixelScaleX, double pixelScaleY) {
    for (ICanvasListener listener : canvasListeners) {
      listener.pixelScaleChanged(pixelScaleX, pixelScaleY);
    }
  }

  /** Return the underlying JavaFX {@link Node} that actually displays the 3D scene. */
  public Node getNode() {
    return fxCanvas;
  }

  @Override
  public GLCanvas getGLCanvas() {
    return support.getGLCanvas();
  }

  @Override
  public GLEventListener getGLEventListener() {
    return support.getGLEventListener();
  }

  @Override
  public void setGLEventListener(GLEventListener listener) {
    support.setGLEventListener(listener);
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
    return (int) Math.round(getWidth());
  }

  @Override
  public int getRendererHeight() {
    return (int) Math.round(getHeight());
  }

  @Override
  public void screenshot(File file) throws IOException {
    // TODO Auto-generated method stub
  }

  @Override
  public Object screenshot() {
    return support.screenshot();
  }

  @Override
  public void display() {
    support.display();
  }

  @Override
  public void forceRepaint() {
    support.forceRepaint();
  }

  @Override
  public void dispose() {
    support.dispose();
  }

  /**
   * The JavaFX input model does not rely on {@code java.awt.event.MouseListener}; a JavaFX
   * controller registers itself directly on the inner JavaFX node. This method is kept to
   * honor {@link IScreenCanvas} but is a no-op: pass the node of this canvas to the controller
   * constructor instead.
   */
  @Override
  public void addMouseController(Object o) {
    // no-op: JavaFX controllers self-register on the node provided at construction time
  }

  @Override
  public void addKeyController(Object o) {
    // no-op: same as addMouseController
  }

  @Override
  public void removeMouseController(Object o) {
    // no-op
  }

  @Override
  public void removeKeyController(Object o) {
    // no-op
  }

  @Override
  public String getDebugInfo() {
    return null;
  }

  /** @see PanamaGLSwingCanvas#setPixelScale(float[]) */
  @Override
  public void setPixelScale(float[] scale) {
    boolean wantIdentity = scale != null && scale.length >= 2 && scale[0] == 1f && scale[1] == 1f;
    support.getGLCanvas().setHiDPIEnabled(!wantIdentity);
  }

  @Override
  public Coord2d getPixelScale() {
    panamagl.canvas.PixelScale s = support.getGLCanvas().getPixelScale();
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
