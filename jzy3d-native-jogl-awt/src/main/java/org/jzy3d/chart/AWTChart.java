package org.jzy3d.chart;

import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.chart.factories.IChartComponentFactory;
import org.jzy3d.maths.Dimension;
import org.jzy3d.plot3d.primitives.AbstractDrawable;
import org.jzy3d.plot3d.primitives.axes.layout.IAxeLayout;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.legends.colorbars.AWTColorbarLegend;
import org.jzy3d.plot3d.rendering.view.AWTView;
import org.jzy3d.plot3d.rendering.view.Renderer2d;

import com.jogamp.opengl.GLCapabilities;

public class AWTChart extends NativeChart {
    public AWTChart(IChartComponentFactory components, Quality quality) {
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
    
    public AWTColorbarLegend colorbar(AbstractDrawable drawable){
        return colorbar(drawable, new Dimension(100, 600), getView().getAxe().getLayout());
    }

    public AWTColorbarLegend colorbar(AbstractDrawable drawable, IAxeLayout layout){
        return colorbar(drawable, new Dimension(100, 600), layout);
    }
    
    public AWTColorbarLegend colorbar(AbstractDrawable drawable, Dimension d, IAxeLayout layout){
        AWTColorbarLegend cbar = new AWTColorbarLegend(drawable, layout);
        cbar.setMinimumSize(d);
        drawable.setLegend(cbar);
        return cbar;
    }
}
