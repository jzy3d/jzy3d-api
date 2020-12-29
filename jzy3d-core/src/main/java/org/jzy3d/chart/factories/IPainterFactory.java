package org.jzy3d.chart.factories;

import org.jzy3d.chart.Animator;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.keyboard.camera.ICameraKeyController;
import org.jzy3d.chart.controllers.keyboard.screenshot.IScreenshotKeyController;
import org.jzy3d.chart.controllers.mouse.camera.ICameraMouseController;
import org.jzy3d.chart.controllers.mouse.picking.IMousePickingController;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.painters.Painter;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;

/**
 * An {@link IPainterFactory} provides all Windowing toolkit dependent objects.
 * 
 * The {@link Painter} itself allows flipping between native rendering
 * ({@link NativeDesktopPainter}) and software rendering
 * ({@link EmulGLPainter}).
 * 
 * Following interfaces allows flipping between native or software AWT, NEWT,
 * SWT, Swing, JavaFX:
 * <ul>
 * <li>The {@link ICanvas} 
 * <li> And so does the {@link IFrame},
 * <li>Controllers : {@link IMousePickingController},
 * {@link ICameraKeyController}, {@link IScreenshotKeyController}.
 * </ul>
 * 
 * @author Martin Pernollet
 */
public interface IPainterFactory {
	public Painter newPainter();

	public ICanvas newCanvas(IChartFactory factory, Scene scene, Quality quality);

	public Animator newAnimator(ICanvas canvas);

	public ICameraMouseController newMouseCameraController(Chart chart);

	public IMousePickingController newMousePickingController(Chart chart, int clickWidth);

	public ICameraKeyController newKeyboardCameraController(Chart chart);

	public IScreenshotKeyController newKeyboardScreenshotController(Chart chart);

	public IFrame newFrame(Chart chart);

	public IFrame newFrame(Chart chart, Rectangle bounds, String title);
}
