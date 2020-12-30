package org.jzy3d.plot3d.rendering.view;

import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.painters.NativeDesktopPainter;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;

/**
 * This view is named {@link AWTNativeView} because it uses AWT to handle
 * background and overlay rendering, and is based on JOGL to render the overlay.
 * 
 * 
 * @author Martin Pernollet
 *
 */
public class AWTNativeView extends AWTView {
	public AWTNativeView(IChartFactory factory, Scene scene, ICanvas canvas, Quality quality) {
		super(factory, scene, canvas, quality);
	}

	public void project() {
		((NativeDesktopPainter) painter).getCurrentGL(canvas);

		scene.getGraph().project(painter, cam);

		((NativeDesktopPainter) painter).getCurrentContext(canvas).release();
	}

	/** Perform the 3d projection of a 2d coordinate. */
	public Coord3d projectMouse(int x, int y) {
		((NativeDesktopPainter) painter).getCurrentGL(canvas);

		Coord3d p = cam.screenToModel(painter, new Coord3d(x, y, 0));

		((NativeDesktopPainter) painter).getCurrentContext(canvas).release();
		return p;
	}
}
