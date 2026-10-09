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
package org.jzy3d.plot3d.rendering.view;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import org.jzy3d.maths.Coord2d;
import org.jzy3d.painters.IPainter;
import org.jzy3d.painters.PanamaGLPainter;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.tooltips.ITooltipRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Renders all {@link ITooltipRenderer}s and {@link AWTRenderer2d}s of an {@link AWTView} on top of
 * the scene.
 * 
 * They are painted with Java2D in an image covering the whole canvas, which is then drawn as a
 * texture by the {@link PanamaGLPainter}. The current pixel scale is taken into account so that
 * {@link AWTRenderer2d}s do not have to worry about it.
 */
public class PanamaGLViewOverlay implements IViewOverlay {
  protected static Logger LOGGER = LoggerFactory.getLogger(PanamaGLViewOverlay.class);

  protected java.awt.Color overlayBackground = new java.awt.Color(0, 0, 0, 0);

  @Override
  public void render(View view, ViewportConfiguration viewport, IPainter painter) {
    if (!(view instanceof AWTView) || !(painter instanceof PanamaGLPainter))
      return;

    AWTView awtView = (AWTView) view;
    ICanvas canvas = view.getCanvas();

    int width = canvas.getRendererWidth();
    int height = canvas.getRendererHeight();

    if (!awtView.hasOverlayStuffs() || width <= 0 || height <= 0)
      return;

    try {
      BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
      Graphics2D g2d = image.createGraphics();

      configureG2D(view, g2d);

      g2d.setBackground(overlayBackground);
      g2d.clearRect(0, 0, width, height);

      // Tooltips
      for (ITooltipRenderer t : awtView.getTooltips()) {
        t.render(g2d);
      }

      // Renderers
      for (AWTRenderer2d renderer : awtView.getRenderers2d()) {
        renderer.setView(awtView);
        renderer.paint(g2d, width, height);
      }

      g2d.dispose();

      // Draw on the full canvas
      painter.glViewport(0, 0, width, height);
      ((PanamaGLPainter) painter).drawImage(image, 0, 0);

    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
    }
  }

  /** Make the overlay HiDPI aware, and enable antialiasing. */
  protected void configureG2D(View view, Graphics2D g2d) {
    Coord2d pixelScale = view.getPixelScale();

    if (pixelScale.x != 1 || pixelScale.y != 1) {
      g2d.scale(pixelScale.x, pixelScale.y);
    }

    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
  }
}
