package org.jzy3d.chart.factories;

import org.jzy3d.chart.AWTChart;
import org.jzy3d.chart.Chart;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.axes.EmulGLAxisBox;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.AWTView;
import org.jzy3d.plot3d.rendering.view.Camera;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.rendering.view.layout.EmulGLViewAndColorbarsLayout;
import org.jzy3d.plot3d.rendering.view.layout.ViewAndColorbarsLayout;
import org.jzy3d.plot3d.rendering.view.modes.ViewBoundMode;

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
		AWTChart chart = new AWTChart(factory, quality);
		chart.getView().setBoundMode(ViewBoundMode.AUTO_FIT); // EMULGL NEEDS AUTO_FIT!!!
        return chart;
    }
	
	@Override
    public AWTView newView(IChartFactory factory, Scene scene, ICanvas canvas, Quality quality) {
        return new AWTView(factory, scene, canvas, quality);
    }

	/*@Override
    public View newView(Scene scene, ICanvas canvas, Quality quality) {
        return newView(getFactory(), scene, canvas, quality);
    }*/

    @Override
    public Camera newCamera(Coord3d center) {
        return new Camera(center);
    }
    
	@Override
	public EmulGLAxisBox newAxe(BoundingBox3d box, View view) {
		EmulGLAxisBox axe = new EmulGLAxisBox(box);
		axe.setView(view);
		return axe;
	}



	/**
	 * This override intend to use jGL image rendering fallback based on AWT as jGL
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
