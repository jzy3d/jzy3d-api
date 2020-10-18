package org.jzy3d.chart.factories;

import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.apache.log4j.Logger;
import org.jzy3d.bridge.awt.FrameAWT;
import org.jzy3d.chart.AWTChart;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.keyboard.camera.AWTCameraKeyController;
import org.jzy3d.chart.controllers.keyboard.camera.ICameraKeyController;
import org.jzy3d.chart.controllers.keyboard.camera.NewtCameraKeyController;
import org.jzy3d.chart.controllers.keyboard.screenshot.AWTScreenshotKeyController;
import org.jzy3d.chart.controllers.keyboard.screenshot.IScreenshotKeyController;
import org.jzy3d.chart.controllers.keyboard.screenshot.IScreenshotKeyController.IScreenshotEventListener;
import org.jzy3d.chart.controllers.mouse.camera.AWTCameraMouseController;
import org.jzy3d.chart.controllers.mouse.camera.ICameraMouseController;
import org.jzy3d.chart.controllers.mouse.picking.AWTMousePickingController;
import org.jzy3d.chart.controllers.mouse.picking.IMousePickingController;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Dimension;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.maths.Utils;
import org.jzy3d.plot3d.primitives.axes.AxeBox;
import org.jzy3d.plot3d.primitives.axes.IAxe;
import org.jzy3d.plot3d.rendering.canvas.CanvasAWT;
import org.jzy3d.plot3d.rendering.canvas.CanvasNewtAwt;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.OffscreenCanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.AWTRenderer3d;
import org.jzy3d.plot3d.rendering.view.AWTView;
import org.jzy3d.plot3d.rendering.view.Renderer3d;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.rendering.view.layout.ColorbarViewportLayout;
import org.jzy3d.plot3d.rendering.view.layout.IViewportLayout;

import com.jogamp.opengl.GLCapabilities;

public class AWTChartComponentFactory extends ChartComponentFactory {
    static Logger logger = Logger.getLogger(AWTChartComponentFactory.class);
    
    public static Chart chart() {
        return chart(Quality.Intermediate);
    }

    public static Chart chart(Quality quality) {
        AWTChartComponentFactory f = new AWTChartComponentFactory();
        return f.newChart(quality, Toolkit.awt/* Toolkit.newt */);
    }

    public static Chart chart(String toolkit) {
        AWTChartComponentFactory f = new AWTChartComponentFactory();
        return f.newChart(Chart.DEFAULT_QUALITY, toolkit);
    }

    public static Chart chart(Toolkit toolkit) {
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

    /**
     * @param toolkit can be used to indicate "offscreen, 800, 600" and thus replace implicit "awt"
     */
    @Override
    public Chart newChart(IChartComponentFactory factory, Quality quality, String toolkit) {
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

    /**
     * The AWTView support Java2d defined components (tooltips, background
     * images)
     */
    @Override
    public View newView(IChartComponentFactory factory, Scene scene, ICanvas canvas, Quality quality) {
        return new AWTView(factory, scene, canvas, quality);
    }

    /** Provide AWT Texture loading for screenshots */
    @Override
    public Renderer3d newRenderer(View view, boolean traceGL, boolean debugGL) {
        return new AWTRenderer3d(view, traceGL, debugGL);
    }

    /** bypass reflection used in super implementation */
    @Override
    protected IFrame newFrameSwing(Chart chart, Rectangle bounds, String title) {
        return null;//new FrameSwing(chart, bounds, title);
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
        return null;//new CanvasSwing(chartComponentFactory, scene, quality, capabilities, traceGL, debugGL);
    }

    @Override
    public IChartComponentFactory getFactory() {
        return this;
    }

    public JFrame newFrame(JPanel panel) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // ignore failure to set default look en feel;
        }
        JFrame frame = new JFrame();
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
        return frame;
    }

    @Override
    public ICameraMouseController newMouseCameraController(Chart chart) {
        return new AWTCameraMouseController(chart);
    }
    
    @Override
    public IMousePickingController newMousePickingController(Chart chart, int clickWidth) {
            return new AWTMousePickingController(chart, clickWidth);
    }

    /**
     * Output file of screenshot can be configured using {@link IScreenshotKeyController#setFilename(String)}.
     */
    @Override
    public IScreenshotKeyController newKeyboardScreenshotController(Chart chart) {
        // trigger screenshot on 's' letter
        String file = SCREENSHOT_FOLDER + "capture-" + Utils.dat2str(new Date(), "yyyy-MM-dd-HH-mm-ss") + ".png";
        IScreenshotKeyController screenshot;

        //if (!chart.getWindowingToolkit().equals("newt"))
            screenshot = new AWTScreenshotKeyController(chart, file);
        //else
        //    screenshot = new NewtScreenshotKeyController(chart, file);
        
        
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
        ICameraKeyController key = null;
        if (!chart.getWindowingToolkit().equals("newt"))
            key = new AWTCameraKeyController(chart);
        else
            key = new NewtCameraKeyController(chart);
        return key;
    }

    @Override
    public IFrame newFrame(Chart chart, Rectangle bounds, String title) {
        return newFrameAWT(chart, bounds, title, null);
    }
}
