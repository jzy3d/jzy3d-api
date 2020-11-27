package org.jzy3d.chart;

import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.plot3d.rendering.canvas.INativeScreenCanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;

import com.jogamp.opengl.GLAnimatorControl;
import com.jogamp.opengl.util.texture.TextureData;

public class AWTNativeChart extends AWTChart {
	public AWTNativeChart(IChartFactory components, Quality quality) {
		super(components, quality);
	}

	protected AWTNativeChart() {
		super();
	}

	public void setAnimated(boolean status) {
		getQuality().setAnimated(status);

		if (status) {
			getAnimator().start();
		} else {
			getAnimator().stop();
		}
	}

	public void pauseAnimator() {
		GLAnimatorControl control = getAnimator();
		if (control != null && control.isAnimating()) {
			control.pause();
		}
	}

	public void resumeAnimator() {
		GLAnimatorControl control = getAnimator();
		if (control != null && control.isPaused()) {
			control.resume();
		}
	}

	public void startAnimator() {
		GLAnimatorControl control = getAnimator();
		if (control != null && !control.isStarted()) {
			control.start();
		}
	}

	public void stopAnimator() {
		GLAnimatorControl control = getAnimator();
		if (control != null)
			control.stop();
	}

	protected GLAnimatorControl getAnimator() {
		if (canvas != null && canvas instanceof INativeScreenCanvas)
			return ((INativeScreenCanvas) canvas).getAnimator();
		else
			return null;
	}

	public TextureData screenshot() {
		return (TextureData) canvas.screenshot();
	}
}
