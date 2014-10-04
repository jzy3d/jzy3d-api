package org.jzy3d.chart.factories;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.media.opengl.GLCapabilities;

import org.apache.log4j.Logger;
import org.jzy3d.bridge.IFrame;
import org.jzy3d.bridge.awt.FrameAWT;
import org.jzy3d.bridge.swing.FrameSwing;
import org.jzy3d.chart.AWTChart;
import org.jzy3d.chart.Chart;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Dimension;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.plot3d.primitives.axes.AxeBox;
import org.jzy3d.plot3d.primitives.axes.IAxe;
import org.jzy3d.plot3d.rendering.canvas.CanvasAWT;
import org.jzy3d.plot3d.rendering.canvas.CanvasNewtAwt;
import org.jzy3d.plot3d.rendering.canvas.CanvasSwing;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.OffscreenCanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.AWTView;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.rendering.view.layout.ColorbarViewportLayout;
import org.jzy3d.plot3d.rendering.view.layout.IViewportLayout;

public class AWTChartComponentFactory extends ChartComponentFactory {
    public static Chart chart() {
        return chart(Quality.Intermediate);
    }
    
    public static Chart chart(Quality quality) {
        AWTChartComponentFactory f = new AWTChartComponentFactory();
        return f.newChart(quality, Toolkit.newt);
    }

    public static Chart chart(String toolkit) {
        AWTChartComponentFactory f = new AWTChartComponentFactory();
        return f.newChart(Chart.DEFAULT_QUALITY, toolkit);
    }

    public static Chart chart(Quality quality, Toolkit toolkit) {
        AWTChartComponentFactory f = new AWTChartComponentFactory();
        return f.newChart(quality, toolkit);
    }

    public static Chart chart(Quality quality, String toolkit) {
        AWTChartComponentFactory f = new AWTChartComponentFactory();
        return f.newChart(quality, toolkit);
    }
    
    /* */
    
    @Override
    public Chart newChart(IChartComponentFactory factory, Quality quality, String toolkit){
        return new AWTChart(factory, quality, toolkit);
    }

    @Override
    public IAxe newAxe(BoundingBox3d box, View view) {
        AxeBox axe = new AxeBox(box);
        axe.setView(view);
        return axe;
    }

    @Override
    public IViewportLayout newViewportLayout() {
        return new ColorbarViewportLayout();
    }

    @Override
    public View newView(Scene scene, ICanvas canvas, Quality quality) {
        return new AWTView(this, scene, canvas, quality);
    }

    @Override
    public IFrame newFrame(Chart chart, Rectangle bounds, String title) {
        Object canvas = chart.getCanvas();

        // Use reflexion to access AWT dependant classes
        // They will not be available for Android

        if (canvas.getClass().getName().equals("org.jzy3d.plot3d.rendering.canvas.CanvasAWT"))
            return newFrameAWT(chart, bounds, title, null); // FrameSWT works as
                                                            // well
        else if (canvas instanceof CanvasNewtAwt)
            return newFrameAWT(chart, bounds, title, "[Newt]"); // FrameSWT
                                                                // works as
                                                                // well
        else if (canvas.getClass().getName().equals("org.jzy3d.plot3d.rendering.canvas.CanvasSwing"))
            return newFrameSwing(chart, bounds, title);
        else
            throw new RuntimeException("No default frame could be found for the given Chart canvas: " + canvas.getClass());
    }

    @Override
    public ICanvas newCanvas(Scene scene, Quality quality, String windowingToolkit, GLCapabilities capabilities) {
        boolean traceGL = false;
        boolean debugGL = false;
        Toolkit chartType = getToolkit(windowingToolkit);
        switch (chartType) {
        case awt:
            return newCanvasAWT(this, scene, quality, capabilities, traceGL, debugGL);
        case swing:
            Logger.getLogger(ChartComponentFactory.class).warn("Swing canvas is deprecated. Use Newt instead");
            return newCanvasSwing(this, scene, quality, capabilities, traceGL, debugGL);
        case newt:
            return new CanvasNewtAwt(this, scene, quality, capabilities, traceGL, debugGL);
        case offscreen:
            Dimension dimension = getCanvasDimension(windowingToolkit);
            return new OffscreenCanvas(this, scene, quality, capabilities, dimension.width, dimension.height, traceGL, debugGL);
        default:
            throw new RuntimeException("unknown chart type:" + chartType);
        }
    }

    /* UTILS */
    
    protected IFrame newFrameSwing(Chart chart, Rectangle bounds, String title) {
        /*try {
            Class frameClass = Class.forName("org.jzy3d.bridge.swing.FrameSwing");
            IFrame frame = (IFrame) frameClass.newInstance();
            frame.initialize(chart, bounds, title);
            return frame;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("newFrameSwing", e);
        }*/
        return new FrameSwing(chart, bounds, title);
    }

    protected IFrame newFrameAWT(Chart chart, Rectangle bounds, String title, String message) {
        /*try {
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
        }*/
        return new FrameAWT(chart, bounds, title, message);
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

    protected ICanvas newCanvasSwing(ChartComponentFactory chartComponentFactory, Scene scene, Quality quality, GLCapabilities capabilities, boolean traceGL, boolean debugGL) {
        /*Class canvasSwingDefinition;
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
        }*/
        return new CanvasSwing(chartComponentFactory, scene, quality, capabilities, traceGL, debugGL);

    }

    protected ICanvas newCanvasAWT(ChartComponentFactory chartComponentFactory, Scene scene, Quality quality, GLCapabilities capabilities, boolean traceGL, boolean debugGL) {
        /*Class canvasAWTDefinition;
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
        }*/
        return new CanvasAWT(chartComponentFactory, scene, quality, capabilities, traceGL, debugGL);
    }
}
