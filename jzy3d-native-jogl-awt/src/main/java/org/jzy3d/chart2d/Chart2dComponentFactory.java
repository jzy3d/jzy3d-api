package org.jzy3d.chart2d;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.chart.factories.IChartComponentFactory;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.View;

public class Chart2dComponentFactory extends AWTChartComponentFactory{
    @Override
    public IChartComponentFactory getFactory() {
        return this;
    }
    
    @Override
    public Chart2d newChart(IChartComponentFactory factory, Quality quality){
        return new Chart2d(factory, quality);
    }
    
    @Override
    public Chart2d newChart(Quality quality) {
        return new Chart2d(getFactory(), quality);
    }

    @Override
    public AxeBox2d newAxe(BoundingBox3d box, View view) {
        AxeBox2d axe = new AxeBox2d(box);
        //axe.setTextRenderer(new TextBitmapRenderer());
        return axe;
    }

    @Override
    public View2d newView(IChartComponentFactory factory, Scene scene, ICanvas canvas, Quality quality) {
        return new View2d(factory, scene, canvas, quality);
    }
    
    /* */
    
    public static Chart2d chart() {
        return chart(Quality.Intermediate);
    }
    public static Chart2d chart(Quality quality) {
        return f.newChart(quality);
    }
    public static Chart2d chart(String toolkit) {
        return f.newChart(Chart.DEFAULT_QUALITY);
    }
    
    static Chart2dComponentFactory f = new Chart2dComponentFactory();
}
