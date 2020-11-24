package org.jzy3d.chart;

import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.maths.Dimension;
import org.jzy3d.plot3d.primitives.Drawable;
import org.jzy3d.plot3d.primitives.axes.layout.IAxisLayout;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.legends.colorbars.AWTColorbarLegend;
import org.jzy3d.plot3d.rendering.view.AWTView;
import org.jzy3d.plot3d.rendering.view.Renderer2d;

public class AWTChart extends NativeChart {
    public AWTChart(IChartFactory components, Quality quality) {
        super(components, quality);
    }
    
    protected AWTChart(){
    	super();
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
    
    public AWTColorbarLegend colorbar(Drawable drawable){
        return colorbar(drawable, new Dimension(100, 600), getView().getAxis().getLayout());
    }

    public AWTColorbarLegend colorbar(Drawable drawable, IAxisLayout layout){
        return colorbar(drawable, new Dimension(100, 600), layout);
    }
    
    public AWTColorbarLegend colorbar(Drawable drawable, Dimension d, IAxisLayout layout){
        AWTColorbarLegend cbar = new AWTColorbarLegend(drawable, layout);
        cbar.setMinimumSize(d);
        drawable.setLegend(cbar);
        return cbar;
    }
}
