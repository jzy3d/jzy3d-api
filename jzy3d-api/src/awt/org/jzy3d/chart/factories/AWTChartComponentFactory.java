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
        return new AWTView(getFactory(), scene, canvas, quality);
    }
    
    
    /** bypass reflection used in super implementation */
    @Override
    protected IFrame newFrameSwing(Chart chart, Rectangle bounds, String title) {
        return new FrameSwing(chart, bounds, title);
    }

    /** bypass reflection used in super implementation */
    @Override
    protected IFrame newFrameAWT(Chart chart, Rectangle bounds, String title, String message) {
        return new FrameAWT(chart, bounds, title, message);
    }
    
    @Override
    public ICanvas newCanvas(IChartComponentFactory factory, Scene scene, Quality quality, String windowingToolkit, GLCapabilities capabilities) {
        boolean traceGL = false;
        boolean debugGL = false;
        Toolkit chartType = getToolkit(windowingToolkit);
        switch (chartType) {
        case awt:
            return newCanvasAWT(factory, scene, quality, capabilities, traceGL, debugGL);
        case swing:
            Logger.getLogger(ChartComponentFactory.class).warn("Swing canvas is deprecated. Use Newt instead");
            return newCanvasSwing(factory, scene, quality, capabilities, traceGL, debugGL);
        case newt:
            return new CanvasNewtAwt(factory, scene, quality, capabilities, traceGL, debugGL);
        case offscreen:
            Dimension dimension = getCanvasDimension(windowingToolkit);
            return new OffscreenCanvas(factory, scene, quality, capabilities, dimension.width, dimension.height, traceGL, debugGL);
        default:
            throw new RuntimeException("unknown chart type:" + chartType);
        }
    }

    /** bypass reflection used in super implementation */
    @Override
    protected ICanvas newCanvasAWT(IChartComponentFactory chartComponentFactory, Scene scene, Quality quality, GLCapabilities capabilities, boolean traceGL, boolean debugGL) {
        return new CanvasAWT(chartComponentFactory, scene, quality, capabilities, traceGL, debugGL);
    }
    
    /** bypass reflection used in super implementation */
    @Override
    protected ICanvas newCanvasSwing(IChartComponentFactory chartComponentFactory, Scene scene, Quality quality, GLCapabilities capabilities, boolean traceGL, boolean debugGL) {
        return new CanvasSwing(chartComponentFactory, scene, quality, capabilities, traceGL, debugGL);
    }



    
    @Override
    public IChartComponentFactory getFactory() {
        return this;
    }

}
