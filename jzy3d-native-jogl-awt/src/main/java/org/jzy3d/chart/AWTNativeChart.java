package org.jzy3d.chart;

import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.plot3d.rendering.canvas.INativeScreenCanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;

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
			((INativeScreenCanvas)getCanvas()).getAnimation().start();
		} else {
			((INativeScreenCanvas)getCanvas()).getAnimation().stop();
		}
	}

	public TextureData screenshot() {
		return (TextureData) canvas.screenshot();
	}
}
