package org.jzy3d.chart.factories;

import org.apache.log4j.Logger;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.OffscreenCanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;

public class OffscreenChartFactory extends AWTChartFactory {
    static Logger logger = Logger.getLogger(OffscreenChartFactory.class);

    int width;
    int height;

    public OffscreenChartFactory(int width, int height) {
		super();
		this.width = width;
		this.height = height;
	}

	@Override
    public ICanvas newCanvas(IChartFactory factory, Scene scene, Quality quality) {
        boolean traceGL = false;
        boolean debugGL = false;
        
        return new OffscreenCanvas((NativeChartFactory)factory, scene, quality, getCapabilities(), width, height, traceGL, debugGL);
    }

}
