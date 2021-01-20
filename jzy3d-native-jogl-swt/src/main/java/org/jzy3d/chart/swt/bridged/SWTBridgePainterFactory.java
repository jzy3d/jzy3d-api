package org.jzy3d.chart.swt.bridged;

import org.apache.log4j.Logger;
import org.jzy3d.bridge.swt.FrameSWTBridge;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.chart.factories.IFrame;
import org.jzy3d.chart.factories.IPainterFactory;
import org.jzy3d.chart.factories.NativePainterFactory;
import org.jzy3d.chart.swt.SWTPainterFactory;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.plot3d.rendering.canvas.CanvasAWT;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;

import com.jogamp.opengl.GLCapabilities;

public class SWTBridgePainterFactory extends SWTPainterFactory implements IPainterFactory{
    public static String SCREENSHOT_FOLDER = "./data/screenshots/";
    static Logger logger = Logger.getLogger(SWTBridgePainterFactory.class);
    
	public SWTBridgePainterFactory() {
		super();
	}

	public SWTBridgePainterFactory(GLCapabilities capabilities) {
		super(capabilities);
	}

	@Override
    public ICanvas newCanvas(IChartFactory factory, Scene scene, Quality quality) {
        boolean traceGL = false;
        boolean debugGL = false;
        
        return new CanvasAWT(factory, scene, quality, ((NativePainterFactory)factory.getPainterFactory()).getCapabilities(), traceGL, debugGL);
    }
	
    @Override
    public IFrame newFrame(Chart chart, Rectangle bounds, String title) {
        return new FrameSWTBridge(chart, bounds, title);
    }
}
