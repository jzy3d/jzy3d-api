package org.jzy3d.junit.replay.trials;

import org.junit.Test;
import org.jzy3d.chart.Chart;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.junit.ChartTest;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Builder;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.legends.colorbars.ColorbarLegend;
import org.jzy3d.utils.LoggerUtils;

public class SimpleChartTest extends ChartTest{
   
	@Test
	public void test() throws Exception {
	    LoggerUtils.minimal();
               Chart chart = chart(getTestCanvasType());
               execute(chart);
	}
	
	public static Chart chart(String wt){
		// Define a function to plot
        Mapper mapper = new Mapper(){
            public double f(double x, double y) {
                double sigma = 10;
                return Math.exp( -(x*x+y*y) / sigma  )  *  Math.abs( Math.cos( 2 * Math.PI * ( x*x + y*y ) ) );
            }
        };

        // Define range and precision for the function to plot
        Range range = new Range(-.5, .5);
        int steps   = 50;
        
        // Create the object to represent the function over the given range.
        final Shape surface = Builder.buildOrthonormal(new OrthonormalGrid(range, steps, range, steps), mapper);
        surface.setColorMapper(new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(), surface.getBounds().getZmax()));
        surface.setFaceDisplayed(true);
        surface.setWireframeDisplayed(true);
        surface.setWireframeColor(Color.BLACK);

        // Setup a colorbar for the surface object and add it to the scene
        Chart chart = new Chart(wt);
        chart.getScene().getGraph().add(surface);
        ColorbarLegend cbar = new ColorbarLegend(surface, chart.getView().getAxe().getLayout());
        surface.setLegend(cbar);
        return chart;
	}
}
