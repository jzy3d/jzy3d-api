package org.jzy3d.plot3d.rendering.view;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.painters.EmulGLPainter;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.tooltips.ITooltipRenderer;
import org.jzy3d.plot3d.rendering.tooltips.Tooltip;

/**
 * An EmulGL view implementation that is able to handle overlay and tooltip
 * rendering using an AWT BufferedImage painted with a Graphics2D passed to
 * {@link AWTRenderer2d#paint(java.awt.Graphics, int, int)} or
 * {@link ITooltipRenderer#render(Graphics2D)}.
 * 
 * @author Martin Pernollet
 *
 */
public class EmulGLView extends AWTView {
	public EmulGLView(IChartFactory factory, Scene scene, ICanvas canvas, Quality quality) {
		super(factory, scene, canvas, quality);
	}

	/**
	 * Renders all provided {@link Tooltip}s and {@link AWTRenderer2d}s on top of
	 * the scene.
	 */
	@Override
	public void renderOverlay(ViewportConfiguration viewport) {
		if (!hasOverlayStuffs())
			return;

		if (viewport.getWidth() > 0 && viewport.getHeight() > 0) {

			try {

				BufferedImage image = new BufferedImage(viewport.getWidth(), viewport.getHeight(),
						BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2d = image.createGraphics();

				g2d.setBackground(overlayBackground);
				g2d.clearRect(0, 0, canvas.getRendererWidth(), canvas.getRendererHeight());

				// Tooltips
				for (ITooltipRenderer t : tooltips)
					t.render(g2d);

				// Renderers
				for (AWTRenderer2d renderer : renderers)
					renderer.paint(g2d, canvas.getRendererWidth(), canvas.getRendererHeight());

				g2d.dispose();

				((EmulGLPainter) painter).getGL().appendImageToDraw(image, 0, 0);

			} catch (Exception e) {
				LOGGER.error(e, e);
			}
		}
	}

}
