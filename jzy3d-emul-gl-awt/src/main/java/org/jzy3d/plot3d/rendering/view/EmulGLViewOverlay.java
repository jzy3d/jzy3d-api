package org.jzy3d.plot3d.rendering.view;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jzy3d.painters.EmulGLPainter;
import org.jzy3d.painters.IPainter;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.tooltips.ITooltipRenderer;
import jgl.GL;

/**
 * An EmulGL view implementation that is able to handle overlay and tooltip
 * rendering using an AWT BufferedImage painted with a Graphics2D passed to
 * {@link AWTRenderer2d#paint(java.awt.Graphics, int, int)} or
 * {@link ITooltipRenderer#render(Graphics2D)}.
 * 
 * @author Martin Pernollet
 *
 */
public class EmulGLViewOverlay implements IViewOverlay {
	protected static Logger LOGGER = LogManager.getLogger(EmulGLViewOverlay.class);

	protected java.awt.Color overlayBackground = new java.awt.Color(0, 0, 0, 0);

	@Override
	public void render(View view, ViewportConfiguration viewport, IPainter painter) {
		AWTView awtView = ((AWTView) view);
		ICanvas canvas = view.getCanvas();

		if (!awtView.hasOverlayStuffs())
			return;

		if (viewport.getWidth() > 0 && viewport.getHeight() > 0) {

			try {
				int imWidth = viewport.getWidth();
				int imHeight = viewport.getHeight();
				if (view.getPixelScale().x > 1 || view.getPixelScale().y > 1) {
					imWidth = (int) (viewport.getWidth() * view.getPixelScale().x);
					imHeight = (int) (viewport.getHeight() * view.getPixelScale().y);
				}

				BufferedImage image = new BufferedImage(imWidth, imHeight, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2d = image.createGraphics();

				// make overlay HiDPI aware
				g2d.scale(view.getPixelScale().x, view.getPixelScale().y);

				g2d.setBackground(overlayBackground);
				g2d.clearRect(0, 0, canvas.getRendererWidth(), canvas.getRendererHeight());

				// Tooltips
				for (ITooltipRenderer t : awtView.getTooltips())
					t.render(g2d);

				// Renderers
				for (AWTRenderer2d renderer : awtView.getRenderers2d())
					renderer.paint(g2d, canvas.getRendererWidth(), canvas.getRendererHeight());

				g2d.dispose();

				// Append Image to draw
				((EmulGLPainter) painter).getGL().appendImageToDraw(image, 0, 0, GL.ImageLayer.FOREGROUND);

			} catch (Exception e) {
				LOGGER.error(e, e);
			}
		}
	}

}
