package org.jzy3d.chart.factories;

import org.jzy3d.chart.AWTChart;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.EmulGLAnimator;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.painters.EmulGLPainter;
import org.jzy3d.plot3d.primitives.axes.EmulGLAxisBox;
import org.jzy3d.plot3d.rendering.canvas.EmulGLCanvas;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.EmulGLView;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.rendering.view.layout.EmulGLViewAndColorbarsLayout;
import org.jzy3d.plot3d.rendering.view.layout.ViewAndColorbarsLayout;

import jgl.GL;

public class EmulGLChartFactory extends ChartFactory {
    public EmulGLChartFactory() {
    	super(new EmulGLPainterFactory());
    }
    
    public EmulGLChartFactory(IPainterFactory windowFactory) {
    	super(windowFactory);
    }

	
	@Override
    public Chart newChart(IChartFactory factory, Quality quality) {
        return new AWTChart(factory, quality);
    }
	
	@Override
    public EmulGLView newView(IChartFactory factory, Scene scene, ICanvas canvas, Quality quality) {
        return new EmulGLView(factory, scene, canvas, quality);
    }
	
	@Override
	public EmulGLAxisBox newAxe(BoundingBox3d box, View view) {
		EmulGLAxisBox axe = new EmulGLAxisBox(box);
		axe.setView(view);
		return axe;
	}



	/**
	 * This overide intend to use jGL image rendering fallback based on AWT as jGL
	 * hardly handles the original {@link GL#glDrawPixel()} primitives.
	 * 
	 * As the View is still rendered center in the frame, we perform a hacky shift
	 * of the OpenGL image rendering in canvas to avoid the axis ticks beeing
	 * covered by the colorbar on the right.
	 * 
	 * @see https://github.com/jzy3d/jGL/issues/5
	 */
	@Override
	public ViewAndColorbarsLayout newViewportLayout() {
		return new EmulGLViewAndColorbarsLayout();
	}
}
