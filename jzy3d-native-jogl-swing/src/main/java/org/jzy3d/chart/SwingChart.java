package org.jzy3d.chart;

import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.chart.factories.SwingChartFactory;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.AWTRenderer2d;
import org.jzy3d.plot3d.rendering.view.AWTView;

public class SwingChart extends AWTNativeChart {
    public SwingChart(Quality quality) {
        super(new SwingChartFactory(), quality);
    }

    public SwingChart(IChartFactory factory, Quality quality) {
        super(factory, quality);
    }
    
    public void addRenderer(AWTRenderer2d renderer2d) {
        getAWTView().addRenderer2d(renderer2d);
    }

    public void removeRenderer(AWTRenderer2d renderer2d) {
        getAWTView().removeRenderer2d(renderer2d);
    }

    public AWTView getAWTView() {
        return (AWTView) view;
    }
}
