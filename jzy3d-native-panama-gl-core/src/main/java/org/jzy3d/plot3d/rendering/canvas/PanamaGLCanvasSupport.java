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

import org.jzy3d.chart.IAnimator;
import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.View;
import panamagl.GLEventListener;
import panamagl.canvas.GLCanvas;

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

  public Object screenshot() {
    return glCanvas.getScreenshot();
  }

  public void dispose() {
    if (animator != null) {
      animator.stop();
    }
  }
}
