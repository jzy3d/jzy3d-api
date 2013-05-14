package org.jzy3d.chart.factories;

import java.lang.reflect.Constructor;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLCapabilitiesImmutable;

import org.apache.log4j.Logger;
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
import org.jzy3d.maths.Dimension;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.maths.Utils;
import org.jzy3d.plot3d.primitives.axes.AxeBox;
import org.jzy3d.plot3d.primitives.axes.IAxe;
import org.jzy3d.plot3d.rendering.canvas.CanvasNewt;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.OffscreenCanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.ordering.AbstractOrderingStrategy;
import org.jzy3d.plot3d.rendering.ordering.BarycentreOrderingStrategy;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.rendering.view.Renderer3d;
import org.jzy3d.plot3d.rendering.view.View;
//import org.jzy3d.plot3d.rendering.canvas.CanvasAWT;
//import org.jzy3d.plot3d.rendering.canvas.CanvasSwing;

public class ChartComponentFactory implements IChartComponentFactory {

	public static enum Toolkit {

		awt, swing, newt, offscreen
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jzy3d.factories.IChartComponentFactory#newScene(boolean)
	 */
	@Override
	public ChartScene newScene(boolean sort) {
		return new ChartScene(sort, this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jzy3d.factories.IChartComponentFactory#newView(org.jzy3d.plot3d.rendering
	 * .scene.Scene, org.jzy3d.plot3d.rendering.canvas.ICanvas,
	 * org.jzy3d.plot3d.rendering.canvas.Quality)
	 */
	@Override
	public View newView(Scene scene, ICanvas canvas, Quality quality) {
		return new ChartView(this, scene, canvas, quality);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jzy3d.factories.IChartComponentFactory#newCamera(org.jzy3d.maths.
	 * Coord3d)
	 */
	@Override
	public Camera newCamera(Coord3d center) {
		return new Camera(center);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jzy3d.factories.IChartComponentFactory#newAxe(org.jzy3d.maths.
	 * BoundingBox3d, org.jzy3d.plot3d.rendering.view.View)
	 */
	@Override
	public IAxe newAxe(BoundingBox3d box, View view) {
		AxeBox axe = new AxeBox(box);
		axe.setView(view);
		return axe;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jzy3d.factories.IChartComponentFactory#newRenderer(org.jzy3d.plot3d
	 * .rendering.view.View, boolean, boolean)
	 */
	@Override
	public Renderer3d newRenderer(View view, boolean traceGL, boolean debugGL) {
		return new Renderer3d(view, traceGL, debugGL);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jzy3d.factories.IChartComponentFactory#newOrderingStrategy()
	 */
	@Override
	public AbstractOrderingStrategy newOrderingStrategy() {
		return new BarycentreOrderingStrategy();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jzy3d.factories.IChartComponentFactory#newCanvas(org.jzy3d.plot3d
	 * .rendering.scene.Scene, org.jzy3d.plot3d.rendering.canvas.Quality,
	 * java.lang.String, javax.media.opengl.GLCapabilities)
	 */
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
		Object canvas = chart.getCanvas();

		// Use reflexion to access AWT dependant classes
		// They will not be available for Android

		if (canvas.getClass().getName().equals("org.jzy3d.plot3d.rendering.canvas.CanvasAWT"))
			return newFrameAWT(chart, bounds, title, null); // FrameSWT works as
															// well
		else if (canvas instanceof CanvasNewt)
			return newFrameAWT(chart, bounds, title, "[Newt]"); // FrameSWT
																// works as
																// well
		else if (canvas.getClass().getName().equals("org.jzy3d.plot3d.rendering.canvas.CanvasSwing"))
			return newFrameSwing(chart, bounds, title);
		else
			throw new RuntimeException(
					"No default frame could be found for the given Chart canvas: "
							+ canvas.getClass());
	}

	private IFrame newFrameSwing(Chart chart, Rectangle bounds, String title) {
		try {
			Class frameClass = Class.forName("org.jzy3d.bridge.awt.FrameSwing");
			IFrame frame = (IFrame) frameClass.newInstance();
			frame.initialize(chart, bounds, title);

			return frame;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("newFrameSwing", e);
		}
	}

	private IFrame newFrameAWT(Chart chart, Rectangle bounds, String title,
			String message) {
		try {
			Class frameClass = Class.forName("org.jzy3d.bridge.awt.FrameAWT");
			IFrame frame = (IFrame) frameClass.newInstance();
			if (message != null) {
				frame.initialize(chart, bounds, title, message);
			} else {
				frame.initialize(chart, bounds, title);
			}
			return frame;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("newFrameSwing", e);
		}
	}

	protected Toolkit getToolkit(String windowingToolkit) {
		if (windowingToolkit.startsWith("offscreen")) {
			return Toolkit.offscreen;
		}
		return Toolkit.valueOf(windowingToolkit);
	}

	protected Dimension getCanvasDimension(String windowingToolkit) {
		if (windowingToolkit.startsWith("offscreen")) {
			Pattern pattern = Pattern.compile("offscreen,(\\d+),(\\d+)");
			Matcher matcher = pattern.matcher(windowingToolkit);
			if (matcher.matches()) {
				int width = Integer.parseInt(matcher.group(1));
				int height = Integer.parseInt(matcher.group(2));
				return new Dimension(width, height);
			} else {
				return new Dimension(500, 500);
			}
		}
		return null;
	}

	protected ICanvas initializeCanvas(Scene scene, Quality quality,
			String windowingToolkit, GLCapabilities capabilities,
			boolean traceGL, boolean debugGL) {
		Toolkit chartType = getToolkit(windowingToolkit);
		switch (chartType) {
		case awt:
			return newCanvasAWT(this, scene, quality, capabilities, traceGL,
					debugGL);
		case swing:
			Logger.getLogger(ChartComponentFactory.class).warn(
					"Swing canvas is deprecated. Use Newt instead");
			return newCanvasSwing(this, scene, quality, capabilities, traceGL,
					debugGL);
		case newt:
			return new CanvasNewt(this, scene, quality, capabilities, traceGL,
					debugGL);
		case offscreen:
			Dimension dimension = getCanvasDimension(windowingToolkit);
			return new OffscreenCanvas(this, scene, quality, capabilities,
					dimension.width, dimension.height, traceGL, debugGL);
		default:
			throw new RuntimeException("unknown chart type:" + chartType);
		}
	}

	private ICanvas newCanvasSwing(ChartComponentFactory chartComponentFactory,
			Scene scene, Quality quality, GLCapabilities capabilities,
			boolean traceGL, boolean debugGL) {

		Class canvasSwingDefinition;
		Class[] constrArgsClass = new Class[] { IChartComponentFactory.class,
				Scene.class, Quality.class, GLCapabilitiesImmutable.class,
				boolean.class, boolean.class };
		Object[] constrArgs = new Object[] { chartComponentFactory, scene,
				quality, capabilities, traceGL, debugGL };
		Constructor constructor;
		ICanvas canvas;

		try {
			canvasSwingDefinition = Class
					.forName("org.jzy3d.plot3d.rendering.canvas.CanvasSwing");
			constructor = canvasSwingDefinition.getConstructor(constrArgsClass);

			return (ICanvas) constructor.newInstance(constrArgs);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("newCanvasSwing", e);
		}
	}

	private ICanvas newCanvasAWT(ChartComponentFactory chartComponentFactory,
			Scene scene, Quality quality, GLCapabilities capabilities,
			boolean traceGL, boolean debugGL) {

		Class canvasAWTDefinition;
		Class[] constrArgsClass = new Class[] { IChartComponentFactory.class,
				Scene.class, Quality.class, GLCapabilitiesImmutable.class,
				boolean.class, boolean.class };
		Object[] constrArgs = new Object[] { chartComponentFactory, scene,
				quality, capabilities, traceGL, debugGL };
		Constructor constructor;
		ICanvas canvas;

		try {
			canvasAWTDefinition = Class
					.forName("org.jzy3d.plot3d.rendering.canvas.CanvasAWT");
			constructor = canvasAWTDefinition.getConstructor(constrArgsClass);

			return (ICanvas) constructor.newInstance(constrArgs);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("newCanvasAWT", e);
		}
	}

}
