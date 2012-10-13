package org.jzy3d.chart.factories;

import java.awt.Rectangle;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;

import org.jzy3d.bridge.IFrame;
import org.jzy3d.bridge.awt.FrameAWT;
import org.jzy3d.bridge.swing.FrameSwing;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.ChartScene;
import org.jzy3d.chart.ChartView;
import org.jzy3d.chart.controllers.keyboard.camera.CameraKeyController;
import org.jzy3d.chart.controllers.keyboard.camera.CameraKeyControllerNewt;
import org.jzy3d.chart.controllers.keyboard.camera.ICameraKeyController;
import org.jzy3d.chart.controllers.keyboard.screenshot.IScreenshotKeyController;
import org.jzy3d.chart.controllers.keyboard.screenshot.IScreenshotKeyController.IScreenshotEventListener;
import org.jzy3d.chart.controllers.keyboard.screenshot.ScreenshotKeyController;
import org.jzy3d.chart.controllers.keyboard.screenshot.ScreenshotKeyControllerNewt;
import org.jzy3d.chart.controllers.mouse.camera.CameraMouseController;
import org.jzy3d.chart.controllers.mouse.camera.CameraMouseControllerNewt;
import org.jzy3d.chart.controllers.mouse.camera.ICameraMouseController;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Utils;
import org.jzy3d.plot3d.primitives.axes.AxeBox;
import org.jzy3d.plot3d.primitives.axes.IAxe;
import org.jzy3d.plot3d.rendering.canvas.CanvasAWT;
import org.jzy3d.plot3d.rendering.canvas.CanvasNewt;
import org.jzy3d.plot3d.rendering.canvas.CanvasSwing;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.OffscreenCanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.ordering.AbstractOrderingStrategy;
import org.jzy3d.plot3d.rendering.ordering.BarycentreOrderingStrategy;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.rendering.view.Renderer3d;
import org.jzy3d.plot3d.rendering.view.View;

public class ChartComponentFactory implements IChartComponentFactory  {
	/* (non-Javadoc)
	 * @see org.jzy3d.factories.IChartComponentFactory#newScene(boolean)
	 */
	@Override
	public ChartScene newScene(boolean sort){
        return new ChartScene(sort, this);
    }
	
	/* (non-Javadoc)
	 * @see org.jzy3d.factories.IChartComponentFactory#newView(org.jzy3d.plot3d.rendering.scene.Scene, org.jzy3d.plot3d.rendering.canvas.ICanvas, org.jzy3d.plot3d.rendering.canvas.Quality)
	 */
	@Override
	public View newView(Scene scene, ICanvas canvas, Quality quality){
        return new ChartView(this, scene, canvas, quality);
    }
	
	/* (non-Javadoc)
	 * @see org.jzy3d.factories.IChartComponentFactory#newCamera(org.jzy3d.maths.Coord3d)
	 */
	@Override
	public Camera newCamera(Coord3d center) {
        return new Camera(center);
    }
	
	/* (non-Javadoc)
	 * @see org.jzy3d.factories.IChartComponentFactory#newAxe(org.jzy3d.maths.BoundingBox3d, org.jzy3d.plot3d.rendering.view.View)
	 */
	@Override
	public IAxe newAxe(BoundingBox3d box, View view) {
	    AxeBox axe = new AxeBox(box);
	    axe.setView(view);
		return axe;
	}
	
	/* (non-Javadoc)
	 * @see org.jzy3d.factories.IChartComponentFactory#newRenderer(org.jzy3d.plot3d.rendering.view.View, boolean, boolean)
	 */
	@Override
	public Renderer3d newRenderer(View view, boolean traceGL, boolean debugGL){
        return new Renderer3d(view, traceGL, debugGL);
    }
	
	/* (non-Javadoc)
	 * @see org.jzy3d.factories.IChartComponentFactory#newOrderingStrategy()
	 */
	@Override
	public AbstractOrderingStrategy newOrderingStrategy() {
		return new BarycentreOrderingStrategy();
	}
	
	/* (non-Javadoc)
	 * @see org.jzy3d.factories.IChartComponentFactory#newCanvas(org.jzy3d.plot3d.rendering.scene.Scene, org.jzy3d.plot3d.rendering.canvas.Quality, java.lang.String, javax.media.opengl.GLCapabilities)
	 */
	@Override
	public ICanvas newCanvas(Scene scene, Quality quality, String chartType, GLCapabilities capabilities){
		if("awt".compareTo(chartType)==0)
			return new CanvasAWT(this, scene, quality, capabilities);
		else if("swing".compareTo(chartType)==0)
			return new CanvasSwing(this, scene, quality, capabilities);
        else if("newt".compareTo(chartType)==0)
            return new CanvasNewt(this, scene, quality, capabilities);
		else if(chartType.startsWith("offscreen")){
            Pattern pattern = Pattern.compile("offscreen,(\\d+),(\\d+)");
            Matcher matcher = pattern.matcher(chartType);
            if(matcher.matches()){
                int width = Integer.parseInt(matcher.group(1));
                int height = Integer.parseInt(matcher.group(2));
                return new OffscreenCanvas(this, scene, quality, GLProfile.getDefault(), width, height);
            }
            else
                return new OffscreenCanvas(this, scene, quality, GLProfile.getDefault(), 500, 500);
        }
		else
			throw new RuntimeException("unknown chart type:" + chartType);
	}
	
	@Override
	public ICameraMouseController newMouseController(Chart chart){
		ICameraMouseController mouse = null;
		if(!chart.getWindowingToolkit().equals("newt"))
			mouse = new CameraMouseController(chart);
		else
			mouse = new CameraMouseControllerNewt(chart);
        return mouse;
	}
	
	@Override
	public IScreenshotKeyController newScreenshotKeyController(Chart chart){
        // trigger screenshot on 's' letter
		String file = SCREENSHOT_FOLDER + "capture-" + Utils.dat2str(new Date()) + ".png";
        IScreenshotKeyController screenshot;
        
        if(!chart.getWindowingToolkit().equals("newt"))
        	screenshot = new ScreenshotKeyController(chart, file);
        else
        	screenshot = new ScreenshotKeyControllerNewt(chart, file);
        
        
        screenshot.addListener(new IScreenshotEventListener() {
            @Override
            public void failedScreenshot(String file, Exception e) {
                System.out.println("Failed to save screenshot:");
                e.printStackTrace();
            }
            
            @Override
            public void doneScreenshot(String file) {
                System.out.println("Screenshot: " + file);
            }
        });
        return screenshot;
	}
	
    public static String SCREENSHOT_FOLDER = "./data/screenshots/";

	@Override
	public ICameraKeyController newKeyController(Chart chart) {
		ICameraKeyController key = null;
		if(!chart.getWindowingToolkit().equals("newt"))
			key = new CameraKeyController(chart);
		else
			key = new CameraKeyControllerNewt(chart);
		return key;
	}
	
	public IFrame newFrame(Chart chart, Rectangle bounds, String title){
        Object canvas = chart.getCanvas();
        
        if(canvas instanceof CanvasAWT)
            return new FrameAWT(chart, bounds, title); // FrameSWT works as well
        else if(canvas instanceof CanvasNewt)
        	return new FrameAWT(chart, bounds, title, "[Newt]"); // FrameSWT works as well
        else if(canvas instanceof CanvasSwing)
        	return new FrameSwing(chart, bounds, title);
        else
            throw new RuntimeException("No default frame could be found for the given Chart canvas: " + canvas.getClass());
    }
}
