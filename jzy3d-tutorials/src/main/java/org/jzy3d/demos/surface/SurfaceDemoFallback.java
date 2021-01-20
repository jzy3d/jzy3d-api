package org.jzy3d.demos.surface;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.fallback.FallbackChartFactory;
import org.jzy3d.chart.fallback.FallbackChartFrameAbstract;
import org.jzy3d.chart.fallback.FallbackChartFrameSwing;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.primitives.Surface;
import org.jzy3d.plot3d.rendering.canvas.Quality;

/**
 * Illustrate how to create charts relying on offscreen image generation, then
 * display this image in a standard swing component.
 * 
 * This is usefull to address layout issues. For example one can better mix JOGL
 * and MigLayout on MacOSX which proved to not be able to downsize properly.
 */
public class SurfaceDemoFallback {
	public static void main(String[] args) {
		Shape surface = surface();

		// -------------------------------
		// Create a chart
		Quality quality = Quality.Advanced;

		FallbackChartFactory factory = new FallbackChartFactory();
		Chart chart = factory.newChart(quality);
		chart.getScene().getGraph().add(surface);

		FallbackChartFrameAbstract w = new FallbackChartFrameSwing(chart);

		chart.addMouseCameraController();
	}

	private static Shape surface() {
		Mapper mapper = new Mapper() {
			@Override
			public double f(double x, double y) {
				return x * Math.sin(x * y);
			}
		};

		// Define range and precision for the function to plot
		Range range = new Range(-3, 3);
		int steps = 80;

		// Create the object to represent the function over the given range.
		Shape surface = Surface.shape(mapper, range, steps, new ColorMapRainbow(), .5f);
		return surface;
	}
}
