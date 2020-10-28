package org.jzy3d.chart;

import org.jzy3d.chart.factories.IChartComponentFactory;
import org.jzy3d.plot3d.rendering.canvas.IScreenCanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;

import com.jogamp.opengl.GLAnimatorControl;
import com.jogamp.opengl.util.texture.TextureData;

public class NativeChart extends Chart{
	public NativeChart(IChartComponentFactory components, Quality quality) {
		super(components, quality);
	}
	
	protected NativeChart(){
    	super();
    }

	public void pauseAnimator() {
        if (canvas != null && canvas instanceof IScreenCanvas) {
            GLAnimatorControl control = ((IScreenCanvas) canvas).getAnimator();
            if (control != null && control.isAnimating()) {
                control.pause();
            }
        }
    }

    public void resumeAnimator() {
        if (canvas != null && canvas instanceof IScreenCanvas) {
            GLAnimatorControl control = ((IScreenCanvas) canvas).getAnimator();
            if (control != null && control.isPaused()) {
                control.resume();
            }
        }
    }

    public void startAnimator() {
        if (canvas != null && canvas instanceof IScreenCanvas) {
            GLAnimatorControl control = ((IScreenCanvas) canvas).getAnimator();
            if (control != null && !control.isStarted()) {
                control.start();
            }
        }
    }

    public void stopAnimator() {
        if (canvas != null && canvas instanceof IScreenCanvas) {
            GLAnimatorControl control = ((IScreenCanvas) canvas).getAnimator();
            if (control != null)
                control.stop();
        }
    }
    
    public TextureData screenshot() {
        return canvas.screenshot();
    }
}
