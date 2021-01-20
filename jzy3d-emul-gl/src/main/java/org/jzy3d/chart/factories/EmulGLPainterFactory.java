package org.jzy3d.chart.factories;

import org.jzy3d.bridge.awt.FrameAWT;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.EmulGLAnimator;
import org.jzy3d.chart.controllers.keyboard.camera.AWTCameraKeyController;
import org.jzy3d.chart.controllers.keyboard.screenshot.IScreenshotKeyController;
import org.jzy3d.chart.controllers.mouse.camera.AWTCameraMouseController;
import org.jzy3d.chart.controllers.mouse.picking.IMousePickingController;
import org.jzy3d.maths.Dimension;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.painters.EmulGLPainter;
import org.jzy3d.plot3d.primitives.symbols.SymbolHandler;
import org.jzy3d.plot3d.rendering.canvas.EmulGLCanvas;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.image.IImageWrapper;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.EmulGLViewOverlay;
import org.jzy3d.plot3d.rendering.view.IViewOverlay;
import org.jzy3d.plot3d.rendering.view.layout.EmulGLViewAndColorbarsLayout;
import org.jzy3d.plot3d.rendering.view.layout.ViewAndColorbarsLayout;

import jgl.GL;

public class EmulGLPainterFactory implements IPainterFactory{
	protected IChartFactory chartFactory;
	protected EmulGLCanvas internalCanvas;
	protected EmulGLPainter internalPainter;

	// not really needed actually since EmulGL renders
	// chart offscreen even when the frame is not shown
	protected boolean offscreen = false;
	protected int width;
	protected int height;


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
	
	/**
	 * This override intend to use jGL image rendering fallback based on AWT as jGL
	 * hardly handles the original {@link GL#glDrawPixel()} primitives.
	 * 
	 * As the View is still rendered center in the frame, we perform a hacky shift
	 * of the OpenGL image rendering in canvas to avoid the axis ticks beeing
	 * covered by the colorbar on the right.
	 * 
	 * @see https://github.com/jzy3d/jGL/issues/5
	 */
	@Override
	public ViewAndColorbarsLayout newViewportLayout() {
		return new EmulGLViewAndColorbarsLayout();
	}
	
	@Override
    public SymbolHandler newSymbolHandler(IImageWrapper image){
        return null;
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
			internalCanvas = newEmulGLCanvas(factory, scene, quality);

			link();
		}
		return internalCanvas;
	}

	protected EmulGLCanvas newEmulGLCanvas(IChartFactory factory, Scene scene, Quality quality) {
		return new EmulGLCanvas(factory, scene, quality);
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
    public Dimension getOffscreenDimension() {
    	return new Dimension(width, height);
    }


}
