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
import org.jzy3d.plot3d.primitives.CompileableComposite;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.primitives.axeTransformablePrimitive.axeTransformers.AxeTransformerSet;
import org.jzy3d.plot3d.primitives.axeTransformablePrimitive.axeTransformers.LinearAxeTransformer;
import org.jzy3d.plot3d.primitives.axeTransformablePrimitive.axeTransformers.LogAxeTransformer;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.*;
import org.jzy3d.plot3d.rendering.view.modes.ViewPositionMode;


public class LogTest {

	public static void main(String[] args) {
		// Define a function to plot
		Mapper mapper = new Mapper() {
		    public double f(double x, double y) {
		        double value =  Math.abs((10 * Math.sin(x) * Math.cos(y) * x) / 10) + 10;
		        return value;
		    }
		};

		// Define range and precision for the function to plot
		Range range = new Range((float)0.1, 10);
		Range range2 = new Range((float)0.1,50);
		int steps = 200;

		AxeTransformerSet transformers = new AxeTransformerSet(new LogAxeTransformer(), new LinearAxeTransformer(), new LinearAxeTransformer());
		
		// Create a surface drawing that function
		CompileableComposite surface = axeTransformableBuilder.buildOrthonormalBig(new OrthonormalGrid(range, steps, range2, steps), mapper, transformers);
		surface.setColorMapper(new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(), surface.getBounds().getZmax(), new Color(1, 1, 1, .5f)));
		surface.setFaceDisplayed(true);
		surface.setWireframeDisplayed(false);
		surface.setWireframeColor(Color.BLACK);

		// Create a chart and add the surface
		Chart chart = AxeTransformableAWTChartComponentFactory.chart(Quality.Advanced, transformers);
		chart.getScene().getGraph().add(surface);
		chart.getView().setTransformers(transformers);
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
