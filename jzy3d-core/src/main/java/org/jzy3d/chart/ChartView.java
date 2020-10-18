package org.jzy3d.chart;

import org.jzy3d.chart.factories.IChartComponentFactory;
import org.jzy3d.plot3d.primitives.AbstractDrawable;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.rendering.view.layout.IViewportLayout;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.glu.GLU;



/**
 * A {@link ChartView} allows displaying a 3d scene on the left,
 * and a set of {@link AbstractDrawable}'s {@link AWTLegend} on the right.
 *
 * @author Martin Pernollet
 */
public class ChartView extends View {

    public ChartView(IChartComponentFactory factory, Scene scene, ICanvas canvas, Quality quality) {
        super(factory, scene, canvas, quality);
        layout = factory.newViewportLayout();
    }

    /* */


    /**
     * Set the camera held by this view, and draw the scene graph.
     * Performs all transformations of eye, target coordinates to adapt the camera settings
     * to the scaled scene.
     */
    @Override
    public void render(GL gl, GLU glu) {
    	fireViewLifecycleWillRender(null);

    	layout.update(getChart());
    	layout.render(gl, glu, getChart());
        
        //renderOverlay(gl);
        if( dimensionDirty )
        	dimensionDirty = false;
    }

    
    /* */

    public IViewportLayout getLayout() {
        return layout;
    }

    public void setLayout(IViewportLayout layout) {
        this.layout = layout;
    }


    protected IViewportLayout layout;
}

