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
import org.jzy3d.painters.PanamaGLPainter;
import org.jzy3d.plot3d.pipelines.NotImplementedException;
import org.jzy3d.plot3d.primitives.symbols.SymbolHandler;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.IScreenCanvas;
import org.jzy3d.plot3d.rendering.image.IImageWrapper;
import org.jzy3d.plot3d.rendering.view.IViewOverlay;
import org.jzy3d.plot3d.rendering.view.layout.IViewportLayout;
import org.jzy3d.plot3d.rendering.view.layout.PanamaGLViewAndColorbarsLayout;
import panamagl.factory.PanamaGLFactory;

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
    p.setGL(panamaGLFactory.newGL());
    return p;
  }

  @Override
  public IViewOverlay newViewOverlay() {
    return null;
  }

  @Override
  public IViewportLayout newViewportLayout() {
    return new PanamaGLViewAndColorbarsLayout();
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

  @Override
  public boolean isDebugGL() {
    return false;
  }

  @Override
  public void setDebugGL(boolean debugGL) {
    throw new NotImplementedException();
  }
}
