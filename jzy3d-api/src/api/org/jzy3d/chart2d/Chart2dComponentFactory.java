package org.jzy3d.chart2d;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.chart.factories.IChartComponentFactory;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.plot3d.primitives.axes.IAxe;
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
    public Chart newChart(IChartComponentFactory factory, Quality quality, String toolkit){
        return new Chart2d(factory, quality, toolkit);
    }
    
    @Override
    public Chart newChart(Quality quality, Toolkit toolkit) {
        return new Chart2d(getFactory(), quality, toolkit.toString());
    }

    @Override
    public Chart newChart(Quality quality, String toolkit) {
        return new Chart2d(getFactory(), quality, toolkit);
    }

    @Override
    public IAxe newAxe(BoundingBox3d box, View view) {
        AxeBox2d axe = new AxeBox2d(box);
        //axe.setTextRenderer(new TextBitmapRenderer());
        return axe;
    }

    @Override
    public View newView(Scene scene, ICanvas canvas, Quality quality) {
        return new View2d(getFactory(), scene, canvas, quality);
    }
    
    /* */
    
    public static Chart2d chart() {
        return chart(Quality.Intermediate);
    }
    public static Chart2d chart(Quality quality) {
        return (Chart2d)f.newChart(quality, Toolkit.newt);
    }
    public static Chart2d chart(String toolkit) {
        return (Chart2d)f.newChart(Chart.DEFAULT_QUALITY, toolkit);
    }
    public static Chart2d chart(Quality quality, Toolkit toolkit) {
        return (Chart2d)f.newChart(quality, toolkit);
    }
    public static Chart2d chart(Quality quality, String toolkit) {
        return (Chart2d)f.newChart(quality, toolkit);
    }
    
    static Chart2dComponentFactory f = new Chart2dComponentFactory();
}
