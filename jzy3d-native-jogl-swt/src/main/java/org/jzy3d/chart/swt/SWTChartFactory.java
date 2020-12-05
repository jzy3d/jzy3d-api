package org.jzy3d.chart.swt;

import java.util.Date;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.NativeAnimator;
import org.jzy3d.chart.controllers.keyboard.camera.ICameraKeyController;
import org.jzy3d.chart.controllers.keyboard.camera.NewtCameraKeyController;
import org.jzy3d.chart.controllers.keyboard.screenshot.IScreenshotKeyController;
import org.jzy3d.chart.controllers.keyboard.screenshot.IScreenshotKeyController.IScreenshotEventListener;
import org.jzy3d.chart.controllers.keyboard.screenshot.NewtScreenshotKeyController;
import org.jzy3d.chart.controllers.mouse.camera.ICameraMouseController;
import org.jzy3d.chart.controllers.mouse.camera.NewtCameraMouseController;
import org.jzy3d.chart.controllers.mouse.picking.IMousePickingController;
import org.jzy3d.chart.controllers.mouse.picking.NewtMousePickingController;
import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.chart.factories.IFrame;
import org.jzy3d.chart.factories.NativeChartFactory;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.maths.Utils;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.AWTNativeView;
import org.jzy3d.plot3d.rendering.view.AWTRenderer3d;
import org.jzy3d.plot3d.rendering.view.Renderer3d;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.rendering.view.layout.IViewportLayout;
import org.jzy3d.plot3d.rendering.view.layout.ViewAndColorbarsLayout;

import com.jogamp.newt.opengl.GLWindow;

public class SWTChartFactory extends NativeChartFactory {
    private static final Logger logger = Logger.getLogger(SWTChartFactory.class);

    protected final Composite canvas;

    public SWTChartFactory(Composite canvas) {
        this.canvas = canvas;
    }

    public static Chart chart(Composite parent) {
        SWTChartFactory f = new SWTChartFactory(parent);
        return f.newChart(Quality.Intermediate);
    }

    public static Chart chart(Composite parent, Quality quality) {
        SWTChartFactory f = new SWTChartFactory(parent);
        return f.newChart(quality);
    }

    /* */

    /**
     */
    @Override
    public Chart newChart(IChartFactory factory, Quality quality) {
        return new SWTChart(canvas, factory, quality);
    }
    
    /** Dedicated to {@link CanvasNewtSWT} implementation. */
    public NativeAnimator newAnimator(GLWindow canvas) {
        return new NativeAnimator(canvas);
    }

    public Composite getComposite() {
        return canvas;
    }

    @Override
    public IViewportLayout newViewportLayout() {
        return new ViewAndColorbarsLayout();
    }

    /**
     * The AWTView support Java2d defined components (tooltips, background
     * images)
     */
    @Override
    public View newView(IChartFactory factory, Scene scene, ICanvas canvas, Quality quality) {
        return new AWTNativeView(factory, scene, canvas, quality);
    }

    /** Provide AWT Texture loading for screenshots */
    @Override
    public Renderer3d newRenderer(View view, boolean traceGL, boolean debugGL) {
        return new AWTRenderer3d(view, traceGL, debugGL);
    }

    @Override
    public ICanvas newCanvas(IChartFactory factory, Scene scene, Quality quality) {
        boolean traceGL = false;
        boolean debugGL = false;
        
        return new CanvasNewtSWT((NativeChartFactory)factory, scene, quality, getCapabilities(), traceGL, debugGL);
    }

    @Override
    public IChartFactory getFactory() {
        return this;
    }

    @Override
    public ICameraMouseController newMouseCameraController(Chart chart) {
        return new NewtCameraMouseController(chart);
    }

    @Override
    public IMousePickingController newMousePickingController(Chart chart, int clickWidth) {
        return new NewtMousePickingController(chart, clickWidth);
    }

    /**
     * Output file of screenshot can be configured using {@link IScreenshotKeyController#setFilename(String)}.
     */
    @Override
    public IScreenshotKeyController newKeyboardScreenshotController(Chart chart) {
        // trigger screenshot on 's' letter
        String file = SCREENSHOT_FOLDER + "capture-" + Utils.dat2str(new Date(), "yyyy-MM-dd-HH-mm-ss") + ".png";
        IScreenshotKeyController screenshot = new NewtScreenshotKeyController(chart, file);

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

    @Override
    public ICameraKeyController newKeyboardCameraController(Chart chart) {
        return new NewtCameraKeyController(chart);
    }

    @Override
    public IFrame newFrame(Chart chart, Rectangle bounds, String title) {
        return null;
    }
}
