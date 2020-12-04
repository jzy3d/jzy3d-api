package org.jzy3d.chart.factories;

import org.jzy3d.chart.Animator;
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
import org.jzy3d.painters.Painter;
import org.jzy3d.plot2d.primitives.Serie2d;
import org.jzy3d.plot3d.primitives.axis.IAxis;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.ordering.AbstractOrderingStrategy;
import org.jzy3d.plot3d.rendering.scene.Graph;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.rendering.view.layout.IViewportLayout;

public interface IChartFactory {
	public Chart newChart();
    public Chart newChart(Quality quality);
    public Chart newChart(IChartFactory factory, Quality quality);

    public Painter newPainter();

    public ChartScene newScene(boolean sort);
    public Graph newGraph(Scene scene, AbstractOrderingStrategy strategy, boolean sort);
    public View newView(Scene scene, ICanvas canvas, Quality quality);
    public View newView(IChartFactory factory, Scene scene, ICanvas canvas, Quality quality);
    public Camera newCamera(Coord3d center);
    public IAxis newAxe(BoundingBox3d box, View view);
    //public Renderer3d newRenderer(View view);
    //public Renderer3d newRenderer(View view, boolean traceGL, boolean debugGL);
    public AbstractOrderingStrategy newOrderingStrategy();

    public ICanvas newCanvas(Scene scene, Quality quality);
    public ICanvas newCanvas(IChartFactory factory, Scene scene, Quality quality);
    
    public ICameraMouseController newMouseCameraController(Chart chart);
    public IMousePickingController newMousePickingController(Chart chart, int clickWidth);
    public ICameraKeyController newKeyboardCameraController(Chart chart);
    public IScreenshotKeyController newKeyboardScreenshotController(Chart chart);
    public CameraThreadController newCameraThreadController(Chart chart);
    
    public Animator newAnimator(ICanvas canvas);
    
    public IFrame newFrame(Chart chart);
    public IFrame newFrame(Chart chart, Rectangle bounds, String title);
    public IViewportLayout newViewportLayout();

    public Serie2d newSerie(String name, Serie2d.Type type);
    
    public IBoundingPolicy newBoundingPolicy();

    
    /** usefull to override the current factory to call, especially for FactoryOverrider
     * that must be used as this instead of its wrapped delegate factory
     */
    public IChartFactory getFactory();
    
    /*public static enum Toolkit {
        awt, swing, newt, offscreen, swt_newt
    }*/



}