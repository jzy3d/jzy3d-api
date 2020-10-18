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

    /*public static Chart chart(Quality quality, Toolkit toolkit) {
        ChartComponentFactory f = new ChartComponentFactory();
        return f.newChart(quality, toolkit);
    }*/
    
    public abstract ICameraMouseController newMouseCameraController(Chart chart);
    public abstract IMousePickingController newMousePickingController(Chart chart, int clickWidth);
    public abstract IScreenshotKeyController newKeyboardScreenshotController(Chart chart);
    public abstract ICameraKeyController newKeyboardCameraController(Chart chart);


    @Override
    public Chart newChart(Quality quality, Toolkit toolkit) {
        return newChart(getFactory(), quality, toolkit.toString());
    }

    @Override
    public Chart newChart(Quality quality, String toolkit) {
        return newChart(getFactory(), quality, toolkit);
    }

    @Override
    public Chart newChart(IChartComponentFactory factory, Quality quality, String toolkit) {
        return new Chart(factory, quality, toolkit);
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

    /**
     * Use reflexion to access AWT dependant classes.
     */
    @Override
    public IFrame newFrame(Chart chart, Rectangle bounds, String title) {
        Object canvas = chart.getCanvas();

        if (canvas.getClass().getName().equals("org.jzy3d.plot3d.rendering.canvas.CanvasAWT"))
            return newFrameAWT(chart, bounds, title, null);
        // FrameSWT works as well
        //else if (canvas instanceof CanvasNewtAwt)
        //    return newFrameAWT(chart, bounds, title, "[Newt]");
        // FrameSWT works as well
        else if (canvas.getClass().getName().equals("org.jzy3d.plot3d.rendering.canvas.CanvasSwing"))
            return newFrameSwing(chart, bounds, title);
        else {
            String m = "No default frame could be found for the given Chart canvas: " + canvas.getClass();
            logger.error(m);
            return null;
            // throw new RuntimeException(m);
        }
    }

    @Override
    public ICanvas newCanvas(Scene scene, Quality quality, String windowingToolkit, GLCapabilities capabilities) {
        return newCanvas(getFactory(), scene, quality, windowingToolkit, capabilities);
    }

    @Override
    public ICanvas newCanvas(IChartComponentFactory factory, Scene scene, Quality quality, String windowingToolkit, GLCapabilities capabilities) {
        return new VoidCanvas(factory, scene, quality);
    }

    /* */

    protected IFrame newFrameSwing(Chart chart, Rectangle bounds, String title) {
        try {
            Class frameClass = Class.forName("org.jzy3d.bridge.swing.FrameSwing");
            IFrame frame = (IFrame) frameClass.newInstance();
            frame.initialize(chart, bounds, title);
            return frame;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("newFrameSwing", e);
        }
    }

    protected IFrame newFrameAWT(Chart chart, Rectangle bounds, String title, String message) {
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
            throw new RuntimeException("newFrameAWT", e);
        }
    }

    protected ICanvas newCanvasSwing(IChartComponentFactory chartComponentFactory, Scene scene, Quality quality, GLCapabilities capabilities, boolean traceGL, boolean debugGL) {
        Class canvasSwingDefinition;
        Class[] constrArgsClass = new Class[] { IChartComponentFactory.class, Scene.class, Quality.class, GLCapabilitiesImmutable.class, boolean.class, boolean.class };
        Object[] constrArgs = new Object[] { chartComponentFactory, scene, quality, capabilities, traceGL, debugGL };
        Constructor constructor;
        ICanvas canvas;

        try {
            canvasSwingDefinition = Class.forName("org.jzy3d.plot3d.rendering.canvas.CanvasSwing");
            constructor = canvasSwingDefinition.getConstructor(constrArgsClass);

            return (ICanvas) constructor.newInstance(constrArgs);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("newCanvasSwing", e);
        }
    }

    protected ICanvas newCanvasAWT(IChartComponentFactory chartComponentFactory, Scene scene, Quality quality, GLCapabilities capabilities, boolean traceGL, boolean debugGL) {
        Class canvasAWTDefinition;
        Class[] constrArgsClass = new Class[] { IChartComponentFactory.class, Scene.class, Quality.class, GLCapabilitiesImmutable.class, boolean.class, boolean.class };
        Object[] constrArgs = new Object[] { chartComponentFactory, scene, quality, capabilities, traceGL, debugGL };
        Constructor constructor;
        ICanvas canvas;

        try {
            canvasAWTDefinition = Class.forName("org.jzy3d.plot3d.rendering.canvas.CanvasAWT");
            constructor = canvasAWTDefinition.getConstructor(constrArgsClass);

            return (ICanvas) constructor.newInstance(constrArgs);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("newCanvasAWT", e);
        }
    }

    /* UTILS */

    public Toolkit getToolkit(String windowingToolkit) {
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

    @Override
    public IChartComponentFactory getFactory() {
        return this;
    }

    
}
