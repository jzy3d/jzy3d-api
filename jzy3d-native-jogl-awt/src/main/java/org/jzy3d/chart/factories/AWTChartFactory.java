package org.jzy3d.chart.factories;

import org.apache.log4j.Logger;
import org.jzy3d.chart.AWTNativeChart;
import org.jzy3d.chart.Chart;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.AWTNativeView;
import org.jzy3d.plot3d.rendering.view.AWTRenderer3d;
import org.jzy3d.plot3d.rendering.view.Renderer3d;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.rendering.view.layout.IViewportLayout;
import org.jzy3d.plot3d.rendering.view.layout.ViewAndColorbarsLayout;

public class AWTChartFactory extends NativeChartFactory {
    public static Chart chart() {
        return chart(Quality.Intermediate);
    }

    public static Chart chart(Quality quality) {
        AWTChartFactory f = new AWTChartFactory();
        return f.newChart(quality);
    }

    /* *************************************************** */
	
    static Logger logger = Logger.getLogger(AWTChartFactory.class);
    
    public AWTChartFactory() {
    	super(new AWTPainterFactory());
    }
    
    public AWTChartFactory(IPainterFactory painterFactory) {
    	super(painterFactory);
    }

    /**
     * The AWTView support Java2d defined components (tooltips, background
     * images)
     */
    @Override
    public View newView(IChartFactory factory, Scene scene, ICanvas canvas, Quality quality) {
        return new AWTNativeView(factory, scene, canvas, quality);
    }
    
	@Override
    public AWTNativeChart newChart(IChartFactory factory, Quality quality) {
        return new AWTNativeChart(factory, quality);
    }

    @Override
    public IViewportLayout newViewportLayout() {
        return new ViewAndColorbarsLayout();
    }

    
    /** Provide AWT Texture loading for screenshots */
    @Override
    public Renderer3d newRenderer(View view, boolean traceGL, boolean debugGL) {
        return new AWTRenderer3d(view, traceGL, debugGL);
    }

    @Override
    public IChartFactory getFactory() {
        return this;
    }
}
