package org.jzy3d.chart.factories;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.SwingChart;
import org.jzy3d.plot3d.rendering.canvas.CanvasSwing;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;

@Deprecated
public class SwingChartFactory extends AWTChartFactory {
    @Override
    public Chart newChart(IChartFactory factory, Quality quality) {
        return new SwingChart(factory, quality);
    }

    @Override
    public ICanvas newCanvas(IChartFactory factory, Scene scene, Quality quality) {
        boolean traceGL = false;
        boolean debugGL = false;
        
        return new CanvasSwing((NativeChartFactory) factory, scene, quality, capabilities, traceGL, debugGL);
    }

    /* */

    public static Chart chart(Quality quality) {
        IChartFactory f = new SwingChartFactory();
        return f.newChart(quality);
    }
}
