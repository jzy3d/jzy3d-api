package org.jzy3d.chart;

import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.chart.factories.SwingChartFactory;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.AWTView;
import org.jzy3d.plot3d.rendering.view.Renderer2d;

public class SwingChart extends Chart {
    public SwingChart(Quality quality) {
        super(new SwingChartFactory(), quality);
    }

    public SwingChart(IChartFactory factory, Quality quality) {
        super(factory, quality);
    }
    
    public void addRenderer(Renderer2d renderer2d) {
        getAWTView().addRenderer2d(renderer2d);
    }

    public void removeRenderer(Renderer2d renderer2d) {
        getAWTView().removeRenderer2d(renderer2d);
    }

    protected AWTView getAWTView() {
        return (AWTView) view;
    }
}
