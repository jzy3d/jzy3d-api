package org.jzy3d.emulgl.integration;

import java.util.Random;

import org.junit.Test;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.EmulGLChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.junit.ChartTester;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.utils.LoggerUtils;

public class ITTestScatterChart {
	@Test
	public void surfaceTest() {
		LoggerUtils.minimal();

		// When
		EmulGLChartFactory factory = new EmulGLChartFactory();
		Chart chart = factory.newChart(Quality.Advanced);

		chart.add(scatter());

		// Then
		ChartTester tester = new ChartTester();
        tester.assertSimilar(chart, ChartTester.EXPECTED_IMAGE_FOLDER_DEFAULT + this.getClass().getSimpleName() + ".png");
	}
	
	private static Scatter scatter() {
		int size = 50000;
		float x;
		float y;
		float z;
		float a;

		Coord3d[] points = new Coord3d[size];
		Color[] colors = new Color[size];

		Random r = new Random();
		r.setSeed(0);
		for (int i = 0; i < size; i++) {
			x = r.nextFloat() - 0.5f;
			y = r.nextFloat() - 0.5f;
			z = r.nextFloat() - 0.5f;
			points[i] = new Coord3d(x, y, z);
			a = 0.75f;
			colors[i] = new Color(x, y, z, a);
		}

		Scatter scatter = new Scatter(points, colors);
		scatter.setWidth(3);
		return scatter;
	}
}
