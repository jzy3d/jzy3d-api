package org.jzy3d.tests;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.ChartLauncher;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.chart.factories.AxeTransformableAWTChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Builder;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.axeTransformable.axeTransformableBuilder;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.primitives.axeTransformablePrimitive.axeTransformers.LinearTransformer;
import org.jzy3d.plot3d.primitives.axeTransformablePrimitive.axeTransformers.LogTransformer;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.*;
import org.jzy3d.plot3d.rendering.view.modes.ViewPositionMode;


public class LogTest {

	public static void main(String[] args) {
		// Define a function to plot
		Mapper mapper = new Mapper() {
		    public double f(double x, double y) {
		        //return (10 * Math.sin(x) * Math.cos(y) * x) / 1000;
		    	return x+y/2;
		    }
		};

		// Define range and precision for the function to plot
		Range range = new Range((float)0.1, 100);
		Range range2 = new Range((float)0.1,10);
		int steps = 50;

		// Create a surface drawing that function
		Shape surface = axeTransformableBuilder.buildOrthonormal(new OrthonormalGrid(range, steps, range2, steps), mapper, new LogTransformer(), new LinearTransformer(), new LinearTransformer());
		surface.setColorMapper(new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(), surface.getBounds().getZmax(), new Color(1, 1, 1, .5f)));
		surface.setFaceDisplayed(true);
		surface.setWireframeDisplayed(false);
		surface.setWireframeColor(Color.BLACK);

		// Create a chart and add the surface
		Chart chart = AxeTransformableAWTChartComponentFactory.chart(Quality.Advanced, new LogTransformer(), new LinearTransformer(), new LinearTransformer());
		chart.getScene().getGraph().add(surface);
		ChartLauncher.openChart(chart);
		/*Shape surface = Builder.buildOrthonormal(new OrthonormalGrid(range, steps, range2, steps), mapper);
		surface.setColorMapper(new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(), surface.getBounds().getZmax(), new Color(1, 1, 1, .5f)));
		surface.setFaceDisplayed(true);
		surface.setWireframeDisplayed(false);
		surface.setWireframeColor(Color.BLACK);

		// Create a chart and add the surface
		Chart chart = AWTChartComponentFactory.chart(Quality.Advanced);
		chart.getView().setSquared(false);
		chart.getScene().getGraph().add(surface);
		ChartLauncher.openChart(chart);*/
	}

}
