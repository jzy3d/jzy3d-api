package org.jzy3d.chart;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.jzy3d.chart.factories.ChartComponentFactory;
import org.jzy3d.chart.factories.IChartComponentFactory;
import org.jzy3d.plot3d.primitives.AbstractDrawable;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.legends.Legend;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.rendering.view.layout.ColorbarViewportLayout;
import org.jzy3d.plot3d.rendering.view.layout.IViewportLayout;



/**
 * A {@link ChartView} allows displaying a 3d scene on the left,
 * and a set of {@link AbstractDrawable}'s {@link Legend} on the right.
 *
 * @author Martin Pernollet
 */
public class ChartView extends View {
    public ChartView(Scene scene, ICanvas canvas, Quality quality) {
    	this(new ChartComponentFactory(), scene, canvas, quality);
    }

    public ChartView(IChartComponentFactory factory, Scene scene, ICanvas canvas, Quality quality) {
        super(factory, scene, canvas, quality);
    }

    /* */


    /**
     * Set the camera held by this view, and draw the scene graph.
     * Performs all transformations of eye, target coordinates to adapt the camera settings
     * to the scaled scene.
     */
    public void render(GL2 gl, GLU glu) {
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


    protected IViewportLayout layout = new ColorbarViewportLayout();
}

