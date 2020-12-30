package org.jzy3d.chart.factories;

import org.jzy3d.bridge.awt.FrameAWT;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.EmulGLAnimator;
import org.jzy3d.chart.controllers.keyboard.camera.AWTCameraKeyController;
import org.jzy3d.chart.controllers.keyboard.screenshot.IScreenshotKeyController;
import org.jzy3d.chart.controllers.mouse.camera.AWTCameraMouseController;
import org.jzy3d.chart.controllers.mouse.picking.IMousePickingController;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.painters.EmulGLPainter;
import org.jzy3d.plot3d.rendering.canvas.EmulGLCanvas;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.EmulGLViewOverlay;
import org.jzy3d.plot3d.rendering.view.IViewOverlay;

public class EmulGLPainterFactory implements IPainterFactory{
	IChartFactory chartFactory;

	EmulGLCanvas internalCanvas;
	EmulGLPainter internalPainter;


	@Override
	public EmulGLPainter newPainter() {
		if (internalPainter == null) {
			internalPainter = new EmulGLPainter();
		}
		return internalPainter;
	}
	
	@Override
	public IViewOverlay newViewOverlay() {
		return new EmulGLViewOverlay();
	}

	
	@Override
    public EmulGLAnimator newAnimator(ICanvas canvas) {
        return new EmulGLAnimator((EmulGLCanvas)canvas);
    }


	
	@Override
	public FrameAWT newFrame(Chart chart, Rectangle bounds, String title) {
		return new FrameAWT(chart, bounds, title, null);
	}

    @Override
    public IFrame newFrame(Chart chart) {
        return newFrame(chart, new Rectangle(0, 0, 800, 600), "Jzy3d");
    }

	@Override
	public EmulGLCanvas newCanvas(IChartFactory factory, Scene scene, Quality quality) {
		if (internalCanvas == null) {
			internalCanvas = new EmulGLCanvas(factory, scene, quality);

			link();
		}
		return internalCanvas;
	}

	protected void link() {
		if (internalPainter == null) {
			newPainter();
		}
		internalPainter.setGL(internalCanvas.getGL());
		internalPainter.setGLU(internalCanvas.getGLU());
		internalPainter.setGLUT(internalCanvas.getGLUT());

		internalPainter.getGLUT().glutInitWindowSize(500, 500);
		internalPainter.getGLUT().glutInitWindowPosition(0, 0);
		internalPainter.getGLUT().glutCreateWindow(internalCanvas);

		internalPainter.getGLUT();
	}

	
	
	
	/**
	 * This override
	 */
	@Override
	public AWTCameraMouseController newMouseCameraController(Chart chart) {
		return new AWTCameraMouseController(chart);
		//return new EmulGLMouse(chart);
	}

	@Override
	public AWTCameraKeyController newKeyboardCameraController(Chart chart) {
		return new AWTCameraKeyController(chart);
	}

	@Override
	public IMousePickingController newMousePickingController(Chart chart, int clickWidth) {
		return null;
	}

	@Override
	public IScreenshotKeyController newKeyboardScreenshotController(Chart chart) {
		return null;
	}
	
	
	public IChartFactory getChartFactory() {
		return chartFactory;
	}

	public void setChartFactory(IChartFactory chartFactory) {
		this.chartFactory = chartFactory;
	}

}
