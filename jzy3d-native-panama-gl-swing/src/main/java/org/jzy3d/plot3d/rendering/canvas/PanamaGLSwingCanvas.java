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

import java.awt.GridLayout;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import org.jzy3d.awt.AWTHelper;
import org.jzy3d.chart.IAnimator;
import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.maths.Dimension;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.View;
import panamagl.GLEventListener;
import panamagl.canvas.GLCanvas;
import panamagl.canvas.GLCanvasSwing;

public class PanamaGLSwingCanvas extends JPanel implements IPanamaGLCanvas {
  private static final long serialVersionUID = 2488043741850146830L;

  protected PanamaGLCanvasSupport support;
  protected List<ICanvasListener> canvasListeners = new ArrayList<>();

  public PanamaGLSwingCanvas(IChartFactory factory, Scene scene, Quality quality,
      GLCanvasSwing glCanvas) {
    super();
    setLayout(new GridLayout(0, 1));
    add(glCanvas);

    this.support = new PanamaGLCanvasSupport(this, factory, scene, quality, glCanvas);

    // Forward PanamaGL pixel scale changes to Jzy3D ICanvasListener.pixelScaleChanged.
    // View.configureHiDPIListener listens for that event to drive HiDPI font resize and
    // colorbar/legend update.
    glCanvas.addPixelScaleListener(
        (oldScale, newScale) -> firePixelScaleChanged(newScale.x(), newScale.y()));
  }

  protected void firePixelScaleChanged(double pixelScaleX, double pixelScaleY) {
    for (ICanvasListener listener : canvasListeners) {
      listener.pixelScaleChanged(pixelScaleX, pixelScaleY);
    }
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
    return getWidth();
  }

  @Override
  public int getRendererHeight() {
    return getHeight();
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

  @Override
  public void addMouseController(Object o) {
    addMouseListener((java.awt.event.MouseListener) o);
    if (o instanceof MouseWheelListener)
      addMouseWheelListener((MouseWheelListener) o);
    if (o instanceof MouseMotionListener)
      addMouseMotionListener((MouseMotionListener) o);
  }

  @Override
  public void addKeyController(Object o) {
    addKeyListener((java.awt.event.KeyListener) o);
  }

  @Override
  public void removeMouseController(Object o) {
    removeMouseListener((java.awt.event.MouseListener) o);
    if (o instanceof MouseWheelListener)
      removeMouseWheelListener((MouseWheelListener) o);
    if (o instanceof MouseMotionListener)
      removeMouseMotionListener((MouseMotionListener) o);
  }

  @Override
  public void removeKeyController(Object o) {
    removeKeyListener((java.awt.event.KeyListener) o);
  }

  @Override
  public String getDebugInfo() {
    return null;
  }

  /**
   * Map Jzy3D's JOGL-style {@code setPixelScale} to PanamaGL's {@code setHiDPIEnabled}: passing
   * {@code [1, 1]} (a.k.a. {@code Quality.preserveViewportSize=true}) disables HiDPI so the FBO is
   * dimensioned in logical pixels; any other value enables it.
   */
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
    return new Coord2d(AWTHelper.getPixelScaleX(this), AWTHelper.getPixelScaleY(this));
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
