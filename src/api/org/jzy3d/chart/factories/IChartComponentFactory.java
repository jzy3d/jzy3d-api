package org.jzy3d.chart.factories;

import java.awt.Rectangle;

import javax.media.opengl.GLCapabilities;

import org.jzy3d.bridge.IFrame;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.ChartScene;
import org.jzy3d.chart.controllers.keyboard.camera.ICameraKeyController;
import org.jzy3d.chart.controllers.keyboard.screenshot.IScreenshotKeyController;
import org.jzy3d.chart.controllers.mouse.camera.ICameraMouseController;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.axes.IAxe;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.ordering.AbstractOrderingStrategy;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.rendering.view.Renderer3d;
import org.jzy3d.plot3d.rendering.view.View;

public interface IChartComponentFactory {
	public abstract ChartScene newScene(boolean sort);
	public abstract View newView(Scene scene, ICanvas canvas, Quality quality);
	public abstract Camera newCamera(Coord3d center);
	public abstract IAxe newAxe(BoundingBox3d box, View view);
	public abstract Renderer3d newRenderer(View view, boolean traceGL,
			boolean debugGL);
	public abstract AbstractOrderingStrategy newOrderingStrategy();
	public abstract ICanvas newCanvas(Scene scene, Quality quality,
			String chartType, GLCapabilities capabilities);
	public ICameraMouseController newMouseController(Chart chart);
	public ICameraKeyController newKeyController(Chart chart);
	public IScreenshotKeyController newScreenshotKeyController(Chart chart);
	public IFrame newFrame(Chart chart, Rectangle bounds, String title);
}