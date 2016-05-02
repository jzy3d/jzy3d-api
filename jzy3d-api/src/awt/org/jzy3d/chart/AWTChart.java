package org.jzy3d.chart;

import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.chart.factories.IChartComponentFactory;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.AWTView;
import org.jzy3d.plot3d.rendering.view.Renderer2d;

import com.jogamp.opengl.GLCapabilities;

public class AWTChart extends Chart {
    public AWTChart() {
        this(DEFAULT_QUALITY, DEFAULT_WINDOWING_TOOLKIT);
    }

    public AWTChart(Quality quality, String windowingToolkit) {
        this(new AWTChartComponentFactory(), quality, windowingToolkit, org.jzy3d.chart.Settings.getInstance().getGLCapabilities());
    }

    
    public AWTChart(IChartComponentFactory factory, Quality quality, String windowingToolkit, GLCapabilities capabilities) {
        super(factory, quality, windowingToolkit, capabilities);
    }

    public AWTChart(IChartComponentFactory factory, Quality quality, String windowingToolkit) {
        super(factory, quality, windowingToolkit);
    }

    public AWTChart(IChartComponentFactory components, Quality quality) {
        super(components, quality);
    }

    public AWTChart(Quality quality) {
        this(quality, DEFAULT_WINDOWING_TOOLKIT);
    }

    public AWTChart(String windowingToolkit) {
        this(DEFAULT_QUALITY, windowingToolkit);
    }

    public void addRenderer(Renderer2d renderer2d) {
        getAWTView().addRenderer2d(renderer2d);
    }

    public void removeRenderer(Renderer2d renderer2d) {
        getAWTView().removeRenderer2d(renderer2d);
    }

    public AWTView getAWTView() {
        return (AWTView) view;
    }
}
