package org.jzy3d.chart2d;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.chart.factories.IChartComponentFactory;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.View;

public class Chart2dComponentFactory extends AWTChartComponentFactory{
    @Override
    public View newView(Scene scene, ICanvas canvas, Quality quality) {
        return new View2d(this, scene, canvas, quality);
    }
    
    
    public static Chart chart() {
        return chart(Quality.Intermediate);
    }
    
    public static Chart chart(Quality quality) {
        Chart2dComponentFactory f = new Chart2dComponentFactory();
        return f.newChart(quality, Toolkit.newt);
    }

    public static Chart chart(String toolkit) {
        Chart2dComponentFactory f = new Chart2dComponentFactory();
        return f.newChart(Chart.DEFAULT_QUALITY, toolkit);
    }

    public static Chart chart(Quality quality, Toolkit toolkit) {
        Chart2dComponentFactory f = new Chart2dComponentFactory();
        return f.newChart(quality, toolkit);
    }

    public static Chart chart(Quality quality, String toolkit) {
        Chart2dComponentFactory f = new Chart2dComponentFactory();
        return f.newChart(quality, toolkit);
    }
    
    @Override
    public Chart newChart(IChartComponentFactory factory, Quality quality, String toolkit){
        return new Chart2d(factory, quality, toolkit);
    }

}
