package org.jzy3d.plot3d.rendering.ddp;

import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.plot3d.rendering.canvas.ICanvas;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.ddp.algorithms.PeelingMethod;
import org.jzy3d.plot3d.rendering.scene.Scene;
import org.jzy3d.plot3d.rendering.view.View;

public class PeelingChartFactory extends AWTChartFactory{
	public PeelingChartFactory(PeelingMethod method) {
		super(new PeelingPainterFactory());
		this.method = method;
	}
	
	@Override
	public View newView(IChartFactory factory, Scene scene, ICanvas canvas, Quality quality){
        return new DepthPeelingView(factory, scene, canvas, quality);
    }
	
    public static boolean CHART_CANVAS_AUTOSWAP = false;

	PeelingMethod method;
}
