package org.jzy3d.chart.factories;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.ChartScene;
import org.jzy3d.chart.controllers.keyboard.camera.ICameraKeyController;
import org.jzy3d.chart.controllers.keyboard.screenshot.IScreenshotKeyController;
import org.jzy3d.chart.controllers.mouse.camera.ICameraMouseController;
import org.jzy3d.chart.controllers.mouse.picking.IMousePickingController;
import org.jzy3d.chart.controllers.thread.camera.CameraThreadController;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.IBoundingPolicy;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.plot2d.primitives.Serie2d;
import org.jzy3d.plot3d.primitives.axes.IAxe;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.ordering.AbstractOrderingStrategy;
import org.jzy3d.plot3d.rendering.scene.Graph;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.rendering.view.Renderer3d;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.rendering.view.layout.IViewportLayout;

import com.jogamp.opengl.GLCapabilities;

public interface IChartComponentFactory {
    public Chart newChart(Quality quality, Toolkit toolkit);
    public Chart newChart(Quality quality, String toolkit);
    public Chart newChart(IChartComponentFactory factory, Quality quality, String toolkit);
    
    public ChartScene newScene(boolean sort);
    public Graph newGraph(Scene scene, AbstractOrderingStrategy strategy, boolean sort);
    public View newView(Scene scene, ICanvas canvas, Quality quality);
    public View newView(IChartComponentFactory factory, Scene scene, ICanvas canvas, Quality quality);
    public Camera newCamera(Coord3d center);
    public IAxe newAxe(BoundingBox3d box, View view);
    public Renderer3d newRenderer(View view);
    public Renderer3d newRenderer(View view, boolean traceGL, boolean debugGL);
    public AbstractOrderingStrategy newOrderingStrategy();

    public ICanvas newCanvas(Scene scene, Quality quality, String chartType, GLCapabilities capabilities);
    public ICanvas newCanvas(IChartComponentFactory factory, Scene scene, Quality quality, String chartType, GLCapabilities capabilities);
    
    public ICameraMouseController newMouseCameraController(Chart chart);
    public IMousePickingController newMousePickingController(Chart chart, int clickWidth);
    public ICameraKeyController newKeyboardCameraController(Chart chart);
    public IScreenshotKeyController newKeyboardScreenshotController(Chart chart);
    public CameraThreadController newCameraThreadController(Chart chart);
    
    public IFrame newFrame(Chart chart);
    public IFrame newFrame(Chart chart, Rectangle bounds, String title);
    public IViewportLayout newViewportLayout();

    public Serie2d newSerie(String name, Serie2d.Type type);
    
    public IBoundingPolicy newBoundingPolicy();

    
    /** usefull to override the current factory to call, especially for FactoryOverrider
     * that must be used as this instead of its wrapped delegate factory
     */
    public IChartComponentFactory getFactory();
    
    public static enum Toolkit {
        awt, swing, newt, offscreen, swt_newt
    }



}