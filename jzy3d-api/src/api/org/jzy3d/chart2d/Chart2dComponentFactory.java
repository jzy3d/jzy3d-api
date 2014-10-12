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
    public Chart2dComponentFactory(){
        System.out.println("init factory");
    }
    @Override
    public Chart newChart(IChartComponentFactory factory, Quality quality, String toolkit){
        return new Chart2d(factory, quality, toolkit);
    }
    @Override
    public IAxe newAxe(BoundingBox3d box, View view) {
        AxeBox2d axe = new AxeBox2d(box);
        //axe.setTextRenderer(new TextBitmapRenderer());
        return axe;
        //return new AxeBox2d(box);
    }
    @Override
    public View newView(Scene scene, ICanvas canvas, Quality quality) {
        return new View2d(this, scene, canvas, quality);
    }
    
    public static Chart chart() {
        return chart(Quality.Intermediate);
    }
    public static Chart chart(Quality quality) {
        return f.newChart(quality, Toolkit.newt);
    }
    public static Chart chart(String toolkit) {
        return f.newChart(Chart.DEFAULT_QUALITY, toolkit);
    }
    public static Chart chart(Quality quality, Toolkit toolkit) {
        return f.newChart(quality, toolkit);
    }
    public static Chart chart(Quality quality, String toolkit) {
        return f.newChart(quality, toolkit);
    }
    
    static Chart2dComponentFactory f = new Chart2dComponentFactory();
}
