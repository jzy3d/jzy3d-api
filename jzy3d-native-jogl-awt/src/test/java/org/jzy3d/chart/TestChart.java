package org.jzy3d.chart;

import org.junit.Assert;
import org.junit.Test;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.chart.factories.ChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.view.modes.ViewBoundMode;

public class TestChart {
	@Test
	public void givenNativeOffscreenChart_whenAddSurface_thenViewIsInAutotFitMode() {
		
		// Given
		
		ChartFactory factory = new AWTChartFactory();
		factory.setOffscreen(500, 500);
		
		Chart chart = factory.newChart();

		// --------------------------
		// When
		
		chart.add(surface());
		
		// --------------------------
		// Then

		Assert.assertEquals(ViewBoundMode.AUTO_FIT, chart.getView().getBoundsMode());
		Assert.assertFalse("Near clipping plane != 0", 0==chart.getView().getCamera().getNear());
		Assert.assertFalse("Far clipping plane != 0", 0==chart.getView().getCamera().getFar());
	}
	

	protected Shape surface() {
		Mapper mapper = new Mapper() {
			@Override
			public double f(double x, double y) {
				return x * Math.sin(x * y);
			}
		};
		Range range = new Range(-3, 3);
		int steps = 50;

		Shape surface = new SurfaceBuilder().orthonormal(new OrthonormalGrid(range, steps, range, steps), mapper);
		ColorMapper colorMapper = new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(),
				surface.getBounds().getZmax(), new Color(1, 1, 1, .5f));
		surface.setColorMapper(colorMapper);
		return surface;
	}
}
