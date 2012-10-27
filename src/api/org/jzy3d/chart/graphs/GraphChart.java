package org.jzy3d.chart.graphs;

import org.jzy3d.chart.Chart;
import org.jzy3d.plot3d.rendering.canvas.Quality;

public class GraphChart extends Chart{
	public GraphChart() {
		super();
	}

	public GraphChart(Quality quality) {
		super(quality);
	}

	public GraphChart(String chartType) {
		super(chartType);
	}
	
	public GraphChart(Quality quality, String windowingToolkit){
        super(new GraphChartComponentFactory(), quality, windowingToolkit, org.jzy3d.global.Settings.getInstance().getGLCapabilities());
    }
/*
	protected ChartScene initializeScene(boolean graphsort){
		return new ChartScene(graphsort){
			protected GraphView initializeChartView(Scene scene, ICanvas canvas, Quality quality){
				return new GraphView(scene, canvas, quality);
			}
		};
	}*/
}
