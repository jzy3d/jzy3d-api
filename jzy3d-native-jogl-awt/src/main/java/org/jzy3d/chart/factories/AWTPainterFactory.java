package org.jzy3d.chart.factories;

import java.util.Date;

import org.apache.log4j.Logger;
import org.jzy3d.bridge.awt.FrameAWT;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.keyboard.camera.AWTCameraKeyController;
import org.jzy3d.chart.controllers.keyboard.camera.ICameraKeyController;
import org.jzy3d.chart.controllers.keyboard.screenshot.AWTScreenshotKeyController;
import org.jzy3d.chart.controllers.keyboard.screenshot.IScreenshotKeyController;
import org.jzy3d.chart.controllers.keyboard.screenshot.IScreenshotKeyController.IScreenshotEventListener;
import org.jzy3d.chart.controllers.mouse.camera.AWTCameraMouseController;
import org.jzy3d.chart.controllers.mouse.camera.ICameraMouseController;
import org.jzy3d.chart.controllers.mouse.picking.AWTMousePickingController;
import org.jzy3d.chart.controllers.mouse.picking.IMousePickingController;
import org.jzy3d.maths.Dimension;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.maths.Utils;
import org.jzy3d.plot3d.rendering.canvas.CanvasAWT;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.OffscreenCanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;

public class AWTPainterFactory extends NativePainterFactory implements IPainterFactory{
	
    public static String SCREENSHOT_FOLDER = "./data/screenshots/";
    static Logger logger = Logger.getLogger(AWTPainterFactory.class);

	
	public AWTPainterFactory() {
	}

    @Override
    public ICanvas newCanvas(IChartFactory factory, Scene scene, Quality quality) {
        boolean traceGL = false;
        boolean debugGL = false;

    	NativeChartFactory nFactory = (NativeChartFactory)factory;
    	
        if(factory.isOffscreen()) {
            return newOffscreenCanvas(nFactory, scene, quality, traceGL, debugGL);
        }
        else {

        	return new CanvasAWT(nFactory, scene, quality, nFactory.getCapabilities(), traceGL, debugGL);
        }
    }
    
	protected ICanvas newOffscreenCanvas(NativeChartFactory factory, Scene scene, Quality quality, boolean traceGL,
			boolean debugGL) {
		Dimension dim = factory.getOffscreenDimension();
		return new OffscreenCanvas(factory, scene, quality, factory.getCapabilities(), dim.width, dim.height, traceGL, debugGL);
	}


    
    
    @Override
    public ICameraMouseController newMouseCameraController(Chart chart) {
        return new AWTCameraMouseController(chart);
    }
    
    @Override
    public IMousePickingController newMousePickingController(Chart chart, int clickWidth) {
        return new AWTMousePickingController(chart, clickWidth);
    }

    @Override
    public ICameraKeyController newKeyboardCameraController(Chart chart) {
        return new AWTCameraKeyController(chart);
    }

    @Override
    public IFrame newFrame(Chart chart, Rectangle bounds, String title) {
        return new FrameAWT(chart, bounds, title, null);
    }
    
    @Override
    public IFrame newFrame(Chart chart) {
        return newFrame(chart, new Rectangle(0, 0, 800, 600), "Jzy3d");
    }


    /**
     * Output file of screenshot can be configured using {@link IScreenshotKeyController#setFilename(String)}.
     */
    @Override
    public IScreenshotKeyController newKeyboardScreenshotController(Chart chart) {
        // trigger screenshot on 's' letter
        String file = SCREENSHOT_FOLDER + "capture-" + Utils.dat2str(new Date(), "yyyy-MM-dd-HH-mm-ss") + ".png";
        
        IScreenshotKeyController screenshot = new AWTScreenshotKeyController(chart, file);
        
        screenshot.addListener(new IScreenshotEventListener() {
            @Override
            public void failedScreenshot(String file, Exception e) {
                logger.error("Failed to save screenshot to '" + file + "'", e);
            }

            @Override
            public void doneScreenshot(String file) {
                logger.info("Screenshot save to '" + file + "'");
            }
        });
        return screenshot;
    }
}
