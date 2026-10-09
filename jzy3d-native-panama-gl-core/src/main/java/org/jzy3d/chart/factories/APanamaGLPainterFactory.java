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
package org.jzy3d.chart.factories;

import org.jzy3d.chart.IAnimator;
import org.jzy3d.chart.PanamaGLAnimator;
import org.jzy3d.maths.Dimension;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.painters.IPainter;
import org.jzy3d.painters.PanamaGLDebug;
import org.jzy3d.painters.PanamaGLPainter;
import org.jzy3d.plot3d.primitives.symbols.SymbolHandler;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.IScreenCanvas;
import org.jzy3d.plot3d.rendering.canvas.PanamaGLOffscreenCanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.canvas.Renderer3D;
import org.jzy3d.plot3d.rendering.image.IImageWrapper;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.IViewOverlay;
import org.jzy3d.plot3d.rendering.view.PanamaGLViewOverlay;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.rendering.view.layout.IViewportLayout;
import org.jzy3d.plot3d.rendering.view.layout.ViewAndColorbarsLayout;
import panamagl.factory.PanamaGLFactory;
import panamagl.opengl.GL;

/**
 * Toolkit-agnostic base for PanamaGL painter factories.
 *
 * Concrete subclasses (Swing/JavaFX/SWT) must provide the toolkit-dependent
 * pieces: canvas, frame, mouse/keyboard controllers, screenshot controller,
 * and mouse picking controller.
 */
public abstract class APanamaGLPainterFactory implements IPainterFactory {

  protected PanamaGLFactory panamaGLFactory = PanamaGLFactory.select();

  protected IChartFactory chartFactory;
  protected boolean offscreen = false;
  protected boolean debugGL = false;
  protected boolean traceGL = false;
  protected int width;
  protected int height;

  public PanamaGLFactory getPanamaGLFactory() {
    return panamaGLFactory;
  }

  public void setPanamaGLFactory(PanamaGLFactory panamaGLFactory) {
    this.panamaGLFactory = panamaGLFactory;
  }

  @Override
  public IPainter newPainter() {
    PanamaGLPainter p = new PanamaGLPainter();
    GL gl = panamaGLFactory.newGL();

    if (traceGL) {
      gl = PanamaGLDebug.trace(gl);
    } else if (debugGL) {
      gl = PanamaGLDebug.debug(gl);
    }
    p.setGL(gl);
    return p;
  }

  /**
   * A canvas rendering in an offscreen buffer without window, used instead of the toolkit canvas
   * when {@link #setOffscreen(int, int)} was invoked.
   */
  protected ICanvas newOffscreenCanvas(IChartFactory factory, Scene scene, Quality quality) {
    return new PanamaGLOffscreenCanvas(factory, scene, quality, panamaGLFactory, width, height);
  }

  /**
   * The renderer drawing the view in a canvas. Override to customize rendering, e.g. to render
   * with depth peeling.
   */
  public Renderer3D newRenderer3D(View view) {
    return new Renderer3D(view);
  }

  /** The renderer of a chart created by this chart factory. */
  public static Renderer3D newRenderer3D(IChartFactory factory, View view) {
    if (factory != null && factory.getPainterFactory() instanceof APanamaGLPainterFactory) {
      return ((APanamaGLPainterFactory) factory.getPainterFactory()).newRenderer3D(view);
    } else {
      return new Renderer3D(view);
    }
  }

  @Override
  public IViewOverlay newViewOverlay() {
    return new PanamaGLViewOverlay();
  }

  @Override
  public IViewportLayout newViewportLayout() {
    return new ViewAndColorbarsLayout();
  }

  @Override
  public SymbolHandler newSymbolHandler(IImageWrapper image) {
    return null;
  }

  @Override
  public IAnimator newAnimator(ICanvas canvas) {
    return new PanamaGLAnimator((IScreenCanvas) canvas);
  }

  @Override
  public IChartFactory getChartFactory() {
    return chartFactory;
  }

  @Override
  public void setChartFactory(IChartFactory chartFactory) {
    this.chartFactory = chartFactory;
  }

  @Override
  public boolean isOffscreen() {
    return offscreen;
  }

  @Override
  public void setOffscreenDisabled() {
    this.offscreen = false;
  }

  @Override
  public void setOffscreen(int width, int height) {
    this.offscreen = true;
    this.width = width;
    this.height = height;
  }

  @Override
  public void setOffscreen(Rectangle rectangle) {
    setOffscreen(rectangle.width, rectangle.height);
  }

  @Override
  public Dimension getOffscreenDimension() {
    return new Dimension(width, height);
  }

  /**
   * If true, painters throw an exception as soon as an OpenGL call raises an error. Must be set
   * before creating a chart. Slows down rendering.
   */
  @Override
  public boolean isDebugGL() {
    return debugGL;
  }

  @Override
  public void setDebugGL(boolean debugGL) {
    this.debugGL = debugGL;
  }

  /** If true, painters print each OpenGL call and check errors. Must be set before creating a chart. */
  public boolean isTraceGL() {
    return traceGL;
  }

  public void setTraceGL(boolean traceGL) {
    this.traceGL = traceGL;
  }
}
