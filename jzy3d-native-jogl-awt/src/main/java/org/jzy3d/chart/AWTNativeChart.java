package org.jzy3d.chart;

import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.plot3d.rendering.canvas.IScreenCanvas;
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

		
		if(getCanvas() instanceof IScreenCanvas) {
			IScreenCanvas screenCanvas =  (IScreenCanvas)getCanvas();

			if (status) {
				screenCanvas.getAnimation().start();
			} else {
				screenCanvas.getAnimation().stop();
			}
		}
	}

	public TextureData screenshot() {
		return (TextureData) canvas.screenshot();
	}
}
