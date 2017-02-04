package org.jzy3d.demos.ddp;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.ChartLauncher;
import org.jzy3d.chart.factories.IChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.io.glsl.GLSLProgram;
import org.jzy3d.io.glsl.GLSLProgram.Strictness;
import org.jzy3d.maths.Dimension;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Builder;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.ddp.PeelingComponentFactory;
import org.jzy3d.plot3d.rendering.ddp.algorithms.PeelingMethod;
import org.jzy3d.plot3d.rendering.legends.colorbars.AWTColorbarLegend;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;


public class PeeledWireSurfaceDemo {
	public static void main(String[] args) throws Exception {
        // Define a function to plot
        Mapper mapper = new Mapper() {
            @Override
            public double f(double x, double y) {
                return 10 * Math.sin(x / 10) * Math.cos(y / 20) * x;
            }
        };

        // Define range and precision for the function to plot
        Range range = new Range(-150, 150);
        int steps = 50;

        // Create the object to represent the function over the given range.
        final Shape surface = Builder.buildOrthonormal(
                new OrthonormalGrid(range, steps, range, steps), mapper);
        surface.setColorMapper(new ColorMapper(new ColorMapRainbow(), surface
                .getBounds().getZmin(), surface.getBounds().getZmax(),
                new Color(1, 1, 1, .5f)));
        surface.setFaceDisplayed(true);
        surface.setWireframeDisplayed(true);
        surface.setWireframeColor(Color.BLACK);

        // Create a chart and add surface
        GLSLProgram.DEFAULT_STRICTNESS = Strictness.CONSOLE_NO_WARN_UNIFORM_NOT_FOUND;
        IChartComponentFactory factory = new PeelingComponentFactory(PeelingMethod.F2B_PEELING_MODE);
        
        
        GLProfile profile = GLProfile.getMaxProgrammable(true);
        GLCapabilities capabilities = new GLCapabilities(profile);
        capabilities.setHardwareAccelerated(true);
        // ATTENTION AVEC 
        
        Chart chart = new Chart(factory, Quality.Advanced);
        chart.getScene().getGraph().add(surface);

        // Setup a colorbar 
        AWTColorbarLegend cbar = new AWTColorbarLegend(surface, chart.getView().getAxe().getLayout());
        cbar.setMinimumSize(new Dimension(100, 600));
        surface.setLegend(cbar);
        
        ChartLauncher.openChart(chart);
    }
}
