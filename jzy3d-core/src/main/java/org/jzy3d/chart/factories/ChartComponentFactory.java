package org.jzy3d.chart.factories;

import java.lang.reflect.Constructor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.ChartScene;
import org.jzy3d.chart.ChartView;
import org.jzy3d.chart.controllers.keyboard.camera.ICameraKeyController;
import org.jzy3d.chart.controllers.keyboard.screenshot.IScreenshotKeyController;
import org.jzy3d.chart.controllers.mouse.camera.ICameraMouseController;
import org.jzy3d.chart.controllers.mouse.picking.IMousePickingController;
import org.jzy3d.chart.controllers.thread.camera.CameraThreadController;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Dimension;
import org.jzy3d.maths.IBoundingPolicy;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.plot2d.primitives.LineSerie2d;
import org.jzy3d.plot2d.primitives.LineSerie2dSplitted;
import org.jzy3d.plot2d.primitives.ScatterPointSerie2d;
import org.jzy3d.plot2d.primitives.ScatterSerie2d;
import org.jzy3d.plot2d.primitives.Serie2d;
import org.jzy3d.plot3d.primitives.axes.AxeBase;
import org.jzy3d.plot3d.primitives.axes.IAxe;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.canvas.VoidCanvas;
import org.jzy3d.plot3d.rendering.ordering.AbstractOrderingStrategy;
import org.jzy3d.plot3d.rendering.ordering.BarycentreOrderingStrategy;
import org.jzy3d.plot3d.rendering.scene.Graph;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.rendering.view.Renderer3d;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.rendering.view.layout.IViewportLayout;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLCapabilitiesImmutable;

/**
 * This {@link IChartComponentFactory} returns non-displayable charts.
 * 
 * @see AWTChartComponentFactory for a working implementation
 */
public abstract class ChartComponentFactory implements IChartComponentFactory {
    static Logger logger = Logger.getLogger(ChartComponentFactory.class);
    
    public abstract ICameraMouseController newMouseCameraController(Chart chart);
    public abstract IMousePickingController newMousePickingController(Chart chart, int clickWidth);
    public abstract IScreenshotKeyController newKeyboardScreenshotController(Chart chart);
    public abstract ICameraKeyController newKeyboardCameraController(Chart chart);
    public abstract IFrame newFrame(Chart chart, Rectangle bounds, String title);


    @Override
    public Chart newChart() {
        return newChart(Quality.Advanced);
    }

    @Override
    public Chart newChart(Quality quality) {
        return newChart(getFactory(), quality);
    }

    @Override
    public Chart newChart(IChartComponentFactory factory, Quality quality) {
        return new Chart(factory, quality);
    }

    @Override
    public ChartScene newScene(boolean sort) {
        return new ChartScene(sort, getFactory());
    }

    @Override
    public Graph newGraph(Scene scene, AbstractOrderingStrategy strategy, boolean sort) {
        return new Graph(scene, strategy, sort);
    }

    @Override
    public View newView(Scene scene, ICanvas canvas, Quality quality) {
        return newView(getFactory(), scene, canvas, quality);
    }
    
    @Override
    public View newView(IChartComponentFactory factory, Scene scene, ICanvas canvas, Quality quality) {
        return new ChartView(factory, scene, canvas, quality);
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
    public Renderer3d newRenderer(View view) {
        return newRenderer(view, false, false);
    }

    @Override
    public AbstractOrderingStrategy newOrderingStrategy() {
        return new BarycentreOrderingStrategy();
    }

    @Override
    public IBoundingPolicy newBoundingPolicy() {
        return null;
    }

    @Override
    public Serie2d newSerie(String name, Serie2d.Type type) {
        if (Serie2d.Type.LINE.equals(type))
            return new LineSerie2d(name);
        else if (Serie2d.Type.LINE_ON_OFF.equals(type))
            return new LineSerie2dSplitted(name);
        else if (Serie2d.Type.SCATTER.equals(type))
            return new ScatterSerie2d(name);
        else if (Serie2d.Type.SCATTER_POINTS.equals(type))
            return new ScatterPointSerie2d(name);
        else
            throw new IllegalArgumentException("Unsupported serie type " + type);
    }

    
    
    
    @Override
    public CameraThreadController newCameraThreadController(Chart chart){
        return new CameraThreadController(chart);
    }
    


    public static String SCREENSHOT_FOLDER = "./data/screenshots/";


    @Override
    public IViewportLayout newViewportLayout() {
        return null;
    }

    @Override
    public IFrame newFrame(Chart chart) {
        return newFrame(chart, new Rectangle(0, 0, 800, 600), "Jzy3d");
    }


    @Override
    public ICanvas newCanvas(Scene scene, Quality quality, String windowingToolkit) {
        return newCanvas(getFactory(), scene, quality, windowingToolkit);
    }

    @Override
    public ICanvas newCanvas(IChartComponentFactory factory, Scene scene, Quality quality, String windowingToolkit) {
        return new VoidCanvas(factory, scene, quality);
    }

    /* UTILS */

    @Override
    public IChartComponentFactory getFactory() {
        return this;
    }

    
}
