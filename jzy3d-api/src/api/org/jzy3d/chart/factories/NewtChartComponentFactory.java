package org.jzy3d.chart.factories;

import java.util.Date;

import javax.media.opengl.GLCapabilities;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.apache.log4j.Logger;
import org.jzy3d.bridge.IFrame;
import org.jzy3d.bridge.awt.FrameAWT;
import org.jzy3d.bridge.swing.FrameSwing;
import org.jzy3d.chart.AWTChart;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.keyboard.camera.ICameraKeyController;
import org.jzy3d.chart.controllers.keyboard.camera.NewtCameraKeyController;
import org.jzy3d.chart.controllers.keyboard.screenshot.IScreenshotKeyController;
import org.jzy3d.chart.controllers.keyboard.screenshot.IScreenshotKeyController.IScreenshotEventListener;
import org.jzy3d.chart.controllers.keyboard.screenshot.NewtScreenshotKeyController;
import org.jzy3d.chart.controllers.mouse.camera.ICameraMouseController;
import org.jzy3d.chart.controllers.mouse.camera.NewtCameraMouseController;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Dimension;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.maths.Utils;
import org.jzy3d.plot3d.primitives.axes.AxeBox;
import org.jzy3d.plot3d.primitives.axes.IAxe;
import org.jzy3d.plot3d.rendering.canvas.CanvasAWT;
import org.jzy3d.plot3d.rendering.canvas.CanvasNewtAwt;
import org.jzy3d.plot3d.rendering.canvas.CanvasSwing;
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

/**
 * Still using some AWT components
 * 
 * @author martin
 *
 */
public class NewtChartComponentFactory extends ChartComponentFactory {

    public static Chart chart() {
        return chart(Quality.Intermediate);
    }

    public static Chart chart(Quality quality) {
        NewtChartComponentFactory f = new NewtChartComponentFactory();
        return f.newChart(quality, Toolkit.newt);
    }

    public static Chart chart(String toolkit) {
        NewtChartComponentFactory f = new NewtChartComponentFactory();
        return f.newChart(Chart.DEFAULT_QUALITY, toolkit);
    }

    public static Chart chart(Quality quality, Toolkit toolkit) {
        NewtChartComponentFactory f = new NewtChartComponentFactory();
        return f.newChart(quality, toolkit);
    }

    public static Chart chart(Quality quality, String toolkit) {
        NewtChartComponentFactory f = new NewtChartComponentFactory();
        return f.newChart(quality, toolkit);
    }

    /* */

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
    public View newView(Scene scene, ICanvas canvas, Quality quality) {
        return new AWTView(getFactory(), scene, canvas, quality);
    }

    /** Provide AWT Texture loading for screenshots */
    @Override
    public Renderer3d newRenderer(View view, boolean traceGL, boolean debugGL) {
        return new AWTRenderer3d(view, traceGL, debugGL);
    }

    /** bypass reflection used in super implementation */
    @Override
    protected IFrame newFrameSwing(Chart chart, Rectangle bounds, String title) {
        return new FrameSwing(chart, bounds, title);
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
            throw new IllegalArgumentException("Can't ask for an AWT chart type in Newt Factory.");
        case swing:
            throw new IllegalArgumentException("Can't ask for an Swing chart type in Newt Factory.");
        case newt:
            return new CanvasNewtAwt(factory, scene, quality, capabilities, traceGL, debugGL);
        case offscreen:
            Dimension dimension = getCanvasDimension(windowingToolkit);
            return new OffscreenCanvas(factory, scene, quality, capabilities, dimension.width, dimension.height, traceGL, debugGL);
        default:
            throw new IllegalArgumentException("unknown chart type:" + chartType);
        }
    }

    /** bypass reflection used in super implementation */
    @Override
    protected ICanvas newCanvasAWT(IChartComponentFactory chartComponentFactory, Scene scene, Quality quality, GLCapabilities capabilities, boolean traceGL, boolean debugGL) {
        throw new IllegalArgumentException("Can't ask for an AWT chart type in Newt Factory.");
    }

    /** bypass reflection used in super implementation */
    @Override
    protected ICanvas newCanvasSwing(IChartComponentFactory chartComponentFactory, Scene scene, Quality quality, GLCapabilities capabilities, boolean traceGL, boolean debugGL) {
        throw new IllegalArgumentException("Can't ask for an Swing chart type in Newt Factory.");

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
    public ICameraMouseController newMouseController(Chart chart) {
        return new NewtCameraMouseController(chart);
    }

    @Override
    public IScreenshotKeyController newScreenshotKeyController(Chart chart) {
        // trigger screenshot on 's' letter
        String file = SCREENSHOT_FOLDER + "capture-" + Utils.dat2str(new Date(), "yyyy-MM-dd-HH-mm-ss") + ".png";
        IScreenshotKeyController screenshot;

        screenshot = new NewtScreenshotKeyController(chart, file);
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

    @Override
    public ICameraKeyController newKeyController(Chart chart) {
        return new NewtCameraKeyController(chart);
    }

    @Override
    public IFrame newFrame(Chart chart, Rectangle bounds, String title) {
        return newFrameAWT(chart, bounds, title, null);
    }
}
