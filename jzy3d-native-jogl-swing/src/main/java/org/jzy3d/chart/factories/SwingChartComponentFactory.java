package org.jzy3d.chart.factories;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.SwingChart;
import org.jzy3d.plot3d.rendering.canvas.CanvasSwing;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;

@Deprecated
public class SwingChartComponentFactory extends AWTChartComponentFactory {
    @Override
    public Chart newChart(IChartComponentFactory factory, Quality quality) {
        return new SwingChart(factory, quality);
    }

    @Override
    public ICanvas newCanvas(IChartComponentFactory factory, Scene scene, Quality quality) {
        boolean traceGL = false;
        boolean debugGL = false;
        
        return new CanvasSwing((NativeChartFactory) factory, scene, quality, capabilities, traceGL, debugGL);
    }

    /* */

    public static Chart chart(Quality quality) {
        IChartComponentFactory f = new SwingChartComponentFactory();
        return f.newChart(quality);
    }
}
