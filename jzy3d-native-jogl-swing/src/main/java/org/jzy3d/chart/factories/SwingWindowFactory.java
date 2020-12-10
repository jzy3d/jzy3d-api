package org.jzy3d.chart.factories;

import org.apache.log4j.Logger;
import org.jzy3d.plot3d.rendering.canvas.CanvasSwing;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;

public class SwingWindowFactory extends AWTPainterFactory{
    public static String SCREENSHOT_FOLDER = "./data/screenshots/";
    static Logger logger = Logger.getLogger(SwingWindowFactory.class);

    
    @Override
    public ICanvas newCanvas(IChartFactory factory, Scene scene, Quality quality) {
        boolean traceGL = false;
        boolean debugGL = false;
        
        NativeChartFactory nFactory = (NativeChartFactory) factory;
        return new CanvasSwing((NativeChartFactory) factory, scene, quality, nFactory.getCapabilities(), traceGL, debugGL);
    }

}
