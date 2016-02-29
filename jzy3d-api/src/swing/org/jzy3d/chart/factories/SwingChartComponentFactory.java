package org.jzy3d.chart.factories;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.SwingChart;
import org.jzy3d.plot3d.rendering.canvas.CanvasSwing;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;

import com.jogamp.opengl.GLCapabilities;

@Deprecated
public class SwingChartComponentFactory extends AWTChartComponentFactory {
    @Override
    public Chart newChart(IChartComponentFactory factory, Quality quality, String toolkit) {
        return new SwingChart(factory, quality, toolkit.toString());
    }

    @Override
    public ICanvas newCanvas(IChartComponentFactory factory, Scene scene, Quality quality, String windowingToolkit, GLCapabilities capabilities) {
        boolean traceGL = false;
        boolean debugGL = false;
        return new CanvasSwing(factory, scene, quality, capabilities, traceGL, debugGL);
    }

    /* */

    public static Chart chart(Quality quality) {
        IChartComponentFactory f = new SwingChartComponentFactory();
        return f.newChart(quality, Toolkit.newt);
    }

    public static Chart chart(String toolkit) {
        IChartComponentFactory f = new SwingChartComponentFactory();
        return f.newChart(Chart.DEFAULT_QUALITY, toolkit);
    }

    public static Chart chart(Quality quality, Toolkit toolkit) {
        IChartComponentFactory f = new SwingChartComponentFactory();
        return f.newChart(quality, toolkit);
    }

    public static Chart chart(Quality quality, String toolkit) {
        IChartComponentFactory f = new SwingChartComponentFactory();
        return f.newChart(quality, toolkit);
    }
}
