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
