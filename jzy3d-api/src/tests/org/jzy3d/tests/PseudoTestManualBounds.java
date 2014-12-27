package org.jzy3d.tests;


import org.jzy3d.analysis.AbstractAnalysis;
import org.jzy3d.analysis.AnalysisLauncher;
import org.jzy3d.analysis.IAnalysis;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.controllers.keyboard.camera.AWTCameraKeyController;
import org.jzy3d.chart.controllers.mouse.camera.NewtCameraMouseController;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Builder;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.modes.ViewBoundMode;

public class PseudoTestManualBounds extends AbstractAnalysis {
    public static void main(String[] args) throws Exception {
    	IAnalysis d = new PseudoTestManualBounds();
    	d.setCanvasType("newt");
        AnalysisLauncher.open(d);
        
        // calling: 
        // d.getChart().getView().setBoundManual(new BoundingBox3d(-1,1, -1,1, -1,1));
        // immediatly will fail since frame is probably not open yet, and thus the view 
        // initialization will reset bounds to the scene graph bounds.
        //
        // to avoid being erased by the view init, one may either:
        // wait a little bit: Thread.sleep(1000);
        // or force an initialization bounds as follow
        // @since oct 27 2012
        d.getChart().getView().setInitBounds(new BoundingBox3d(-1,1, -1,1, -1,1));
        System.out.println(d.getChart().getView().getBounds());
    }

    @Override
    public void init() {
        // Define a function to plot
        Mapper mapper = new Mapper() {
            @Override
            public double f(double x, double y) {
                return 0;//x * Math.sin(x * y);
            }
        };

        // Define range and precision for the function to plot
        Range range = new Range(-3, 3);
        int steps = 80;

        // Create the object to represent the function over the given range.
        final Shape surface = Builder.buildOrthonormal(new OrthonormalGrid(range, steps, range, steps), mapper);
        surface.setColorMapper(new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(), surface.getBounds().getZmax(), new Color(1, 1, 1, .5f)));
        surface.setFaceDisplayed(true);
        surface.setWireframeDisplayed(false);
System.out.println(getCanvasType());
        // Create a chart
        chart = new Chart(Quality.Advanced, getCanvasType());
        chart.getScene().getGraph().add(surface);
        chart.addController(new AWTCameraKeyController());
        
        chart.getView().setBoundMode(ViewBoundMode.MANUAL);
        NewtCameraMouseController c = new NewtCameraMouseController(chart);
    }
}
