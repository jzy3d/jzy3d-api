package org.jzy3d.chart.factories;

import java.util.Date;

import javax.media.opengl.GLCapabilities;

import org.jzy3d.bridge.IFrame;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.ChartScene;
import org.jzy3d.chart.ChartView;
import org.jzy3d.chart.controllers.keyboard.camera.CameraKeyController;
import org.jzy3d.chart.controllers.keyboard.camera.CameraKeyControllerNewt;
import org.jzy3d.chart.controllers.keyboard.camera.ICameraKeyController;
import org.jzy3d.chart.controllers.keyboard.screenshot.IScreenshotKeyController;
import org.jzy3d.chart.controllers.keyboard.screenshot.IScreenshotKeyController.IScreenshotEventListener;
import org.jzy3d.chart.controllers.keyboard.screenshot.ScreenshotKeyController;
import org.jzy3d.chart.controllers.keyboard.screenshot.ScreenshotKeyControllerNewt;
import org.jzy3d.chart.controllers.mouse.camera.CameraMouseController;
import org.jzy3d.chart.controllers.mouse.camera.CameraMouseControllerNewt;
import org.jzy3d.chart.controllers.mouse.camera.ICameraMouseController;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.maths.Utils;
import org.jzy3d.plot3d.primitives.axes.AxeBase;
import org.jzy3d.plot3d.primitives.axes.IAxe;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.ordering.AbstractOrderingStrategy;
import org.jzy3d.plot3d.rendering.ordering.BarycentreOrderingStrategy;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.rendering.view.Renderer3d;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.rendering.view.layout.IViewportLayout;

public class ChartComponentFactory implements IChartComponentFactory {
    public static Chart chart(Quality quality, Toolkit toolkit){
        ChartComponentFactory f = new ChartComponentFactory();
        return f.newChart(quality, toolkit);
    }
    
    /** to be implemented */
    protected ICanvas initializeCanvas(Scene scene, Quality quality, String chartType, GLCapabilities capabilities, boolean b, boolean c){
        throw new RuntimeException("should be implemented");
    }

    @Override
    public Chart newChart(Quality quality, Toolkit toolkit){
        return new Chart(this, quality, toolkit.toString());
    }
	@Override
	public ChartScene newScene(boolean sort) {
		return new ChartScene(sort, this);
	}
	@Override
	public View newView(Scene scene, ICanvas canvas, Quality quality) {
		return new ChartView(this, scene, canvas, quality);
	}
	@Override
	public Camera newCamera(Coord3d center) {
		return new Camera(center);
	}
	@Override
	public IAxe newAxe(BoundingBox3d box, View view) {
		AxeBase axe = new AxeBase(box);
		return axe;
	}
	@Override
	public Renderer3d newRenderer(View view, boolean traceGL, boolean debugGL) {
		return new Renderer3d(view, traceGL, debugGL);
	}
	@Override
	public AbstractOrderingStrategy newOrderingStrategy() {
		return new BarycentreOrderingStrategy();
	}
	@Override
	public ICanvas newCanvas(Scene scene, Quality quality, String chartType,
			GLCapabilities capabilities) {
		return initializeCanvas(scene, quality, chartType, capabilities, false,
				false);
	}

    @Override
	public ICameraMouseController newMouseController(Chart chart) {
		ICameraMouseController mouse = null;
		if (!chart.getWindowingToolkit().equals("newt"))
			mouse = new CameraMouseController(chart);
		else
			mouse = new CameraMouseControllerNewt(chart);
		return mouse;
	}

	@Override
	public IScreenshotKeyController newScreenshotKeyController(Chart chart) {
		// trigger screenshot on 's' letter
		String file = SCREENSHOT_FOLDER + "capture-"
				+ Utils.dat2str(new Date()) + ".png";
		IScreenshotKeyController screenshot;

		if (!chart.getWindowingToolkit().equals("newt"))
			screenshot = new ScreenshotKeyController(chart, file);
		else
			screenshot = new ScreenshotKeyControllerNewt(chart, file);

		screenshot.addListener(new IScreenshotEventListener() {
			@Override
			public void failedScreenshot(String file, Exception e) {
				System.out.println("Failed to save screenshot:");
				e.printStackTrace();
			}

			@Override
			public void doneScreenshot(String file) {
				System.out.println("Screenshot: " + file);
			}
		});
		return screenshot;
	}

	public static String SCREENSHOT_FOLDER = "./data/screenshots/";

	@Override
	public ICameraKeyController newKeyController(Chart chart) {
		ICameraKeyController key = null;
		if (!chart.getWindowingToolkit().equals("newt"))
			key = new CameraKeyController(chart);
		else
			key = new CameraKeyControllerNewt(chart);
		return key;
	}

	public IFrame newFrame(Chart chart, Rectangle bounds, String title) {
		throw new RuntimeException(
					"No implemented exception");
	}

	@Override
    public IViewportLayout newViewportLayout() {
        return null;
    }
}
